import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
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
import processing.core.PVector;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import de.voidplus.leapmotion.Hand;
import de.voidplus.leapmotion.LeapMotion;
import edu.ufl.digitalworlds.j4k.PDepthMap;
import edu.ufl.digitalworlds.j4k.PKinect;

public class SingularitySketch extends PApplet {
	private static final long serialVersionUID = 5131702049942670416L;

	private CascadeClassifier face_cascade;

	private float DAMP = 0.6f;
	private float HEAD_ANGLE_RANGE = PI / 8;
	private float EYE_ANGLE_RANGE = PI / 5;

	boolean shouldSpeak;
	boolean isPuzzled;

	Minim minim;
	AudioPlayer player;

	int MAX_FILE_SIZE = 2048;

	float targetX;
	float targetY;
	float prevX;
	float prevY;

	byte[] audio;

	PShape[] heads;
	PShape puzzleHead;
	PShape eye;
	int counter;

	float headAngleX = 15 * PI / 8;
	float headAngleY = 0;
	float headAngleYAdj = 0;

	float eyeAngleX = PI / 70;
	float eyeAngleY = 0;

	PKinect myKinect;
	LeapMotion leap;
	PVector prevHandPosition;
	boolean switched = false;

	int personality = 0;

	Thread spiderman;
	Thread musakBox;
	Thread cucumber;

	public void setup() {
		size(1200, 900, P3D);
		heads = new PShape[4];
		heads[0] = loadShape("my_model.obj");
		heads[1] = loadShape("eee.obj");
		heads[2] = loadShape("erh.obj");
		heads[3] = loadShape("oh.obj");
		puzzleHead = loadShape("puzzled.obj");
		// heads[4] = loadShape("short_i.obj");
		// heads[4] = loadShape("ychj.obj");
		counter = 0;
		shouldSpeak = false;
		isPuzzled = false;

		setupKinect();
		leap = new LeapMotion(this);

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

		spiderman = new Thread(new Processor());
		spiderman.start();
		musakBox = new Thread(new AudioRetriever());
		musakBox.start();
		cucumber = new Thread(new FacialNotifier());
		cucumber.start();
	}

	public void draw() {
		if (player != null && !player.isPlaying()) {
			shouldSpeak = false;
		}

		background(0x000000);
//		lights();
		directionalLight(50,255,50,0,-1,0);
		
//		float fov = PI/3;
//		float cameraZ = (height/2.0f) / tan(fov/2.0f);
//		perspective(fov, (float)(width)/(height), (float)(cameraZ/10.0), (float)(cameraZ*10.0));
//		printMatrix();

//		pushMatrix();
		drawKinect();
//		popMatrix();
//		  translate(0,0,-2);
//		  rotateX(this.radians((float)(mouseY*1f/height-0.5)*180));
//		  rotateY(this.radians((float)(mouseX*1f/width-0.5)*180));
//		  translate(0,0,2);
		//Leap motion hands

//		translate(0,0,-600);
		pushMatrix();
		strokeWeight(20);
		stroke(255,100,255);
		boolean first = true;
		for (Hand hand : leap.getHands ()) {
			pushMatrix();
		    PVector hand_position = hand.getPosition();
			translate(-width/2,-height/2,-600-2*hand_position.z);
			hand.draw(false);
		    popMatrix();
//			hand.drawSphere();
		    
		    if(first) {
		    	if(prevHandPosition != null && hand_position.z > 50) {
		    		headAngleYAdj += (hand_position.x - prevHandPosition.x)/100.0f;
		    		if (!switched && abs(headAngleYAdj) > PI) {
		    			switched = true;
		    			personality++;		//Switch AI personality!
		    		}
		    	}
		    	prevHandPosition = hand_position;
		    }
		    first = false;
		}
		if (leap.getHands().size() == 0)
			switched = false;
		popMatrix();
		

		pushMatrix();
		
		if (myKinect == null) {
			translate(width / 2, height / 2, 0);
			scale(3.0f);
		}
		

		headAngleYAdj -= headAngleYAdj * .1;

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
		rotateY(headAngleY + headAngleYAdj);
		rotateZ(PI);
		if (shouldSpeak) {
			shape(heads[(counter / 2) % heads.length]);
			counter++;
		} else {
			if (!isPuzzled) {
				shape(heads[0]);
			} else {
				shape(puzzleHead);
			}
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
		if (isPuzzled) {
			rotateX(-PI / 8);
		}
		rotateY(-eyeAngleY);
		rotateX(-eyeAngleX);
		scale(22.5f);
		shape(eye);
		popMatrix();

		translate(36, 36.5f, 28);
		if (isPuzzled) {
			rotateX(-PI / 8);
		}
		rotateY(-eyeAngleY);
		rotateX(-eyeAngleX);
		scale(22.5f);
		shape(eye);

		popMatrix();
	}

	public void setupKinect() {
		try {
			myKinect = new PKinect(this);
			if (!myKinect.start(PKinect.XYZ)) {
				println("ERROR: The Kinect device could not be initialized.");
				println("1. Check if the Microsoft's Kinect SDK was succesfully installed on this computer.");
				println("2. Check if the Kinect is plugged into a power outlet.");
				println("3. Check if the Kinect is connected to a USB port of this computer.");
				myKinect = null;
			}
		} catch (Exception e) {
			myKinect = null;
			e.printStackTrace();
			System.out.println("No kinect worky");
		}
	}

	public void drawKinect() {
		if (myKinect != null) {
			myKinect.setFrustum();
			translate(0, 0, -.5f);
			scale(4);

			// Draw the depth map
			PDepthMap map = myKinect.getPDepthMap();
//			  translate(0,0,-2);
//			  rotateX(this.radians((float)(mouseY*1f/height-0.5)*180));
//			  rotateY(this.radians((float)(mouseX*1f/width-0.5)*180));
//			  translate(0,0,2);
			noStroke();
			map.draw();

			// or draw lower resolution depthmap like that:
			// int skip=1;
			// map.draw(skip);

			resetMatrix();
			translate(0, 0, -2);
			scale(0.005f);

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
					}
				}
			}
		}
	}

	class FacialNotifier implements Runnable {
		ServerSocket server;
		DataOutputStream out;

		FacialNotifier() {
			try {
				server = new ServerSocket(80);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (true) {
				Socket sock = null;
				try {
					sock = server.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isPuzzled = true;
				try {
					if (sock != null) {
						sock.close();
					} else {
						System.out.println("Sock is null!");
					}
				} catch (/* InterruptedException | */IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
					Socket snake = server.accept();
					System.out.println("Connected!");
					File out = new File("data/audio.mp3");

					if (!out.exists()) {
						out.createNewFile();
					}

					BufferedInputStream in = new BufferedInputStream(
							snake.getInputStream());

					byte[] data = new byte[4096];
					int read = 0;
					BufferedOutputStream toFile = new BufferedOutputStream(
							new FileOutputStream(out));
					while ((read = in.read(data)) != -1) {
						toFile.write(data, 0, read);
						toFile.flush();
					}
					toFile.close();
					player = minim.loadFile("data/audio.mp3");
					player.play();
					isPuzzled = false;
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
