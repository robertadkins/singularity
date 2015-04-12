import processing.core.*;


public class Example extends PApplet{
	PFont f;
	public static void main(String[] args){
		PApplet.main(new String[] {"--present","Example"}); 
	}
	public void setup() {
	    size(200,200);
	    f = createFont("Arial", 16, true);
	  }

	  public void draw() {
	    background(255);
	    textFont(f, 16);
	    fill(0);
	    text("Hello Strings!",10,100);
	  }
}
