package com.DotA3.main;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.DotA3.main.Util.Color;

public class Hero extends Alive{
	private static final int inventorySize = 6;
	public int shotAlg = 1;
	int numberOfAbilities = 3;
	public ArrayList<Ability> abilities = new ArrayList<Ability>();
	HeroOwner owner;
	HeroType heroType;
	ItemList inventory;
	Actor manaBar;
	boolean ready;
	boolean initialized;
	public enum HeroType{
		NONE(getHeroStats(0), false,128, Alive.Attribute.STRENGTH,// "none",
				Ability.AbilityType.NONE, Ability.AbilityType.NONE, Ability.AbilityType.NONE, Ability.AbilityType.NONE),
		GYROCOPTER(getHeroStats(1),true, 350, Alive.Attribute.AGILITY, //"gyrocopter",
				Ability.AbilityType.ROCKETBARRAGE, Ability.AbilityType.GYROCKET,  Ability.AbilityType.FLAKCANNON, Ability.AbilityType.CALLDOWN), 
		RAZOR(getHeroStats(2), true, 550, Alive.Attribute.AGILITY, //"razor",
				Ability.AbilityType.PLAZMAFIELD, Ability.AbilityType.STATICLINK, Ability.AbilityType.UNSTABLECURRENT, Ability.AbilityType.STATICSTORM),
		WINDRUNNER(getHeroStats(3), true, 625, Alive.Attribute.INTELLIGENCE, //"windrunner",
				Ability.AbilityType.SHACKLESHOT, Ability.AbilityType.POWERSHOT, Ability.AbilityType.WINDRUN, Ability.AbilityType.FOCUS),
		DROWRANGER(getHeroStats(4), true, 600, Alive.Attribute.AGILITY, //"drowranger",
				Ability.AbilityType.FROSTARROWS, Ability.AbilityType.DROWSILENCE, Ability.AbilityType.DROWAURA, Ability.AbilityType.MARKSMANSHIP),
		;
		double startingSTR, startingAGI, startingINT, STRGain, AGIGain, INTGain;
		Ability.AbilityType q,w,e,r;
		boolean ranged;
		double range;
		//String name;
		Alive.Attribute primaryAttribute;
		HeroType(double[] heroStats, boolean setRanged, double setRange, Alive.Attribute setPrimaryAttribute, //String setName,
				 Ability.AbilityType setQ, Ability.AbilityType setW, Ability.AbilityType setE, Ability.AbilityType setR){
			ranged = setRanged;
			range = setRange;
			//name = setName;
			primaryAttribute = setPrimaryAttribute;
			startingSTR = heroStats[Constants.startingSTR];
			startingAGI = heroStats[Constants.startingAGI];
			startingINT = heroStats[Constants.startingINT];
			STRGain = heroStats[Constants.strGain];
			AGIGain = heroStats[Constants.agiGain];
			INTGain = heroStats[Constants.intGain];
			q = setQ; 
			w = setW; 
			e = setE; 
			r = setR; 
		}
		private static double[] getHeroStats(int i){
			double[] d = {0,0,0,0,0,0};
			switch(i){
			case 1:
				d[0] = 20;
				d[1] = 20;
				d[2] = 20;
				d[3] = 2;
				d[4] = 2;
				d[5] = 2;
				break;
			case 2:
				d[0] = 20;
				d[1] = 20;
				d[2] = 20;
				d[3] = 2;
				d[4] = 2;
				d[5] = 2;
				break;
			case 3:
				d[0] = 20;
				d[1] = 20;
				d[2] = 20;
				d[3] = 2;
				d[4] = 2;
				d[5] = 2;
				break;
			case 4:
				d[0] = 20;
				d[1] = 20;
				d[2] = 20;
				d[3] = 2;
				d[4] = 2;
				d[5] = 2;
				break;
			}
			return d;
		}
	}
	
