import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import processing.core.PApplet;
import processing.core.PShape;

public class SingularitySketch extends PApplet {
	private static final long serialVersionUID = 5131702049942670416L;

	private CascadeClassifier face_cascade;

	private float DAMP = 0.2f;
	private float HEAD_ANGLE_RANGE = PI / 8;
	private float EYE_ANGLE_RANGE = PI / 5;

	float targetX;
	float targetY;
	float prevX;
	float prevY;

	PShape s;
	PShape eye;
	
	float headAngleX = 15 * PI / 8;
	float headAngleY = 0;
	
	float eyeAngleX = PI / 70;
	float eyeAngleY = 0;

	public void setup() {
		size(1200, 900, P3D);
		s = loadShape("my_model.obj");
		eye = loadShape("my_eye.obj");

		targetX = 0.5f;
		targetY = 0.5f;
		prevX = 0.5f;
		prevY = 0.5f;

		System.loadLibrary("opencv_java2411");
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
	}

	public void draw() {
		background(0x000000);
		lights();
		//directionalLight(50,255,50,0,-1,0);
		pushMatrix();
		translate(width / 2, height / 2, 0);
		scale(3.0f);
		
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
		shape(s);
		
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
		camera(0, 0, 1000, 0, 0, 0, 0, 1, 0);
		
		popMatrix();
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
						break;
					}
				}
			}
		}
	}
}
