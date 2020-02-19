class Population {
  Dot[] dots;
  float fitnessSum;
  int generation = 1;

  int bestDot = 0;
  int minStep;
  
  boolean goalReached = false;
  
  float bestFitness = 0;

  Population(int size) {
    dots = new Dot[size];
    for (int i = 0; i < size; i++) {
      dots[i] = new Dot();
    }
    
    minStep = dots[0].brain.directions.length-1;
  }

  //-------------------------------------------------------------------------------------

  void show() {
    for (int i = 1; i < dots.length; i++) {
      dots[i].show();
    }
    dots[0].show();
  }

  //-------------------------------------------------------------------------------------

  void update() {
    for (int i = 0; i < dots.length; i++) {
      if (dots[i].brain.step > minStep) {
        dots[i].dead = true;
      } else {
        dots[i].update();
      }
      
      // Check Obstacle Collisions
      checkObstacleCollisions(i);
    }
  }

  //--------------------------------------------------------------------------------------

  void calculateFitness() {    
    // Calculate shared fitnesses
    float minDistance = sharedFitnessDistance;  // 50
    float shareParam = sharedFitnessParameter;    // 5
    float[] sharedFitnesses = new float[dots.length];
    for (int i = 0; i < dots.length; i++) {
      
      // Calculate dot goal distance fitness
      dots[i].calculateFitness();
      
      // reset share number
      dots[i].shareNumber = 0;
      // Only calculate shared fitness if dot has not reached goal
      if(dots[i].reachedGoal)
      {
        sharedFitnesses[i] = dots[i].fitness;
      }
      else
      {
        sharedFitnesses[i] = computeSharedFitnessValue(i, minDistance, shareParam);
      }
    }
    
    // Normalize Fitnesses
    //normalizeFitnesses();
    
    // Set fitnesses to shared fitness values
    bestFitness = 0;
    for (int i = 0; i < dots.length; i++) {
      dots[i].fitness = sharedFitnesses[i];
      
      if(dots[i].fitness > bestFitness)
      {
        bestFitness = dots[i].fitness;
      }
    }
    
    // set relative fitnesses
    for(int i = 0; i < dots.length; i++)
    {
      dots[i].relativeFitness = dots[i].fitness / bestFitness;
    }
  }
  
  //--------------------------------------------------------------------------------------
  
  void normalizeFitnesses()
  // Make lower fitnesses closer to bigger fitnesses so that our next generation isn't full of the best.
  {
    float maxFitness = 0;
    for(int i = 0; i < dots.length; i++)
    {
      if(dots[i].fitness > maxFitness)
        maxFitness = dots[i].fitness;
    }
    
    for(int i = 0; i < dots.length; i++)
    {
      dots[i].fitness = sqrt(dots[i].fitness * maxFitness * maxFitness);
    }
  }
  
  //--------------------------------------------------------------------------------------
  
  float getWallFitness(int index)
  // Adds the fitness measurement for the number of walls in the way of the goal
  {
    // get the number of walls between dot and goal
    PVector dotPos = dots[index].pos;
    PVector goalPosition = goal.pos;
    
    int wallCount = wallCountBetweenPoints(dotPos, goalPosition);
    
    // get normalized wall percentage
    float wallPercentage = wallCount / (float)obstacles.size();
    wallPercentage *= 100;
    return wallPercentage;
  }
  
  //--------------------------------------------------------------------------------------
  
  public float computeSharedFitnessValue(int index, float minDistance, float shareParam)
  // Index : index of the dot for which shared fitness will be calculated.
  // minDistance : any dot closer than minDistance will share fitness with the current dot.
  // shareParam : a paramter that defines how much influence sharing has. 
  //   Higher = more sharing.
  {
    float denominator = 1;
    
    for(int j = 0; j < dots.length; j++)
    {
      if(j != index)
      {
        float distance = euclideanDistance(dots[index], dots[j]);
        //float distance = hammingDistance(dots[index], dots[j]);
        
        if(distance < minDistance)
        {
          //denominator += (1-(distance/shareParam));
          denominator += shareParam / distance;
          dots[index].shareNumber++;
        }  
      }
    } 
    
    dots[index].debugSharedFit = denominator;
    
    return dots[index].fitness / denominator;
  }
  
  //--------------------------------------------------------------------------------------
  
  float hammingDistance(Dot dot1, Dot dot2)
  {
    float distance = 0;
    for(int i = 0; i < dot1.brain.directions.length; i++)
    {
      distance += PVector.dist(dot1.brain.directions[i], dot2.brain.directions[i]);
    }
    return distance;
  }
  
  //--------------------------------------------------------------------------------------
  
  public float euclideanDistance(Dot dot1, Dot dot2)
  {
    // If wall is inbetween dots return distance of "Infinity".

    if(wallBetweenDots(dot1, dot2))
    {
      return 99999;
    }

    return PVector.dist(dot1.pos, dot2.pos);
  }
  
  //--------------------------------------------------------------------------------------
  
  public int wallCountBetweenPoints(PVector p1, PVector p2)
  {
    int wallCount = 0;
    Line pointLine = new Line(p1, p2);
    
    for(int i = 0; i < obstacles.size(); i++)
    {
      Line obstacleLine = new Line(obstacles.get(i).start, obstacles.get(i).end);
      float x = obstacleLine.intersectPoint(pointLine);
      float y = obstacleLine.y(x);
      
      // Does the intersection point (x, y) fall within the bounding box of the two dots?
      float xMin = min(p1.x, p1.x);
      float xMax = max(p1.x, p1.x);
      float yMin = min(p1.y, p1.y);
      float yMax = max(p1.y, p1.y);
      
      if( x >= xMin && x <= xMax)
      {
        if( y >= yMin && y <= yMax)
        {
          wallCount++;
        }
      }
    }
    return wallCount;
  }
  
  public boolean wallBetweenDots(Dot dot1, Dot dot2)
  {
    Line dotLine = new Line(dot1.pos, dot2.pos);
    
    for(int i = 0; i < obstacles.size(); i++)
    {
      Line obstacleLine = new Line(obstacles.get(i).start, obstacles.get(i).end);
      float x = obstacleLine.intersectPoint(dotLine);
      float y = obstacleLine.y(x);
      
      // Does the intersection point (x, y) fall within the bounding box of the two dots?
      float xMin = min(dot1.pos.x, dot2.pos.x);
      float xMax = max(dot1.pos.x, dot2.pos.x);
      float yMin = min(dot1.pos.y, dot2.pos.y);
      float yMax = max(dot1.pos.y, dot2.pos.y);
      
      if( x >= xMin && x <= xMax)
      {
        if( y >= yMin && y <= yMax)
        {
          return true;
        }
      }
    }
    return false;
  }

  //--------------------------------------------------------------------------------------

  boolean allDotsDead() {
    for (int i = 0; i < dots.length; i++) {
      if (!dots[i].dead && !dots[i].reachedGoal) {
        return false;
      }
    }
    return true;
  }

  //-----------------------------------------------------------------------------------------

  void naturalSelection() {
    Dot[] newDots = new Dot[dots.length];
    setBestDot();
    calculateFitnessSum();

    newDots[0] = dots[bestDot].getChild(); // put best dot from last generation into next generation
    newDots[0].isBest = true;

    for (int i = 1; i < newDots.length; i++) {

      // Select parent based on fitness
      Dot parent = selectParent();

      // Get baby from them
      newDots[i] = parent.getChild();
    } 

    dots = newDots.clone();
    generation++;
    println(generation);
  }

  //-----------------------------------------------------------------------------------------

  void calculateFitnessSum() {
    fitnessSum = 0;
    for (int i = 0; i < dots.length; i++) {
      fitnessSum += dots[i].fitness;
    }
  }

  //-----------------------------------------------------------------------------------------

  Dot selectParent() {
    float rand = random(fitnessSum);

    float runningSum = 0;

    for (int i = 0; i < dots.length; i++) {
      runningSum += dots[i].fitness;
      if (runningSum > rand) {
        return dots[i];
      }
    }

    // Should never reach this point
    // (but now we are...)
    // So lets return a new random dot
    return new Dot();
  }

  //-----------------------------------------------------------------------------------------

  void mutateChildren() {
    for (int i = 1; i < dots.length; i++) {
      dots[i].brain.mutate();
    }
  }

  //-----------------------------------------------------------------------------------------

  void setBestDot() {
    float max = 0;
    int maxIndex = 0;
    for (int i = 0; i < dots.length; i++) {
      if (dots[i].fitness > max) {
        max = dots[i].fitness;
        maxIndex = i;
      }
    }

    bestDot = maxIndex;

    if (dots[bestDot].reachedGoal) {
      minStep = dots[bestDot].brain.step;
      goalReached = true;
    }
  }

  //-----------------------------------------------------------------------------------------

  void checkObstacleCollisions(int i) {
    for(int l = 0; l < obstacles.size(); l++){
        // if collision, kill dot
        if(obstacles.get(l).checkCollision(dots[i].pos, dots[i].radius)){
          dots[i].dead = true;
        }
      }
  }
  
  //----------------------------------------------------------------------------------------
  
  void populationResetMinStep(){
    minStep = dots[0].brain.directions.length-1;
  }
}