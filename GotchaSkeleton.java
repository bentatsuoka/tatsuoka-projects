package hw22_ec;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class GotchaSkeleton extends PApplet {

	// Timer
    int timer;
    
    // Game length in milliseconds
    int gameDuration = 10 * 1000;
    
    // Keep track of current score
    static int score = 0;
    // Canvas size
    final int canvasWidth  = 500;
    final int canvasHeight = 500;
    // Declare disks
    float[] yValue = {70, 140, 210, 280};
    
    Disk[] d = new Disk[4];
    
    // Setup runs one time at the beginning of your program
    public void setup() {
        size(canvasWidth, canvasHeight);
        smooth();
        
        // Set time now
        timer = millis() + gameDuration;
        
        for (int i = 0; i < d.length; i++) {
            d[i] = new Disk();
        }
    }
    
    // Draw is called in an infinite loop.
    // By default, it is called about 60 times per second.
    public void draw() {
        // Erase the background, if you don't, the previous shape will 
        // still be displayed
        eraseBackground();
        // Move the shape, i.e. calculate the next x and y position
        // where the shape will be drawn. Draw the shape, display point
        // value.
        for (int j = 0; j < d.length; j++) {
            d[j].calcCoords();
            d[j].drawShape();
            //TODO: display value on disk
        }
        
        // Display player's score
        text("YOUR CURRENT SCORE: " + score, 175, 50);
        
        if (millis() >= timer) {
            // Clear the canvas
            background(0, 0, 255);
            
            // Output the final score
            text("Game over!", 225, 225);
            
            text("Score: " + score, 200, 200);
            // Let the user click when finished reading score
      
            if (this.mousePressed) {        
              // Exit
              System.exit(0);
            }
        }
    }
    public void eraseBackground() {
        background(255);
    }
    
    // mousePressed is a PApplet method that you can override.
    // This method is designed to be called one time when the mouse is pressed
    public void mousePressed() {
      // Draw a circle wherever the mouse is
      int mouseWidth  = 20;
      int mouseHeight = 20;
      fill(0, 255, 0);
      ellipse(this.mouseX, this.mouseY, mouseWidth, mouseHeight);
      // Check whether the click occurred within range of the shape
      if (mousePressed == true && mouseX > (d[0].x - d[0].targetRange) && mouseX < (d[0].x + d[0].targetRange) && mouseY > (d[0].y - d[0].targetRange) && mouseY < (d[0].y + d[0].targetRange)) { 
      	score = score + d[0].pointValue;
      }
    }
    class Disk extends GotchaSkeleton {
        // Shape size
        final int shapeWidth  = 80;
        final int shapeHeight = 50;
        // Shape value
        final static int defaultValue = 10;
        int pointValue = defaultValue;
        // Keep track of ball's x and y position
        float x = 300;
        float y = 250;
        // Horizontal speed
        float xSpeed = 2;
        // It's hard to click a precise position, to make it easier, 
        // require the click to be somewhere on the shape
        int targetRange = Math.round((min(shapeWidth, shapeHeight)) / 2);
        float red;
        float green;
        float blue;
        Disk(float red, float green, float blue, float x, float y, 
             float xSpeed) {
            // TODO: Initialize instance variables as needed
            // TODO: Compute pointValue
            System.out.println("Constructor pointValue = " + this.pointValue);
        }
        Disk() {
            this(0, 0, 255, 300, 250, 2);
        }
        public void calcCoords() {      
            // Compute the x position where the shape will be drawn
            this.x += this.xSpeed; 
            // If the x position is off the canvas, reverse direction of 
            // movement
            if (this.x > canvasWidth) {
                System.out.println("<===  Change direction, go left because x = " + this.x);
                this.xSpeed = -1 * this.xSpeed;
            }
            // If the x position is off the canvas, reverse direction of 
            // movement
            if (this.x < 0) {
                System.out.println("     ===> Change direction, go right because x = " + this.x + "\n");
                this.xSpeed = -1 * this.xSpeed;
            } 
        }
        public void drawShape() {
            // Select color, then draw the shape at computed x, y location
        	fill(random(0,255),random(0,255),random(0,255));
            ellipse(this.x, this.y, this.shapeWidth, this.shapeHeight);
        }
        public void displayPointValue() {
            // Draw the text at computed x, y location
            text("Score: " + score, 180, 180);
        }
    }
}

	

