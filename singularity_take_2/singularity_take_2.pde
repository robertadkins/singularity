PShape s;
PShape eye;
float angleX = PI / 8;
float angleY = 7 * PI / 4;
float prevMouseX = 0;
float prevMouseY = 0;

void setup(){
 size(1920, 1080, P3D); 
 s = loadShape("my_model.obj");
 eye = loadShape("my_eye.obj");
}

void draw(){
  background(#000000);
    
  
  lights();
//  directionalLight(50,255,50,0,-1,0);
  pushMatrix();
  translate(width/2, height/2, 0);
  scale(2.0);
  angleY += (mouseX - prevMouseX)/width * PI / 2;
  prevMouseX = mouseX;
  angleX += (prevMouseY - mouseY)/height * PI / 2;
  prevMouseY = mouseY;
//  rotateY(7 * PI / 4);
  rotateY(angleY);
//  rotateX(PI / 8);
  rotateX(angleX);
  rotateZ(PI);
  shape(s);
  pushMatrix();
  translate(-36, 36.5, 28);
  rotateY(-angleY);
  rotateX(-angleX);
  scale(22.5);
  shape(eye);
  popMatrix();
  translate(36, 36.5, 28);
  rotateY(-angleY);
  rotateX(-angleX);
  scale(22.5);
  shape(eye);
  camera(0, 0, 1000,
         0  , 0  , 0   ,
         0  , 1  , 0    );
  popMatrix();
}
