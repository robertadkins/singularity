PShape s;
float angleX = 0;
float angleY = 0;
float prevMouseX = 0;
float prevMouseY = 0;

void setup(){
 size(600, 600, P3D);
 s = loadShape("ModelsFace.obj");
}

void draw(){
  background(#ee9999);
    
  
  lights();
  pushMatrix();
  translate(width/2, height/2, 0);
  scale(2.0);
  angleY += (mouseX - prevMouseX)/width * PI / 2;
  prevMouseX = mouseX;
  angleX += (prevMouseY - mouseY)/height * PI / 2;
  prevMouseY = mouseY;
  rotateY(7 * PI / 4);
  rotateY(angleY);
  rotateX(PI / 8);
  rotateX(angleX);
  rotateZ(PI);
  shape(s);
  camera(0, 0, 1000,
         0  , 0  , 0   ,
         0  , 1  , 0    );
  popMatrix();
}
