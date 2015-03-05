package com.DotA3.main;

public class Location {
	double x;
	double y;
	public Location(){x=0;y=0;}
	public Location(double setX, double setY){
		x = setX;
		y = setY;
	}
	public Location(Location location){
		x = location.x;
		y = location.y;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public void setX(double newX){
		x = newX;
	}
	public void setY(double newY){
		y = newY;
	}
	public void changeX(double xChange){
		x += xChange;
	}
	public void changeY(double yChange){
		y += yChange;
	}
	public void set(Location location){
		x = location.x;
		y = location.y;
	}
	public void set(double newX, double newY){
		x = newX; y = newY;
	}
}
