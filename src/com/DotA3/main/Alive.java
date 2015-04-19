package com.DotA3.main;

import java.io.IOException;
import java.util.ArrayList;

public class Alive extends Actor{
	static int experienceRange = 650; 
	MissileType equippedSpell;
	boolean invulnerable;
	int invulnerabilityCount;
	double damageMultiplier;
	private double[] stats;
	int shots;
	Faction faction;
	Alive attackTarget;
	private AttackState attackState;
	ArrayList<Buff> buffs = new ArrayList<Buff>();
	public static ArrayList<Alive> allAlives = new ArrayList<Alive>();
	Actor healthBar;
	Alive lastHitter;
	boolean alive;
	Attribute primaryAttribute;
	Buff mainUAM;
	boolean missile;
	MissileType missileType;
	boolean noRetraction;
	private boolean[] permissions = new boolean[Constants.numberOfPermissions];
	boolean resetTexture;
	public Alive(int setWidth, int setHeight, Faction setFaction){
		super(setWidth, setHeight, true);
		stats = new double[Constants.statsSize];
		damageMultiplier = 1;
		stats[Constants.level] = 1;
		shape = Actor.Shape.QUAD;
		shots = 1;
		faction = setFaction;
		setAttackState(AttackState.NONE);
		needToMove = false;
		healthBar = new Actor(this.getWidth(), this.getHeight() / 4, false);
		alive = true;
		setTarget(location);
		allAlives.add(this);
		permissions[Constants.movementAllowed]= true;
		permissions[Constants.spellCastAllowed] = true;
		permissions[Constants.channelingAllowed] = true;
		attackOrientedInit();
		resetTexture = false;
	}
	public enum AttackState{
		NONE, SELECTED, MOVING, ANIMATION, RETRACTION, WAIT,;
	}
	public enum Direction{
		LEFT, RIGHT, UP, DOWN,;
	}
	public enum Faction{
		RADIANT, DIRE,;
	}
	public enum DamageType{
		PHYSICAL, MAGICAL, PURE, UNIVERSAL, HPREMOVAL,;
	}
	public enum Attribute{
		STRENGTH, AGILITY, INTELLIGENCE,;
	}
	public void equipSpell(MissileType newSpell){
		equippedSpell = newSpell;
	}

