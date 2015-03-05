package com.DotA3.main;

public class Camera {
	Location centralPoint;
	Location dimensions;
	private final int speed = (int) (600 / Head.tickRate);
	private double additionalSpeed, acceleration;
	public Camera(Location setCentralPoint){
		centralPoint = setCentralPoint;
		dimensions = Head.getScreenDimensions();
		additionalSpeed = 0;
		acceleration = 1;
	}
	public Camera(Location setCentralPoint, Location setDimensions){
		
	}
	public boolean canSeeActor(Actor actor){
		if(actor.location.x /*- actor.width / 2*/ < centralPoint.x + dimensions.x / 2
		&& actor.location.x + actor.width / 2 > centralPoint.x - dimensions.x / 2
		&& actor.location.y - actor.height / 2 < centralPoint.y + dimensions.y / 2
		&& actor.location.y + actor.height / 2 > centralPoint.y - dimensions.y / 2)
			return true;
		return false;
			
	}
	public int getCameraSpeed(){
		return (int) (speed + (additionalSpeed += acceleration));
	}
	public void resetCameraSpeed(){
		additionalSpeed = 0;
	}
}
