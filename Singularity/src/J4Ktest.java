import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import processing.core.PApplet;
import processing.core.PShape;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

import edu.ufl.digitalworlds.j4k.*;


public class J4Ktest extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PKinect myKinect;

	public void setup()
	{
	  size(1400,1000,P3D);
	  
	  //Initialize one PKinect object and start the sensor
	  myKinect=new PKinect(this);
	  if(!myKinect.start(PKinect.XYZ))
	  {
	    println("ERROR: The Kinect device could not be initialized.");
	    println("1. Check if the Microsoft's Kinect SDK was succesfully installed on this computer.");
	    println("2. Check if the Kinect is plugged into a power outlet.");
	    println("3. Check if the Kinect is connected to a USB port of this computer.");   
	    System.exit(0);
	   }  
	}

	public void draw()
	{   
	  background(0);
	  //Set the camera parameters
	  myKinect.setFrustum();
	  
	  //Simple rotation of the 3D scene using the mouse
	  translate(0,0,-2);
	  rotateX(this.radians((float)(mouseY*1f/height-0.5)*180));
	  rotateY(this.radians((float)(mouseX*1f/width-0.5)*180));
	  translate(0,0,2);
	 
	  //Draw the depth map
	  PDepthMap map=myKinect.getPDepthMap();
	//  lights();
	  directionalLight(50,255,50,0,-1,0);

	  noStroke();
	  map.draw();
	  //or draw lower resolution depthmap like that:
	  //int skip=1;
	  //map.draw(skip);
	  
	}
}
