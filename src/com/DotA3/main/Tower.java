package com.DotA3.main;

import java.io.IOException;

public class Tower extends Alive {
	private static final int towerHP = 1600;
	private static final int towerSize = 85;
	public Tower(Faction setFaction) {
		super(towerSize, towerSize, setFaction);
		changePermissions(Constants.movementAllowed, false);
		if(setFaction == Alive.Faction.RADIANT){
			this.setLocation(new Location(100, 1000));
			//color = Util.Color.GREEN;
			color = Util.Color.WHITE;
			changeStat(Constants.maxHealth, towerHP);
			changeStat(Constants.health, towerHP);
		}
		else if(setFaction == Alive.Faction.DIRE){
			this.setLocation(new Location(Head.getWrap().x - 100, 1000));
			//color = Util.Color.RED;
			color = Util.Color.WHITE;
			changeStat(Constants.maxHealth, towerHP);
			changeStat(Constants.health, towerHP);
		}
		changeStat(Constants.visibilityRange, 700);
		changeStat(Constants.attackRange, 700);
		immobile = true;
		changeStat(Constants.startingDamage, 70);
		changeStat(Constants.baseAnimationTime, 1);
		changeStat(Constants.baseAttackTime, 1);
		changeStat(Constants.attackSpeed, 100);
		this.missile = true;
		missileType = MissileType.TOWERSHOT;
		changePermissions(Constants.autoAttackAllowed, true);
		try {
			this.setTexture();
		} catch (IOException e) {
			e.printStackTrace();
		}
		noRetraction = true;
	}
	public boolean tick(){
		if(!super.tick())return false;
		if(attackTarget != null)
			if(!attackTarget.active || 
			Util.getRealCentralDistance(this, attackTarget) > getStat(Constants.visibilityRange))
				attackTarget = null;
		if(attackTarget == null || !attackTarget.active || !attackTarget.alive)findAttackTarget();
		return active;
	}
	private void findAttackTarget(){
		Alive potentialAttackTarget = null;
		for(Alive a: Alive.allAlives){
			if(a.faction != this.faction && getStat(Constants.visibilityRange) > Util.getRealCentralDistance(a, this)){
				if(potentialAttackTarget == null)potentialAttackTarget = a;
				else{
					if(Util.getRealCentralDistance(potentialAttackTarget, this)>
					Util.getRealCentralDistance(a, this))
						potentialAttackTarget = a;
				}
			}
		}
		aggroOn(potentialAttackTarget);
	}
	
}
