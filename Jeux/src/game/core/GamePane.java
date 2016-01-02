package game.core;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import game.map.Chunck;
import game.map.Map;
import game.map.View;

public class GamePane extends JPanel implements Runnable{
	
	private static final long serialVersionUID = 1204454795981689683L;
	
	//----FIELDS----// 
	
	//essentials
	private Thread thread;
	public static int WIDTH = 1280;
	public static int HEIGHT = 800;
	private Listener l;
	private View v;
	
	//frame
	private final int FPS = 60;
	private double averageFPS = 0;
	
	//map
	Map map;
	
	//image
	private Graphics2D g;
	private BufferedImage image;
	
	//impoted images
	public static Image[] texturesBlock = new Image[10];

	/**
	 * Constructor of the GamePane class.
	 * 
	 */
	public GamePane(){
		super();
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setFocusable(true);
		l = new Listener();
		
		requestFocus();
	}
	
	public void initiate(){
		//import images
		textureImport();
		
		//all fields for the game will be initiate here
		map = new Map((long)2107554565, 1_000);
		map.firstGenerate(4);
		
		//Graphics
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		v = new View();
		
	}
	
	public void textureImport(){
		texturesBlock[0] = new ImageIcon("assets/textures/map/green3.png").getImage();
		texturesBlock[1] = new ImageIcon("assets/textures/map/waterFrame1.png").getImage();
	}
	
	public void addNotify(){
		super.addNotify();
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
		//Initiate keylisteners here
		this.addKeyListener(l);
		this.addMouseListener(l);
		this.addMouseWheelListener(l);
	}
	
	

	/**
	 * run the game thread
	 */
	public void run() {
		
		//---- Innitiation ----//
		initiate();
		
		
		//---- field for the fps system ----//
		long startTime = 0;
		long URDTimeMillis;
		long waitTime;
		long totalTime = 0;
		
		int frameCount = 0;
		int maxFrameCount = 30;
		
		long targetTime = 1000/FPS;
		
		//----Thread----//
		while(true){
			startTime = System.nanoTime();
			
			GameUpdate();
			GameRender();
			GameDraw();
			
			//---- System for the fps ----//
			
			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			
			waitTime = targetTime - URDTimeMillis;
			if(waitTime <0){waitTime = 0;}
			
			
				
			
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {}
			
			totalTime += System.nanoTime() - startTime;
			frameCount++;
			
			if(frameCount == maxFrameCount){
				averageFPS = 1000.0 / ((totalTime / frameCount)/1000000);
				frameCount = 0;
				totalTime = 0;
			}
		}
		
	}
	
	public void GameUpdate(){
		
		int speed = 10;
		
		if(Listener.SHIFT){
			speed = 50;
		}
		if(Listener.AOrLeft){
			View.x += speed;
		}
		if(Listener.DOrRight){
			View.x-=speed;
		}
		if(Listener.SOrDown){
			View.y-=speed;
		}
		if(Listener.WOrUp){
			View.y+=speed;
		}
		
		v.zoom(Listener.getWhellRotation());
		v.update();
		
		//---- Map Update ----//
		map.update();
		
	}
	
	public void GameRender(){
		g.setColor(Color.gray);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//map
		Chunck c = map.getByPixel(View.x, View.y);
		map.draw(g, View.x, View.y, 1/*c.getX()*/, 0/*c.getY()*/);
		
		//----INFO----//
		g.setColor(Color.black);
		g.setFont(new Font("Arial", 0, 20));
		g.drawString("Pos: "+View.x+":"+View.y+" Chunck Pos: "+c.getX()+":"+c.getY(), 30, 30);
		
	}
	
	public void GameDraw(){
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0,0, null);
		
		g2.dispose();
		
	}
	
	//---- getter and setter ----//

	public double getAverageFPS() {
		return averageFPS;
	}

	public void setAverageFPS(double averageFPS) {
		this.averageFPS = averageFPS;
	}

}
