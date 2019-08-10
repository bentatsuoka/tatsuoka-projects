package hw22_ec;

import processing.core.PApplet;
@SuppressWarnings("serial")

// Setting up the applet and drawing rectangles at random locations.
public class ProcessingExample01 extends PApplet {
    
  // This method runs once at the beginning to setup the drawing area
  public void setup() {
    // set the size of the canvas for your "drawing"
    size(500, 500);
  }

  // This method will execute in a loop (by default, you do not need
  // to do anything special)
  public void draw() {
    // this draws a rectangle with both sides equal to 10
    // (that makes it a square) at a randomly generated x,y coordinates;
    // the method random(0,500) generates a random number from 0 to 500
    float x = random(0, 500);
    float y = random(0, 500);
    rect(x, y, 20, 20);
  }
}