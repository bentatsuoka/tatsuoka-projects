package hw22_ec;

import processing.core.PApplet;
@SuppressWarnings("serial")

// Changing color of the background and fill color of shapes. 
public class ProcessingExample02 extends PApplet {

  final static int canWidth  = 1000;
  final static int canLength = 500;
  
  final static int colorMin = 0;
  final static int colorMax = 255;
    
  public void setup() {
    // Set the background color of the canvas: the three values
    // represent levels of red, green and blue. They should be between
    // 0 and 255.
    background(0, 0, 0);
    
    // Set size of canvas
    size(canWidth, canLength);
  }

  public void draw() {
    // Calling fill() before the shape is drawn changes the fill color
    // for the next drawn shape, the circles appear in different colors.
    fill(random(colorMin, colorMax), random(colorMin, colorMax), random(colorMin, colorMax));
    ellipse(random(0, canWidth), random(0, canLength), 30, 10);
  }

}
