package com.DotA3.main;

import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Item {
	double[] stats;
	Preset preset;
	int cost;
	String itemName;
	Texture texture;
	public Item(double[] setStats){
		stats = setStats;
	}
	public enum Preset{
		IRONBRANCH,CIRCLET,GAUNTLETOFSTRENGTH, SLIPPERSOFAGILITY, MANTLEOFINTELLIGENCE,;
	}
	public Item(Preset setPreset){
		stats = new double[Constants.statsSize];
		preset = setPreset;
		switch(preset){
		case IRONBRANCH:
			cost = 50;
			itemName = "iron branch";
			stats[Constants.allAttributesBonus] = 1;
			break;
		case CIRCLET:
			cost = 200;
			itemName = "circlet";
			stats[Constants.allAttributesBonus] = 2;
			break;
		case GAUNTLETOFSTRENGTH:
			cost = 150;
			itemName = "gauntlet of strength";
			stats[Constants.bonusStrength] = 3;
			break;
		case SLIPPERSOFAGILITY:
			cost = 150;
			itemName = "slippers of agility";
			stats[Constants.bonusAgility] = 3;
			break;
		case MANTLEOFINTELLIGENCE:
			cost = 150;
			itemName = "mantle of intelligence";
			stats[Constants.bonusIntelligence] = 3;
			break;
		}
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/"+itemName+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
