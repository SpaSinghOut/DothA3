package com.DotA3.main;

import java.util.ArrayList;

public class Ability {
	public enum AbilityType{
		PLAZMAFIELD(7,10,120,Util.Color.GREEN, CastType.INSTANT, LevellingType.NORMAL),
		STATICLINK(13,17,20,Util.Color.BLUE, CastType.ALIVETARGET, LevellingType.NORMAL),
		STATICSTORM(15,30, 60,Util.Color.BLUE, CastType.INSTANT, LevellingType.ULTIMATE),
		CALLDOWN(1.5,4,60,Util.Color.RED, CastType.POINTTARGET, LevellingType.ULTIMATE),
		UNSTABLECURRENT(0,0,0,Util.Color.PINK, CastType.PASSIVE, LevellingType.NORMAL),
		ROCKETBARRAGE(3,7,75, Util.Color.YELLOW, CastType.INSTANT, LevellingType.NORMAL),
		FLAKCANNON(15,20,120,Util.Color.PINK, CastType.INSTANT, LevellingType.NORMAL),
		GYROCKET(0,12,150,Util.Color.ORANGE, CastType.ALIVETARGET, LevellingType.NORMAL),
		SHACKLESHOT(2,12,30,Util.Color.YELLOW, CastType.ALIVETARGET, LevellingType.NORMAL),
		POWERSHOT(2,6, 20,Util.Color.ORANGE, CastType.CHANNELING, LevellingType.NORMAL),
		WINDRUN(5, 20, 50, Util.Color.GREEN, CastType.INSTANT, LevellingType.NORMAL),
		FOCUS(5, 60, 150, Util.Color.BLUE, CastType.POINTTARGET, LevellingType.ULTIMATE),
		FROSTARROWS(0,1,20,Util.Color.WHITE, CastType.TOGGLE, LevellingType.NORMAL),
		DROWSILENCE(3,15,50,Util.Color.BLUE, CastType.POINTTARGET, LevellingType.NORMAL),
		DROWAURA(0,0,0,Util.Color.BLUE,CastType.PASSIVE, LevellingType.NORMAL),
		MARKSMANSHIP(0,0,0,Util.Color.GREEN, CastType.PASSIVE, LevellingType.ULTIMATE),
		NONE(0,0,0, Util.Color.WHITE, CastType.INSTANT, LevellingType.NORMAL),;
		int CD;										//The cool down of this ability
		int manaCost;								//The amount of mana required to use this ability
		public Util.Color color;							//The main color of this ability
		int duration;								//How long this ability lasts
		public CastType castType;							//The way in which this ability is cast
		public int[] levelRequirements;					//A list of the hero levels at which this ability can have more points put towards it
		Ability owner;
		private AbilityType(double setDuration, int setCD, int setManaCost,
				Util.Color setColor, CastType setCastType, LevellingType setLevellingType){
			CD = setCD * Head.tickRate;
			manaCost = setManaCost;
			color = setColor;
			//duration is taken in as a double value of seconds and is then converted to duration in ticks as an integer
			duration = (int)(setDuration * Head.tickRate);
			castType = setCastType;
			LevellingType.setLevellingType(this, setLevellingType);
		}
		void setOwner(Ability ability){
			owner = ability;
		}
		public enum CastType{
			POINTTARGET, ALIVETARGET, INSTANT, PASSIVE, CHANNELING, TOGGLE,;
			boolean isTimeBased(){
				if(this == POINTTARGET || this == ALIVETARGET ||
					this == INSTANT || this == CHANNELING)return true;
				else return false;
			}
		}
		public enum LevellingType{
			NORMAL,
			ULTIMATE,;
			static void setLevellingType(AbilityType setAbility, LevellingType setLevellingType){
				setAbility.levelRequirements = getLevelRequirements(setLevellingType);
			}
			private static int[] getLevelRequirements(LevellingType setLevellingType){
				int[] levelRequirements = new int[0];
				switch(setLevellingType){
				case NORMAL:
					levelRequirements = new int[4];
					for(int i = 0; i < 4; i++)levelRequirements[i] = 2 * (i + 1) - 1;
					break;
				case ULTIMATE:
					levelRequirements = new int[3];
					for(int i = 0; i < 3; i++)levelRequirements[i] = 5 * (i + 1) + 1;
				}
				return levelRequirements;
			}
		}
	}
	Hero owner; 								//The hero that has this ability
	int CDRemaining;							//The amount of time remaining until this ability is off of cool down
	public State state;								//Is the ability ready, on cool down, etc.
	int durationLeft;							//How much time this ability has left until it ends
	private Alive target;						//If this ability targets an Alive what is that target
	private Location targetLocation;			//and what is that Alive's location
	public int level;									//The amount of skill points that were put into this ability
	int castControl;							//Is used to prevent toggle skills from activating multiple times per click/press
	ArrayList<Actor> components = new ArrayList<Actor>(); //A list of actors that are visual effects of this ability
	public AbilityType abilityType;
	public Ability(AbilityType setAbilityType, Hero setOwner){
		owner = setOwner;
		abilityType = setAbilityType;
		level = 0;
		castControl = 0;
		state = State.DOWN;
		abilityType.setOwner(this);
	}
	public enum State{
		READY,DOWN,CHANNELING,ACTIVE ;
	}
	
