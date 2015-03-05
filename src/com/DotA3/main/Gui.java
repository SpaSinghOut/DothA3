package com.DotA3.main;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Font;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Gui extends JFrame implements KeyListener{
	Canvas canvas;
	BufferedImage image;
	Image slickImage;
	Texture texture;
	final int screenX = (int) Head.getScreenDimensions().x;
	final int screenY = (int) Head.getScreenDimensions().y;
	JLabel HUD_component_level, HUD_component_health_number, HUD_component_health_bar,
	HUD_component_damage,HUD_component_strength,HUD_component_agility,HUD_component_intelligence,
	HUD_component_mana_bar, HUD_component_mana_number, HUD_component_move_speed, 
	HUD_component_attack_speed, HUD_component_armor, HUD_component_gold, HUD_component_clock, bcg;
	private JPanel inventory;
	LevelUpButton levelUpButton;
	ArrayList<BuffIcon> buffs = new ArrayList<BuffIcon>();
	JTextArea description, rsBox;JScrollPane scrollPane;
	StatsButton stats = new StatsButton(this);
	ArrayList<AbilityButton> abilityButtons = new ArrayList<AbilityButton>();
	Player owner;
	static int heroChoiceMenuBarOrdinal = 0;
	static final int healthBarMaxWidth = (int) (Head.getScreenDimensions().x / 3);
	static final int healthBarHeight = (int) (Head.getScreenDimensions().y / 30);
	StateChangingButton abilityPoints = new StateChangingButton(this);
	Console console;
	JMenuBar menuBar;
	JMenuBar itemBar = new JMenuBar();
	public Gui(Player setOwner){
		setUndecorated(false);
		setVisible(true);
		owner = setOwner;
		canvas = new Canvas();
		this.setBounds(0, 0, (int)Head.getScreenDimensions().getX(), (int)Head.getScreenDimensions().getY());
		getContentPane().add(canvas, 0);
		this.getLayeredPane().add(this.getContentPane(),0);
		initHUD();
		heroChoiceMenuBarOrdinal = getLayeredPane().getComponentCount();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		initItemBar();
	}
	private void initItemBar(){
		itemBar.setLocation((int)(screenX * 0.85), (int)(screenY * 0.01));
		itemBar.setSize((int)(screenX * .15), (int)(screenY * 0.032));
		JMenu menu = new JMenu("base items");
		for(Item.Preset ip: Item.Preset.values()){
			menu.add(new ShopIcon(this, ip.name()));
		}
		itemBar.add(menu);
		itemBar.setVisible(true);
		getLayeredPane().add(itemBar);
	}
	public void initAbilityButtons(){
		for(Ability a: owner.hero.abilities){
			AbilityButton ab = new AbilityButton(a, this);
			ab.setOwner(this);
			abilityButtons.add(ab);
			getLayeredPane().add(ab, (12 + owner.hero.abilities.indexOf(a)));
		}
	}
	private void initBcg(){
		bcg = new JLabel();
		getLayeredPane().add(bcg);
		bcg.addKeyListener(this);
	}
	private void initHUD() {
		HUD_component_level = new JLabel(((Integer)(1)).toString());
		HUD_component_level.setLocation((int)(screenX * .236),(int)(screenY * .939));
		HUD_component_level.setSize((int)(screenX * 0.065), (int)(screenY * 0.021));
		HUD_component_level.setVisible(true);
		HUD_component_level.setBackground(java.awt.Color.BLUE);
		HUD_component_level.setForeground(java.awt.Color.YELLOW);
		HUD_component_level.setOpaque(true); 
		HUD_component_health_number = new JLabel("0");
		HUD_component_health_number.setLocation((int)(Head.getScreenDimensions().getX() / 3),
				(int)(Head.getScreenDimensions().getY() * .735));
		HUD_component_health_number.setSize(healthBarMaxWidth, healthBarHeight);
		HUD_component_health_number.setOpaque(false);
		HUD_component_health_number.setForeground(java.awt.Color.BLACK);
		HUD_component_health_bar = new JLabel();
		HUD_component_health_bar.setLocation(HUD_component_health_number.getLocation());
		HUD_component_health_bar.setSize(HUD_component_health_number.getSize());
		HUD_component_health_bar.setBackground(java.awt.Color.RED);
		HUD_component_health_bar.setOpaque(true);
		HUD_component_mana_number = new JLabel("0");
		HUD_component_mana_number.setLocation((int)(Head.getScreenDimensions().getX() / 3),
				(int)(Head.getScreenDimensions().getY() * .777));
		HUD_component_mana_number.setSize(healthBarMaxWidth, healthBarHeight);
		HUD_component_mana_number.setOpaque(false);
		HUD_component_mana_number.setForeground(java.awt.Color.BLACK);
		HUD_component_mana_bar = new JLabel();
		HUD_component_mana_bar.setLocation(HUD_component_mana_number.getLocation());
		HUD_component_mana_bar.setSize(HUD_component_mana_number.getSize());
		HUD_component_mana_bar.setBackground(java.awt.Color.BLUE);
		HUD_component_mana_bar.setOpaque(true);
		rsBox = new JTextArea("activity");
		rsBox.setLocation(0,0);
		rsBox.setSize((int)(screenX * .258), (int)(screenY * .469));
		rsBox.setVisible(true);
		rsBox.setEditable(false);rsBox.setLineWrap(true);
		scrollPane = new JScrollPane(rsBox);
		scrollPane.setLocation((int)(screenX * .673), (int) (screenY * .737));
		scrollPane.setSize((int)(screenX * .327), (int)(screenY * .219));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		HUD_component_damage = new JLabel(((Integer)(int)owner.hero.getStat(Constants.damage)).toString());
		HUD_component_damage.setLocation((int)(screenX * .27), (int)(screenY * .766));
		HUD_component_damage.setSize((int)(screenX * .042), (int)(screenY * 0.023));
		HUD_component_damage.setBackground(java.awt.Color.WHITE);
		HUD_component_damage.setForeground(java.awt.Color.BLACK);
		HUD_component_armor = new JLabel();
		HUD_component_armor.setLocation((int)(screenX * .27), (int)(screenY * .7425));
		HUD_component_armor.setSize((int)(screenX * .042), (int)(screenY * 0.023));
		HUD_component_attack_speed = new JLabel(((Integer)(int)owner.hero.getStat(Constants.attackSpeed)).toString());
		HUD_component_attack_speed.setLocation((int)(Head.getScreenDimensions().getX() * .27),
				(int)(Head.getScreenDimensions().getY() * .72));
		HUD_component_attack_speed.setSize((int)(screenX * .042), (int)(screenY * 0.023));
		HUD_component_attack_speed.setBackground(java.awt.Color.WHITE);
		HUD_component_attack_speed.setForeground(java.awt.Color.BLACK);
		HUD_component_strength = new JLabel(((Integer)(int)owner.hero.getStat(Constants.strength)).toString());
		HUD_component_strength.setLocation((int)(Head.getScreenDimensions().getX() * .27),
				(int)(Head.getScreenDimensions().getY() * .79));
		HUD_component_strength.setSize(80,25);
		HUD_component_strength.setBackground(java.awt.Color.WHITE);
		HUD_component_strength.setForeground(java.awt.Color.RED);
		HUD_component_agility = new JLabel(((Integer)(int)owner.hero.getStat(Constants.agility)).toString());
		HUD_component_agility.setLocation((int)(Head.getScreenDimensions().getX() * .27),
				(int)(Head.getScreenDimensions().getY() * .825));
		HUD_component_agility.setSize(80,25);
		HUD_component_agility.setBackground(java.awt.Color.WHITE);
		HUD_component_agility.setForeground(java.awt.Color.GREEN);
		HUD_component_intelligence = new JLabel(((Integer)(int)owner.hero.getStat(Constants.intelligence)).toString());
		HUD_component_intelligence.setLocation((int)(Head.getScreenDimensions().getX() * .27),
				(int)(Head.getScreenDimensions().getY() * .862));
		HUD_component_intelligence.setSize(80,25);
		HUD_component_intelligence.setBackground(java.awt.Color.WHITE);
		HUD_component_intelligence.setForeground(java.awt.Color.BLUE);
		HUD_component_move_speed = new JLabel(((Integer)(1)).toString());
		HUD_component_move_speed.setLocation((int)(screenX * .27),(int)(screenY * .9));
		HUD_component_move_speed .setSize(80, 25);
		HUD_component_move_speed .setVisible(true);
		HUD_component_move_speed .setBackground(java.awt.Color.YELLOW);
		HUD_component_move_speed .setForeground(java.awt.Color.BLACK);
		HUD_component_gold = new JLabel();
		HUD_component_gold.setLocation((int)(screenX * .87), (int)(screenY * .712));
		HUD_component_gold.setSize((int)(screenX * .044), (int)(screenY * 0.028));
		HUD_component_gold.setForeground(java.awt.Color.YELLOW);
		HUD_component_gold.setOpaque(true);
		HUD_component_gold.setBackground(java.awt.Color.BLUE);
		HUD_component_clock = new JLabel();
		HUD_component_clock.setSize((int)(screenX * .03635), (int)(screenY * 0.04672));
		HUD_component_clock.setLocation((int)(screenX * .5 - HUD_component_clock.getSize().width / 2), 0);
		HUD_component_clock.setBackground(Color.GRAY);
		HUD_component_clock.setForeground(Color.BLUE);
		description = new JTextArea("test");
		description.setSize((int)(screenX * .113), (int)(screenY * .131));
		description.setLocation((int)(screenX * .345),(int)(screenY * .622));
		description.setVisible(false);
		inventory = new JPanel();
		//initInventoryUsingSwing();
		try {
			texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/iron branch.jpg"));
		} catch (IOException e) {
		}
		inventory.setLocation((int)(screenX * .68), (int)(screenY * .775));
		inventory.setSize((int)(screenX * .0645), (int)(screenY * 0.0895));
		inventory.setVisible(true);
		this.getLayeredPane().add(HUD_component_level, 2);
		this.getLayeredPane().add(HUD_component_health_number, 7);
		this.getLayeredPane().add(HUD_component_health_bar, -2);
		this.getLayeredPane().add(HUD_component_damage, 4);
		this.getLayeredPane().add(HUD_component_strength, 5);
		this.getLayeredPane().add(HUD_component_agility, 3);
		this.getLayeredPane().add(HUD_component_intelligence, 2);
		levelUpButton = new LevelUpButton(this);
		this.getLayeredPane().add(HUD_component_mana_number, 10);
		this.getLayeredPane().add(HUD_component_mana_bar, 9);
		this.getLayeredPane().add(description, 11);
		this.getLayeredPane().add(stats, 12);
		this.getLayeredPane().add(HUD_component_move_speed, 13);
		this.getLayeredPane().add(HUD_component_attack_speed, 14);
		this.getLayeredPane().add(scrollPane, 15);
		this.getLayeredPane().add(HUD_component_armor,16);
		this.getLayeredPane().add(HUD_component_gold);
		this.getLayeredPane().add(HUD_component_clock);
		initBcg();
		console = new Console(this);
		for(int i = 0; i < 10; i++){
			buffs.add(new BuffIcon(this, i));
			getLayeredPane().add(buffs.get(i));
		}
	}
	public void tick(){
		if(!console.consoleInput.hasFocus())bcg.requestFocusInWindow();
		for(int i = 0; i < buffs.size(); i++)
			if(owner.selectedUnit.buffs.size() > i && 
					owner.selectedUnit.buffs.get(i) != null)
				buffs.get(i).setBuff(owner.selectedUnit.buffs.get(i));
			else buffs.get(i).setBuff(null);
	}
	public void render(){
		String string = ("  lvl:" + (Integer)(int)(owner.selectedUnit.getStat(Constants.level))).toString() + "   " + ("xp:" + (Integer)(int)(owner.selectedUnit.getStat(Constants.experience))).toString() + "/" + ((Integer)(int)(owner.hero.getStat(Constants.level) * 100 + 100));
		HUD_component_level.setText(string);
		string = "                " + 
		((Integer)(int)owner.selectedUnit.getStat(Constants.health)).toString() + "/" + 
		((Integer)(int)owner.selectedUnit.getStat(Constants.maxHealth));
		HUD_component_health_number.setFont(
		new Font(HUD_component_health_number.getFont().getName(), Font.BOLD, 20));
		HUD_component_health_number.setText(string);
		HUD_component_health_number.setOpaque(false);
		HUD_component_health_bar.setSize((int)(owner.selectedUnit.getRatio("health") * healthBarMaxWidth), healthBarHeight);
		string = "dmg: " + ((Integer)(int)owner.selectedUnit.getStat(Constants.damage)).toString();
		HUD_component_damage.setText(string);
		string = "     " + (owner.hero.primaryAttribute == Alive.Attribute.STRENGTH ? "!" : " ") +
		((Integer)(int)owner.selectedUnit.getStat(Constants.baseStrength)).toString();
		if(owner.hero.getStat(Constants.bonusStrength) > 0)
			string += (" + " + ((Integer)(int)owner.selectedUnit.getStat(Constants.bonusStrength)).toString());
		HUD_component_strength.setText(string);
		string = "     " +  (owner.hero.primaryAttribute == Alive.Attribute.AGILITY ? "!" : " ") +
		((Integer)(int)owner.selectedUnit.getStat(Constants.baseAgility)).toString();
		if(owner.hero.getStat(Constants.bonusAgility) > 0)
			string += (" + " + ((Integer)(int)owner.selectedUnit.getStat(Constants.bonusAgility)).toString());
		HUD_component_agility.setText(string);
		string = "     " +  (owner.hero.primaryAttribute == Alive.Attribute.INTELLIGENCE ? "!" : " ") +
		((Integer)(int)owner.selectedUnit.getStat(Constants.baseIntelligence)).toString();
		if(owner.hero.getStat(Constants.bonusIntelligence) > 0)
			string += (" + " + ((Integer)(int)owner.selectedUnit.getStat(Constants.bonusIntelligence)).toString());
		HUD_component_intelligence.setText(string);
		levelUpButton.update();
		string = ((Integer)(int)owner.selectedUnit.getStat(Constants.mana)).toString() + "/" + 
					((Integer)(int)owner.selectedUnit.getStat(Constants.maxMana));
		HUD_component_mana_bar.getGraphics().setFont(HUD_component_health_number.getFont());
		string =  "                " + 
				((Integer)(int)owner.selectedUnit.getStat(Constants.mana)).toString() + "/" + 
				((Integer)(int)owner.selectedUnit.getStat(Constants.maxMana));
		HUD_component_mana_number.setFont(HUD_component_health_number.getFont());
		HUD_component_mana_number.setText(string);
		string = "ms: " + ((Integer)(int)this.owner.selectedUnit.getSpeed()).toString();
		HUD_component_move_speed.setText(string);
		string = "as: " + ((Integer)(int)this.owner.selectedUnit.getStat(Constants.attackSpeed)).toString();
		HUD_component_attack_speed.setText(string);
		scrollPane.getViewport().setViewPosition(new Point(scrollPane.getBounds().x, 999999999));
		string = "arm: " + ((Integer)(int)(owner.selectedUnit.getStat(Constants.armor)));
		HUD_component_armor.setText(string);
		string = "gold: " + ((Integer)(int)(owner.selectedUnit.getStat(Constants.gold)));
		HUD_component_gold.setText(string);
		string = "        " + String.format("%02d" , Head.tickCount/60/60) + ":" + String.format("%02d", Head.tickCount/60%60);
		HUD_component_clock.setText(string);
		//renderInventoryUsingSwing();
		renderInventoryUsingSlick();
		texture.bind();
		for(AbilityButton ab: abilityButtons){
			if(ab.correspondingAbility.state == Ability.State.READY)
				{
				ab.setBackground(Util.getAsJavaColor(ab.correspondingAbility.abilityType.color));
				}
				else 
				ab.setBackground(Color.BLACK);
		}
	}
	public void renderInventoryUsingSlick() {
		for(int i = 0; i < owner.hero.inventory.size(); i++)if(owner.hero.inventory.getItemInSlot(i) != null){
			org.newdawn.slick.Color.white.bind();
			texture = owner.hero.inventory.getItemInSlot(i).texture;
			texture.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0,0);
				GL11.glVertex2f((int)(screenX * .68) + i * (int)(screenX * 0.0345), (int)(screenY * .697));
				GL11.glTexCoord2f(1,0);
				GL11.glVertex2f((int)(screenX * .68) + (int)(screenX * .0345) * (i+1), (int)(screenY * .697));
				GL11.glTexCoord2f(1,1);
				GL11.glVertex2f((int)(screenX * .68) + (int)(screenX * .0345) * (i+1), (int)(screenY * .697) + (int)(screenY * 0.0595));
				GL11.glTexCoord2f(0,1);
				GL11.glVertex2f((int)(screenX * .68) + i * (int)(screenX * 0.0345), (int)(screenY * .697) + (int)(screenY * 0.0595));
			GL11.glEnd();
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	public void heroPickScreen(){
		menuBar = new JMenuBar();
		menuBar.setLocation((int)Head.getScreenDimensions().getX() / 2 - 100,
				(int)Head.getScreenDimensions().getY() / 2 - 100);
		menuBar.setSize(100, 25);
		JMenu heroMenu = new JMenu("select your hero");
		menuBar.add(heroMenu);
		for(int i = 0; i < Hero.HeroType.values().length; i++){
			Hero.HeroType[] heroes = Hero.HeroType.values();
			HeroChoice heroChoice = new HeroChoice(heroes[i], this);
			//heroChoice.addMouseListener(heroChoice);
			heroMenu.add(heroChoice);
		}
		getLayeredPane().add(menuBar, heroChoiceMenuBarOrdinal);
	}
	public void showDecription(boolean b, String sDescription){
		if(b){
			description.setText(sDescription);
			description.setVisible(true);
		}
		else description.setVisible(false);
	}
	public void out(String string){
		rsBox.append("\n" + string);
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_LEFT:
			owner.camera.centralPoint.changeX(-owner.camera.getCameraSpeed());
			break;
		case KeyEvent.VK_RIGHT:
			owner.camera.centralPoint.changeX(owner.camera.getCameraSpeed());
			break;
		case KeyEvent.VK_DOWN:
			owner.camera.centralPoint.changeY(owner.camera.getCameraSpeed());
			break;
		case KeyEvent.VK_UP:
			owner.camera.centralPoint.changeY(-owner.camera.getCameraSpeed());
			break;
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_T:
			levelUpButton.mouseClicked(new MouseEvent(
			this,MouseEvent.MOUSE_PRESSED,System.nanoTime(), 0,levelUpButton.getLocation().x, levelUpButton.getLocation().y, 1,true));
			out("t");
			break;
		case KeyEvent.VK_Q:
			this.abilityButtons.get(0).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_W:
			this.abilityButtons.get(1).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_E:
			this.abilityButtons.get(2).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_R:
			this.abilityButtons.get(3).mouseClicked(new MouseEvent(this, 0,0,0,0,0,0,false));
			break;
		case KeyEvent.VK_SPACE:
			owner.selectedUnit = owner.hero;
			owner.camera.centralPoint.set(owner.hero.location.x, owner.hero.location.y);
			break;
		case KeyEvent.VK_LEFT:
			owner.camera.resetCameraSpeed();
			break;
		case KeyEvent.VK_RIGHT:
			owner.camera.resetCameraSpeed();
			break;
		case KeyEvent.VK_DOWN:
			owner.camera.resetCameraSpeed();
			break;
		case KeyEvent.VK_UP:
			owner.camera.resetCameraSpeed();
			break;
		case KeyEvent.VK_ESCAPE:
			Head.running = false;
			break;
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
