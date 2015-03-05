package com.DotA3.main;

import java.util.ArrayList;

public class AOE extends Actor {
	private final int pointsPerLine = 5;
	private final int pointDistance = 5;
	final double precision = .2;
	private final int pingPointSize = 5;
	Hero parent;
	ArrayList<Actor> pingRegionPoints = new ArrayList<Actor>();
	public AOE(Hero setParent, Location setLocation, int width, int height){
		super(width, height, false);
		color = Util.Color.YELLOW;
		shape = Actor.Shape.QUAD;
		/*					IN THE FOLLOWING
		 * parentLocation is the location of the actor that is creating the AOE
		 * setLocation is the target location for the creation of the AOE
		 * the goal is to get the degree at which the actor is casting the spell
		 * the actor's location during the cast, or as it is here "parentLocation" 
		 * is considered to be the origin, the x and y variable are the respective
		 * distances of the cast point the origin
		 * theta is the degree of cast
		 * precision constant determines the thickness of the to be created ping region
		 * it is a decimal between 0 and 1 and the higher it is the more precision the
		 * ping region will have
		 */
		parent = setParent;
		Location parentLocation = parent.getLocation();
		location = setLocation;
		double x = setLocation.getX() - parentLocation.getX();
		double y = setLocation.getY() - parentLocation.getY();
		double hypotenuse = Math.abs(Math.sqrt(Math.pow(x,2) + Math.pow(y, 2)));
		double theta = Math.abs(180 / Math.asin(y/hypotenuse) / Math.PI);
		//if(x < 0)
			if(y < 0)theta += 180;else if(y > 0)theta += 90;else theta = 180;
		//if(x > 0 && y < 0)theta += 270;
		//System.out.println(x + "," + y);
		//System.out.println(y/hypotenuse + " " + 1/ Math.sin(y/hypotenuse) + " " + theta);
		/*
		 * 						CIRCULAR PING REGION CREATION LOOP
		 * i is the number of outward steps taken
		 * j is the angle
		 */
		/*for(int i = 1; i < pointsPerLine; i++)
			for(double j = 0; j < 360; j += 1/precision){
				Actor a = new Actor(pingPointSize, pingPointSize, false);
				pingRegionPoints.add(a);
				a.color = color;
				a.shape = shape;
				a.setLocation(new Location(
				setLocation.getX() + Math.cos(j / 180 * Math.PI) * i * pointDistance, 
				setLocation.getY() + Math.sin(j / 180 * Math.PI) * i * pointDistance ));
				System.out.println(setLocation.getX() + " " + 
				Math.cos(j / 180 * Math.PI) + " " +  i * pointDistance);
			}*/
		/*for(int i = 1; i < pointsPerLine; i++)
			for(double rotation = 0; rotation <= 360; rotation += 1/precision){
				Actor a = new Actor(pingPointSize, pingPointSize, false);
				pingRegionPoints.add(a);
				a.color = color;
				a.shape = shape;
				a.setLocation(new Location(
				setLocation.getX() + Math.cos((rotation) / 180 * Math.PI) * i * pointDistance
				* Math.pow(Math.sqrt(2) , Math.abs(Math.cos((rotation + theta) / 90 * Math.PI))), 
				setLocation.getY() + Math.sin((rotation)/ 180 * Math.PI) * i * pointDistance 
				* Math.pow(Math.sqrt(2) , Math.abs(Math.sin((rotation - 45 + theta) / 90 * Math.PI)))));
				System.out.println(Math.cos(rotation / 180 * Math.PI));
			}*/
		for(int i = 1; i < pointsPerLine; i++)
			for(double rotation = 0; rotation <= 360; rotation += 1/precision){
				Actor a = new Actor(pingPointSize, pingPointSize, false);
				pingRegionPoints.add(a);
				a.color = color;
				a.shape = shape;
				double r = i * pointDistance;
				double rotation90 = Math.abs(rotation % 90 != 0 ? rotation % 90 : 90);
				//double rotation180 = Math.abs(rotation % 180 != 0 ? rotation % 180 : 180);
				double xMod = rotation90 < 45 ? 
						1 : Math.cos(rotation);
				double yMod = rotation90 > 45 ? 
						1 : Math.sin(rotation);
				if(rotation > 180)yMod = -yMod;
				if(rotation > 90 && rotation < 270)xMod = -xMod;
				a.setLocation(new Location(
				setLocation.getX() + r * xMod,
				setLocation.getY() + r * yMod));
				//System.out.println(cosine);
			}
	}
	public void tick(){
		
	}
	public void drawMe(){
		for(Actor a: pingRegionPoints)
			Util.drawActor(a, ((Player)parent.owner).camera);
	}
}
