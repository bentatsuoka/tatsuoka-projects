/*
 Ben Tatsuoka
 HW 24 Disk Game
 A game that scores points for user clicks on moving disks.
 12/13/18
 */


package hw22_ec;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class Gotcha extends PApplet {
	
	//Instead of creating 5 disks, the program makes each disk equivalent to clicking 5 disks. Cuz All the disks have the same movement
	Disk[] disks;
	
	float xPos = 400;
	
	float yPos = 100;
	
	float xSpeed = 3;
	
	float ySpeed = 3;
	
	float ellWidth = 40;
	
	float ellHeight = 40;
	
	int points = 0;
	
	int s;
	
	float sec;
	
	int time = millis();

	public void setup() {
		
		size(500, 500);
		
		this.disks = new Disk[5];
	
		for (int i = 0; i < this.disks.length; i++) {
		      
			this.disks[i] = new Disk((int)(random(1,21)), random(0,255), random(0,255), random(0, 255), random(0, Disk.width), random(0, Disk.height));
			
		}
	}

	
	public void draw() {

		background(random(0,100),random(100,200),random(200,225));
		
		text("YOUR CURRENT SCORE: " + points, 175, 50);
		
		for (int i = 0;  i < this.disks.length; i++) {
			
			this.disks[i].move();
			
			fill(this.disks[i].red,this.disks[i].blue,this.disks[i].green);
			
			ellipse(this.disks[i].x, this.disks[i].y, ellWidth, ellHeight);
			
			fill(255);
			
			text(this.disks[i].pointValue, this.disks[i].x - 4, this.disks[i].y + 4);
		}
		
		timer();
		
		text("Timer: " + sec, 400, 50);
		
		
		if (sec == 30.000) {
			
			eraseBackground();
			
			text("Game over!", 225, 225);
			
			text("Score: " + points, 200, 200);
			
			System.out.println("Your time is up! Game Over.");
			
			System.out.println("Your Score: " + points);
			
			//Don't know how to delay
			exit();
			
		}
			
	}

	public void mousePressed() {
		
		for (int i = 0; i < this.disks.length; i++) {
			
			if (mousePressed == true && mouseX > (this.disks[i].x - (ellWidth/2)) && mouseX < (this.disks[i].x + (ellWidth/2)) && mouseY > (this.disks[i].y - (ellHeight/2)) && mouseY < (this.disks[i].y + (ellHeight/2))) {
				
				points += this.disks[i].pointValue;
				
				System.out.println(points);
				
			}
		}
		
	}
	
    public void eraseBackground() {      

        background(105);
	
    }
    
	public void timer() {
		
		s = millis() - time;
		
		sec = s/1000;
		
	}
	
	public class Disk {
        // Shape size
        final static int width  = 500;
        final static int height = 500;
        // Shape value
        
        int pointValue = 10;

        float x = 300;
        float y = 250;

        float xSpeed;
        float ySpeed;

        float red;
        float green;
        float blue;
        Disk(int pointValue, float red, float green, float blue, float x, float y) {
            
        	this.pointValue = pointValue;
        	this.xSpeed = (float) (Math.random()*3);
    		this.ySpeed = (float) (Math.random()*3)+3;
        	x = this.x;
        	y = this.y;
        	this.red = red;
        	this.green = green;
        	this.blue = blue;
           
        	System.out.println("Constructor pointValue = " + this.pointValue);
        }
        Disk() {
        	
            this(10, 0, 0, 255, 300, 250);
            
        }
        
        public void drawShape() {
            // Select color, then draw the shape at computed x, y location
        	fill(random(0,255),random(0,255),random(0,255));
            
        	ellipse(this.x, this.y, Disk.width, Disk.height);
        	
        }
        
        public void move() {
    		
        	this.x += this.xSpeed;
    		
    		if (this.x > Disk.width) {
    			
    			this.x = Disk.width - 60;
    			
    			this.xSpeed = -3;
    			
    		}
    		
    		if (this.x < 0) {
    			
    			this.x = 60;
    			
    			this.xSpeed = 7;
    			
    		}

    		this.y += this.ySpeed;
    		
    		if (this.y > Disk.height) {
    			
    			this.y = Disk.height - 60;
    			
    			this.ySpeed = -3;
    			
    		}
    		
    		if (this.y < 0) {
    			
    			this.y = 60;
    			
    			this.ySpeed = 7;
    			
    		}

    	}
        
    }
}

	