	public Hero(int setWidth, int setHeight, HeroType setHeroType, Faction setFaction) {
		super(setWidth, setHeight, setFaction);
		defaultColor = Util.Color.WHITE;
		shape = Actor.Shape.QUAD;
		shots = 1;
		setLocation(new Location(faction == Alive.Faction.RADIANT ? 500: 2500, 750));
		AI = false;
		childSetsOwnMovement = true;
		equippedSpell = MissileType.FIREBALL;
		faction = setFaction;
		setAttackState(AttackState.NONE);
		abilities.add(0, new Ability(Ability.AbilityType.NONE,this));
		abilities.add(1, new Ability(Ability.AbilityType.NONE,this));
		abilities.add(2, new Ability(Ability.AbilityType.NONE,this));
		abilities.add(3, new Ability(Ability.AbilityType.NONE,this));
		manaBar = new Actor(this.getWidth(), this.getHeight() / 4, false);
		manaBar.color = Util.Color.BLUE;
		for(Ability a: abilities)a.setOwner(this);
		color = defaultColor;
		childSetsOwnMovement = false;
		initStats();
		inventory = new ItemList(inventorySize, ItemList.Type.INVENTORY, this);
		if(faction == Alive.Faction.RADIANT)setTarget(new Location (500,1000));
	}
	private void initStats(){
		changeBaseSpeed(300);
		changeStat(Constants.visibilityRange, 900);
		changeStat(Constants.maxHealth, Constants.baseHealth);
		changeStat(Constants.health, getStat(Constants.maxHealth));
		changeStat(Constants.maxMana, 300);
		changeStat(Constants.mana, getStat(Constants.maxMana));
		changeStat(Constants.manaRegen, 1);
		changeStat(Constants.healthRegen, 0.1);
		changeStat(Constants.experienceGiven, 200);
		changeStat(Constants.startingDamage, 0);
		changeStat(Constants.abilityPoints, 1);
		changeStat(Constants.attackSpeed, 0);
	}
	public void initHeroType(HeroType setHeroType){
		heroType = setHeroType;
		primaryAttribute = setHeroType.primaryAttribute;
		changeStat(Constants.baseStrength, heroType.startingSTR);
		changeStat(Constants.baseAgility, heroType.startingAGI);
		changeStat(Constants.baseIntelligence, heroType.startingINT);
		changeStat(Constants.mana, getStat(Constants.maxMana) - getStat(Constants.mana));
		this.abilities.set(0, new Ability(heroType.q, this));
		this.abilities.set(1, new Ability(heroType.w, this));
		this.abilities.set(2, new Ability(heroType.e, this));
		this.abilities.set(3, new Ability(heroType.r, this));
		for(Ability a: abilities)a.setOwner(this);
		missile = heroType.ranged;
		changeStat(Constants.attackRange, heroType.range);
		try { 
			setTexture();
			String heroNameString = ((Hero)this).heroType.toString().toLowerCase();
			texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/" + heroNameString + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ready = false;
		if(owner.getClass() == Player.class)((Player)owner).gui.initAbilityButtons();
		initialized = true;
		Head.map.spawnCreeps();
	}
	public boolean tick(){
		if(!super.tick())return false;
		healthBar.getLocation().changeY(-healthBar.height);
		regen();
		manaBar.setLocation(new Location(
				(this.getLocation().getX() - this.getWidth() / 2) + this.getRatio("mana") * (this.getWidth() / 2 + 1),
				this.getLocation().getY() - this.getHeight() / 2 - manaBar.getHeight() / 2));
				manaBar.setWidth(this.width * getRatio("mana"));
		for(Ability a: abilities)a.tick();
		for(Buff b: buffs)for(Effect e: b.effects)if(!e.tick())Head.addToDeleteList(e);
		//testBranchItemAdding();
		if(ready)this.initHeroType(heroType);
		return active;
	}
	
	protected void castSpell(Ability ability){
		if(ability.state != Ability.State.READY && ability.abilityType.castType != Ability.AbilityType.CastType.TOGGLE)
			return;
		if(ability.abilityType.castType == Ability.AbilityType.CastType.ALIVETARGET){
			if(owner.selectedUnit == null)return;
			ability.setTarget(owner.selectedUnit);
		}
		ability.activate();
		for(Buff b: buffs)
			if(b.triggerType == Buff.TriggerType.ONSPELLCAST)
				b.trigger(this);
	}
	public void die(){
		super.die();
		owner.setRespawnTimer(2);
		Alive.allAlives.remove(this);
	}
	public void drawMe(Camera fromCamera){
		if(!super.drawMe(getRGB(), fromCamera))return;
		Util.drawActor(manaBar, manaBar.color, fromCamera);
	}
	private void debug(){
		System.out.println("Hero: " + "Attack state: " + getAttackState());
		System.out.println("Hero: " + "immobile: " + immobile);
		System.out.println("Hero: " + "needToMove: " + needToMove);
	}
	private void testBranchItemAdding(){
		if(Head.tickCount == Head.tickRate * (2 + Head.heroPickSecondDelay))
			inventory.addItem(1, new Item(Item.Preset.IRONBRANCH));
		else if(Head.tickCount == Head.tickRate * (3 + Head.heroPickSecondDelay)){
			//inventory.removeItem(1);
			inventory.addItem(1,new Item(Item.Preset.IRONBRANCH));
		}
		else if(Head.tickCount == Head.tickRate * (3 + Head.heroPickSecondDelay) + 1){
			inventory.removeItem(1);
			inventory.removeItem(2);
		}
	}
}
