package com.DotA3.main;

public class Effect extends Actor {
	int timeLeft;
	Actor owner;
	public Effect(double setWidth, double setHeight, boolean setSolid, int setTimeLeft, Actor setOwner) {
		super(setWidth, setHeight, setSolid);
		timeLeft = (int) (setTimeLeft * Head.tickRate);
		owner = setOwner;
	}
	public boolean tick(){
		if(timeLeft-- > 0){
			active = false;
			Head.addToDeleteList(this);
		}
		return super.tick(timeLeft > 0);
	}
}
