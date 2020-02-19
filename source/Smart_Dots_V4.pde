final float INFINITY = pow(1, 10000000);
final int FPS = 60;
final float DELTATIME = 1.0/FPS;

// Variables to change
int fastUpdateCount = 1000;  // 1000
int populationSize = 1000;
float sharedFitnessDistance = 100;
float sharedFitnessParameter = 20;


public String state = "Menu";

boolean newKeyPressed = false;

boolean inspectFitness = false;
boolean fitnessInspectionComplete = false;

int updateCount = 1;


float goldenRatio = 1.618;
float fingerOffset = 30;

Population test;
Goal goal;

ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

boolean dotsPaused = true;
boolean drawingObstacle = true;
boolean drawingGoal = false;
boolean menuClick = false;
float menuPosY;

float obstacleSize = 10;

Button obstacleButton;
Button goalButton;
Button playButton;
Button resetButton;
Button increaseSpeedButton;
Button normalSpeedButton;
Button backButton;
Button clearButton;

PShape playIcon;
float iconScalingWidth = 1.618*0.75*0.5;
float iconScalingHeight = 0.75;

float textSize;

boolean setUpdatesAfterReachingGoal = false; // Used to set updates to 1 only once.

// Menu Variables
Button menuButton_start;
Button menuButton_defaults;
boolean mainMenuSelection = false;

int default_populationSize;
float default_sharedFitnessDistance;
float default_sharedFitnessParameter;
int default_fastUpdateCount;

TextBox tBox_populationSize;
TextBox tBox_sharedFitnessDistance;
TextBox tBox_sharedFitnessParameter;
TextBox tBox_fastUpdateCount;

//-----------------------------------------------------------------------------------------------

public void settings() {
  //fullScreen();
  size(800,800); // 1000, 1000
  
}

