class Dot {
  PVector pos;
  PVector vel;
  PVector acc;
  Brain brain;

  boolean dead = false;
  boolean reachedGoal = false;
  boolean isBest = false;

  float fitness = 0;
  float relativeFitness = 0; // from 0 - 1
  
  float radius = 8;
  
  // Debuggin
  float debugSharedFit = 0;
  int shareNumber = 0;

  Dot() {
    brain = new Brain(500);

    pos = new PVector(width/2, height/2);
    vel = new PVector(0, 0);
    acc = new PVector(0, 0);
  }

  //-----------------------------------------------------------------------------------

  void show() {
    strokeWeight(1);
    stroke(0);
    float r = 0;
    if (isBest) {
      strokeWeight(3);
      fill(0,255,0);
      r = radius*0.5;
    } else {
      float v = relativeFitness * 255;
      fill(v);
    }
    ellipse(pos.x, pos.y, radius + r, radius + r); // 4
  }

  //-------------------------------------------------------------------------------------

  void move() {
    if (brain.directions.length > brain.step) {
      acc = brain.directions[brain.step];
      brain.step++;
    } else {
      // dot dies if it has no more directions to follow
      dead = true;
    }

    vel.add(acc);
    vel.limit(5);
    pos.add(vel);
  }

  void update() {
    if (!dead && !reachedGoal) {
      move();
      if (pos.x < 2 || pos.y < 2 || pos.x > width-2 || pos.y > height-2) {
        dead = true;
      } else if (dist(pos.x, pos.y, goal.pos.x, goal.pos.y) < (goal.size/2)) {
        // if reached goal
        reachedGoal = true;
      }
    }
  }

  //-----------------------------------------------------------------------------------------

  void calculateFitness() {
    if (reachedGoal) {
      fitness = 1.0/16.0 + 10000.0/(float)(brain.step * brain.step);
    } else {
      float distanceToGoal = dist(pos.x, pos.y, goal.pos.x, goal.pos.y);
    //fitness = 1.0/(distanceToGoal * distanceToGoal);
      fitness = 1.0 / distanceToGoal;
    }
  }

  //-----------------------------------------------------------------------------------------

  Dot getChild() {
    Dot child = new Dot();
    child.relativeFitness = relativeFitness;
    child.brain = brain.clone();
    return child;
  }
}