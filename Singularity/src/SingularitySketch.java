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

	float normX;
	float normY;
	float prevNormX;
	float prevNormY;

	PShape s;
	PShape eye;
	float angleX = 15 * PI / 8;
	float angleY = 0;

	public void setup() {
		size(600, 600, P3D);
		s = loadShape("my_model.obj");
		eye = loadShape("my_eye.obj");

		normX = 0.5f;
		normY = 0.5f;
		prevNormX = 0.5f;
		prevNormY = 0.5f;

		System.loadLibrary("opencv_java2411");
		face_cascade = new CascadeClassifier(
				"C:/Users/Rafael/Downloads/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml");
		if (face_cascade.empty()) {
			System.out.println("--(!)Error loading A\n");
			return;
		} else {
			System.out.println("Face classifier loooaaaaaded up");
		}
		Thread spiderman = new Thread(new Processor());
		spiderman.start();
	}

	public void draw(){
	  background(0x000000);
	    
	  
	  lights();
	//  directionalLight(50,255,50,0,-1,0);
	  pushMatrix();
	  translate(width/2, height/2, 0);
	  scale(2.0f);
	  angleY += (prevNormX - normX) * PI / 2;
	  prevNormX = normX;
	  angleX += (prevNormY - normY) * PI / 2;
	  prevNormY = normY;
	  rotateY(angleY);
	  rotateX(angleX);
	  rotateZ(PI);
	  shape(s);
	  pushMatrix();
	  translate(-36, 36.5f, 28);
	  rotateY(angleY);
	  rotateX(angleX);
	  scale(22.5f);
	  shape(eye);
	  popMatrix();
	  translate(36, 36.5f, 28);
	  rotateY(angleY);
	  rotateX(angleX);
	  scale(22.5f);
	  shape(eye);
	  camera(0, 0, 1000,
	         0  , 0  , 0   ,
	         0  , 1  , 0    );
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
			Rect best = new Rect();
			int maxArea = 0;
			for (Rect rect : faces.toArray()) {
				if (rect.width * rect.height > maxArea) {
					maxArea = rect.width * rect.height;
					best = rect;
				}
			}
			normX = (float) (best.x / inputframe.size().width);
			normY = (float) (best.y / inputframe.size().height);

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