	public void tick(){
		if(abilityType.castType.isTimeBased() && level > 0){
			if(--durationLeft == 0)terminate();
			if(--CDRemaining <= 0)state = State.READY;
			if(owner.getStat(Constants.mana) > abilityType.manaCost && CDRemaining <= 0 && level > 0)this.state = State.READY;
			else state = State.DOWN;
			if(state == State.CHANNELING)channel();
		}
		castControl--;
	}
	public void activate(){
		CDRemaining = abilityType.CD;
		durationLeft = abilityType.duration;
		switch(abilityType.castType){
		case INSTANT:
			activate(true);
			state = State.DOWN;
			break;
		case PASSIVE:
			activate(true);
			state = State.READY;
			break;
		case POINTTARGET:
			if(owner.owner.getClass() == Player.class){
				targetLocation = ((Player)owner.owner).getMouseInWorld();
			}
			activate(targetLocation);
			state = State.DOWN;
			break;
		case ALIVETARGET:
			activate(owner.owner.selectedUnit.location);
			state = State.DOWN;
			break;
		case CHANNELING:
			if(owner.getPermissions(Constants.channelingAllowed)
			&& owner.getPermissions(Constants.spellCastAllowed)){
				state = State.CHANNELING;
				if(owner.owner.getClass() == Player.class){
					targetLocation = ((Player)owner.owner).getMouseInWorld();
				}
				state = State.DOWN;
			}
			break;
		case TOGGLE:
			if(castControl <= 0){
				if(state == State.READY){
					state = State.ACTIVE;
					activate(true);
				}
				else if(state == State.ACTIVE){
					state = State.READY;
					activate(false);
				}
				castControl = Head.tickRate / 2;
			}
			break;
		}
	}
	private void activate(Location setLocation){
		owner.changeStat(Constants.mana, -(abilityType.manaCost));
		Location location = setLocation;
		if(abilityType.castType == AbilityType.CastType.ALIVETARGET)target = owner.owner.selectedUnit;
		switch(abilityType){
		case CALLDOWN:
			this.durationLeft *= level;
			Missile missile;
			Head.out("test");
			for(int i = 0; i < 15 * level; i++){
				missile = new Missile(MissileType.GYROW, owner, new Location(location.x - 75 * level, location.y - 75 * level + i * 10), location);
				components.add(missile);
				missile = new Missile(MissileType.GYROW,owner, new Location(location.x + 75 * level, location.y - 75 * level + i * 10), location);
				components.add(missile);
				missile = new Missile(MissileType.GYROW,owner,new Location(location.x - 75 * level + i * 10, location.y - 75 * level), location);
				components.add(missile);
				missile = new Missile(MissileType.GYROW,owner, new Location(location.x - 75 * level + i *10, location.y + 75 * level), location);
				components.add(missile);
			}
			break;
		case STATICLINK:
			if(target == null)return;
			Buff b = new Buff(owner, Buff.BuffName.STATICLINKSAP, this);
			final int numLinks = 80;
			for(int i = 0; i < numLinks; i++){
				Effect link = new Effect(2,2, false, abilityType.duration, b);
				link.color = abilityType.color;
				link.setLocation(new Location(
				owner.location.x + (target.location.x - owner.location.x) / (numLinks - i),
				owner.location.y + (target.location.y - owner.location.y) / (numLinks - i)));
				b.effects.add(link);
			}
			break;
		case GYROCKET:
			//Head.out(String.valueOf(owner == owner.owner.selectedUnit));
			Missile ms = new Missile(MissileType.GYROCKET, owner, owner.owner.selectedUnit);
			ms.damage *= level;
			break;
		case SHACKLESHOT:
			new Missile(MissileType.SHACKLESHOT, owner, target);
			break;
		case FOCUS:
			new Buff(owner, Buff.BuffName.FOCUS, this);
			break;
		case DROWSILENCE:
			int radius = 200;
			for(Actor a: Head.qt.retrieveBox(setLocation.x - radius, setLocation.y - radius,
												setLocation.x + radius, setLocation.y + radius))
				if(Alive.class.isAssignableFrom(a.getClass()) 
				&& Util.getRealCentralDistance(a, setLocation) < radius)
					new Buff((Alive)a, Buff.BuffName.SILENCE, abilityType.duration + level);
			break;
			default:
				break;
		}
	}
	@SuppressWarnings("incomplete-switch")
	private void activate(boolean b){
		owner.changeStat(Constants.mana, -(abilityType.manaCost));
		switch(abilityType){
		case PLAZMAFIELD:
			Buff pf = new Buff(owner, Buff.BuffName.PLAZMAFIELD, this);
			Effect a = new Effect(15,15, false, abilityType.duration, owner );
			a.changeBaseSpeed(210);
			a.color = Util.Color.GREEN;
			for(int i = 0; i <= 50; i++){
				a.setTarget(new Location(owner.location.x - abilityType.duration / 2 * a.getTrueSpeed(),
						owner.location.y + abilityType.duration / 2 * a.getTrueSpeed() - (i * 4)));
				pf.effects.add(a);
				a.setTarget(new Location(owner.location.x + abilityType.duration / 2 * a.getTrueSpeed(),
						owner.location.y + abilityType.duration / 2 * a.getTrueSpeed() - (i * 4)));
				pf.effects.add(a);
				a.setTarget(new Location(owner.location.x + abilityType.duration / 2 * a.getTrueSpeed() - (i * 4),
						owner.location.y + abilityType.duration / 2 * a.getTrueSpeed()));
				pf.effects.add(a);
				a.setTarget(new Location(owner.location.x + abilityType.duration / 2 * a.getTrueSpeed() - (i * 4),
						owner.location.y - abilityType.duration / 2 * a.getTrueSpeed()
						));
				pf.effects.add(a);
			}
			for(Effect e: pf.effects)e.setLocation(new Location(owner.location));
			break;
		case STATICSTORM:
			Buff ss = new Buff(owner, Buff.BuffName.STATICSTORM, this);
			ss.color = abilityType.color;
			break;
		case UNSTABLECURRENT:
			activatePassiveStatChange(Buff.BuffName.UNSTABLECURRENT);
			break;
		case ROCKETBARRAGE:
			new Buff(owner, Buff.BuffName.ROCKETBARRAGE,this);
			break;
		case FLAKCANNON:
			new Buff(owner, Buff.BuffName.FLAKCANNON, this);
			break;
		case WINDRUN:
			new Buff(owner, Buff.BuffName.WINDRUN, this);
			break;
		case FROSTARROWS:
			if(b){
				new Buff(owner, Buff.BuffName.FROSTARROWSPARENT, this);
			}
			else Buff.removeBuff((owner), Buff.BuffName.FROSTARROWSPARENT);
			break;
		case DROWAURA:
			new Aura(owner, 600, Buff.BuffName.PRECISIONAURA, Aura.AffectedUnits.ALL,level);
			break;
		case MARKSMANSHIP:
			activatePassiveStatChange(Buff.BuffName.MARKSMANSHIP);
			break;
		}
	}
	private void activatePassiveStatChange(Buff.BuffName buffName){
		if(level == 1)
			new Buff(owner, buffName, this);
		else
			for(Buff uc:owner.getBuffsOfType(buffName))
				uc.
				setIntensity(level);
	}
	private void channel(){
		if(!owner.getPermissions(Constants.channelingAllowed)){
			endChannel();
			return;
		}
		switch(abilityType){
		case POWERSHOT:
			break;
			default:break;
		}
	}
	void endChannel(){
		switch(abilityType){
		case POWERSHOT:
			Missile m = new Missile(MissileType.POWERSHOT, owner,
					targetLocation.x, targetLocation.y);
			m.penetrating = true;
			break;
			default:break;
		}
	}
	private void terminate(){
		for(Actor a: components)Head.addToDeleteList(a);
		for(Actor a: owner.effects)Head.addToDeleteList(a);
		components.clear();
		owner.effects.clear();
		state = State.DOWN;
		endChannel();
	}
	public void setOwner(Hero setOwner){
		owner = setOwner;
	}
	public void setTarget(Alive setTarget){
		target = setTarget;
	}
	public void levelAbility(){
		owner.changeStat(Constants.abilityPoints, -1);
		if(abilityType.castType == AbilityType.CastType.TOGGLE)
			state = State.READY;
		level++;
		if(abilityType.castType == AbilityType.CastType.PASSIVE)
			activate();
	}
}
