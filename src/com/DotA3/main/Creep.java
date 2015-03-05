package com.DotA3.main;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Creep extends Alive{
	//Alive attackTarget;
	Location moveTarget;
	Location[] staticMovePointsRadiant, staticMovePointsDire;
	boolean[] checkPoints;
	static final int numberOfStaticMovePoints = (int) (Head.getWrap().x / 200 - 3);
	public static ArrayList<Creep> allCreeps = new ArrayList<Creep>();
	private static ArrayList<Creep> deadCreeps = new ArrayList<Creep>();
	public static final int creepSize = 25;
	Location old;
	int movementRestriction;
	public Creep(int setWidth, int setHeight, Faction setFaction) {
		super(setWidth, setHeight, setFaction);
		staticMovePointsRadiant = new Location[numberOfStaticMovePoints];
		staticMovePointsDire = new Location[numberOfStaticMovePoints];
		checkPoints = new boolean[numberOfStaticMovePoints];
		for(int i = 0; i < numberOfStaticMovePoints; i++){
			checkPoints[i] = false;
			staticMovePointsRadiant[i] = new Location(Head.getWrap().x / 2 + i * 100, 1000);
			staticMovePointsDire[i] = new Location(Head.getWrap().x / 2 - i * 100, 1000);
			//staticMovePoints[i] = new Location(500 * (i + 1) + 30 + Math.random() * 500 - 250, 500 * (i + 1) + 30 + Math.random() * 500 - 250);
		}
		changeStat(Constants.maxHealth, 300);
		changeStat(Constants.health, 300);
		if(faction == Alive.Faction.RADIANT)moveTarget = this.staticMovePointsRadiant[0];
		else if(faction == Alive.Faction.DIRE)moveTarget = this.staticMovePointsDire[0];
		changeBaseSpeed(250);
		setTarget(moveTarget);
		needToMove = false;
		changeStat(Constants.visibilityRange, 450);
		changeStat(Constants.attackRange, 12);
		this.childSetsOwnMovement = false;
		changeStat(Constants.startingDamage, 30);
		changeStat(Constants.experienceGiven, 62);
		changeStat(Constants.healthRegen, 0.3);
		changeStat(Constants.goldGiven, ((int)(Math.random() * 8)) + 36); 
		if(setFaction == Alive.Faction.RADIANT){
			defaultColor = Util.Color.GREEN;
			color = Util.Color.GREEN;
		}
		else if(setFaction == Alive.Faction.DIRE){
			defaultColor = Util.Color.RED;
			color = Util.Color.RED;
		}
		color = Util.Color.WHITE;
		allCreeps.add(this);
		old = new Location(0,0);
		movementRestriction = 0;
		try {
			setTexture();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean tick(){
		if(movementRestriction-- <= 0)creepAI2();
		if(reachedPoint())checkPoint();
		if(Util.everySecond(1/Head.tickRate * 6)){
			if(Math.abs(old.x - location.x) < getTrueSpeed() && Math.abs(old.y - location.y) < getTrueSpeed())movementRestriction = 6;
			old = new Location(location);
		}
		return super.tick();
	}
	private Alive checkForPotentialAttackTarget(){
		//return Util.getRealCentralDistance(this, Head.player.hero) < this.stats[Constants.visibilityRange] ? Head.player.hero : null;
		Alive potentialAttackTarget = null;
		for(Alive a: Alive.allAlives){
			if(a.faction != this.faction 
			&& Util.getRealCentralDistance(this, a) < getStat(Constants.visibilityRange)){
				if(potentialAttackTarget == null)potentialAttackTarget = a;
				else{
					if(Util.getRealCentralDistance(potentialAttackTarget, this)>
					Util.getRealCentralDistance(a, this))potentialAttackTarget = a;
				}
			}
		}
		return potentialAttackTarget;
	}
	private Location getNextStaticMovePoint(){
		for(int i = 0; i < numberOfStaticMovePoints; i++){
			if(!checkPoints[i])
				if(faction == Alive.Faction.RADIANT)
					return this.staticMovePointsRadiant[i];
				else if(faction == Alive.Faction.DIRE)
					return staticMovePointsDire[i];
		}
		for(boolean b: this.checkPoints)b = false;
		return new Location(0,0);
	}
	private void checkPoint(){
		for(int i = 0; i < numberOfStaticMovePoints; i++){
			if(checkPoints[i] == false){
				checkPoints[i] = true;
				return;
			}
		}
	}
	private void creepAI2(){
		if(getAttackTarget() != null && Util.everySecond(2)){
			Alive alive =  this.checkForPotentialAttackTarget(); 
			if(alive != null)
			if(Util.getRealCentralDistance(this, alive)
			<  Util.getRealCentralDistance(this, getAttackTarget()))
				aggroOn(alive);
		}
		if(getAttackTarget() == null || !getAttackTarget().alive){
			Alive pat = this.checkForPotentialAttackTarget();
			if(pat != null)aggroOn(pat);
			else target = this.getNextStaticMovePoint();
		}
		if(Util.everySecond(2) && getAttackTarget() != null 
		&& !this.isVisible(getAttackTarget()) && isInForgetfulState()){
			aggroOn(null);
			target = this.getNextStaticMovePoint();
		}
		if(target == null)target = this.getNextStaticMovePoint();
	}
	private boolean reachedPoint(){
		if(this.getLocation().getX() < this.getNextStaticMovePoint().getX() + this.getWidth()
		&& this.getLocation().getX() > this.getNextStaticMovePoint().getX() - this.getWidth()
		&& this.getLocation().getY() < this.getNextStaticMovePoint().getY() + this.getHeight()
		&& this.getLocation().getY() > this.getNextStaticMovePoint().getY() - this.getHeight())
			return true;
		return false;
	}
	public void drawMe(Camera camera){
		if(!camera.canSeeActor(this))return;
		float[] rgb = getRGB();
		rgb[0] *= getRatio("health");
		rgb[1] *= getRatio("health");
		rgb[2] *= getRatio("health");
		super.drawMe(rgb, camera);
	}
	public static void creepCleanup(){
		for(Creep creep: deadCreeps){
			allCreeps.remove(creep);
		}
		deadCreeps.clear();
	}
}
