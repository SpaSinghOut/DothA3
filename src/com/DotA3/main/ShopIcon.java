package com.DotA3.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;

public class ShopIcon extends JMenuItem implements MouseListener{
	Item represents;
	Gui parent;
	public ShopIcon(Gui gui, String itemName){
		super(itemName);
		parent = gui;
		addMouseListener(this);
		for(Item.Preset ip: Item.Preset.values())if(ip.name() == itemName)represents = new Item(ip);
		setText(represents.itemName);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		if(parent.owner.hero.getStat(Constants.gold) > represents.cost){
			parent.owner.hero.inventory.addItem(1, represents);
			parent.owner.hero.changeStat(Constants.gold, -represents.cost);
		}
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
