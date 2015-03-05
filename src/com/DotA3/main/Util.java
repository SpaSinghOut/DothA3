package com.DotA3.main;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Util {
	private static Texture texture;
	public static Location getLocationOnScreen(Actor actor, Camera camera){ 
		return new Location((Head.getScreenDimensions().getX() / 2) - (camera.centralPoint.getX() - actor.getLocation().getX()),
				(Head.getScreenDimensions().getY() / 2) - (camera.centralPoint.getY() - actor.getLocation().getY()));
	}
	public static Location getLocationOnScreen(Location location, Location camera) {
		Actor actor = new Actor(4,4,true);
		actor.setLocation(new Location(location));
		return new Location((Head.getScreenDimensions().getX() / 2) - (camera.getX() - actor.getLocation().getX()),
			(Head.getScreenDimensions().getY() / 2) - (camera.getY() - actor.getLocation().getY()));
	}
	public static boolean checkForCollision(Actor first, Actor second){
		if((first.location.getX() < (second.location.getX() + second.getWidth() / 2 + first.getWidth() / 2) &&
			first.location.getX() > (second.location.getX() - second.getWidth() / 2 - first.getWidth() / 2)) &&
			(first.location.getY() < (second.location.getY() + second.getHeight() / 2 + first.getHeight() / 2)&&
			first.location.getY() > (second.location.getY() - second.getHeight() / 2 - first.getHeight() / 2))){
			return true;
		}
		return false;
	}
	public static void drawActor(Actor actor, Camera fromCamera){
		if(!actor.isOnScreen(fromCamera))return;
		try {
			setTexture(actor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		GL11.glBegin(GL11.GL_QUADS);
		if(actor.color != null)GL11.glColor3f(actor.getRGB()[0], actor.getRGB()[1], actor.getRGB()[2]);
		GL11.glTexCoord2f(0,0);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
		GL11.glTexCoord2f(1,0);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
		GL11.glTexCoord2f(1,1);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
		GL11.glTexCoord2f(0,1);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
		GL11.glEnd();
	}
	public static boolean onMap(Actor actor){
		if(actor.getLocation().getX() < Head.getWrap().x && actor.getLocation().getX() > 0 && 
			actor.getLocation().getY() < Head.getWrap().y && actor.getLocation().getY() > 0)return true;
		return false;
	}
	public static void drawActor(Actor actor, Util.Color color, Camera fromCamera){
		Location camera = fromCamera.centralPoint;
		float[] RGB = getRGB(color);
		try {
			setTexture(actor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(actor.shape == Actor.Shape.QUAD){
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
		GL11.glTexCoord2f(0,0);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
		GL11.glTexCoord2f(1,0);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
		GL11.glTexCoord2f(1,1);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
		GL11.glTexCoord2f(0,1);
		GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
		GL11.glEnd();
		}
		else if(actor.shape == Actor.Shape.TRI){
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
			GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
			GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
			GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
		GL11.glEnd();
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	public static void drawActor(Actor actor, float[] RGB, Camera fromCamera){
		Location camera = fromCamera.centralPoint;
		if(actor.shape == Actor.Shape.QUAD){
			try {
				setTexture(actor);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			float textureWidth = actor.texture != null ? (float) (actor.texture.getWidth() ) : 1, 
					  textureHeight = actor.texture != null ? (float) (actor.texture.getHeight() ) : 1;
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
				GL11.glTexCoord2f(0,0);
				GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
				GL11.glTexCoord2f(textureWidth,0);
				GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
				GL11.glTexCoord2f(textureWidth,textureHeight);
				GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
				GL11.glTexCoord2f(0,textureHeight);
				GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
			GL11.glEnd();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			}
		else if(actor.shape == Actor.Shape.TRI){
			GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
				GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
				GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() + actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() - actor.getHeight() / 2);
				GL11.glVertex2d(Util.getLocationOnScreen(actor, fromCamera).getX() - actor.getWidth() / 2, Util.getLocationOnScreen(actor, fromCamera).getY() + actor.getHeight() / 2);
			GL11.glEnd();
		}
	}
	public static void drawOnScreen(Actor actor, Util.Color color, Location locationOnScreen){
		float[] RGB = getRGB(color);
		float textureWidth = actor.texture != null ? (float) (actor.texture.getWidth() ) : 1, 
				  textureHeight = actor.texture != null ? (float) (actor.texture.getHeight() ) : 1;
				  if(actor.shape == Actor.Shape.QUAD){
				try {
					setTexture(actor);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
				GL11.glTexCoord2f(0,0);
				GL11.glVertex2d(locationOnScreen.x - actor.getWidth() / 2,locationOnScreen.y - actor.getHeight() / 2);
				GL11.glTexCoord2f(textureWidth,0);
				GL11.glVertex2d(locationOnScreen.x + actor.getWidth() / 2, locationOnScreen.y - actor.getHeight() / 2);
				GL11.glTexCoord2f(textureWidth,textureHeight);
				GL11.glVertex2d(locationOnScreen.x + actor.getWidth() / 2, locationOnScreen.y + actor.getHeight() / 2);
				GL11.glTexCoord2f(0,textureHeight);
				GL11.glVertex2d(locationOnScreen.x - actor.getWidth() / 2, locationOnScreen.getY() + actor.getHeight() / 2);
				GL11.glEnd();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				
			}
			else if(actor.shape == Actor.Shape.TRI){
				GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glColor3f(RGB[0], RGB[1], RGB[2]);
				GL11.glVertex2d(locationOnScreen.getX() - actor.getWidth() / 2, locationOnScreen.getY() - actor.getHeight() / 2);
				GL11.glVertex2d(locationOnScreen.getX() + actor.getWidth() / 2, locationOnScreen.getY() - actor.getHeight() / 2);
				GL11.glVertex2d(locationOnScreen.getX() - actor.getWidth() / 2, locationOnScreen.getY() + actor.getHeight() / 2);
			GL11.glEnd();
			}
	}
	public enum Color{
		RED, GREEN, BLUE, YELLOW, PURPLE, PINK, BLACK,GRAY, WHITE, LIGHTBLUE, ORANGE,;
	}
	public static boolean everySecond(double secondRate){
		if(Head.tickCount % (Head.tickRate * secondRate) == 0)return true;
		return false;
	}
	public static double getInverse(boolean x, double a){
		if(x){
			return ((double) (Head.getScreenDimensions().getX()/2 + (Head.getScreenDimensions().getX()/2 - a)));
		}
		else {
			return ((double) (Head.getScreenDimensions().getY()/2 + (Head.getScreenDimensions().getY()/2 - a )));
		}
	}
	public static double getXDistance(Actor a, Actor b){
		if(a.getLocation().getX() > 
		b.
		getLocation().
		getX())
			return a.getLocation().getX()
					- b.getLocation().getX();
		else if(a.getLocation().getX() < b.getLocation().getX())return b.getLocation().getX() - a.getLocation().getX();
		return 0;
	}
	public static double getXDistance(Actor a, Location target) {
		if(a.getLocation().getX() > target.getX()){
			return a.getLocation().getX()
					- target.getX();
		}
		else if(a.getLocation().getX() < target.getX()){
			return target.getX() - a.getLocation().getX();
		}
		return 0;
	}
	public static double getYDistance(Actor a, Actor b){
		if(a.getLocation().getY() > b.getLocation().getY())return a.getLocation().getY() - b.getLocation().getY();
		else if(a.getLocation().getY() < b.getLocation().getY())return b.getLocation().getY() - a.getLocation().getY();
		return 0;
	}
	public static double getYDistance(Actor a, Location target) {
		if(a.getLocation().getY() > target.getY()){
			return a.getLocation().getY() - target.getY();
		}
		else if(a.getLocation().getY() < target.getY()){
			return target.y - a.getLocation().getY();
		}
		return 0;
	}
	public static double getCentralDistance(Actor a, Actor b){
		double xDist = getXDistance(a,b);
		double yDist = getYDistance(a,b);
		if(xDist > yDist)return xDist;
		else if(yDist > xDist)return yDist;
		return xDist;
	}
	public static double getRealCentralDistance(Actor a, Actor b){
		double xDist = getXDistance(a,b);
		double yDist = getYDistance(a,b);
		double distance = 0;
		distance = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
		return distance;
	}
	public static double getRealCentralDistance(Actor a, Location l){
		double xDist = getXDistance(a,l);
		double yDist = getYDistance(a,l);
		double distance = 0;
		distance = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
		return distance;
	}
	public static double getPeripheralDistance(Actor a, Actor b){
		double basicDistance = getCentralDistance(a,b);
		double minus = getXDistance(a,b) > getYDistance(a,b) ? a.getHeight() / 2 + b.getHeight() / 2 : a.getWidth() / 2 + b.getWidth() / 2;
		return basicDistance - minus;
	}
	public static double getPrecisePeripheralDistance(Actor a, Actor b){
		double centralDistance = getRealCentralDistance(a,b);
		double minus = getXDistance(a,b) > getYDistance(a,b) ? a.getHeight() / 2 + b.getHeight() / 2 : a.getWidth() / 2 + b.getWidth() / 2;
		return centralDistance - minus;
	}
	public static double getRealPeripheralDistance(Actor a, Actor b){
		double centralDistance = getRealCentralDistance(a,b);
		double xDist = getXDistance(a,b) > getYDistance(a,b) ? a.getWidth() / 2 + b.getWidth() / 2 :
			(a.getWidth() / 2) * getDistanceTangent(a,b) + (b.getWidth() / 2) * getDistanceTangent(a,b);
		double yDist = getYDistance(a,b) > getXDistance(a,b) ? a.getHeight() / 2 + b.getHeight() / 2 :
			(a.getHeight() / 2) * (1 / getDistanceTangent(a,b)) + (b.getHeight() / 2) * (1 / getDistanceTangent(a,b));
		return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
	}
	public static double getDistanceTangent(Actor a, Actor b){
		return getYDistance(a,b) / getXDistance(a,b);
	}
	public static boolean missileDeath(Missile a) {
		for(Actor b : Head.allActors){
			if(b != null && b != a && b != a.parent && b.solid && Util.checkForCollision(a, b)){
				if(b.getClass() == Alive.class)if(((Alive)b).getStat(Constants.health) < 0)return false;
				return true;
			}
		}
		return false;
	}
	
	public static Location inverseLocationOnScreen(Location locationOnScreen, Camera getCamera){
		Location camera = getCamera.centralPoint;
		return new Location(camera.getX() + (locationOnScreen.getX() - Head.getHalfScreenSize().getX()),
							camera.getY() - (locationOnScreen.getY() - Head.getHalfScreenSize().getY()));
	}
	public static java.awt.Color getAsJavaColor(Util.Color color){
		switch(color){
		case GREEN:
			return java.awt.Color.GREEN;
		case RED:
			return java.awt.Color.RED;
		case BLUE:
			return java.awt.Color.BLUE;
		case WHITE:
			return java.awt.Color.WHITE;
		case YELLOW:
			return java.awt.Color.YELLOW;
		case PINK:
			return java.awt.Color.PINK;
		case ORANGE:
			return java.awt.Color.ORANGE;
		}
		return java.awt.Color.BLACK;
	}
	public static void drawCircle(double setX, double setY, double setSize, Util.Color setColor){
		final int centerX = (int) setX, centerY = (int) setY, edges = 180, size = (int) setSize;
		Actor a = new Actor(0,0,false);
		Head.addToDeleteList(a);
		a.color = setColor;
		GL11.glColor3f(a.getRGB()[0], a.getRGB()[1], a.getRGB()[2]);
		for(int theta = 0; theta < edges; theta++){
			GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glVertex2d(centerX, centerY );
				GL11.glVertex2d((centerX + Math.cos(theta) * size), centerY + Math.sin(theta) * size);
				GL11.glVertex2d(centerX + Math.cos(theta + 1) * size, centerY + Math.sin(theta + 1) * size);
			GL11.glEnd();
		}
	}
	public static float[] getRGB(Util.Color color){
		float[] RGB = new float[3];
		switch(color){
		case GREEN:
			RGB[0] = 0;
			RGB[1] = 1;
			RGB[2] = 0;
			break;
		case RED:
			RGB[0] = 1;
			RGB[1] = 0;
			RGB[2] = 0;
			break;
		case WHITE:
			RGB[0] = 1.0f;
			RGB[1] = 1.0f;
			RGB[2] = 1.0f;
			break;
		case BLACK:
			RGB[0] = 0.0f;
			RGB[1] = 0.0f;
			RGB[2] = 0.0f;
			break;
		case BLUE:
			RGB[0] = 0.0f;
			RGB[1] = 0.0f;
			RGB[2] = 1.0f;
			break;
		case GRAY:
			RGB[0] = 0.8f;
			RGB[1] = 0.8f;
			RGB[2] = 0.8f;
			break;
		case PINK:
			RGB[0] = 2.0f;
			RGB[1] = 0.0f;
			RGB[2] = 1.0f;
			break;
		case PURPLE:
			RGB[0] = 0.5f;
			RGB[1] = 0.5f;
			RGB[2] = 1.0f;
			break;
		case LIGHTBLUE:
			RGB[0] = 0.2f;
			RGB[1] = 0.4f;
			RGB[2] = 0.8f;
			break;
		case YELLOW:
			RGB[0] = 1.0f;
			RGB[1] = 1.0f;
			RGB[2] = 0.0f;
			break;
		case ORANGE:
			RGB[0] = 1f;
			RGB[1] = .6f;
			RGB[2] = 0.2f;
			break;
		default:
			break;
		}
		return RGB;
	}
	public static boolean checkPointCollision(Actor a, Location l){
		if(l.getX() > a.getLocation().getX() - (a.getWidth() / 2) &&
			l.getX() < a.getLocation().getX() + (a.getWidth() / 2)&&
			l.getY() > a.getLocation().getY() - (a.getHeight() / 2)&&
			l.getY() < a.getLocation().getY() + (a.getHeight() / 2))return true;
		return false;
	}
	private static void setTexture(Actor actor) throws IOException{
		if(actor.texture == null)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		else {
			org.newdawn.slick.Color.white.bind();
			actor.texture.bind();
		}
	}
	public static void drawOnMap(Player player, Actor actor){
		Location savedLocation = new Location(actor.location.x, actor.location.y);							//save actor location
		actor.width /= Constants.mapMultiplicationFactor; actor.height /= Constants.mapMultiplicationFactor;//actor size decrease
		double xRatio = actor.location.x / Head.getWrap().x, yRatio = actor.location.y / Head.getWrap().y;
		actor.location.x = player.mapBackground.location.x + ( -0.5 + xRatio) * player.mapBackground.width;
		actor.location.y = player.mapBackground.location.y + ( -0.5 + yRatio) * player.mapBackground.height;
		drawOnScreen(actor, Color.WHITE, actor.location);
		actor.width *= Constants.mapMultiplicationFactor;actor.height *= Constants.mapMultiplicationFactor;//reverse actor size decrease
		actor.location.set(savedLocation.x, savedLocation.y);												//revert actor location
	}
}
