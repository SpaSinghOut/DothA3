package com.DotA3.main;

import java.util.ArrayList;


public class Quadtree<Number extends Comparable, Element> {
	private Node root;
	
	private class Node {
		Number x, y;
		Node northWest, northEast, southEast, southWest;
		Element element;
		
		Node(Number x, Number y, Element element) {
			this.x = x;
			this.y = y;
			this.element = element;
		}
	}
	
	//************************* insertion ******************************
	public void insert(Number x, Number y, Element element) {
		root = insert(root, x, y, element);
	}
	
	private Node insert(Node node, Number x, Number y, Element element) {
		if(node == null) return new Node(x, y, element);
		
		if(node.element == null) {
			node.element = element;
			return node;
		}
		
		boolean lessX = x.compareTo(node.x) < 0;
		boolean lessY = y.compareTo(node.y) < 0;
		
			 if( lessX && !lessY) node.northWest = insert(node.northWest, x, y, element);
		else if(!lessX && !lessY) node.northEast = insert(node.northEast, x, y, element);
		else if(!lessX &&  lessY) node.southEast = insert(node.southEast, x, y, element);
		else if( lessX &&  lessY) node.southWest = insert(node.southWest, x, y, element);
		return node;
	}
	
	//************************* retrieval ******************************
	public ArrayList<Element> retrieveBox(Number minX, Number minY, Number maxX, Number maxY) {
		ArrayList<Element> arrayList = new ArrayList<Element>();
		retrieveBox(root, minX, minY, maxX, maxY, arrayList);
		return arrayList;
	}
	
	private void retrieveBox(Node node, Number minX, Number minY, Number maxX, Number maxY, ArrayList<Element> arrayList) {
		if(node == null) return;
		
		boolean lessMinX = minX.compareTo(node.x) < 0;
		boolean lessMinY = minY.compareTo(node.y) < 0;
		boolean lessMaxX = maxX.compareTo(node.x) < 0;
		boolean lessMaxY = maxY.compareTo(node.y) < 0;
		
		if(lessMinX && lessMinY && !lessMaxX && !lessMaxY && node.element != null) arrayList.add(node.element);
		if( lessMinX && !lessMaxY) retrieveBox(node.northWest, minX, minY, maxX, maxY, arrayList);
		if(!lessMaxX && !lessMaxY) retrieveBox(node.northEast, minX, minY, maxX, maxY, arrayList);
		if(!lessMaxX &&  lessMinY) retrieveBox(node.southEast, minX, minY, maxX, maxY, arrayList);
		if( lessMinX &&  lessMinY) retrieveBox(node.southWest, minX, minY, maxX, maxY, arrayList);
	}
	
	//************************* deletion ******************************
	public void remove(Number x, Number y, Element element) {
		remove(root, x, y, element);
	}
	
	private void remove(Node node, Number x, Number y, Element element) {
		if(node == null)return;
		else if(node.element == element){
			node.element = null;
			return;
		}
		
		boolean lessX = x.compareTo(node.x) < 0;
		boolean lessY = y.compareTo(node.y) < 0;
		
			 if( lessX && !lessY) remove(node.northWest, x, y, element);
		else if(!lessX && !lessY) remove(node.northEast, x, y, element);
		else if(!lessX &&  lessY) remove(node.southEast, x, y, element);
		else if( lessX &&  lessY) remove(node.southWest, x, y, element);
	}
	public void clear(){
		root = null;
		if(Util.everySecond(1))Head.tracker.log("Inserting " + Head.allActors.size() + " actors");
		for(Actor a: Head.allActors)
			Head.qt.insert(a.getLocation().getX(), a.getLocation().getY(), a);
	}
	public ArrayList<Alive> getAlivesAroundMe(Alive alive, int area){
		ArrayList<Actor> box = Head.qt.retrieveBox(alive.getLocation().getX() - area, 
				alive.getLocation().getY() - area, alive.getLocation().getX() + area,alive.getLocation().getY() + area);
		//System.out.println(box.size());
		ArrayList<Alive> ala = new ArrayList<Alive>();
		for(Actor a : box)
			if(Alive.class.isAssignableFrom(a.getClass()))
				ala.add((Alive) a);
		return ala;
	}
	public ArrayList<Alive> getAlivesAroundLocation(Location location){
		ArrayList<Actor> ala = Head.qt.retrieveBox(location.x - 100, location.y - 100, location.x + 100, location.y + 100);
		ArrayList<Alive> all = new ArrayList<Alive>();
		for(Actor a:ala)if(Alive.class.isAssignableFrom(a.getClass()))all.add((Alive)a);
		return all;
	}
	//************************* unit test ******************************
	public static void unitTests() {
		System.out.println("***************************************************");
		System.out.println("****** Performing unit tests for: Quadtree ********");
		System.out.println("***************************************************");
		
		Quadtree<Integer, String> quadtree = new Quadtree<Integer, String>();
		
		System.out.println("Inserting element ( 0,  0, \"( 0,  0)\")");
		System.out.println("Inserting element ( 1,  1, \"( 1,  1)\")");
		System.out.println("Inserting element (-1, -1, \"(-1, -1)\")");
		System.out.println("Inserting element (10, 10, \"(10, 10)\")");
		quadtree.insert( 0,  0, "( 0,  0)");
		quadtree.insert( 1,  1, "( 1,  1)");
		quadtree.insert(-1, -1, "(-1, -1)");
		quadtree.insert(10, 10, "(10, 10)");
		
		System.out.println("Found elements over (-1, -1, 4, 4]:");
		for(String s: quadtree.retrieveBox(-1, -1, 4, 4))
			System.out.println(s);
		System.out.println("Found elements over (-11, -11, 11, 11]:");
		for(String s: quadtree.retrieveBox(-11, -11, 11, 11))
			System.out.println(s);
		
		System.out.println("Removing element ( 0,  0, \"( 0,  0)\")");
		System.out.println("Removing element (10, 10, \"(10, 10)\")");
		quadtree.remove( 0,  0, "( 0,  0)");
		quadtree.remove(10, 10, "(10, 10)");
		
		System.out.println("Found elements over (-11, -11, 11, 11]:");
		for(String s: quadtree.retrieveBox(-11, -11, 11, 11))
			System.out.println(s);
		
		System.out.println("***************************************************");
	}
}
