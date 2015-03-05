package com.DotA3.main;

import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Actor {
	double height;
	double depth;
	double width;
	//byte color;
	Texture texture;
	public Shape shape;
	protected Location target;
	private double baseSpeed;
	private double speedModifier;
	private double uniqueSpeedModifier;
	Location location;
	public boolean active;
	boolean AI;
	boolean needToMove;
	boolean childSetsOwnMovement;
	Util.Color color;
	boolean solid;
	boolean immobile;
	private static int pushOrder;
	public Util.Color defaultColor;
	protected MovementType movementType;
	ArrayList<Effect> effects = new ArrayList<Effect>();
	protected Alive homingTarget;
	private final double pathRotation = 18;
	int debugMoveCount;
	//the higher the following value is the slower everything in the world will be moving
	private static final double globalMoveSpeedModifier = 1.5;
	public Actor(double setWidth, double setHeight, boolean setSolid){
		debugMoveCount = 0;
		Head.allActors.add(this);
		width = setWidth;
		height = setHeight;
		active = true;
		if(shape == null)shape = Shape.QUAD;
		location = new Location(-1,-1);
		AI = true;		
		solid = setSolid;
		locChange = new Location(0,0);
		pushOrder = 0;
		movementType = MovementType.LOCATIONBASED;
		speedModifier = 1;
		uniqueSpeedModifier = 1;
		
	}
	protected void setTexture() throws IOException{
		if(this.getClass() == Hero.class){
			String heroNameString = ((Hero)this).heroType.toString().toLowerCase();
			if(heroNameString != "none")
				texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/" + heroNameString + ".jpg"));
		}
		else if(this.getClass() == Creep.class)
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/radiant creep.png"));
		else if(this.getClass() == Tower.class)
			if(((Tower)this).faction == Alive.Faction.RADIANT)
				texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/radiant tower.jpg"));
			else if(((Tower)this).faction == Alive.Faction.DIRE)
				texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/dire tower.png"));
	}
	public void setTexture(Texture setTexture){
		texture = setTexture;
	}
	protected enum MovementType{
		LOCATIONBASED, DIRECTIONBASED, HOMING;
	}

	public boolean isOnScreen(Camera fromCamera){
		double cx = fromCamera.centralPoint.x; double cy = fromCamera.centralPoint.y;
		double dx = fromCamera.dimensions.x; double dy = fromCamera.dimensions.y;
		if(location.x < cx + dx / 2 && location.x > cx - dx / 2 && location.y < cy + dy / 2 && location.y > cy - dy / 2)return true;
		return false;
	}
	protected boolean tick(boolean setActive){
		active = setActive;
		if(!active){
			return active;
		}
		if(!childSetsOwnMovement && target != null && !immobile){
			setMovement(target);
		}
		if((target != null && location.getX() == target.getX() && location.getY() == target.getY()) || target == null){
			needToMove = false;
		}
		if(needToMove && !immobile)
			switch(movementType){
			case LOCATIONBASED:
				if(target != null){
					moveToALocation();
				}
				break;
			case DIRECTIONBASED:
				moveInADirection();
				break;
			case HOMING:
				setMovement(homingTarget.getLocation());
				home();
				break;
			}
		//if(getClass() == Hero.class)((Player)((Hero)this).owner).gui.out(String.valueOf(debugMoveCount));
		if(debugMoveCount>2)
			System.out.println("test");
		debugMoveCount = 0;
		pushOrder = 0;
		
		//keepWithinBorder();
		return active;
	}
	protected void moveToALocation(){
		Location old = new Location(location);
		if(Util.getRealCentralDistance(this, target) < getTrueSpeed()){
			location = new Location(target);
			target = null;
			return;
		}
		else move();
		int maxRotation = 0;
		double angle;
		while(old.x == location.x && old.y == location.y && maxRotation ++ < pathRotation / 2){
			angle = locChange.y > 0 || (locChange.y == 0 && locChange.x > 0) ?
					Math.acos(locChange.x / getTrueSpeed()) : 2 * Math.PI - Math.acos(locChange.x / getTrueSpeed());
			setMovement(new Location(location.x + Math.cos(angle + Math.PI / pathRotation * maxRotation) * getTrueSpeed(),
					location.y + Math.sin(angle	+ Math.PI / pathRotation * maxRotation) * getTrueSpeed()));
			if(!anythingInTheWay(8))
				move();
			/*if(old.x == location.x && old.y == location.y){
				setMovement(new Location(location.x + Math.cos(2 * Math.PI - Math.PI / pathRotation * maxRotation) * getTrueSpeed(), 
				location.y + Math.sin(2 * Math.PI - Math.PI / pathRotation * maxRotation) * getTrueSpeed()));
				if(anythingInTheWay(8))continue;
				move();
			}*/
		}
		/*while(old.x == location.x && old.y == location.y && maxRotation -- > 0){
			setMovement(new Location(location.x + Math.cos(Math.PI / pathRotation) * getTrueSpeed(), location.y + Math.sin(Math.PI / pathRotation) * getTrueSpeed()));
			move();
		}*/
	}
	protected void moveInADirection(){
		move();
	}
	private void home(){
		move();
	}
	Location locChange;
	public void setMovement(Location setTarget){
		double xChange;
		double yChange;
		double hypotenuse = Math.sqrt(Math.pow((this.getLocation().getX() - setTarget.getX()),2) + Math.pow((this.getLocation().getY() - setTarget.getY()), 2)) ;
		xChange = -( getTrueSpeed() * (this.getLocation().getX() - setTarget.getX()) / hypotenuse);
		yChange = -( getTrueSpeed() * (this.getLocation().getY() - setTarget.getY()) / hypotenuse);
		locChange = new Location(xChange, yChange);
	}
	public void move(){
		if(immobile)return;
		debugMoveCount++;
		this.location.changeX(this.locChange.getX());
		this.location.changeY(this.locChange.getY());
		//the following should check for self being solid
		if(Alive.class.isAssignableFrom(getClass()) && anythingInTheWay()){
			this.location.changeX(-this.locChange.getX());
			this.location.changeY(-this.locChange.getY());
			debugMoveCount--;
		}
	}
	public void push(Actor whom, int order){
		if(pushOrder<3 && !whom.immobile)pushOrder++;else return;
		whom.locChange.setX(locChange.getX());
		whom.locChange.setY(locChange.getY());
		whom.move();
	}
	private boolean anythingInTheWay() {
		boolean selfCheck, collisionCheck;
		for(Actor a : Head.allActors){
			selfCheck = a!= this;
			collisionCheck = Util.checkForCollision(this, a);
			if(a.active && a.solid &&  a.location != target && selfCheck && collisionCheck 
			/*|| (Util.checkPointCollision(a, new Location(location.x + 2 * locChange.x, location.y + 2 * locChange.y)) && 
			Util.getXDistance(this, target) > locChange.x * 3 && Util.getYDistance(this, target) > locChange.y * 3)*/){
				//push(a, pushOrder);
				return true;
			}
		}
		return false;
	}
	private boolean anythingInTheWay(double distanceCheck){
		for(Actor a : Head.allActors){
			if(a.active && a.solid && a!= this && a.location != target)
				for(int i = 0; i < distanceCheck; i++)if(Util.checkForCollision(this, a) || 
			(Util.checkPointCollision(a, new Location(location.x + i * locChange.x, location.y + i * locChange.y)) 
			//&& Util.getXDistance(this, target) > locChange.x * 3 && Util.getYDistance(this, target) > locChange.y * 3
			)){
				//System.out.println(i);;
				return true;
			}
		}
		return false;
	}
	public double getWidth(){
		return width;
	}
	public double getHeight(){
		return height;
	}
	public Location getLocation(){
		return location;
	}
	public void setLocation(Location setLocation){
		location = setLocation;
	}
	public void changeBaseSpeed(double d){
		baseSpeed += d;
	}
	public void addToSpeedModifier(double d){
		speedModifier += d;
	}
	public void setUniqueSpeedModifier(double d){
		if(d != 0 && uniqueSpeedModifier != 1);
		else if(d != 0 && uniqueSpeedModifier == 1)uniqueSpeedModifier = 1 + d;
		else if(d == 0)uniqueSpeedModifier = 1;
	}
	public double getSpeed(){
		double speed = baseSpeed * speedModifier * uniqueSpeedModifier;
		if(speed <= 522 && speed >= 10);
		else if(speed > 522)speed = 522;
		else if(speed < 10)speed = 10;
		return speed;
	}
	protected double getTrueSpeed(){
		double speed = getSpeed() / Head.tickRate / globalMoveSpeedModifier;
		return speed;
	}
	public void setTarget(Location setTarget){
		target = setTarget;
	}
	public void setNewTarget(Location setTarget){
		needToMove = true;
		setTarget(setTarget);
	}
	public void setWidth(double stats){
		width = stats;
	}
	public enum Shape{
		QUAD, TRI,;
	}
	protected boolean reachedTarget(){
		if(this.getLocation().getX() < target.getX() + this.getWidth() / 2
		&& this.getLocation().getX() > target.getX() - this.getWidth() / 2
		&& this.getLocation().getY() < target.getY() + this.getHeight() / 2
		&& this.getLocation().getY() > target.getY() - this.getHeight() / 2)
			return true;
		return false;
	}
	protected float[] getRGB(){
		return Util.getRGB(color);
	}
	public Texture getTexture(){
		Texture rTexture = texture;
		try {
			rTexture = texture == null ? TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/black.jpg")) : texture;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rTexture;
	}
}
