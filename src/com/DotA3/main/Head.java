 package com.DotA3.main;

import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.*;

public class Head {  
	public static void main(String[] args){
		init();
		while(Head.running){ 
			run();
		}
		tracker.closeWriter();
		Display.destroy();
		((Player)controllers.get(0)).gui.dispatchEvent(new WindowEvent(((Player)controllers.get(0)).gui, WindowEvent.WINDOW_CLOSING));
	}
	static boolean running;
	private static int xDisplay;
	private static int yDisplay;
	static final int heroPickSecondDelay = 2;
	public static ArrayList<HeroOwner> controllers = new ArrayList<HeroOwner>();
	private final static Location wrap = new Location(3000,2000);
	static ArrayList<Missile> missiles = new ArrayList<Missile>();
	static int tickCount;
	private static ArrayList<Actor> deleteThis = new ArrayList<Actor>();
	public static ArrayList<Actor> allActors = new ArrayList<Actor>();
	public static Map map;
	public static boolean pause;
	public static Quadtree<Double, Actor>  qt = new Quadtree<Double, Actor>();
	public static Tracker tracker = new Tracker();
	private static final ResolutionMode resolutionMode = ResolutionMode.SCAN;
	enum ResolutionMode{
		DEFAULT, CUSTOM, SCAN,;
	}
	private static void init(){
		running = true;
		switch(resolutionMode){
		case DEFAULT:
			setResolutionToDefault();
			break;
		case CUSTOM:
			setCustomResolution();
			break;
		case SCAN:
			scanAndSetResolution();
			break;
		}
		try {
			Display.setDisplayMode(new DisplayMode((int)xDisplay,(int)yDisplay));
			Display.create();
			Display.setVSyncEnabled(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			GL11.glEnable(GL11.GL_BLEND);
        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, (int)xDisplay, (int)yDisplay, 0, 1, -1);
		} catch (LWJGLException e) {
		}
		map = new Map();
		controllers.add(new Player(Alive.Faction.RADIANT));
		//controllers.add(new AI(Alive.Faction.DIRE));
		tickCount = 0;
		try {
			tracker.initialize(Tracker.TrackerPreset.PRESET_RUN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void setResolutionToDefault(){
		xDisplay = 1920;
		yDisplay = 1080;
	}
	private static void setCustomResolution(){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Put in the width of the screen:");
		xDisplay = scanner.nextInt();
		System.out.println("Put in the height of the screen:");
		yDisplay = scanner.nextInt();
	}
	private static void scanAndSetResolution(){
		xDisplay = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
		yDisplay = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
	}
	static long time = System.nanoTime();
	static final int tickRate = 60;
	private static void run(){
		while(System.nanoTime() > time + 1000000000 / tickRate){
			time += 1000000000 / tickRate;
			if(tickCount++ > tickRate * heroPickSecondDelay)
				tick();
		}
		if(tracker.trackedEntities[Tracker.FUNC_RENDER])tracker.giveStartTime(Tracker.FUNC_RENDER);
		for(HeroOwner heroOwner:controllers)if(heroOwner.getClass() == Player.class)render(((Player)heroOwner));
		if(tracker.trackedEntities[Tracker.FUNC_RENDER])tracker.giveEndTime(Tracker.FUNC_RENDER);
	}
	private static void tick(){
		if(tracker.trackedEntities[Tracker.FUNC_TICK])tracker.giveStartTime(Tracker.FUNC_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_QUADTREE_RESET])tracker.giveStartTime(Tracker.FUNC_QUADTREE_RESET);
		qt.clear();
		if(tracker.trackedEntities[Tracker.FUNC_QUADTREE_RESET])tracker.giveEndTime(Tracker.FUNC_QUADTREE_RESET);
		if(tracker.trackedEntities[Tracker.FUNC_HERO_OWNER_TICK])tracker.giveStartTime(Tracker.FUNC_HERO_OWNER_TICK);
		for(HeroOwner heroOwner: controllers)heroOwner.tick();
		if(tracker.trackedEntities[Tracker.FUNC_HERO_OWNER_TICK])tracker.giveEndTime(Tracker.FUNC_HERO_OWNER_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_MAP_TICK])tracker.giveStartTime(Tracker.FUNC_MAP_TICK);
		map.tick();
		if(tracker.trackedEntities[Tracker.FUNC_MAP_TICK])tracker.giveEndTime(Tracker.FUNC_MAP_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_MISSILE_TICK])tracker.giveStartTime(Tracker.FUNC_MISSILE_TICK);
		tickMissiles();
		if(tracker.trackedEntities[Tracker.FUNC_MISSILE_TICK])tracker.giveEndTime(Tracker.FUNC_MISSILE_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_AURA_TICK])tracker.giveStartTime(Tracker.FUNC_AURA_TICK);
		tickAuras();
		if(tracker.trackedEntities[Tracker.FUNC_AURA_TICK])tracker.giveEndTime(Tracker.FUNC_AURA_TICK);
		if(tracker.trackedEntities[Tracker.FUNC_ACTOR_DELETION])tracker.giveStartTime(Tracker.FUNC_ACTOR_DELETION);
		deleteStuff();
		if(tracker.trackedEntities[Tracker.FUNC_ACTOR_DELETION])tracker.giveEndTime(Tracker.FUNC_ACTOR_DELETION);
		try {
			tracker.tick();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(tracker.trackedEntities[Tracker.FUNC_TICK])tracker.giveEndTime(Tracker.FUNC_TICK);
	}	
	private static void render(Player player){
		try {
			Display.setParent(player.gui.canvas);
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}
		GL11.glClearColor(0.0f,0.0f,0.0f,0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		tracker.giveStartTime(Tracker.REND_MISSILE);
		drawSpells(player.camera);
		tracker.giveEndTime(Tracker.REND_MISSILE);
		tracker.giveStartTime(Tracker.REND_MAP);
		map.drawMap(player.camera);
		tracker.giveEndTime(Tracker.REND_MAP);
		tracker.giveStartTime(Tracker.REND_HEROES);
		for(HeroOwner heroOwner: controllers){
			if(heroOwner.hero.alive)heroOwner.hero.drawMe(player.camera);
		}
		tracker.giveEndTime(Tracker.REND_HEROES);
		tracker.giveStartTime(Tracker.REND_PLAYER);
		player.drawMe(player.camera);
		tracker.giveEndTime(Tracker.REND_PLAYER);
		Display.update();
	}
	private static void tickMissiles(){
		for(Missile spell: missiles){
			if(!spell.tick())Head.addToDeleteList(spell);
		}
	}
	private static void tickAuras(){
		for(Aura a: Aura.auras)a.tick();
	}
	private static void drawSpells(Camera camera){
		for(Missile missile : missiles){
			Util.drawActor(missile, missile.color, camera);	
		}
	}
	public static Location getScreenDimensions(){
		return new Location(xDisplay, yDisplay);
	}
	static double getRandom(){
		return Math.random();
	}
	public static Location getWrap(){
		return wrap;
	}
	public static ArrayList<Missile> getSpells(){
		return missiles;
	}
	private static void deleteStuff(){
		for(Actor a: Head.deleteThis){
			if(a.getClass() == Missile.class && missiles.contains(a))
				missiles.remove(a);
			else if(a.getClass() == Buff.class)((Buff)(a)).owner.buffs.remove(a);
			else if(a.getClass() == Creep.class)Creep.allCreeps.remove(a);
			else if(a.getClass() == Effect.class);
			else if(a.getClass() == Tower.class);
			if(a.getClass() == Alive.class || Alive.class.isAssignableFrom(a.getClass())){
				Alive.allAlives.remove(a);
			}
			Head.allActors.remove(a);
		}
		Head.deleteThis.clear();
	}
	public static ArrayList<Actor> getDeleteList(){
		return Head.deleteThis;
	}
	public static void addToDeleteList(Actor poorGuy){
		Head.deleteThis.add(poorGuy);
	}
	public static Location getHalfScreenSize(){
		return new Location(Head.getScreenDimensions().getX() / 2, Head.getScreenDimensions().getY() / 2);
	}
	public static void out(String string){
		for(HeroOwner p: controllers)if(p.getClass() == Player.class)((Player)(p)).gui.out(string);
	}
}
