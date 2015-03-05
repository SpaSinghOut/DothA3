package com.DotA3.main;

public enum MissileType {
	AUTO(2,2,0,300, Util.Color.GREEN, true, true, Ability.AbilityType.NONE),
	FIREBALL(3, 3, 5, 600, Util.Color.YELLOW, true, false, null),
	FREEZEBALL(9,9,3, 450, Util.Color.BLUE, true, false, null),
	 PLAZMAFIELD(15,15,0,210,Util.Color.GREEN, false, true, Ability.AbilityType.PLAZMAFIELD),
	GYROW(15,15,5,115,Util.Color.RED, false, true, Ability.AbilityType.CALLDOWN),
	TOWERSHOT(25,25,70,550, Util.Color.LIGHTBLUE, true, true, null),
	GYROCKET(35,35, 50, 100, Util.Color.ORANGE, true, true, Ability.AbilityType.GYROCKET ),
	SHACKLESHOT(3,3,50, 1000, Ability.AbilityType.SHACKLESHOT.color, true, true, Ability.AbilityType.SHACKLESHOT),
	POWERSHOT(4,4,50,850, Ability.AbilityType.POWERSHOT.color, false, true, Ability.AbilityType.POWERSHOT),
	FLAKCANNON(4,4,1,1200,Ability.AbilityType.FLAKCANNON.color, true, true, Ability.AbilityType.FLAKCANNON),
	;
	boolean homing;
	boolean penetrating;
	double damage;
	int width;
	int height;
	int speed;
	Util.Color color;
	Ability.AbilityType parentAbility;
	MissileType(int setWidth, int setHeight, double setDamage,int setMissileSpeed, 
			Util.Color setColor, boolean setHoming, boolean setPenetrating, Ability.AbilityType setParentAbility){
		damage = setDamage;
		width = setWidth;
		height = setHeight;
		speed = setMissileSpeed;
		color = setColor;
		homing = setHoming;
		parentAbility = setParentAbility;
		penetrating = setPenetrating;
	}
	public void onCollision(Missile parent, Alive collidedWith){
		if(parent.parent != null)
			collidedWith.getSpellAffected(parentAbility.owner, parent.parent);
		switch(this){
		case GYROCKET:
			new Buff(collidedWith, Buff.BuffName.STUN, (int)parent.damage);;
			break;
		case FREEZEBALL:
			collidedWith.changeBaseSpeed(-50);
			break;
		case SHACKLESHOT:
			int level = ((Hero)parent.parent).abilities.get(
					((Hero)parent.parent).
					abilities.indexOf(Ability.AbilityType.SHACKLESHOT) ).level;
			collidedWith.takeDamage(parent.parent, damage * level,
					Alive.DamageType.MAGICAL);
			new Buff(collidedWith, Buff.BuffName.STUN, level * Head.tickRate);
			parent.locChange.setX(parent.locChange.getX() / 10); parent.width *= 5;
			parent.locChange.setY(parent.locChange.getY() / 10); parent.height *= 5;
			place:for(int i = 0; i < 400; i++){
				parent.move();
				for(Alive a: Head.qt.getAlivesAroundLocation(parent.getLocation()))
					if(collidedWith != a && Util.checkForCollision(parent, a)){
						new Buff(a, Buff.BuffName.STUN, level * Head.tickRate);
						a.takeDamage(parent.parent, damage * level,
										Alive.DamageType.MAGICAL);
						break place;
					}
			}
			break;
		}
	}
}
