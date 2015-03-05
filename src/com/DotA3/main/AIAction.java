package com.DotA3.main;

import java.util.ArrayList;

public class AIAction {
	AI owner;
	ActionName actionName;
	int[] parameters;
	boolean actionStarted;
	public AIAction(AI setOwner, ActionName setActionName, int[] setParameters){
		owner = setOwner;
		actionName = setActionName;
		parameters = setParameters;
		actionStarted = false;
	}
	enum ActionName{
		MOVETO, MOVEBY, FINDCREEPS,WATCH, LASTHIT,;
	}
	public void tick(){
		if(!actionStarted)start();
		if(!isComplete()){
			doAction();
		}
		else owner.actionQueue.completeAction(this);
	}
	private void start(){
		actionStarted = true;
		switch(actionName){
		case MOVETO:
			owner.hero.setTarget(new Location(parameters[0], parameters[1]));
			break;
		case MOVEBY:
			parameters[0] += owner.hero.location.x;parameters[1] += owner.hero.location.y;
			owner.hero.setTarget(new Location(parameters[0], parameters[1]));
			break;
		case FINDCREEPS:
			if(isComplete())return;
			int[] location = { 2500, 1000 };
			owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner, ActionName.MOVETO, location));
			Head.out("starting to look for creeps");
			break;
		case WATCH:
			Head.out("starting to watch");break;
		case LASTHIT:
			owner.hero.aggroOn(Creep.allCreeps.get(parameters[0]));
			break;
		}
	}
	private void doAction(){
		switch(actionName){
		case MOVETO:
			break;
		case FINDCREEPS:
			int[] location = {-100,0};
			owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner,ActionName.MOVEBY, location));
			break;
		case WATCH:
			ArrayList<Alive> alives = Head.qt.getAlivesAroundMe(owner.hero, (int)owner.hero.getStat(Constants.visibilityRange));
			int creepCount = 0;
			for(Alive c: Head.qt.getAlivesAroundMe(owner.hero, (int)owner.hero.getStat(Constants.visibilityRange))){
				if(c.getClass() == Hero.class && c.faction != owner.hero.faction){
					owner.selectedUnit = c;
					owner.hero.castSpell(owner.hero.abilities.get(1));
					owner.hero.aggroOn(c);
					return;
				}
				else if(c.getClass() == Creep.class)
					if(c.faction != owner.hero.faction){//there is no need to check for visibility here because using the quadtree ensured visibility
						creepCount++;
						if(c.getRatio("health") < .5){
							int[] indexOfCreep = {Creep.allCreeps.indexOf(c)};
							owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner, ActionName.LASTHIT, indexOfCreep));
							this.actionStarted = false;
							//Head.out(actionName + " is delayed");
							return;
						}
					}
				if(creepCount == 0){
					//Head.out("inserting a find creeps action");
					owner.actionQueue.insert(owner.actionQueue.indexOf(this), new AIAction(owner, ActionName.FINDCREEPS, new int[0]));
				}
			}
			break;
			default:break;
		}
	}
	private boolean isComplete(){
		boolean completion = false;
		switch(actionName){
		case MOVETO:
			if(owner.hero.getLocation().x == parameters[0] && owner.hero.getLocation().y == parameters[1]){
				completion = true;
			}
			break;
		case MOVEBY:
			if(owner.hero.getLocation().x == parameters[0] && owner.hero.getLocation().y == parameters[1]){
				completion = true;
			}
			break;
		case FINDCREEPS:
			int creepCount = 0;
			ArrayList<Alive> alives = Head.qt.getAlivesAroundMe(owner.hero, (int)owner.hero.getStat(Constants.visibilityRange));
			for(Alive a: alives)if(a.active && a.alive && a.getClass() == Creep.class && a.faction != owner.hero.faction)creepCount++;
			if(creepCount >= 1)completion = true;
			break;
		case WATCH:
			return false;
		case LASTHIT:
			/*there has to be a distance check here even though alive should deaggro if the attackTarget is not visible
			 * because despite the deaggro the last hit action will not consider itself finished unless it sees for itself 
			 * that the attack target is too far away
			 */
			Alive attackTarget = owner.hero.attackTarget;
			if(attackTarget == null || !attackTarget.alive || 
			Util.getRealCentralDistance(owner.hero, owner.hero.attackTarget) > owner.hero.getStat(Constants.visibilityRange)){
				owner.hero.aggroOn(null);
				completion = true;
			}
		default:break;
		}
		//Head.out(actionName + " completion is: " + completion);
		return completion;
	}
}
