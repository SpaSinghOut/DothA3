package com.DotA3.main;

import java.io.IOException;

import org.lwjgl.input.*;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.DotA3.main.Util.Color;

public class Player extends HeroOwner{
	static Player activePlayer;
	static int abilityButtonHeight = 50;
	static int abilityButtonWidth = 50;
	static Location[] abilityButtonLocations = new Location[3];
	Actor nukePath;
	//declaring the camera and the camera's speed attribute
	Camera camera;
	final static double edgePanningRange = Head.getScreenDimensions().getX() / 30;
	int rightClickMove;
	LeftClickState leftClickState;
	Ability selectedAbility;
	Gui gui;
	int leftClickRestriction;
	int heroSelect;
	Alive hoverSelected;
	boolean outsideClickHeard;
	int tickTracker;
	Actor mapBackground;
	Actor portrait;
	public Player(Alive.Faction setFaction){
		super(setFaction);
		camera = new Camera(new Location(hero.getLocation().getX(), hero.getLocation().getY()));
		for(int i = 0; i < 3; i++){
			int length = abilityButtonLocations.length;
			abilityButtonLocations[i] = new Location(
					Head.getScreenDimensions().getX() - 
					(length + 14) * abilityButtonWidth +
					(i * 1.5 + 1) * abilityButtonWidth,
					abilityButtonHeight);
		}
		leftClickState = LeftClickState.DEFAULT;
		//initializing nukepath
		nukePath = new Actor(10,10, false);
		nukePath.changeBaseSpeed(225);
		nukePath.setLocation(new Location(hero.getLocation()));
		nukePath.setTarget(Head.map.rune.getLocation());
		nukePath.needToMove = true;
		gui = new Gui(this);
		selectedUnit = hero;
		gui.heroPickScreen();
		heroSelect = 0;
		outsideClickHeard = false;
		camera.centralPoint = new Location(2500,1000);
		mapBackground = new Actor(gui.screenX * 0.1, gui.screenX * 0.1, false);
		mapBackground.color = Util.Color.YELLOW;
		mapBackground.location.set(mapBackground.width / 2, gui.screenY - mapBackground.height / 2);
		portrait = new Actor(gui.screenX * 0.1, gui.screenY *.2, false);
		portrait.location.x = gui.screenX * 0.219;portrait.location.y = gui.screenY * 0.872;
		try {
			portrait.texture = TextureLoader.getTexture("jpg", ResourceLoader.getResourceAsStream("res/iron branch.jpg"));
		} catch (IOException e){}
	}
	public Location getLocation(){
		return hero.location;
	}
	public void tick(){
		listenForInput();
		nukePath.setTarget(Head.map.rune.getLocation());
		if(Head.map.rune.active)nukePath.tick(nukePath.active);
		if(Util.everySecond(5)){
			nukePath.setLocation(new Location(hero.getLocation()));
			nukePath.setNewTarget(Head.map.rune.location);
		}
		super.tick();
		gui.tick();
		if(selectedUnit != null && hero.initialized){
			portrait.setTexture(selectedUnit.texture);
		}
	}
	private void listenForInput(){
		edgePan();
		
		//START UNIT SELECTION
		if(Head.tracker.trackedEntities[Tracker.ALG_UNIT_SELECTION])Head.tracker.giveStartTime(Tracker.ALG_UNIT_SELECTION);
		/*checks if the left mouse button is pressed down and leftClicking is permitted
		 * then attempts to find a unit under the mouse and set that as the selected unit
		 */
		Location mouseClickLocation = new Location(Mouse.getX(), Mouse.getY());//declares the location which the mouse is at
		if((Mouse.isButtonDown(0))){
			if(!this.hero.initialized)return;
			checkForMapClick(mouseClickLocation);
			Alive clicked = selected(mouseClickLocation);
			if(clicked != null){
				selectedUnit.resetTexture = false;
				selectedUnit = clicked;
				selectedUnit.resetTexture = true;
			}
			/* There are two main reasons for checking if leftClickState is set to ability use 
			 * 1. to make sure that the player's click only signifies an ability cast if the player has chosen to cast an ability
			 * 2. to make sure that after an ability has been cast it does not get cast again bacause the player is still holding down the button
			 */
			gui.out(leftClickState.name());
			if(leftClickState == LeftClickState.ABILITYUSE){
				gui.out("hero, " + hero.heroType + ", casts spell: " + selectedAbility.abilityType);
				hero.castSpell(selectedAbility);
			}
			leftClickState = LeftClickState.DEFAULT;
			leftClickRestriction = (int) (.3 * Head.tickRate);
		}	
		if(Head.tracker.trackedEntities[Tracker.ALG_UNIT_SELECTION]){
			Head.tracker.giveEndTime(Tracker.ALG_UNIT_SELECTION);
		}
		//END UNIT SELECTION
		
		//the following moves and/or aggros the player using the rightmouseclick
		if(Mouse.isButtonDown(1)){
			Alive a = selected(mouseClickLocation);
			if(a != null){
				hero.aggroOn(a);
				//hero.needToMove = true;
			}
			else{
				hero.target = Util.inverseLocationOnScreen(mouseClickLocation, camera);
				hero.aggroOn(null);
			}
		}
		//use the spacebar key to center the camera on your hero
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			selectedUnit = hero;
			if(heroSelect > 0)
				camera.centralPoint.set(new Location(hero.getLocation()));
			heroSelect = 60;
		}
		if(heroSelect > 0)heroSelect--;
	}
	private void checkForMapClick(Location mouseClickLocation){
		if(mouseClickLocation.x < mapBackground.location.x + mapBackground.width / 2 
		&& mouseClickLocation.x > mapBackground.location.x - mapBackground.width / 2 
		&& (Head.getScreenDimensions().y - mouseClickLocation.y) > mapBackground.location.y - mapBackground.height / 2
		&& (Head.getScreenDimensions().y - mouseClickLocation.y) < mapBackground.location.y + mapBackground.height / 2)
			camera.centralPoint.set((mouseClickLocation.x - (mapBackground.location.x - mapBackground.width / 2)) / (mapBackground.width) * Head.getWrap().x,
			(Head.getScreenDimensions().y - mouseClickLocation.y - (mapBackground.location.y - mapBackground.height / 2)) / (mapBackground.height) * Head.getWrap().y);
	}
	public Alive selected(Location clicked){
		final int searchRange = 100;
		Location b = Util.inverseLocationOnScreen(new Location(clicked.getX(), clicked.getY()),camera);
		for(Actor a : Head.qt.retrieveBox(b.x - searchRange, b.y - searchRange, b.x + searchRange, b.y + searchRange)){
			if(Alive.class.isAssignableFrom(a.getClass()) && Util.checkPointCollision(a, b))
				return (Alive)(a);
		}
		return null;
	}
	
	public enum LeftClickState{
		DEFAULT, ABILITYUSE, ABILITYTAKE,;
	}
	
	public void drawMe(Camera camera) {
		gui.render();
		int r = 0, d = 0;
		for(HeroOwner ho: Head.controllers)
			if(ho.hero.faction == Alive.Faction.RADIANT)
				Util.drawOnScreen(ho.hero, Util.Color.WHITE, 
				new Location(gui.screenX / 2 - gui.HUD_component_clock.getSize().width / 2 - ho.hero.width * ++r,ho.hero.height / 2));
			else Util.drawOnScreen(ho.hero, Util.Color.WHITE,
				new Location(gui.screenX / 2 + gui.HUD_component_clock.getSize().width / 2 + ho.hero.width * ++d,ho.hero.height / 2));
		Util.drawOnScreen(mapBackground, Color.YELLOW, mapBackground.location);
		for(Actor a: Head.allActors)if(Alive.class.isAssignableFrom(a.getClass()))Util.drawOnMap(this,a);
		Util.drawOnScreen(portrait,Util.Color.WHITE,portrait.location);
		if(Head.map.rune.active)Util.drawActor(nukePath, Util.Color.GREEN, camera);
	}
	public Location getMouseLocation(){
		return new Location(Mouse.getX(), Mouse.getY());
	}
	public Location getMouseInWorld(){
		return Util.inverseLocationOnScreen(new Location(Mouse.getX(), Mouse.getY()), camera);
	}
	public void edgePan(){
		if(Mouse.getX() < edgePanningRange)camera.centralPoint.changeX(-camera.getCameraSpeed());
		else if(Mouse.getX() > Head.getScreenDimensions().getX() - edgePanningRange)camera.centralPoint.changeX(camera.getCameraSpeed());
		else camera.resetCameraSpeed();
		if(Mouse.getY() < edgePanningRange)camera.centralPoint.changeY(camera.getCameraSpeed());
		else if(Mouse.getY() > Head.getScreenDimensions().getY() - edgePanningRange)camera.centralPoint.changeY(-camera.getCameraSpeed());
		else camera.resetCameraSpeed();
	}
}
