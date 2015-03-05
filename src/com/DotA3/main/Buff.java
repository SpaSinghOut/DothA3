package com.DotA3.main;

import java.util.ArrayList;

public class Buff extends Actor{
	boolean limitedByTime;
	int timeRemaining;
	BuffName buffName;
	Alive owner;
	double intensity;
	//boolean active;
	TriggerType triggerType;
	Alive target;
	ArrayList<Buff> children = new ArrayList<Buff>();
	Missile onMissile;
	Ability ability;
	StackingType stackingType;
	BuffName[] restrictions;
	int count;
	ArrayList<Alive> hitList = new ArrayList<Alive>();
	enum BuffName{
		STUN,SPLITSHOT,HASTE,DOUBLEDAMAGE, ORBFROSTARROWS, STATICSTORM, RAPIDREGEN, 
		STATICLINKSAP,STATICLINKDAMAGE, UNSTABLECURRENT, ROCKETBARRAGE, FLAKCANNON,
		WINDRUN, FOCUS, FROSTARROWSPARENT, FROSTARROWSCHILD, SILENCE, PRECISIONAURA,
		MARKSMANSHIP, UNSTABLECURRENTSLOW, PLAZMAFIELD,
		;
	}
	/*enum BuffType{
		NONE, UAM, ORB,;
	}*/
	enum TriggerType{
		ONATTACK, ONATTACKDECLARATION, ONHIT, ONBEINGATTACKED, ONBEINGHIT, 
		ONDEALINGDAMAGE, ONTAKINGDAMAGE, ONPHYSICAL, ONMAGICAL, UNIQUEATTACKMODIFIER, 
		ORBATTACK, ORBHIT, TIMED, NOTRIGGER, ONBEINGTARGETED, ONDEATH, ONKILL,
		ONSPELLCAST, ONSPELLTARGETED, ONSPELLAFFECTED, ONABILITYLEVEL, ONAGGRO,;
	}
	public Buff(Alive setOwner, BuffName setBuffType, Ability parentAbility){
		super(0,0, false);
		genericBuffInit(setOwner,setBuffType);
		ability = parentAbility;
		setIntensity(parentAbility.level);
		timeLimitConfig(ability.abilityType.duration);
		this.place();
	}
	public Buff(Alive setOwner, BuffName setBuffType, int buffDurationInTicks){
		super(0,0, false);
		genericBuffInit(setOwner,setBuffType);
		timeLimitConfig(buffDurationInTicks);
		this.place();
	}
	private Buff(Alive setOwner, BuffName setBuffType, int buffDurationInTicks, double setIntensity){
		super(0,0, false);
		genericBuffInit(setOwner, setBuffType);
		timeLimitConfig(buffDurationInTicks);
		intensity = setIntensity;
		this.place();
	}
	private void genericBuffInit(Alive setOwner, BuffName setBuffType){
		buffName = setBuffType;
		owner = setOwner;
		stackTypeInit();
		if(!this.stackCheck()){
			this.active = false;
			return;
		}
		if(owner.getClass() == Hero.class)
			target = ((Hero)owner).owner.selectedUnit;
		owner.buffs.add(this);
		triggerConfig();
		if(owner.getClass() == Hero.class)if(((Hero)owner).owner.getClass() == Player.class)
		for(BuffIcon bi: ((Player)((Hero)owner).owner).gui.buffs)if(bi.buff == null){
			bi.setBuff(this);
			break;
		}
	}
	enum StackingType{
		COUNT, STACK, NONE, RESTRICTION,;
	}
	private void timeLimitConfig(int setTimeLimit) {
		timeRemaining = setTimeLimit;
		limitedByTime = true;
		switch(buffName){
		case FROSTARROWSPARENT:
			limitedByTime = false;
			break;
		case UNSTABLECURRENT:
			limitedByTime = false;
			break;
		case MARKSMANSHIP:
			limitedByTime = false;
			break;
		case ORBFROSTARROWS:
			break;
		case STATICSTORM:
			break;
		case RAPIDREGEN:
			break;
		case STATICLINKSAP:
			break;
		case STATICLINKDAMAGE:
			break;
		default:
			break;
		}
	}
	private void triggerConfig(){
		switch(this.buffName){
		case UNSTABLECURRENT:
			triggerType = TriggerType.ONSPELLTARGETED;
			break;
		case ORBFROSTARROWS:
			triggerType = TriggerType.ORBHIT;
			break;
		case STATICSTORM:
			triggerType = TriggerType.TIMED;
			break;
		case FLAKCANNON:
			triggerType = TriggerType.ONATTACK;
			break;
		case FOCUS:
			triggerType = TriggerType.ONAGGRO;
			break;
		case FROSTARROWSPARENT:
			triggerType = TriggerType.ORBHIT;
			break;
		}
	}
	public boolean tick(){
		if(onMissile != null)return onMissile.active;
		if(!active || !super.tick(active))return false;
		if(limitedByTime)timeRemaining--;
		if(limitedByTime && timeRemaining <= 0){
			destroy();
			return active;
		}
		switch(buffName){
		case PLAZMAFIELD:
			if(timeRemaining == ability.abilityType.duration)return active;
			if(timeRemaining == this.ability.abilityType.duration / 2){
				for(Actor m: ability.components)m.setMovement(owner.location);
			}
			ArrayList<Alive> possibleTargets = Head.qt.getAlivesAroundMe(owner,(int)(ability.components.get(0).getSpeed() * ability.abilityType.duration / 2));
			double innerLimit = ability.durationLeft > ability.abilityType.duration / 2 ? 
					(ability.abilityType.duration - ability.durationLeft)*ability.components.get(0).getTrueSpeed():
					(ability.durationLeft)*(ability.components.get(0).getTrueSpeed());
			double outerLimit = ability.durationLeft > ability.abilityType.duration / 2 ? 
					(ability.abilityType.duration - ability.durationLeft + 1)*(ability.components.get(0).getTrueSpeed()):
					(ability.durationLeft + 1)*(ability.components.get(0).getTrueSpeed());
			ArrayList<Alive> withinBoundaries = new ArrayList<Alive>();
			for(Alive a: possibleTargets)
				if(Util.getRealCentralDistance(owner, a) > innerLimit && Util.getRealCentralDistance(owner,a) < outerLimit)
					withinBoundaries.add(a);
				for(Alive a: withinBoundaries)
					if(!hitList.contains(a))
						owner.dealDamage(a, intensity * Math.abs(timeRemaining - ability.abilityType.duration / 2), Alive.DamageType.MAGICAL);
				hitList = withinBoundaries;
			break;
		case SPLITSHOT:
			owner.shots = 4;
			break;
		case STATICSTORM:
			ArrayList<Alive> victims = new ArrayList<Alive>();
			effects = new ArrayList<Effect>();
			for(int i = 0; i < 100; i++){
				effects.add(new Effect(5,5,false,1, owner));
				effects.get(i).color = color;
			}
			for(Alive a : Alive.allAlives){
				if(a.faction != owner.faction && Util.getRealCentralDistance(owner, a) < 350){
					victims.add(a);
				}
			}
			for(Effect e: effects){
				if(victims.size() > 0){
					int number = (int)(Math.random() * victims.size());
					victims.get(number).takeDamage(owner, .1, Alive.DamageType.MAGICAL);
					e.setLocation(victims.get(number).getLocation());
					e.color = Ability.AbilityType.STATICSTORM.color;
				}
			}
			break;
		case RAPIDREGEN:
			owner.color = Util.Color.PINK;
			break;
		case STATICLINKSAP:
			final int staticLinkMaxDistance = 600;
			if(Util.getRealCentralDistance(owner, target) > staticLinkMaxDistance){
				destroy();
				return active;
			}
			final int damageSappedPerLevel = 1;
			int sapDamage;
			sapDamage = damageSappedPerLevel * (int)intensity;
			children.get(0).setIntensity(sapDamage * (Ability.AbilityType.STATICLINK.duration - timeRemaining));
			children.get(1).setIntensity(sapDamage * -(Ability.AbilityType.STATICLINK.duration - timeRemaining));
			/*if(timeRemaining == 0){
				intensity = sapDamage * (Ability.STATICLINK.duration - timeRemaining);
				children.get(0).destroy();
				intensity = sapDamage * -(Ability.STATICLINK.duration - timeRemaining);
				children.get(1).destroy();
			}*/
			for(Effect link: effects)
				link.setLocation(new Location(
				owner.getLocation().getX() + (target.getLocation().getX() - 
				owner.getLocation().getX()) * (effects.size() - effects.indexOf(link)) / effects.size(),
				owner.getLocation().getY() + (target.getLocation().getY() - 
				owner.getLocation().getY()) * (effects.size() - effects.indexOf(link)) / effects.size()));
			break;
		case ROCKETBARRAGE:
			int range = (int) (50 * intensity);
			ArrayList<Alive> ala = Head.qt.getAlivesAroundMe(owner, range);
			final int damage = 4;
			ArrayList<Alive> potentialTargets = new ArrayList<Alive>();
			for(Alive a: ala){
				if(a.faction != owner.faction)
					potentialTargets.add(a);
			}
			if(potentialTargets.size() > 0){
				target = potentialTargets.get((int)(Math.random() * potentialTargets.size()));
				Missile ms = new Missile(MissileType.FLAKCANNON, owner, target);
				ms.damage = damage * intensity;
			}
			break;
		case MARKSMANSHIP:
			if(Util.everySecond(1)){
				remove();
				for(Alive a: Head.qt.getAlivesAroundMe(owner, 400))
					if(a.getClass() == Hero.class && a.faction != owner.faction)
						break;
					else place();
			}
		default:;
		}
		setMySelfToUAM();
		return active;
	}
	private void setMySelfToUAM() {
		if(this.triggerType == TriggerType.UNIQUEATTACKMODIFIER &&
				owner.mainUAM == null)
					owner.mainUAM = this;
		else if((this.triggerType == TriggerType.ORBATTACK || 
				this.triggerType == TriggerType.ORBATTACK) && (
				owner.mainUAM == null || 
				owner.mainUAM.triggerType == TriggerType.UNIQUEATTACKMODIFIER))
					owner.mainUAM = this;
	}
	public void place(){
		switch(buffName){
		case STUN:
			owner.changePermissions(Constants.movementAllowed, false);
			owner.changePermissions(Constants.autoAttackAllowed, false);
			owner.changePermissions(Constants.spellCastAllowed, false);
			owner.changePermissions(Constants.channelingAllowed, false);
			break;
		case SILENCE:
			if(owner.getClass() == Hero.class)for(Ability a: ((Hero)owner).abilities)a.endChannel();
			owner.changePermissions(Constants.spellCastAllowed, false);
			owner.changePermissions(Constants.channelingAllowed, false);
			owner.color = Util.Color.WHITE;
			break;
		case SPLITSHOT:
			((Hero)owner).shotAlg = ( (int) (Math.random() * 2));
			owner.shots = 4;
			break;
		case HASTE:
			owner.changeBaseSpeed(522);
			break;
		case DOUBLEDAMAGE:
			owner.damageMultiplier *= 2;
			break;
		case ORBFROSTARROWS:
			owner.equipSpell(MissileType.FREEZEBALL);
			break;
		case RAPIDREGEN:
			owner.changeStat(Constants.healthRegen, 2 * owner.getStat(Constants.healthRegen));
			owner.changeStat(Constants.manaRegen, 2 * owner.getStat(Constants.healthRegen));
			break;
		case STATICLINKSAP:
			children.add(0,new Buff(owner, Buff.BuffName.STATICLINKDAMAGE,Ability.AbilityType.STATICLINK.duration,intensity));
			children.add(1,new Buff(target, Buff.BuffName.STATICLINKDAMAGE,Ability.AbilityType.STATICLINK.duration,intensity));
			break;
		case UNSTABLECURRENT:
			//owner.addToSpeedModifier(.04);
			break;
		case WINDRUN:
			owner.addToSpeedModifier(intensity);
			owner.changeStat(Constants.evasion, 100);
			break;
		case FROSTARROWSPARENT:
			owner.mainUAM = this;
			break;
		case FROSTARROWSCHILD:
			intensity *= -.07d;
			owner.addToSpeedModifier(intensity);
			break;
		case MARKSMANSHIP:
			owner.changeStat(Constants.bonusAgility, 20 + 20 * intensity);
			break;
		case FLAKCANNON:
			count += this.ability.level;
			break;
		default:;
		}
	}
	public void remove(){
		switch(buffName){
		case STUN:
			owner.changePermissions(Constants.movementAllowed, true);
			owner.changePermissions(Constants.autoAttackAllowed, true);
			owner.changePermissions(Constants.spellCastAllowed, true);
			owner.changePermissions(Constants.channelingAllowed, true);
			break;
		case SILENCE:
			owner.changePermissions(Constants.spellCastAllowed, true);
			owner.changePermissions(Constants.channelingAllowed, true);
			owner.color = owner.defaultColor;
			break;
		case SPLITSHOT:
			owner.shots = 1;
			break;
		case HASTE:
			owner.addToSpeedModifier(-.5);
			break;
		case DOUBLEDAMAGE:
			owner.damageMultiplier /= 2;
			break;
		case ORBFROSTARROWS:
			owner.equipSpell(MissileType.FIREBALL);
			break;
		case RAPIDREGEN:
			owner.changeStat(Constants.healthRegen, - 2 / 3 * owner.getStat(Constants.healthRegen));
			owner.changeStat(Constants.manaRegen, - 2 / 3 * owner.getStat(Constants.manaRegen));
			owner.color = this.owner.defaultColor;
			break;
		case STATICSTORM:
			owner.effects.clear();
			break;
		case STATICLINKDAMAGE:
			owner.changeStat(Constants.bonusDamage, -(intensity));
			break;
		case WINDRUN:
			owner.addToSpeedModifier(-intensity);
			owner.changeStat(Constants.evasion, -100);
			break;
		case FOCUS:
			 if(owner.getAttackTarget() == this.target)
				 owner.changeStat(Constants.attackSpeed, -intensity);
			break;
		case FROSTARROWSPARENT:
			owner.mainUAM = null;
			break;
		case FROSTARROWSCHILD:
			owner.addToSpeedModifier(-intensity);
			break;
		case PRECISIONAURA:
			owner.changeStat(Constants.bonusDamage, -intensity);
			break;
		case MARKSMANSHIP:
			owner.changeStat(Constants.bonusAgility, -20 + 20 * -intensity);
			break;
		default:;
		}
	}
	public void destroy(){
		active = false;
		if(owner.getClass() == Hero.class)if(((Hero)owner).owner.getClass() == Player.class)
		for(BuffIcon bi: ((Player)((Hero)owner).owner).gui.buffs)if(bi.buff == this)
			bi.setBuff(null);
		
		remove();
	}
	public void trigger(Alive  triggerTarget){
		switch(buffName){
		case ORBFROSTARROWS: 
			target.changeBaseSpeed(-20);
			break;
		case FLAKCANNON:
			ArrayList<Alive> alivesInArea = Head.qt.getAlivesAroundMe(owner, (int)owner.getStat(Constants.attackRange));
			ArrayList<Alive> properFaction = new ArrayList<Alive>();
			for(Alive a: alivesInArea){
				if(a.faction != this.owner.faction && a != owner.getAttackTarget())
					properFaction.add(a);
			}
			for(Alive a: properFaction){
				if(owner.missile){
					Missile auto = owner.missileType == null ? new Missile(true, owner, a) : new Missile(owner.missileType, owner, a);
					auto.setAuto(true);
					auto.setDamage(owner.getStat(Constants.damage));
				}
				else a.getAttacked(owner);
			}
			if(--count == 0)destroy();
			break;
		case FOCUS:
			if(owner.getAttackTarget() != target && triggerTarget == target){
				owner.changeStat(Constants.attackSpeed, intensity);
			}
			else if(owner.getAttackTarget() == target && triggerTarget != target){
				owner.changeStat(Constants.attackSpeed, -intensity);
			}
			break;
		case FROSTARROWSPARENT: 
			new Buff(triggerTarget, Buff.BuffName.FROSTARROWSCHILD, 2*Head.tickRate,intensity);
			break;
		case UNSTABLECURRENT:
			new Buff(triggerTarget, BuffName.UNSTABLECURRENTSLOW, 10 * Head.tickRate, intensity);
			break;
		}
	}
	public void drawMe(Camera camera){
		for(Effect e: effects){
			Util.drawActor(e, e.color, camera);
		}
	}
	public void setIntensity(double d){
		if(d == 1){
			int g;
			g= 5;
			if(g == 5);
		}
		switch(buffName){
		case STATICLINKDAMAGE:
			owner.changeStat(Constants.bonusDamage, -1 * intensity);
			intensity = d;
			owner.changeStat(Constants.bonusDamage, intensity);
			break;
		case UNSTABLECURRENT:
			owner.addToSpeedModifier(-.04 * intensity);
			intensity = d;
			owner.addToSpeedModifier(0.04 * intensity);
			break;
		case WINDRUN:
			intensity = .5;
			break;
		case FOCUS:
			intensity = 100 + 100 * intensity;
			break;
		case PRECISIONAURA:
			owner.changeStat(Constants.bonusDamage, -intensity);
			intensity = d;
			owner.changeStat(Constants.bonusDamage, intensity);
			break;
		default:
			intensity = d;
			break;
		}
	}
	public Alive getTarget(){
		return target;
	}
	public void setTarget(Alive setTarget){
		target = setTarget;
	}

	public static void removeBuff(Alive a, Buff.BuffName bn){
		for(Buff b: a.buffs)if(b.buffName == bn)b.active = false;
	}
	public void costTrigger(){
		switch(buffName){
		case FROSTARROWSPARENT:
			owner.changeStat(Constants.mana, -ability.abilityType.manaCost);
			break;
		}
	}
	private void stackTypeInit(){
		switch(this.buffName){
		case PRECISIONAURA:
			stackingType = StackingType.NONE;
			break;
			default: stackingType = StackingType.STACK;
		}
	}
	private boolean stackCheck(){
		switch(this.stackingType){
		case RESTRICTION:
			for(Buff onTarget: this.owner.buffs)for(BuffName restricted: this.restrictions)
				if(onTarget.buffName == restricted)return false;
			break;
		case NONE:
			for(Buff onTarget: this.owner.buffs)if(onTarget.buffName == buffName)
				return false;
			break;
		}
		return true;
	}
}