void setup() {
  frameRate(FPS);
  //size(screen.width, screen.height);
  //size(500,1000);
  //fullScreen();
  Initialize();

  textSize = height / 32.0;

  float goldenMultiple = (1.0/pow(goldenRatio, 4));
  menuPosY = height - (height * goldenMultiple);
  float menuHeight = height - menuPosY;

  float buttonMarginX = 10;
  float buttonMarginY = 10;
  int buttonCount = 8;

  PVector buttonSize = new PVector((width - buttonMarginX * (buttonCount + 1)) / buttonCount, menuHeight - (2 * buttonMarginY));
  //PVector buttonSize = new PVector(100,100);
  float buttonHeight = menuPosY + menuHeight/2.0;
  int buttonIndex = 1;
  obstacleButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  goalButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  playButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  normalSpeedButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  increaseSpeedButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  resetButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  clearButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  backButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0/2.0)) * buttonSize.x, buttonHeight), buttonSize, "Back");

  //obstacles.add(new Obstacle(new PVector(width/2-(width/4),height/2), new PVector(width/2+(width/4), height/2), 20));

  // play icon shape
  
  float playHeight = playButton.size.y * iconScalingHeight;
  float playWidth = playButton.size.x * iconScalingWidth;
  playIcon = createShape();
  playIcon.beginShape();
  playIcon.fill(0, 200, 0);
  playIcon.stroke(0);
  playIcon.strokeWeight(3);
  playIcon.vertex(-playWidth/2.0, playHeight/2.0); // top left
  playIcon.vertex(playWidth/2.0, 0); // right middle
  playIcon.vertex(-playWidth/2.0, -playHeight/2.0); // bottom left
  playIcon.endShape(CLOSE);
  

  //shapeMode(CENTER);
  
  // Setup Main Menu stuff
  default_populationSize = populationSize;
  default_sharedFitnessDistance = sharedFitnessDistance;
  default_sharedFitnessParameter = sharedFitnessParameter;
  default_fastUpdateCount = fastUpdateCount;
  
  float mainButtonTextSize = 32;
  buttonSize = new PVector(width*0.25, width*0.125);
  menuButton_start = new Button(new PVector(3*width/4.0, height-height/8.0), buttonSize, "Start", mainButtonTextSize);
  menuButton_defaults = new Button(new PVector(width/4.0, height - height/8.0), buttonSize, "Defaults", mainButtonTextSize);
  
  int tBoxNumber = 4;
  int tIndex = 0;
  PVector tBoxSize = new PVector(buttonSize.x, buttonSize.y*0.75);
  float tBoxMargin = 50;
  float yOffset = width/2.0 - ((tBoxMargin + tBoxSize.y)*tBoxNumber) * 0.5;
  tBox_populationSize = new TextBox(new PVector(width/2.0, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(populationSize));
  tBox_populationSize.setMinMax(0, 1000);
  tIndex++;
  tBox_sharedFitnessDistance = new TextBox(new PVector(width/2.0, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(int(sharedFitnessDistance)));
  tBox_sharedFitnessDistance.setMinMax(0, int(sqrt(pow(width,2) + pow(height,2))));
  tIndex++;
  tBox_sharedFitnessParameter = new TextBox(new PVector(width/2.0, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(int(sharedFitnessParameter)));
  tBox_sharedFitnessParameter.setMinMax(0, 400);
  tIndex++;
  tBox_fastUpdateCount = new TextBox(new PVector(width/2.0, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(int(fastUpdateCount)));
  tBox_fastUpdateCount.setMinMax(1, 1000);
}

//----------------------------------------------------------------------------------------------

void Initialize() {
  test = new Population(populationSize);  // 1000
  goal = new Goal(new PVector(width/2, 10));
  obstacles.clear();
}

//---------------------------------------------------------------------------------------------


void draw() {
  background(200);
  
  if(state.equals("Menu"))
  {
    mainMenuSelection = true;
    
    // Display Title
    textSize(64);
    fill(0);
    String text = "Smart Dots";
    text(text, width/2.0 - textWidth(text)/2.0, height/8.0);
    
    menuButton_start.show();
    menuButton_defaults.show();
    
    // text Boxes
    float tSize = 25;
    textSize(tSize);
    
    float labelX = width/2.0 - tBox_populationSize.size.x * 0.75;
    float labelX2 = width/2.0 + tBox_populationSize.size.x * 0.75;
    
    String label1 = "Population Size:";
    textAlign(RIGHT);
    fill(0);
    text(label1, labelX, tBox_populationSize.pos.y);
    textAlign(LEFT);
    String l1 = tBox_populationSize.getMinMaxString();
    text(l1, labelX2, tBox_populationSize.pos.y);
    tBox_populationSize.show();
    
    textSize(tSize);
    String label2 = "Shared \nFitness Distance:";
    textAlign(RIGHT);
    fill(0);
    text(label2, labelX, tBox_sharedFitnessDistance.pos.y);
    textAlign(LEFT);
    String l2 = tBox_sharedFitnessDistance.getMinMaxString();
    text(l2, labelX2, tBox_sharedFitnessDistance.pos.y);
    tBox_sharedFitnessDistance.show();
    
    textSize(tSize);
    String label3 = "Shared \nFitness Parameter:";
    textAlign(RIGHT);
    fill(0);
    text(label3, labelX, tBox_sharedFitnessParameter.pos.y);
    textAlign(LEFT);
    String l3 = tBox_sharedFitnessParameter.getMinMaxString();
    text(l3, labelX2, tBox_sharedFitnessParameter.pos.y);
    tBox_sharedFitnessParameter.show();
    
    textSize(tSize);
    String label4 = "Fast \nUpdate Speed:";
    textAlign(RIGHT);
    fill(0);
    text(label4, labelX, tBox_fastUpdateCount.pos.y);
    textAlign(LEFT);
    String l4 = tBox_fastUpdateCount.getMinMaxString();
    text(l4, labelX2, tBox_fastUpdateCount.pos.y);
    tBox_fastUpdateCount.show();

    textAlign(LEFT);
    
    if(menuButton_start.clicked())
    {
      state = "Dots";
      mousePressed = false;
      
      // set values for dots
      populationSize = tBox_populationSize.getValue();
      sharedFitnessDistance = tBox_sharedFitnessDistance.getValue();
      sharedFitnessParameter = tBox_sharedFitnessParameter.getValue();
      fastUpdateCount = tBox_fastUpdateCount.getValue();
      
      // If increase speed is active
      if(updateCount > 1)
      {
        updateCount = fastUpdateCount;
      }
    }
    else if(menuButton_defaults.clicked())
    {
      tBox_populationSize.setText(str(default_populationSize));
      tBox_sharedFitnessDistance.setText(str(int(default_sharedFitnessDistance)));
      tBox_sharedFitnessParameter.setText(str(int(default_sharedFitnessParameter)));
      tBox_fastUpdateCount.setText(str(default_fastUpdateCount));
    }
  }
  else if(state.equals("Dots"))
  {
    goal.show();
    showObstacles();
  
    if (!dotsPaused) {
      for (int i = 0; i < updateCount; i++) {
        if (test.allDotsDead()) {
          // Genetic Algorithm!!!
          test.calculateFitness();
          
          if(inspectFitness)
          {
            dotsPaused = true; 
            if(mousePressed)
            {
              dotsPaused = false;
              fitnessInspectionComplete = true;
            }
          }
          else
          {
            test.naturalSelection();
            test.mutateChildren();
          }
        }
  
        test.update();
      }
    }
  
    test.show();
  
    if (dotsPaused) 
    {
      // Show Menu
      showMenu();
      
      if(inspectFitness)
      {
        inspectFitnesses();
      }
    }
    
    if(!dotsPaused)
    {
      if(inspectFitness)
      {
        if(fitnessInspectionComplete)
        {
          test.naturalSelection();
          test.mutateChildren();
          fitnessInspectionComplete = false;
        }
      }
    }
    
    if(test.goalReached && !setUpdatesAfterReachingGoal){
      setUpdatesAfterReachingGoal = true;
      updateCount = 1;
      println("Reached Goal!");
    }
  
    // Show Generation Text
    textSize(textSize);
    noStroke();
    fill(0, 100);
    text("Gen: " + test.generation, 0, textSize);
    
    //testLineIntersectWithObstacles();
  }
  
  newKeyPressed = false;
}

void keyPressed()
{
  newKeyPressed = true;
}

//--------------------------------------------------------------------------------------------

void inspectFitnesses()
{
  String fitness = "NA";
  String debugSharedFit = "NA";
  String shareNumber = "NA";
  String relativeFitness = "NA";
  
  // check for mouseover of dot
  for(int i = 0; i < test.dots.length; i++)
  {
    PVector dotPos = test.dots[i].pos;
    float dotRad = test.dots[i].radius;
    PVector mousePos = new PVector(mouseX, mouseY);
    float distance = PVector.dist(mousePos, dotPos);
    if(distance <= dotRad/2.0)
    {
      fitness = str(test.dots[i].fitness);
      debugSharedFit = str(test.dots[i].debugSharedFit);
      shareNumber = str(test.dots[i].shareNumber);
      relativeFitness = str(test.dots[i].relativeFitness);
    }
  }
  
  float textSize = 32;
  textSize(textSize);
  fill(0);
  String text = "Fitness: " + fitness;
  text(text, width * 3.0 / 4.0, textSize*1.5);
  String text2 = "Denom: " + debugSharedFit;
  text(text2, width * 3.0 / 4.0, textSize*2.5);
  String text3 = "shareNumber: " + shareNumber;
  text(text3, width * 3.0 / 4.0,textSize*3.5);
  String text4 = "Relative Fitness: " + relativeFitness;
  text(text4, width * 3.0 / 4.0,textSize*4.5);
}

//--------------------------------------------------------------------------------------------

void testLineIntersectWithObstacles()
{
  for(int i = 0; i < obstacles.size(); i++)
  {
    for(int j = 0; j < obstacles.size(); j++)
    {
      // If not same obstacle.
      if(i != j)
      {
        Line obstacleLine1 = new Line(obstacles.get(i).start, obstacles.get(i).end);
        Line obstacleLine2 = new Line(obstacles.get(j).start, obstacles.get(j).end);
        // Draw intersect point
        float x = obstacleLine1.intersectPoint(obstacleLine2);
        float y = obstacleLine1.y(x);
        fill(0,255,255);
        ellipse(x, y, 25, 25);
      }
    }
  }
}

//--------------------------------------------------------------------------------------------

void showObstacles() {
  for (int i = 0; i < obstacles.size(); i++) {
    obstacles.get(i).show();
  }
}

//--------------------------------------------------------------------------------------------

void mouseClicked() {
}

void mousePressed() {

  if (!drawingGoal && !drawingObstacle && !mainMenuSelection) {
    //if (mouseButton == LEFT) {
    if (!dotsPaused) {
      dotsPaused = true;
    } else if (dotsPaused && (mouseY < menuPosY)) {
      dotsPaused = false;
    }
    //}
  }

  //if (mouseButton == LEFT) {
  if (mouseY >= menuPosY) {
    menuClick = true;
  } else {
    menuClick = false;
  }

  if (!menuClick && !mainMenuSelection) {
    // left here
    if (dotsPaused && !menuClick) {
      //if press is not on menu
      if (drawingGoal) {
        goalToFinger();
      }

      if (drawingObstacle) {
        obstacles.add(new Obstacle(new PVector(mouseX, mouseY), new PVector(mouseX, mouseY), obstacleSize));
      }
    }
  } else {
    // Clicked On Menu!
    // Check Buttons!!!
    checkButtons();
  }
  //}
}

void mouseDragged() {
  //if (mouseButton == LEFT) {
  if (drawingGoal && !menuClick) {
    goalToFinger();
    resetMinStep();
  }

  if (drawingObstacle && !menuClick && !mainMenuSelection) {
    obstacles.get(obstacles.size()-1).end = new PVector(mouseX, mouseY);
    resetMinStep();
  }
  //}
}

void mouseReleased()
{
  if(state.equals("Dots"))
  {
    mainMenuSelection = false;
  }
}

void resetMinStep() {
  test.populationResetMinStep();
}

void goalToFinger() {
  goal.pos = new PVector(mouseX, mouseY - fingerOffset);
}

//-------------------------------------------------------------------------------------
void showMenu() {
  //strokeWeight(3);
  //stroke(0);
  noStroke();
  fill(0);
  rect(0, menuPosY, width, height);
  showButtons();
}

//---------------------------------------------------------------------------------------------

void showButtons() {
  obstacleButton.show(drawingObstacle);
  // Draw Icon
  strokeWeight(obstacleSize);
  stroke(0, 0, 255);
  float offset = obstacleButton.size.x/4.0;
  line(obstacleButton.pos.x - offset, obstacleButton.pos.y, obstacleButton.pos.x + obstacleButton.size.x/4, obstacleButton.pos.y);

  goalButton.show(drawingGoal);
  fill(255, 0, 0);
  strokeWeight(2);
  stroke(0);
  ellipse(goalButton.pos.x, goalButton.pos.y, goal.size, goal.size);

  playButton.show();
  shape(playIcon, playButton.pos.x, playButton.pos.y);

  resetButton.show();
  /*
  noFill();
  strokeWeight(5);
  stroke(0);
  float circleRadius = (resetButton.size.y * iconScalingHeight) * 0.75;
  ellipse(resetButton.pos.x, resetButton.pos.y, circleRadius, circleRadius); 
  float arrowOffsetX = circleRadius/4.0;
  float arrowOffsetY = circleRadius/4.0;
  PVector arrowStart = new PVector(resetButton.pos.x + circleRadius/2.0, resetButton.pos.y + 5);
  line(arrowStart.x, arrowStart.y, resetButton.pos.x + circleRadius/2.0 - arrowOffsetX, resetButton.pos.y - arrowOffsetY);
  line(arrowStart.x, arrowStart.y, resetButton.pos.x + circleRadius/2.0 + arrowOffsetX*0.75, resetButton.pos.y - arrowOffsetY);
  */
  float textSize = resetButton.size.x/5.0;
  textSize(textSize);
  String text1 = "Reset";
  String text2 = "Dots";
  PVector buttonCenter = resetButton.pos;
  float margin = resetButton.size.y/10.0;
  fill(0);
  text(text1, buttonCenter.x - textWidth(text1)/2.0, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0, buttonCenter.y + textSize - margin);
  
  textSize = normalSpeedButton.size.x/5.0;
  textSize(textSize);
  normalSpeedButton.show(updateCount == 1);
  buttonCenter = new PVector(normalSpeedButton.pos.x, normalSpeedButton.pos.y);
  fill(0);
  text1 = "Normal";
  text2 = "Speed";
  margin = normalSpeedButton.size.y/10.0;
  text(text1, buttonCenter.x - textWidth(text1)/2.0, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0, buttonCenter.y + textSize - margin);
  
  increaseSpeedButton.show(updateCount > 1);
  buttonCenter = new PVector(increaseSpeedButton.pos.x, increaseSpeedButton.pos.y);
  fill(0);
  text1 = "Fast";
  text2 = "Speed";
  margin = normalSpeedButton.size.y/10.0;
  text(text1, buttonCenter.x - textWidth(text1)/2.0, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0, buttonCenter.y + textSize - margin);
  
  backButton.show();
  
  
  clearButton.show();
  textSize(textSize);
  buttonCenter = clearButton.pos;
  fill(0);
  text1 = "Clear";
  text2 = "Maze";
  margin = normalSpeedButton.size.y/10.0;
  text(text1, buttonCenter.x - textWidth(text1)/2.0, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0, buttonCenter.y + textSize - margin);
}

//---------------------------------------------------------------------------------------------

void checkButtons() {
  if(!mainMenuSelection)
  {
    if (obstacleButton.clicked()) 
    {
      drawingObstacle = true;
      drawingGoal = false;
    } 
    else if (goalButton.clicked()) 
    {
      drawingGoal = true;
      drawingObstacle = false;
    }
    else if (playButton.clicked()) 
    {
      dotsPaused = false;
      drawingGoal = false;
      drawingObstacle = false;
    } 
    else if (resetButton.clicked()) 
    {
      // only reset dots
      test = new Population(populationSize);  // 1000
    } 
    else if(clearButton.clicked())
    {
      setUpdatesAfterReachingGoal = false;
      reset();
    }
    else if(increaseSpeedButton.clicked())
    {
      updateCount = fastUpdateCount;
    }
    else if(normalSpeedButton.clicked())
    {
      updateCount = 1;
    }
    else if(backButton.clicked())
    {
      state = "Menu";
    }
  }
}

//---------------------------------------------------------------------------------------------

void reset() {
  Initialize();
  dotsPaused = true;
  drawingGoal = false;
  drawingObstacle = false;
}