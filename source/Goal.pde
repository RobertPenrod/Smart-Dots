class Goal{

  PVector pos;
  float size;
  
  Goal(PVector p){
    pos = new PVector(p.x, p.y);
    size = 10;
  }
  
  //---------------------------------------------------------------------------------------
  
  void show(){
    fill(255,0,0);
    strokeWeight(2);
    stroke(0);
    ellipse(pos.x, pos.y, size, size);
  }
}