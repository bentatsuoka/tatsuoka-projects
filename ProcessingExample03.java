package hw22_ec;

import processing.core.PApplet;
@SuppressWarnings("serial")

// Printing text and changing its properties. Detecting mouse clicks.
public class ProcessingExample03 extends PApplet {
    
  public void setup() {
    // Set the background color to white
    background(255, 255, 255);
    size(500, 500);
  }

  public void draw() {
    // Print text on the canvas: the font size is 32, the text color is black,
    // the text as centered on its x and y coordinates
    textSize(32);
    fill(0, 0, 0);
    textAlign(CENTER);
    text("Click to change the color", 250, 250);

    // change the background color to a random color on mouse click
    if (mousePressed)
      background(random(0, 255), random(0, 255), random(0, 255));
  }
}