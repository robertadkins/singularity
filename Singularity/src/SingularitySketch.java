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


public class SingularitySketch extends PApplet {
	private static final long serialVersionUID = 5131702049942670416L;

	private CascadeClassifier face_cascade;

	private float DAMP = 0.6f;
	private float HEAD_ANGLE_RANGE = PI / 8;
	private float EYE_ANGLE_RANGE = PI / 5;
	
	boolean shouldSpeak;

	Minim minim;
	AudioPlayer player;

	int MAX_FILE_SIZE = 2048;

	float targetX;
	float targetY;
	float prevX;
	float prevY;

	byte[] audio;

	PShape[] heads;
	PShape eye;
	int counter;

	float headAngleX = 15 * PI / 8;
	float headAngleY = 0;

	float eyeAngleX = PI / 70;
	float eyeAngleY = 0;
	
	PKinect myKinect;

	public void setup() {		
		size(1200, 900, P3D);
		heads = new PShape[4];
		heads[0] = loadShape("my_model.obj");
		heads[1] = loadShape("eee.obj");
		heads[2] = loadShape("erh.obj");
		heads[3] = loadShape("oh.obj");
		// heads[4] = loadShape("short_i.obj");
		//heads[4] = loadShape("ychj.obj");
		counter = 0;
		shouldSpeak = false;
		
		setupKinect();

		eye = loadShape("my_eye.obj");

		targetX = 0.5f;
		targetY = 0.5f;
		prevX = 0.5f;
		prevY = 0.5f;
		player = null;
		audio = new byte[MAX_FILE_SIZE];

		minim = new Minim(this);

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		face_cascade = new CascadeClassifier(
				"data/haarcascade_frontalface_default.xml");
		if (face_cascade.empty()) {
			System.out.println("--(!)Error loading A\n");
			return;
		} else {
			System.out.println("Face classifier loooaaaaaded up");
		}
		
		Thread spiderman = new Thread(new Processor());
		spiderman.start();
		Thread musakBox = new Thread(new AudioRetriever());
		musakBox.start();
	}

	public void draw() {
		if (player != null && !player.isPlaying()) {
			shouldSpeak = false;
		}
		
		background(0x000000);
//		lights();
		 directionalLight(50,255,50,0,-1,0);
		
//		pushMatrix();
//		scale(0.2f);
		drawKinect();
//		popMatrix();
//		camera(0, 0, -1000, 0, 0, 0, 0, -1, 0);
		
		pushMatrix();
//		translate(width / 2, height / 2, 0);
//		scale(3.0f);
		scale(0.005f);
//		translate(0,0,-4);
//		translate(-20,20,0);
//		rotateY(PI/2);

		float dx = prevX - targetX;
		if (abs(dx) > 0.001) {
			headAngleY += dx * DAMP * HEAD_ANGLE_RANGE;
			prevX -= dx * DAMP;
		}

		float dy = prevY - targetY;
		if (abs(dy) > 0.001) {
			headAngleX += dy * DAMP * HEAD_ANGLE_RANGE;
			prevY -= dy * DAMP;
		}

		rotateX(headAngleX);
		rotateY(headAngleY);
		rotateZ(PI);
		if (shouldSpeak) {
			shape(heads[(counter / 2) % heads.length]);
			counter++;
		} else {
			shape(heads[0]);
		}

		dx = prevX - targetX;
		if (abs(dx) > 0.001) {
			eyeAngleY += dx * DAMP * EYE_ANGLE_RANGE;
			prevX -= dx * DAMP;
		}

		dy = prevY - targetY;
		if (abs(dy) > 0.001) {
			eyeAngleX += dy * DAMP * EYE_ANGLE_RANGE;
			prevY -= dy * DAMP;
		}

		pushMatrix();
		translate(-36, 36.5f, 28);
		rotateY(-eyeAngleY);
		rotateX(-eyeAngleX);
		scale(22.5f);
		shape(eye);
		popMatrix();

		translate(36, 36.5f, 28);
		rotateY(-eyeAngleY);
		rotateX(-eyeAngleX);
		scale(22.5f);
		shape(eye);

		popMatrix();
	}
	