	private void attackOrientedInit() {
	permissions[Constants.autoAttackAllowed] = true;
	changeStat(Constants.baseAttackTime, 1.7);
	changeStat(Constants.baseAttackSpeed, 100);
	changeStat(Constants.baseAnimationTime, 1);
	this.resetAllAttackCDs();
	}
	public boolean tick(){
		if(!(getPermissions(Constants.movementAllowed) ? super.tick(getStat(Constants.health) > 0 ): (active && alive))){
			alive = false;
			return false;
		}
		if (this.invulnerabilityCount != 0)this.invulnerabilityCount--;
		else invulnerable = false;
		regen();
		healthBar.setWidth(width * getRatio("health"));
		healthBar.setLocation(new Location(
				(this.getLocation().getX() - this.getWidth() / 2) + this.getRatio("health") * (this.getWidth() / 2 + 1),
				this.getLocation().getY() - this.getHeight() / 2 - healthBar.getHeight() / 2));
		for(Buff buff: buffs)if(!buff.tick())Head.addToDeleteList(buff);
		needToMove = permissions[Constants.movementAllowed];
		if(permissions[Constants.autoAttackAllowed])configureAttack();
		if(attackState == AttackState.ANIMATION || 
		attackState == AttackState.RETRACTION || attackState == AttackState.WAIT)
			changePermissions(Constants.movementAllowed, false);
		else changePermissions(Constants.movementAllowed, true);
		return active;
	}
	protected void regen(){
		if(getClass() == Hero.class){
			if(stats[Constants.healthRegen] > 0)
				changeStat(Constants.health, stats[Constants.healthRegen] / Head.tickRate);
			if(stats[Constants.manaRegen] > 0)
				changeStat(Constants.mana, stats[Constants.manaRegen] / Head.tickRate);
		}
		else if(getClass() == Creep.class){
			if(stats[Constants.healthRegen] > 0)
				changeStat(Constants.health, stats[Constants.healthRegen] / Head.tickRate);
		}
	}
	public void setTarget(Location setTarget){
		target = setTarget;
	}
	public void heal(int heal){
		stats[Constants.health] += heal;
	}
	protected boolean isVisible(Actor seen){
		return (Util.getRealCentralDistance(this, seen) < this.stats[Constants.visibilityRange]);
	}
	protected boolean isAttackTargetWithinAttackRange(){
		return (Util.getRealCentralDistance(this, attackTarget) < stats[Constants.attackRange] + this.getWidth() / 2 + attackTarget.getWidth() / 2);
	}
	public void setFaction(Faction setFaction){
		faction = setFaction;
	}
	protected void issueAttack(Alive attacking){
		if(attacking.attackState == AttackState.NONE || attacking.attackState == AttackState.SELECTED ||
		attacking.attackState == AttackState.MOVING)
			if(attacking.faction != this.faction)
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONATTACKDECLARATION)
				b.trigger(attackTarget);
	}
	protected void getTargeted(Alive attacker){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONBEINGTARGETED)
				b.trigger(attackTarget);
	}
	protected void doAttack(Alive attacking){
		if(mainUAM != null && mainUAM.triggerType == Buff.TriggerType.ORBATTACK)
			mainUAM.trigger(attacking);
		else if(mainUAM != null && mainUAM.triggerType == Buff.TriggerType.ORBHIT)
			mainUAM.costTrigger();
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONATTACK)
				b.trigger(attacking);
		if(missile){
			Missile auto = missileType == null ? new Missile(true, this, attacking) : new Missile(missileType, this, attacking);
			auto.setAuto(true);
			auto.setDamage(getStat(Constants.damage));
		}
		else attacking.getAttacked(this);
	}
	protected void getAttacked(Alive attacker){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONBEINGATTACKED)
				b.trigger(attacker);
		if(100d * Math.random() > getStat(Constants.evasion)){
			attacker.hit(this);getHit(attacker);
		}
	}
	protected void hit(Alive attacking){
		if(mainUAM != null && mainUAM.triggerType != Buff.TriggerType.ORBATTACK)
			this.mainUAM.trigger(attacking);
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONHIT)
				b.trigger(attacking);
		attacking.getHit(this);
		dealDamage(attacking, getStat(Constants.damage), DamageType.PHYSICAL);
	}
	protected void getHit(Alive attacker){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONBEINGHIT)
				b.trigger(attackTarget);
	}
	
	/*
	 * no use for this right now as i do not have any spell or even the mechanic set up to target alives
	 * directly. before i start implementing the following function i have to make sure i first create
	 * a function like the one above only with another argument that being the spell target.
	 * only from that function will the below function be called
	 */
	protected void getSpellTargeted(){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONSPELLTARGETED)
				b.trigger(this);
	}
	protected void getSpellAffected(Ability ability, Alive caster){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONSPELLAFFECTED)
				b.trigger(caster);		
	}
	public void dealDamage(Alive attacking, double damageDealt, DamageType setDamageType){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONDEALINGDAMAGE)
				b.trigger(attackTarget);
		double calculateRealDamage = damageDealt < 0 ? 0 : damageDealt;
		attacking.takeDamage(this, calculateRealDamage, setDamageType );
	}
	public void takeDamage(Alive attacker, double d, DamageType damageType){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONTAKINGDAMAGE)
				b.trigger(attackTarget);
		d *= 1 - ( getStat(Constants.armor) * .06 ) / ( 1 + getStat(Constants.armor) * .06);
		changeStat(Constants.health, -d);
		//Head.out(attacker.toString() + " dealt " + d + " damage to: " + this.toString());
		if(stats[Constants.health] <= 0 && alive){
			lastHitter = attacker;
			die();
			attacker.kill(this);
		}
	}
	protected void changeStat(int stat, double netChange){
		switch(stat){
		case Constants.experience:
			stats[stat] += netChange;
			while(getStat(Constants.experience) >= 
					getStat(Constants.level) * 100 + 100)
						changeStat(Constants.level, 1);
			break;
		case Constants.level:
			if(netChange == 1){
			stats[stat] += netChange;
			changeStat(Constants.experience, -(getStat(Constants.level) * 100));
			changeStat(Constants.baseStrength, ((Hero)this).heroType.STRGain);
			changeStat(Constants.baseAgility, ((Hero)this).heroType.AGIGain);
			changeStat(Constants.baseIntelligence, ((Hero)this).heroType.INTGain);
			changeStat(Constants.abilityPoints, 1);
			}
			break;
		case Constants.baseAttackSpeed:
			stats[stat] += netChange;
			changeStat(Constants.attackSpeed, netChange);
			break;
		case Constants.health:
			stats[Constants.health] += netChange;
			if(stats[Constants.health] > getStat(Constants.maxHealth))
				stats[Constants.health] = getStat(Constants.maxHealth);
			break;
		case Constants.mana:
			stats[Constants.mana] += netChange;
			if(stats[Constants.mana] > getStat(Constants.maxMana))
				stats[Constants.mana] = getStat(Constants.maxMana);
			else if(stats[Constants.mana] < 0)
				stats[Constants.mana] = 0;
			break;
		case Constants.damage:
			if(this.getClass() == Hero.class){
			}
			stats[stat] += netChange;
			break;
		case Constants.startingDamage:
			stats[stat] += netChange;
			changeStat(Constants.baseDamage, netChange);
			break;
		case Constants.baseDamage:
			stats[stat] += netChange;
			changeStat(Constants.damage, netChange);
			break;
		case Constants.bonusDamage:
			stats[stat] += netChange;
			changeStat(Constants.damage, netChange);
			break;
		case Constants.healthRegen:
			stats[stat] += netChange;
			break;
		case Constants.manaRegen:
			stats[stat] += netChange;
			break;
		case Constants.maxHealth:
			stats[stat] += netChange;
			if(stats[stat] < 1)stats[stat] = 1;
			if(stats[Constants.health] > stats[stat])stats[Constants.health] = stats[stat];
			break;
		case Constants.maxMana:
			stats[stat] += netChange;
			if(stats[stat] < 1)stats[stat] = 1;
			if(stats[Constants.mana] > stats[stat])stats[Constants.mana] = stats[stat];
			break;
		case Constants.attackSpeed:
			stats[stat] += netChange;
			break;
		case Constants.armor:
			stats[stat] += netChange;
			break;
		case Constants.strength:
			stats[stat] += netChange;
			float hpRatio = getRatio("health");
			changeStat(Constants.maxHealth, netChange * 19);
			changeStat(Constants.healthRegen, netChange * 0.03);
			changeStat(Constants.health, hpRatio * getStat(Constants.maxHealth) - getStat(Constants.health));
			if(this.primaryAttribute == Attribute.STRENGTH)
				changeStat(Constants.baseDamage, netChange);
			break;
		case Constants.agility:
			stats[stat] += netChange;
			changeStat(Constants.attackSpeed, netChange);
			changeStat(Constants.armor, netChange / 7);
			if(primaryAttribute == Alive.Attribute.AGILITY)changeStat(Constants.baseDamage, netChange);
			break;
		case Constants.intelligence:
			stats[stat] += netChange;
			float mpRatio = getRatio("mana");
			changeStat(Constants.maxMana, netChange * 14);
			changeStat(Constants.manaRegen, netChange * 0.04);
			changeStat(Constants.mana, mpRatio * getStat(Constants.maxMana) - getStat(Constants.mana));
			if(primaryAttribute == Alive.Attribute.INTELLIGENCE)changeStat(Constants.baseDamage, netChange);
			break;
		case Constants.gold:
			stats[stat] += netChange;
			break;
		case Constants.baseStrength:
			stats[stat] += netChange;
			changeStat(Constants.strength, netChange);
			break;
		case Constants.baseAgility:
			stats[stat] += netChange;
			changeStat(Constants.agility, netChange);
			break;
		case Constants.baseIntelligence:
			stats[stat] += netChange;
			changeStat(Constants.intelligence, netChange);
			break;
		case Constants.bonusStrength:
			stats[stat] += netChange;
			changeStat(Constants.strength, netChange);
			break;
		case Constants.bonusAgility:
			stats[stat] += netChange;
			changeStat(Constants.agility, netChange);
			break;
		case Constants.bonusIntelligence:
			stats[stat] += netChange;
			changeStat(Constants.intelligence, netChange);
			break;
		case Constants.allAttributesBase:
			changeStat(Constants.baseStrength, netChange);
			changeStat(Constants.baseAgility, netChange);
			changeStat(Constants.baseIntelligence, netChange);
			break;
		case Constants.allAttributesBonus:
			changeStat(Constants.bonusStrength, netChange);
			changeStat(Constants.bonusAgility, netChange);
			changeStat(Constants.bonusIntelligence, netChange);
			break;
		default:
			stats[stat] += netChange;
			break;
		}
	}
	protected double getStat(int stat){
		switch(stat){
		case Constants.health:
			return stats[Constants.health];
		case Constants.maxHealth:
			if(stats[Constants.maxHealth] > 1)return stats[Constants.maxHealth];
			else return 1;
		case Constants.mana:
			return stats[Constants.mana];
		case Constants.maxMana:
			if(stats[Constants.maxMana] > 1)return stats[Constants.maxMana];
			else return 1;
		case Constants.damage:
			return stats[Constants.damage];
		case Constants.gold:
			return stats[stat];
		default:
			return stats[stat];
		}
	}
	public void kill(Alive fallen){
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONKILL)
				b.trigger(attackTarget);
	}
	public void die(){
		alive = false;
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONDEATH)
				b.trigger(attackTarget);
		ArrayList<Hero> receivers = new ArrayList<Hero>();
		for(Alive a : Alive.allAlives){
			if(a.getClass() == Hero.class 
			&& a.faction != this.faction 
			&& Util.getRealCentralDistance(a, this) < Alive.experienceRange)
				receivers.add((Hero)a);
		}
		if(!receivers.contains(lastHitter) && lastHitter.getClass() == Hero.class)
			receivers.add((Hero)lastHitter);
		for(Hero a: receivers)if(a != null)
			a.changeStat(Constants.experience,
					getStat(Constants.experienceGiven) 
							/ (receivers.size()));
		double goldGiven = getStat(Constants.goldGiven) * 0.9 + (int)(Math.random() * (getStat(Constants.goldGiven) * 0.2));
		lastHitter.changeStat(Constants.gold, goldGiven );
		if(lastHitter.getClass() == Hero.class)Head.out(lastHitter.toString() + " received: " + goldGiven);
	}
	protected void setAttackState(AttackState setAttackState){
		attackState = setAttackState;
	}
	public AttackState getAttackState(){
		return attackState;
	}
	private void configureAttack(){
		needToMove = attackTarget == null && target != null;
		changePermissions(Constants.movementAllowed, attackTarget == null && target != null);
		if(attackTarget == null || !attackTarget.alive){
			if(getAttackState() != AttackState.NONE)
				setAttackState(Alive.AttackState.NONE);
			needToMove = true;
		}
		else if(attackState == AttackState.NONE && attackTarget == null){
			needToMove = true;
		}
		else if(attackState == AttackState.NONE && attackTarget != null && attackTarget.active){
			aggroOn(attackTarget);
		}
		else if(attackState == AttackState.NONE && attackTarget != null && !attackTarget.active){
			attackTarget = null;
		}
		else if(attackState == AttackState.SELECTED){
			if(attackTarget == null){
				needToMove = true;
				this.setAttackState(Alive.AttackState.NONE);
				target = null;
				return;
			}
			if(!this.isVisible(attackTarget)){
				attackTarget = null;
				needToMove = true;
				target = null;
			}
			else if(this.isVisible(attackTarget)){
				if(isAttackTargetWithinAttackRange()){
					issueAttack(attackTarget);
					attackTarget.getTargeted(this);
					setAttackState(Alive.AttackState.ANIMATION);
				}
				else if(!isAttackTargetWithinAttackRange()){
					setAttackState(AttackState.MOVING);
					needToMove = true;
					setAttackState(AttackState.MOVING);
				}
			}
		}
		else if(this.attackState == Alive.AttackState.MOVING){
			if(attackTarget == null){
				needToMove = true;
				setAttackState(Alive.AttackState.NONE);
				return;
			}
			if(!isAttackTargetWithinAttackRange()){
			needToMove = true;
			target = attackTarget.location;
			}
			else {
				target = null;
				issueAttack(attackTarget);
				setAttackState(Alive.AttackState.ANIMATION);
			}
		}
		else if(this.attackState == Alive.AttackState.ANIMATION && stats[Constants.animationCD] >   0){
			stats[Constants.animationCD]--;
		}
		else if(this.attackState == Alive.AttackState.ANIMATION && stats[Constants.animationCD] <= 0){
			resetAnimationCD();
			setAttackState(Alive.AttackState.RETRACTION);
			this.doAttack(attackTarget);
		}
		else if(attackState == AttackState.RETRACTION && stats[Constants.retractionCD] > 0){
			if(noRetraction)this.setAttackState(AttackState.WAIT);
			stats[Constants.retractionCD]--;
		}
		else if(attackState == AttackState.RETRACTION && stats[Constants.retractionCD] <= 0){
			this.resetRetractionCD();
			this.setAttackState(Alive.AttackState.WAIT);
		}
		else if(attackState == AttackState.WAIT && stats[Constants.attackCD] > 0){
			this.stats[Constants.attackCD]--;
		}
		else if(attackState == AttackState.WAIT && stats[Constants.attackCD] == 0){
			if(attackTarget != null)setAttackState(AttackState.SELECTED);
			else setAttackState(AttackState.NONE);
			this.resetAttackCD();
		}
		//if(this.getClass() == Hero.class && oldTarget != null && target == null)target = oldTarget;
		//else if(this.getClass() == Hero.class & oldTarget == null)target = new Location(location.x, location.y);
	}
	protected void resetAnimationCD(){
		this.stats[Constants.animationCD] = this.stats[Constants.baseAttackTime]
				/ (this.stats[Constants.attackSpeed]  / 100) * (int)Head.tickRate
				* this.stats[Constants.baseAnimationTime];
	}
	protected void resetRetractionCD(){
		stats[Constants.retractionCD] = (int)(this.stats[Constants.animationCD] / 3);
	}
	protected void resetAttackCD(){
		this.stats[Constants.attackCD] =  (int)(Head.tickRate / 5);
	}
	protected void resetAllAttackCDs(){
		this.resetAnimationCD();
		this.resetAttackCD();
		this.resetRetractionCD();
	}
	protected boolean isInForgetfulState(){
		switch(attackState){
		case ANIMATION:
			return false;
		default: return true;
		}
	}
	public float getRatio(String ratioType){
		switch(ratioType){
		case "health":
			return (((float)(stats[Constants.health])) / ((float)(stats[Constants.maxHealth])));
		case "mana":
			return (((float)(stats[Constants.mana])) / ((float)(stats[Constants.maxMana])));
		case "animation":
			return (((float)(stats[Constants.animationCD])) / ((float)(stats[Constants.baseAnimationTime])));
		case "experience":
			return (float)((stats[Constants.experience]) / (stats[Constants.level] * 100 + 100));
		}
		return 0.0f;
	}
	protected boolean drawMe(float[] RGB, Camera camera){
		if(resetTexture){
			try {
				setTexture();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(camera.canSeeActor(this)){
			Util.drawActor(this, RGB, camera);
			Util.drawActor(healthBar, faction == Alive.Faction.RADIANT ? Util.Color.GREEN: Util.Color.RED, camera);
			for(Buff b: buffs)
				b.drawMe(camera);
			return true;
		}
		return false;
	}
	public void aggroOn(Alive setTarget){
		if(setTarget == attackTarget)return;
		if(setTarget != null)setAttackState(AttackState.SELECTED);
		else{
			setAttackState(AttackState.NONE);
			this.resetAllAttackCDs();
		}
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONAGGRO)
				b.trigger(setTarget);
		attackTarget = setTarget;
	}
	public Buff[] getBuffsOfType(Buff.BuffName bn){
		Buff[] ba = new Buff[buffs.size()];
		int i = 0;
		for(Buff b:buffs)if(b.buffName == bn){
			ba[i++] = b;
		}
		return ba;
	}
	public void changePermissions(int permission, boolean allowed){
		switch(permission){
		case Constants.movementAllowed:
			if(allowed){
				boolean permitted = true;
				for(Buff b:buffs){
					if(b.active && b.buffName == Buff.BuffName.STUN)permitted = false;
				}
				if(permitted)permissions[permission] = allowed;
			}
			else permissions[permission] = allowed;
			//immobile = getPermissions(Constants.movementAllowed) ? false:true;
			break;
		case Constants.spellCastAllowed:
			if(allowed)for(Buff b:buffs)if(b.buffName == Buff.BuffName.STUN || 
			b.buffName == Buff.BuffName.SILENCE);
			else permissions[permission] = allowed;
		case Constants.channelingAllowed:
			if(allowed)for(Buff b:buffs)if(b.buffName == Buff.BuffName.STUN || 
			b.buffName == Buff.BuffName.SILENCE);
			else permissions[permission] = allowed;
			break;
		case Constants.autoAttackAllowed:
			if(allowed)for(Buff b:buffs)if(b.buffName == Buff.BuffName.STUN);
			else permissions[permission] = allowed;
			break;
		default: permissions[permission] = allowed;
		}
	}
	public boolean getPermissions(int permission) {
		return permissions[permission];
	}
	public Alive getAttackTarget() {
		return attackTarget;
	}
	protected boolean hasBuffType(Buff.BuffName setBuffType){
		for(Buff b: buffs)if(b.buffName == setBuffType)return true;
		return false;
	}
	public boolean hasAttackTarget(){
		return attackTarget != null;
	}
}
