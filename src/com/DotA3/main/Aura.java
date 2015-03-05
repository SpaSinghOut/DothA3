package com.DotA3.main;

import java.util.ArrayList;

public class Aura extends Actor{
	Alive holder;
	double radius;
	Buff.BuffName toBePlaced;
	AffectedUnits affectedUnits;
	int intensity;
	public static ArrayList<Aura> auras = new ArrayList<Aura>();
	public Aura(Alive setHolder, double setRadius, Buff.BuffName setTBP, AffectedUnits au, int setIntensity){
		super(setRadius, setRadius, false);
		holder = setHolder;
		radius = setRadius;
		toBePlaced = setTBP;
		affectedUnits = au;
		auras.add(this);
		intensity = setIntensity;
	}
	enum AffectedUnits{
		ALL, HEROES, RANGEDHEROES, MELEEHEROES, RANGEDUNITS, MELEEUNITS,;
	}
	public boolean tick(){
		ArrayList<Alive> candidates = Head.qt.getAlivesAroundMe(holder, (int)radius);
		for(Alive a: sortThroughCandidates(candidates))
		if(Util.everySecond(2))
			nonGenericAdjustments(new Buff(holder, toBePlaced, Head.tickRate * 2 - 1));
		return active;
	}
	private ArrayList<Alive> sortThroughCandidates(ArrayList<Alive> candidates){
		ArrayList<Alive> accepted = new ArrayList<Alive>();
		switch(affectedUnits){
		case ALL:
			accepted = candidates;
			break;
		case HEROES:
			for(Alive a: candidates)if(a.getClass() == Hero.class)accepted.add(a);
			break;
		case RANGEDHEROES:
			for(Alive a: candidates)
				if(a.getClass() == Hero.class && ((Hero)a).missile)
					accepted.add(a);
		default:
			accepted = candidates;	
			break;
		}
		return accepted;
	}
	private void nonGenericAdjustments(Buff buff){
		if(buff.active)
		switch(buff.buffName){
		case PRECISIONAURA:
			final int increaseInPercentagePerLevel = 4;
			final int basePercentage = 5;
			//The damage given by the aura ought to change with drow's agility
			//therefore the following will set the damage given by the buff
			//every time the buff is created
			buff.setIntensity(
			//the following two lines change the percentage of agility gained 
			// as damage based on drow's level of the aura
			(basePercentage + increaseInPercentagePerLevel * ((Hero)holder).abilities.get(
			((Hero)holder).abilities.indexOf(Ability.AbilityType.DROWAURA)).level) * 0.01d * 
			//agility based damage change
			holder.getStat(Constants.agility));
		}
	}
}
