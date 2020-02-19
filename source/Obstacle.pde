class Obstacle {
  PVector start;
  PVector end;
  float size;

  Obstacle(PVector initStart, PVector initEnd, float initSize) {
    start = initStart;
    end = initEnd;
    size = initSize;
  }

  void show() {
    strokeWeight(size);
    stroke(0, 0, 255);
    line(start.x, start.y, end.x, end.y);
  }

  boolean checkCollision(PVector posToCheck, float radius) {
    float distance = getDistance(start.x, start.y, end.x, end.y, posToCheck.x, posToCheck.y).z;
    if(distance <= size/2 + radius/2.0){
      return true;
    }
    return false;
  }

  PVector getDistance( float x1, float y1, float x2, float y2, float x, float y ) {
    PVector result = new PVector(); 

    float dx = x2 - x1; 
    float dy = y2 - y1; 
    float d = sqrt( dx*dx + dy*dy ); 
    float ca = dx/d; // cosine
    float sa = dy/d; // sine 

    float mX = (-x1+x)*ca + (-y1+y)*sa; 

    if ( mX <= 0 ) {
      result.x = x1; 
      result.y = y1;
    } else if ( mX >= d ) {
      result.x = x2; 
      result.y = y2;
    } else {
      result.x = x1 + mX*ca; 
      result.y = y1 + mX*sa;
    }

    dx = x - result.x; 
    dy = y - result.y; 
    result.z = sqrt( dx*dx + dy*dy ); 

    return result;
  }
}