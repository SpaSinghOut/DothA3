package com.DotA3.main;

import java.util.ArrayList;

public class Missile extends Actor{
	double damage;
	Alive parent;
	MissileType missileType;
	boolean auto;
	boolean penetrating;
	//the constructor to use for putting in a custom missile target location
	public Missile(MissileType setSpellName,  Alive setParent, double passX, double passY){
		super(setSpellName.width, setSpellName.height, false);
		missileType = setSpellName;
		damage = missileType.damage;
		setLocation(new Location(setParent.getLocation().getX(), setParent.getLocation().getY()));
		changeBaseSpeed(missileType.speed);
		parent = setParent;
		setNewTarget(new Location(passX, passY));
		setMovement(new Location(passX, passY));
		shape = Actor.Shape.QUAD;
		childSetsOwnMovement = true;
		color = missileType.color;
		Head.missiles.add(this);
		movementType = Actor.MovementType.DIRECTIONBASED;
		penetrating = missileType.penetrating;
	}
	//the default constructor that will create a non-homing missile at the target location with the given target
	public Missile(MissileType setSpellName,  Alive setParent, Location startingLocation, Location setTarget){
		super(setSpellName.width, setSpellName.height, false);
		missileType = setSpellName;
		damage = missileType.damage;
		setLocation(startingLocation);
		changeBaseSpeed(missileType.speed);
		parent = setParent;
		setNewTarget(setTarget);
		setMovement(target);
		shape = Actor.Shape.QUAD;
		childSetsOwnMovement = true;
		color = missileType.color;
		Head.missiles.add(this);
		movementType = Actor.MovementType.DIRECTIONBASED;
	}
	//the constructor to use for a typical homing missile
	public Missile(MissileType setSpellName,  Alive setParent, Alive setHomingTarget){
		super(setSpellName.width, setSpellName.height, false);
		missileType = setSpellName;
		damage = missileType.damage;
		setLocation(new Location(setParent.getLocation().getX(), setParent.getLocation().getY()));
		changeBaseSpeed(missileType.speed);
		parent = setParent;
		homingTarget = setHomingTarget;
		setTarget(homingTarget.getLocation());
		setMovement(new Location(homingTarget.getLocation().getX(), homingTarget.getLocation().getY()));
		shape = Actor.Shape.QUAD;
		needToMove = true;
		childSetsOwnMovement = true;
		color = missileType.color;
		this.childSetsOwnMovement = true;
		Head.missiles.add(this);
		movementType = Actor.MovementType.HOMING;
		if(this.missileType == MissileType.GYROCKET)Head.out("rocket appears at: " + location.x + ", " + location.y);
	}
	//THIS IS THE CONSTRUCTOR TO BE USED FOR AUTO ATTACK MISSILES
	public Missile(boolean auto,  Alive setParent, Alive setHomingTarget){
		super(MissileType.AUTO.width, MissileType.AUTO.height, false);
		auto = true;
		missileType = MissileType.AUTO;
		damage = missileType.damage;
		setLocation(new Location(setParent.getLocation().getX(), setParent.getLocation().getY()));
		changeBaseSpeed(missileType.speed);
		parent = setParent;
		//the following is because for a homing missile you will use the homingTarget variable
		setTarget(null);
		homingTarget = setHomingTarget;
		setTarget(homingTarget.getLocation());
		setMovement(new Location(homingTarget.getLocation().getX(), homingTarget.getLocation().getY()));
		shape = Actor.Shape.QUAD;
		needToMove = true;
		childSetsOwnMovement = true;
		color = missileType.color;
		this.childSetsOwnMovement = true;
		Head.missiles.add(this);
		movementType = Actor.MovementType.HOMING;
	}
	public boolean tick(){
		if(!super.tick(Util.onMap(this)))return false;
		if(!active)return active;
		switch(missileType){
		case GYROCKET:
			damage += 15d / Head.tickRate;
			changeBaseSpeed(getSpeed() + (15d / Head.tickRate));
			break;
		}
		if(!missileType.homing)
			for(Alive a: Alive.allAlives){
				if(Util.checkForCollision(a, this) && a.faction != parent.faction && a.active)
					onCollision(a);
			}
		else if(missileType.homing){
			this.setMovement(homingTarget.getLocation());
			setTarget(homingTarget.getLocation());
			if(Util.checkForCollision(this, homingTarget))
				onCollision(homingTarget);
		}
		if(Util.missileDeath(this) && !missileType.homing && !penetrating){
			return false;
		}
		return active;
	}
	public void setOwner(Alive newOwner){
		parent = newOwner;
	}
	private void onCollision(Alive gotHit){
		if(auto)gotHit.getAttacked(parent);
		else {
			missileType.onCollision(this, gotHit);
			parent.dealDamage(gotHit, damage, Alive.DamageType.MAGICAL);
		}
		if(!penetrating)active = false;
		
	}
	public void setAuto(boolean setAuto){
		auto = setAuto;
	}
	public void setDamage(double d){
		damage = d;
	}
}
