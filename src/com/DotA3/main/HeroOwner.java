package com.DotA3.main;

import com.DotA3.main.Alive.Faction;;

public class HeroOwner {
	Hero hero;
	Alive selectedUnit;
	int respawnTimer;
	public HeroOwner(Faction setFaction){
		hero = new Hero(50, 50, Hero.HeroType.NONE, setFaction);
		hero.owner = this;
		selectedUnit = hero;
		hero.heroType = Hero.HeroType.RAZOR;
		hero.changeStat(Constants.experience, (50 * (Math.pow(8, 2) + 8) - 100));
	}
	public void tick(){
		if(hero.alive)hero.tick();
		else if(respawnTimer-- == 0){
			respawn();
		}
	}
	public void drawMe(Camera camera){
		if(hero.alive)hero.drawMe(camera);
	}
	public void setRespawnTimer(int i) {
		respawnTimer = i * (int)Head.tickRate;
		
	}
	protected void respawn(){
		Alive.allAlives.add(hero);
		if(hero.faction == Alive.Faction.RADIANT)hero.setLocation(new Location(500, 750));
		else hero.setLocation(new Location(2500, 1000));
		hero.alive = true;
		hero.changeStat(Constants.health, hero.getStat(Constants.maxHealth));
		hero.changeStat(Constants.mana, hero.getStat(Constants.maxMana));
	}
}
