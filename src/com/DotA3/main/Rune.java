package com.DotA3.main;

public class Rune extends Actor{
	PowerType powerType;
	public Rune(PowerType setPowerType){
		super(40,40, false);
		powerType = setPowerType;
		switch(powerType){
		case NUKE:
			color = Util.Color.YELLOW;
			break;
		case EXTRALIFE:
			color = Util.Color.GREEN;
			break;
		case SPLITSHOT:
			color = Util.Color.PURPLE;
			break;
		case DOUBLEDAMAGE:
			color = Util.Color.PINK;
			break;
		case HASTE:
			color = Util.Color.RED;
			break;
		case FRIDGE:
			color = Util.Color.BLUE;
			break;
		default:;
		}
		shape = Actor.Shape.TRI;
	}
	public enum PowerType{
		NUKE, EXTRALIFE, SPLITSHOT, DOUBLEDAMAGE, HASTE, FRIDGE;
	}
	public void use(Hero hero){
		active = false;
		switch(powerType){
		case NUKE:
			//does nothing so far
			break;
		case SPLITSHOT:
			new Buff(hero, Buff.BuffName.SPLITSHOT, (int) (Head.tickRate * 10));
			break;
		case EXTRALIFE:
			hero.changeStat(Constants.health, 200);
			break;
		case DOUBLEDAMAGE:
			new Buff(hero, Buff.BuffName.DOUBLEDAMAGE, (int)(Head.tickRate * 10));
			new Buff(hero, Buff.BuffName.RAPIDREGEN, (int)(Head.tickRate * 30));
			break;
		case HASTE:
			new Buff(hero, Buff.BuffName.HASTE, (int) (Head.tickRate * 10));
			break;
		case FRIDGE:
			new Buff(hero, Buff.BuffName.ORBFROSTARROWS, (int) (Head.tickRate * 13));
			break;
			default:
		}
		Head.addToDeleteList(this);
	}
}
