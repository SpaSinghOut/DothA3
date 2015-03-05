package com.DotA3.main;

import java.util.ArrayList;

import com.DotA3.main.Alive.Faction;

public class AI extends HeroOwner{
	int abilityPoint;
	int[] levellingList;
	ActionQueue<AIAction> actionQueue = new ActionQueue<AIAction>();
	public AI(Faction setFaction) {
		super(setFaction);
		hero.heroType = Hero.HeroType.GYROCOPTER;
		hero.initHeroType(hero.heroType);
		initAbilityLevellingList();
		int[] locations = {2500, 800, 2500,1200,2500,1000};
		for(int i = 0; i < locations.length / 2; i++){
			int[] subLocation = {locations[i * 2], locations[i * 2 + 1]};
			actionQueue.add(new AIAction(this, AIAction.ActionName.MOVETO, subLocation));
		}
		actionQueue.add(new AIAction(this, AIAction.ActionName.FINDCREEPS, new int[0]));
		actionQueue.add(new AIAction(this, AIAction.ActionName.WATCH, new int[0]));
	}
	public void tick(){
		super.tick();
		if(hero.getStat(Constants.abilityPoints) > 0){
			Head.out(String.valueOf(hero.getStat(Constants.abilityPoints)));
			hero.abilities.get(getAbilityToLevel()).levelAbility();
		}
		if(actionQueue.size() > 0)actionQueue.get(0).tick();
		else{
			ArrayList<Alive> pat = Head.qt.getAlivesAroundMe(this.hero, (int)this.hero.getStat(Constants.visibilityRange));
			for(Alive a: pat){
				if(hero.faction != a.faction){
					if(!hero.hasAttackTarget())
						hero.aggroOn(a);
					else{
						if(Util.getRealCentralDistance(hero, a) < Util.getRealCentralDistance(hero, hero.attackTarget))
						hero.aggroOn(a);
						else if(!hero.attackTarget.alive)hero.aggroOn(a);
					}
					if(a.getClass() == Hero.class){
						selectedUnit = a;
						hero.castSpell(hero.abilities.get(1));
					}
				}
			}
		}
	}
	private int getAbilityToLevel(){
		return levellingList[abilityPoint++];
	}
	private void initAbilityLevellingList(){
		levellingList = new int[12];
		levellingList[0] = 1;
		levellingList[1] = 0;
		levellingList[2] = 1;
		levellingList[3] = 0;
		levellingList[4] = 1;
		levellingList[5] = 3;
		levellingList[6] = 1;
		levellingList[7] = 0;
		levellingList[8] = 2;
		levellingList[9] = 0;
		levellingList[10] = 3;
		levellingList[11] = 2;
	}
	protected void respawn(){
		super.respawn();
		hero.setTarget(new Location(500, 1000));
		Head.out(String.valueOf(hero.getPermissions(Constants.movementAllowed)));
	}
}
