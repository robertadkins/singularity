PShape s;

void setup(){
 size(600, 600, P3D);
 s = loadShape("head.obj");
}

void draw(){
  background(#ee9999);
  translate(width/2, height/2);
  shape(s, 0, -height/2, 400, 400);
  camera(mouseX, height/2, (height/2) / tan(PI/6), mouseX, height/2, -500, 0, -1, 0);
  pushMatrix();
  translate(0,0,200);
  rotate(PI*mouseY/width);
  stroke(255);
  popMatrix();
}