	public void setupKinect() {
		try {
			myKinect=new PKinect(this);
			if(!myKinect.start(PKinect.XYZ)) {
				println("ERROR: The Kinect device could not be initialized.");
				println("1. Check if the Microsoft's Kinect SDK was succesfully installed on this computer.");
				println("2. Check if the Kinect is plugged into a power outlet.");
				println("3. Check if the Kinect is connected to a USB port of this computer.");   
				myKinect = null;
			} 
		} catch(Exception e) {
			myKinect = null;
			e.printStackTrace();
			System.out.println("No kinect worky");
		}
	}
	
	public void drawKinect() {
		if(myKinect != null) {
			myKinect.setFrustum();
			translate(0,0,-.5f);
			scale(4);
//			myKinect.drawFOV();
//			camera(0, 0, 1000, 0, 0, 0, 0, 1, 0);
			//Simple rotation of the 3D scene using the mouse
//			translate(0,0,-2);
//			rotateX(this.radians((float)(mouseY*1f/height-0.5)*180));
//			rotateY(this.radians((float)(mouseX*1f/width-0.5)*180)); 	
//			translate(0,0,2);
 
			//Draw the depth map
			PDepthMap map=myKinect.getPDepthMap();
//			lights();
//					  directionalLight(50,255,50,0,-1,0);

			noStroke();
			map.draw();

			resetMatrix();
			translate(0,0,-2);

			
			//or draw lower resolution depthmap like that:
//			int skip=1;
//			shape(heads[0]);

//			map.draw(skip);
//			shape(heads[0]);

		}
	}

	class Processor implements Runnable {
		private Mat webcam_image;
		private VideoCapture capture;

		// Create a constructor method
		public Processor() {
			webcam_image = new Mat();
			capture = new VideoCapture(0);
		}

		public Mat detect(Mat inputframe) {
			Mat mRgba = new Mat();
			Mat mGrey = new Mat();
			MatOfRect faces = new MatOfRect();
			inputframe.copyTo(mRgba);
			inputframe.copyTo(mGrey);
			Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
			Imgproc.equalizeHist(mGrey, mGrey);
			face_cascade.detectMultiScale(mGrey, faces);
			Rect best = null;
			int maxArea = 0;
			for (Rect rect : faces.toArray()) {
				if (rect.width * rect.height > maxArea) {
					maxArea = rect.width * rect.height;
					best = rect;
				}
			}

			if (best != null && maxArea > 15000) {
				targetX = (float) (best.x / inputframe.size().width);
				targetY = (float) (best.y / inputframe.size().height);
			}

			return mRgba;
		}

		@Override
		public void run() {
			if (capture.isOpened()) {
				while (true) {
					capture.read(webcam_image);
					if (!webcam_image.empty()) {
						// -- 3. Apply the classifier to the captured image
						webcam_image = detect(webcam_image);
						// -- 4. Display the ie
					} else {
						System.out
								.println(" --(!) No captured frame -- Break!");
//						break;
					}
				}
			}
		}
	}

	class AudioRetriever implements Runnable {

		ServerSocket server;
		BufferedInputStream in;

		AudioRetriever() {
			try {
				server = new ServerSocket(33333);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					System.out.println("Listening...");
					Socket snake = server.accept();
					System.out.println("IP: " + snake.getInetAddress());
					System.out.println("Port: " + snake.getLocalPort());
					System.out.println("Connected!");
					
					File out = new File("data/audio.mp3");
					
					if (!out.exists()) {
						out.createNewFile();
					}
					
					BufferedInputStream in = new BufferedInputStream(snake.getInputStream());
					
					byte[] data = new byte[4096];
					int read = 0;
					BufferedOutputStream toFile = new BufferedOutputStream(new FileOutputStream(out));
					while ((read = in.read(data)) != -1) {
						System.out.println("DATA: " + data);
						toFile.write(data, 0, read);
						toFile.flush();
					}
					toFile.close();
					player = minim.loadFile("data/audio.mp3");
					player.play();
					shouldSpeak = true;

				} catch (IOException e) {
					System.out.println("crap");
					e.printStackTrace();
					break;
				}
			}
			try {
				server.close();
			} catch (IOException e) {
				System.out
						.println("You couldn't even close? What kind of a Socket are you?");
			}
		}
	}
}
