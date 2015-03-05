package com.DotA3.main;

import java.util.ArrayList;

public class Map {
	private static final int obstacleWidth = 30;
	private Actor[] terrain = new Actor[(int) (Head.getWrap().x / 15)];
	int numberOfCreepsToSpawn;
	static Actor[] borders = new Actor[4];
	static Tower[] towers = new Tower[2];
	public Rune rune;
	public Map(){
		numberOfCreepsToSpawn = 6;
		//spawnCreeps();
		//this generates the unpathable red squares that make up the pseudo lane
		generateTerrain();
		generateBorders();
		runeInit();
		for(int i = 0; i < towers.length / 2; i++){
			towers[i] = new Tower(Alive.Faction.RADIANT);
			towers[i + 1] = new Tower(Alive.Faction.DIRE);
		}
	}
	private void generateTerrain(){
		for(int i = 0; i < Head.getWrap().x / 60; i++){
			terrain[i * 4] = new Actor(obstacleWidth,obstacleWidth,true);
			terrain[i * 4].setLocation(new Location((obstacleWidth * (i * 2 + 0.5)), 500));
			terrain[i * 4 + 1] = new Actor(obstacleWidth,obstacleWidth,true);
			terrain[i * 4 + 1].setLocation(new Location((obstacleWidth * (i * 2 + 1.5)), 500 + obstacleWidth));
			terrain[i * 4 + 2] = new Actor(obstacleWidth,obstacleWidth,true);
			terrain[i * 4 + 2].setLocation(new Location((obstacleWidth * (i * 2 + 0.5)), 1500));
			terrain[i * 4 + 3] = new Actor(obstacleWidth,obstacleWidth,true);
			terrain[i * 4 + 3].setLocation(new Location((obstacleWidth * (i  * 2+ 1.5)), 1500 + obstacleWidth));
			terrain[i * 4 + 0].immobile = true;
			terrain[i * 4 + 1].immobile = true;
			terrain[i * 4 + 2].immobile = true;
			terrain[i * 4 + 3].immobile = true;
		}
	}
	private void generateBorders(){
		borders[0] = new Actor(3,Head.getWrap().y, true);
		borders[1] = new Actor(3,Head.getWrap().y, true);
		borders[2] = new Actor(Head.getWrap().x, 3, true);
		borders[3] = new Actor(Head.getWrap().x, 3, true);
		borders[0].setLocation(new Location(0, (Head.getWrap().y / 2)));
		borders[1].setLocation(new Location(Head.getWrap().x, Head.getWrap().y/ 2));
		borders[2].setLocation(new Location(Head.getWrap().x / 2, 0));
		borders[3].setLocation(new Location(Head.getWrap().x / 2, Head.getWrap().y));
		borders[0].immobile = true;
		borders[1].immobile = true;
		borders[2].immobile = true;
		borders[3].immobile = true;
	}
	private void runeInit(){
		rune = new Rune(Rune.PowerType.HASTE);
		rune.setLocation(new Location(600,600));
	}
	public void drawMap(Camera camera){
		//draws the unpathable red squares
		for(Actor a: terrain){
			Util.drawActor(a, Util.Color.PURPLE, camera);
		}
		for(Creep creep: Creep.allCreeps){
			creep.drawMe(camera);
		}
		for(Actor border: borders){
			Util.drawActor(border, Util.Color.GREEN, camera);
		}
		for(Tower tower: towers)if(tower.active)tower.drawMe(tower.getRGB(), camera);
		if(rune.active)Util.drawActor(rune, camera);
	}
	public void tick(){
		if(Util.everySecond(30))
			spawnCreeps();
		ArrayList<Creep> creepList = Creep.allCreeps;
		for(Creep creep: creepList)if(!creep.tick())Head.addToDeleteList(creep);
		for(Tower tower: towers)if(!tower.tick());
		tickRune();
	}
	private void tickRune(){
		for(HeroOwner heroOwner: Head.controllers)
			if(rune.active && Util.checkForCollision(heroOwner.hero, rune))rune.use(heroOwner.hero);
		if(Util.everySecond(10)){
			double number = Math.random() * Constants.numberOfPowerUps;
			Head.allActors.remove(rune);
			if(number < 1)rune = new Rune(Rune.PowerType.NUKE);
			else if(number >= 1 && number < 2)rune = new Rune(Rune.PowerType.SPLITSHOT);
			else if(number >= 2 && number < 3)rune = new Rune(Rune.PowerType.EXTRALIFE);
			else if(number >= 3 && number < 4)rune = new Rune(Rune.PowerType.DOUBLEDAMAGE);
			else if(number >= 4 && number < 5)rune = new Rune(Rune.PowerType.HASTE);
			else if(number >= 5 && number < 6)rune = new Rune(Rune.PowerType.FRIDGE);
			rune.setLocation(new Location(((int)(Math.random() * Head.getWrap().x)), ((int)(Math.random() * Head.getWrap().y))));
		}
	}
	void spawnCreeps(){
		Creep[] creeps = new Creep[numberOfCreepsToSpawn * 2];
		for(int i = 0; i < numberOfCreepsToSpawn; i++){
			creeps [i] = new Creep(Creep.creepSize, Creep.creepSize, Alive.Faction.RADIANT);
			creeps [i + numberOfCreepsToSpawn] = new Creep(Creep.creepSize, Creep.creepSize, Alive.Faction.DIRE);
			creeps[i].setLocation(new Location(200, 600 + 800 / numberOfCreepsToSpawn * i));
			creeps[i + numberOfCreepsToSpawn].setLocation(new Location(Head.getWrap().x - 200, 600 + 800 / numberOfCreepsToSpawn * i));
		}
	}
}
