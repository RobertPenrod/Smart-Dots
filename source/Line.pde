class Line{
  float m;
  float b;
  
  Line(){
    set(0,0);
  }
  
  Line(float _m, float _b){
    set(_m, _b);
  }
  
  Line(float x1, float y1, float x2, float y2){
    setFromPoints(x1, y1, x2, y2);
  }
  
  Line(PVector p1, PVector p2){
    setFromPoints(p1, p2);
  }
  
  void set(float _m, float _b){
    m = _m;
    b = _b;
  }
  
  void setFromPoints(float x1, float y1, float x2, float y2){
    float rise = y2 - y1;
    float run = x2 - x1;
    
    // Check for vertical line
    if(abs(run) < EPSILON){
      m = INFINITY;
    }else{
      m =(rise/run);
    }
    
    b = y1 - m * x1;
  }
  
  void setFromPoints(PVector p1, PVector p2){
    setFromPoints(p1.x, p1.y, p2.x, p2.y);
  }
  
  float intersectPoint(Line otherLine){
    if(abs(m - otherLine.m) < EPSILON){
      return INFINITY;
    }
    return (otherLine.b - b) / (m - otherLine.m);
  }
  
  float y(float x){
    return m*x + b;
  }
}