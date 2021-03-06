package com.DotA3.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Tracker {
	int tickRate = Head.tickRate;
	private static final int numberOfTrackedEntities = 13;
	boolean[] trackedEntities = new boolean[numberOfTrackedEntities];
	Location[] recordedTimes = new Location[numberOfTrackedEntities];
	Location[] entityStats = new Location[numberOfTrackedEntities];
	String[] entityNames = new String[numberOfTrackedEntities];
	//Location[] lifespanTimes = new Location[numberOfTrackedEntities];
	Location[] lifespanStats = new Location[numberOfTrackedEntities];
	File file = new File("log.txt");
	BufferedWriter writer;
	int ticksTracked;
	private int secondTracked;
	public static final int ALG_UNIT_SELECTION = 0, FUNC_TICK = 1, FUNC_RENDER = 2, FUNC_QUADTREE_RESET = 3, FUNC_HERO_OWNER_TICK = 4,
	FUNC_MAP_TICK = 5, FUNC_MISSILE_TICK = 6, FUNC_AURA_TICK = 7, FUNC_ACTOR_DELETION = 8, REND_MISSILE = 9, REND_MAP = 10, REND_PLAYER = 11, 
	REND_HEROES = 12;
	private static final int writerDeathTime = 10;
	public enum TrackerPreset{
		PRESET_RUN, PRESET_TICK, PRESET_RENDER,;
	}
	public void initialize() throws IOException{
		writer = new BufferedWriter(new FileWriter(file));
		ticksTracked = 0;
		secondTracked = 0;
		initEntityNames();
		for(int i = 0; i < numberOfTrackedEntities; i++){
			if(trackedEntities[i]){
				recordedTimes[i] = new Location(0,0);
				entityStats[i] = new Location(0,0);
				lifespanStats[i] = new Location(0,0);
			}
		}
	}
	public void initialize(TrackerPreset trackerPreset) throws IOException{
		switch(trackerPreset){
		case PRESET_RUN:
			setEntityTracked(FUNC_TICK, true);
			setEntityTracked(FUNC_RENDER, true);
			break;
		case PRESET_TICK:
			setEntityTracked(FUNC_QUADTREE_RESET, true);
			setEntityTracked(FUNC_HERO_OWNER_TICK, true);
			setEntityTracked(FUNC_MAP_TICK, true);
			setEntityTracked(FUNC_MISSILE_TICK, true);
			setEntityTracked(FUNC_AURA_TICK, true);
			setEntityTracked(FUNC_ACTOR_DELETION, true);
			break;
		case PRESET_RENDER:
			setEntityTracked(REND_MISSILE, true);
			setEntityTracked(REND_MAP, true);
			setEntityTracked(REND_PLAYER, true);
			setEntityTracked(REND_HEROES, true);
			break;
		}
		initialize();
	}
	private void initEntityNames(){
		entityNames[0] = "Unit Selection"; entityNames[1] = "Tick"; entityNames[2] = "Render"; entityNames[3] = "Quadtree reset";
		entityNames[4] = "Hero owner tick"; entityNames[5] = "Map Tick";
	}
	public void tick() throws IOException{
		if(Util.everySecond(15)){
			for(int i = 0; i < numberOfTrackedEntities; i++){
				if(trackedEntities[i])
					//displayEntityStats(i);
					displayLifespanStats(i);
			}
			logLifespanStats();
		}
		if(Util.everySecond(5))logLifespanStats();
		for(int i = 0; i < numberOfTrackedEntities; i++){
			if(!trackedEntities[i])continue;
			lifespanStats[i].x += entityStats[i].x;
			lifespanStats[i].y = lifespanStats[i].x * tickRate * 100 / ++ticksTracked;
			entityStats[i] = new Location(0,0);
		}
		if(trackedEntities[Tracker.FUNC_TICK])log();
	}
	public void setEntityTracked(int entityIdentity, boolean toTrackOrNotToTrack){
		trackedEntities[entityIdentity] = toTrackOrNotToTrack;
	}
	public void giveStartTime(int entityIdentity){
		if(trackedEntities[entityIdentity])
		recordedTimes[entityIdentity].x = System.nanoTime();
	}
	public void giveEndTime(int entityIdentity){
		if(!trackedEntities[entityIdentity])return;
		recordedTimes[entityIdentity].y = System.nanoTime();
		calculateEntityStats(entityIdentity);
	}
	private void calculateEntityStats(int entityIdentity){
		entityStats[entityIdentity].x += (recordedTimes[entityIdentity].y - recordedTimes[entityIdentity].x) / (1000 * 1000 * 1000);
		entityStats[entityIdentity].y = entityStats[entityIdentity].x * tickRate * 100;
	}
	public void displayEntityStats(int entityIdentity){
		System.out.print("Entity ");
		System.out.print(entityNames[entityIdentity] != null ? entityNames[entityIdentity] : ("number: "+ entityIdentity)); 
		System.out.println(" took " + entityStats[entityIdentity].x + " seconds\n" + "which is "
		+ entityStats[entityIdentity].y + "% of the time appropriated for a single tick");
	}
	public void displayLifespanStats(int entityIdentity){
		System.out.print("Entity ");
		System.out.print(entityNames[entityIdentity] != null ? entityNames[entityIdentity] : ("number: "+ entityIdentity)); 
		System.out.println(" took " + String.format("%.9f", (lifespanStats[entityIdentity].x / ticksTracked)) + " seconds\n" + "which is "
		+ String.format("%.2f", lifespanStats[entityIdentity].y) + "% of the time appropriated for a single tick");
	}
	private void log() throws IOException{
		if(Util.everySecond(1)){
			writer.write("At Second number " + ++secondTracked + ", " + lifespanStats[FUNC_TICK].x + " seconds were used for the tick function");
			writer.newLine();
		}
	}
	public void closeWriter(){
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void log(String string){
		try {
			writer.write(string);
			writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void logLifespanStats(){
		for(int i = 0; i < numberOfTrackedEntities; i++)if(this.trackedEntities[i]){
		log("Entity " + String.valueOf(entityNames[i] != null ? entityNames[i] : ("number: "+ i)) + " took " + String.format("%.9f", (lifespanStats[i].x / ticksTracked)) + " seconds");
		log("which is " + String.format("%.2f", lifespanStats[i].y) + "% of the time appropriated for a single tick");
		}
	}
}
