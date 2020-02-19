import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Smart_Dots_V4 extends PApplet {

final float INFINITY = pow(1, 10000000);
final int FPS = 60;
final float DELTATIME = 1.0f/FPS;

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


float goldenRatio = 1.618f;
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
float iconScalingWidth = 1.618f*0.75f*0.5f;
float iconScalingHeight = 0.75f;

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

public void setup() {
  frameRate(FPS);
  //size(screen.width, screen.height);
  //size(500,1000);
  //fullScreen();
  Initialize();

  textSize = height / 32.0f;

  float goldenMultiple = (1.0f/pow(goldenRatio, 4));
  menuPosY = height - (height * goldenMultiple);
  float menuHeight = height - menuPosY;

  float buttonMarginX = 10;
  float buttonMarginY = 10;
  int buttonCount = 8;

  PVector buttonSize = new PVector((width - buttonMarginX * (buttonCount + 1)) / buttonCount, menuHeight - (2 * buttonMarginY));
  //PVector buttonSize = new PVector(100,100);
  float buttonHeight = menuPosY + menuHeight/2.0f;
  int buttonIndex = 1;
  obstacleButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  goalButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  playButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  normalSpeedButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  increaseSpeedButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  resetButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  clearButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize);
  buttonIndex++;
  backButton = new Button(new PVector(buttonIndex * buttonMarginX + (buttonIndex - (1.0f/2.0f)) * buttonSize.x, buttonHeight), buttonSize, "Back");

  //obstacles.add(new Obstacle(new PVector(width/2-(width/4),height/2), new PVector(width/2+(width/4), height/2), 20));

  // play icon shape
  
  float playHeight = playButton.size.y * iconScalingHeight;
  float playWidth = playButton.size.x * iconScalingWidth;
  playIcon = createShape();
  playIcon.beginShape();
  playIcon.fill(0, 200, 0);
  playIcon.stroke(0);
  playIcon.strokeWeight(3);
  playIcon.vertex(-playWidth/2.0f, playHeight/2.0f); // top left
  playIcon.vertex(playWidth/2.0f, 0); // right middle
  playIcon.vertex(-playWidth/2.0f, -playHeight/2.0f); // bottom left
  playIcon.endShape(CLOSE);
  

  //shapeMode(CENTER);
  
  // Setup Main Menu stuff
  default_populationSize = populationSize;
  default_sharedFitnessDistance = sharedFitnessDistance;
  default_sharedFitnessParameter = sharedFitnessParameter;
  default_fastUpdateCount = fastUpdateCount;
  
  float mainButtonTextSize = 32;
  buttonSize = new PVector(width*0.25f, width*0.125f);
  menuButton_start = new Button(new PVector(3*width/4.0f, height-height/8.0f), buttonSize, "Start", mainButtonTextSize);
  menuButton_defaults = new Button(new PVector(width/4.0f, height - height/8.0f), buttonSize, "Defaults", mainButtonTextSize);
  
  int tBoxNumber = 4;
  int tIndex = 0;
  PVector tBoxSize = new PVector(buttonSize.x, buttonSize.y*0.75f);
  float tBoxMargin = 50;
  float yOffset = width/2.0f - ((tBoxMargin + tBoxSize.y)*tBoxNumber) * 0.5f;
  tBox_populationSize = new TextBox(new PVector(width/2.0f, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(populationSize));
  tBox_populationSize.setMinMax(0, 1000);
  tIndex++;
  tBox_sharedFitnessDistance = new TextBox(new PVector(width/2.0f, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(PApplet.parseInt(sharedFitnessDistance)));
  tBox_sharedFitnessDistance.setMinMax(0, PApplet.parseInt(sqrt(pow(width,2) + pow(height,2))));
  tIndex++;
  tBox_sharedFitnessParameter = new TextBox(new PVector(width/2.0f, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(PApplet.parseInt(sharedFitnessParameter)));
  tBox_sharedFitnessParameter.setMinMax(0, 400);
  tIndex++;
  tBox_fastUpdateCount = new TextBox(new PVector(width/2.0f, tIndex * (tBoxSize.y + tBoxMargin) + yOffset), tBoxSize, str(PApplet.parseInt(fastUpdateCount)));
  tBox_fastUpdateCount.setMinMax(1, 1000);
}

//----------------------------------------------------------------------------------------------

public void Initialize() {
  test = new Population(populationSize);  // 1000
  goal = new Goal(new PVector(width/2, 10));
  obstacles.clear();
}

//---------------------------------------------------------------------------------------------


public void draw() {
  background(200);
  
  if(state.equals("Menu"))
  {
    mainMenuSelection = true;
    
    // Display Title
    textSize(64);
    fill(0);
    String text = "Smart Dots";
    text(text, width/2.0f - textWidth(text)/2.0f, height/8.0f);
    
    menuButton_start.show();
    menuButton_defaults.show();
    
    // text Boxes
    float tSize = 25;
    textSize(tSize);
    
    float labelX = width/2.0f - tBox_populationSize.size.x * 0.75f;
    float labelX2 = width/2.0f + tBox_populationSize.size.x * 0.75f;
    
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
      tBox_sharedFitnessDistance.setText(str(PApplet.parseInt(default_sharedFitnessDistance)));
      tBox_sharedFitnessParameter.setText(str(PApplet.parseInt(default_sharedFitnessParameter)));
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

public void keyPressed()
{
  newKeyPressed = true;
}

//--------------------------------------------------------------------------------------------

public void inspectFitnesses()
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
    if(distance <= dotRad/2.0f)
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
  text(text, width * 3.0f / 4.0f, textSize*1.5f);
  String text2 = "Denom: " + debugSharedFit;
  text(text2, width * 3.0f / 4.0f, textSize*2.5f);
  String text3 = "shareNumber: " + shareNumber;
  text(text3, width * 3.0f / 4.0f,textSize*3.5f);
  String text4 = "Relative Fitness: " + relativeFitness;
  text(text4, width * 3.0f / 4.0f,textSize*4.5f);
}

//--------------------------------------------------------------------------------------------

public void testLineIntersectWithObstacles()
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

public void showObstacles() {
  for (int i = 0; i < obstacles.size(); i++) {
    obstacles.get(i).show();
  }
}

//--------------------------------------------------------------------------------------------

public void mouseClicked() {
}

public void mousePressed() {

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

public void mouseDragged() {
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

public void mouseReleased()
{
  if(state.equals("Dots"))
  {
    mainMenuSelection = false;
  }
}

public void resetMinStep() {
  test.populationResetMinStep();
}

public void goalToFinger() {
  goal.pos = new PVector(mouseX, mouseY - fingerOffset);
}

//-------------------------------------------------------------------------------------
public void showMenu() {
  //strokeWeight(3);
  //stroke(0);
  noStroke();
  fill(0);
  rect(0, menuPosY, width, height);
  showButtons();
}

//---------------------------------------------------------------------------------------------

public void showButtons() {
  obstacleButton.show(drawingObstacle);
  // Draw Icon
  strokeWeight(obstacleSize);
  stroke(0, 0, 255);
  float offset = obstacleButton.size.x/4.0f;
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
  float textSize = resetButton.size.x/5.0f;
  textSize(textSize);
  String text1 = "Reset";
  String text2 = "Dots";
  PVector buttonCenter = resetButton.pos;
  float margin = resetButton.size.y/10.0f;
  fill(0);
  text(text1, buttonCenter.x - textWidth(text1)/2.0f, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0f, buttonCenter.y + textSize - margin);
  
  textSize = normalSpeedButton.size.x/5.0f;
  textSize(textSize);
  normalSpeedButton.show(updateCount == 1);
  buttonCenter = new PVector(normalSpeedButton.pos.x, normalSpeedButton.pos.y);
  fill(0);
  text1 = "Normal";
  text2 = "Speed";
  margin = normalSpeedButton.size.y/10.0f;
  text(text1, buttonCenter.x - textWidth(text1)/2.0f, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0f, buttonCenter.y + textSize - margin);
  
  increaseSpeedButton.show(updateCount > 1);
  buttonCenter = new PVector(increaseSpeedButton.pos.x, increaseSpeedButton.pos.y);
  fill(0);
  text1 = "Fast";
  text2 = "Speed";
  margin = normalSpeedButton.size.y/10.0f;
  text(text1, buttonCenter.x - textWidth(text1)/2.0f, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0f, buttonCenter.y + textSize - margin);
  
  backButton.show();
  
  
  clearButton.show();
  textSize(textSize);
  buttonCenter = clearButton.pos;
  fill(0);
  text1 = "Clear";
  text2 = "Maze";
  margin = normalSpeedButton.size.y/10.0f;
  text(text1, buttonCenter.x - textWidth(text1)/2.0f, buttonCenter.y - margin);
  text(text2, buttonCenter.x - textWidth(text2)/2.0f, buttonCenter.y + textSize - margin);
}

//---------------------------------------------------------------------------------------------

public void checkButtons() {
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

public void reset() {
  Initialize();
  dotsPaused = true;
  drawingGoal = false;
  drawingObstacle = false;
}
class Brain {
  PVector[] directions;
  int step = 0;

  Brain(int size) {
    directions = new PVector[size];
    randomize();
  }

  //----------------------------------------------------------------------------------------

  public void randomize() {
    for (int i = 0; i < directions.length; i++) {
      float randomAngle = random(2*PI);
      directions[i] = PVector.fromAngle(randomAngle);
    }
  }

  //-----------------------------------------------------------------------------------------

  public Brain clone() {
    Brain clone = new Brain(directions.length);
    for (int i = 0; i < directions.length; i++) {
      clone.directions[i] = directions[i].copy();
    }
    return clone;
  }

  //-----------------------------------------------------------------------------------------

  public void mutate() {
    float mutationRate = 0.01f; // 0.01
    for (int i = 0; i < directions.length; i++) {
      float rand = random(1);
      if (rand < mutationRate) {
        // set this direction as a random direction
        float randomAngle = random(2*PI);
        directions[i] = PVector.fromAngle(randomAngle);
      }
    }
  }
}
class Button {

  PVector pos;
  PVector size;
  String text;
  float textSize; // if <= 0, adaptive text size

  Button(PVector p, PVector s) {
    pos = p;
    size = s;
    text = "";
    textSize = -1;
  }
  
  Button(PVector p, PVector s, String txt)
  {
    pos = p;
    size = s;
    text = txt;
    textSize = -1;
  }
  
  Button(PVector p, PVector s, String txt, float ts)
  {
    pos = p;
    size = s;
    text = txt;
    textSize = ts;
  }

  public void show() {
    strokeWeight(3);
    stroke(100);
    
    if(clicked())
      fill(100);
    else if(mouseOver())
      fill(250);
    else
      fill(200);
    
    
    rect(pos.x - size.x/2, pos.y - size.y/2, size.x, size.y);
    
    if(text != "")
    {
      // draw text
      fill(0);
      float textMargin = 5;
      float textHeight = textSize;
      if(textHeight > 0)
        textSize(textHeight);
      float textWidth = textWidth(text);
      
      if(textHeight <= 0)
      {
        textHeight = size.y - 2*textMargin;
        // Resize text until it fits within button
        textSize(textHeight);
        textWidth = textWidth(text);
        
        while(textWidth >= size.x - 2 * textMargin)
        {
          textHeight -= textMargin;
          textSize(textHeight);
          textWidth = textWidth(text);
        }
        
      }
      
      text(text, pos.x - textWidth/2.0f, pos.y + textHeight/2.0f - 2 * textMargin);
    }
  }

  public void show(boolean selected) {
    if (selected) {
      strokeWeight(10);
      stroke(0, 200, 0);
      fill(200);
      rect(pos.x - size.x/2, pos.y - size.y/2, size.x, size.y);
    } else {
      show();
    }
  }
  
  public boolean mouseOver()
  {
    return checkInBounds(mouseX, mouseY);
  }
  
  public boolean clicked()
  {
    return mousePressed && mouseOver();
  }

  public boolean checkInBounds(float x, float y) {
    if (x > pos.x - size.x/2 && x < pos.x + size.x/2) {
      if (y > pos.y - size.y/2 && y < pos.y + size.y/2) {
        return true;
      }
    }
    return false;
  }

  public boolean checkInBounds(PVector p) {
    return checkInBounds(p.x, p.y);
  }
}
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

  public void show() {
    strokeWeight(1);
    stroke(0);
    float r = 0;
    if (isBest) {
      strokeWeight(3);
      fill(0,255,0);
      r = radius*0.5f;
    } else {
      float v = relativeFitness * 255;
      fill(v);
    }
    ellipse(pos.x, pos.y, radius + r, radius + r); // 4
  }

  //-------------------------------------------------------------------------------------

  public void move() {
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

  public void update() {
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

  public void calculateFitness() {
    if (reachedGoal) {
      fitness = 1.0f/16.0f + 10000.0f/(float)(brain.step * brain.step);
    } else {
      float distanceToGoal = dist(pos.x, pos.y, goal.pos.x, goal.pos.y);
    //fitness = 1.0/(distanceToGoal * distanceToGoal);
      fitness = 1.0f / distanceToGoal;
    }
  }

  //-----------------------------------------------------------------------------------------

  public Dot getChild() {
    Dot child = new Dot();
    child.relativeFitness = relativeFitness;
    child.brain = brain.clone();
    return child;
  }
}
class Goal{

  PVector pos;
  float size;
  
  Goal(PVector p){
    pos = new PVector(p.x, p.y);
    size = 10;
  }
  
  //---------------------------------------------------------------------------------------
  
  public void show(){
    fill(255,0,0);
    strokeWeight(2);
    stroke(0);
    ellipse(pos.x, pos.y, size, size);
  }
}
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
  
  public void set(float _m, float _b){
    m = _m;
    b = _b;
  }
  
  public void setFromPoints(float x1, float y1, float x2, float y2){
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
  
  public void setFromPoints(PVector p1, PVector p2){
    setFromPoints(p1.x, p1.y, p2.x, p2.y);
  }
  
  public float intersectPoint(Line otherLine){
    if(abs(m - otherLine.m) < EPSILON){
      return INFINITY;
    }
    return (otherLine.b - b) / (m - otherLine.m);
  }
  
  public float y(float x){
    return m*x + b;
  }
}
class Obstacle {
  PVector start;
  PVector end;
  float size;

  Obstacle(PVector initStart, PVector initEnd, float initSize) {
    start = initStart;
    end = initEnd;
    size = initSize;
  }

  public void show() {
    strokeWeight(size);
    stroke(0, 0, 255);
    line(start.x, start.y, end.x, end.y);
  }

  public boolean checkCollision(PVector posToCheck, float radius) {
    float distance = getDistance(start.x, start.y, end.x, end.y, posToCheck.x, posToCheck.y).z;
    if(distance <= size/2 + radius/2.0f){
      return true;
    }
    return false;
  }

  public PVector getDistance( float x1, float y1, float x2, float y2, float x, float y ) {
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

  public void show() {
    for (int i = 1; i < dots.length; i++) {
      dots[i].show();
    }
    dots[0].show();
  }

  //-------------------------------------------------------------------------------------

  public void update() {
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

  public void calculateFitness() {    
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
  
  public void normalizeFitnesses()
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
  
  public float getWallFitness(int index)
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
  
  public float hammingDistance(Dot dot1, Dot dot2)
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

  public boolean allDotsDead() {
    for (int i = 0; i < dots.length; i++) {
      if (!dots[i].dead && !dots[i].reachedGoal) {
        return false;
      }
    }
    return true;
  }

  //-----------------------------------------------------------------------------------------

  public void naturalSelection() {
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

  public void calculateFitnessSum() {
    fitnessSum = 0;
    for (int i = 0; i < dots.length; i++) {
      fitnessSum += dots[i].fitness;
    }
  }

  //-----------------------------------------------------------------------------------------

  public Dot selectParent() {
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

  public void mutateChildren() {
    for (int i = 1; i < dots.length; i++) {
      dots[i].brain.mutate();
    }
  }

  //-----------------------------------------------------------------------------------------

  public void setBestDot() {
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

  public void checkObstacleCollisions(int i) {
    for(int l = 0; l < obstacles.size(); l++){
        // if collision, kill dot
        if(obstacles.get(l).checkCollision(dots[i].pos, dots[i].radius)){
          dots[i].dead = true;
        }
      }
  }
  
  //----------------------------------------------------------------------------------------
  
  public void populationResetMinStep(){
    minStep = dots[0].brain.directions.length-1;
  }
}
class TextBox
{
  PVector pos;
  PVector size;
  String text;

  boolean selected;
  
  boolean cursorOn = true;
  float cursorFlashTick = 0;
  float cursorFlashTime = 0.75f;
  
  int maxValue = 999999999;
  int minValue = 0;
  
  TextBox(PVector p, PVector s, String t)
  {
    pos = p;
    size = s;
    text = t;
    selected = false;
  }
  
  public void show()
  {
    strokeWeight(3);
    fill(200);
    stroke(0);
    
    if(mouseOver())
    {
      stroke(50);
      fill(225);
    }
    
    if(selected)
    {
      fill(255);
    }
    
    rect(pos.x - size.x/2, pos.y - size.y/2, size.x, size.y);
    float tSize = 32;
    textSize(tSize);
    float tWidth = textWidth(text);
    fill(50);
    
    if(selected)
    {
      fill(0);
    }
    
    text(text, pos.x - tWidth/2.0f, pos.y + tSize/2.0f);
    
    if(selected)
    {
      showCursor(tSize);
    }
    
     update(); 
  }
  
  public void setText(String txt)
  {
    text = txt;
  }
  
  public int getValue()
  {
    return Integer.parseInt(text);
  }
  
  public void showCursor(float tSize)
  {
    cursorFlashTick += DELTATIME;
    if(cursorFlashTick >= cursorFlashTime)
    {
      cursorFlashTick -= cursorFlashTime;
      cursorOn = !cursorOn;
    }
    
    if(cursorOn)
    {
      textSize(tSize);
      float tWidth = textWidth(text);
      
      strokeWeight(3);
      float margin = 5;
      float x = pos.x + tWidth/2.0f + margin;
      line(x, pos.y - tSize/2.0f, x, pos.y + tSize/2.0f);
    }
  }
  
  public void setMax(int max)
  {
    maxValue = max;
  }
  
  public void setMin(int min)
  {
    minValue = min;
  }
  
  public void setMinMax(int min, int max)
  {
    setMin(min);
    setMax(max);
  }
  
  public String getMinMaxString()
  {
    return "(" + minValue + " to " + maxValue + ")";
  }
  
  public void update()
  {
    if(selected)
    {
      if(clickedOff())
      {
        selected = false; 
        // Check that value is valid
        if(text.length() == 0)
        {
          text = "0";
        }
        else
        {
          // check if field is within range of max and min
          int value = 0;
          try
          {
            value = Integer.parseInt(text);
          }
          catch (NumberFormatException e)
          {
            value = maxValue;
          }
          
          
          if(value < minValue)
          {
            value = minValue;
          }
          else if(value > maxValue)
          {
            println("Hit max value");
            value = maxValue;
          }
          print("Setting value to " + value + " -> " + str(value));
          text = str(value);
        }
      }
      else if(newKeyPressed)
      {
        if(key == BACKSPACE)
        // Remove one character from text
        {
          if(text.length() > 0)
          {
            text = text.substring(0, text.length()-1);
          }
        }
        else if(key == ENTER)
        {
          selected = false;
        }
        else
        {
          // If key is number
          if(PApplet.parseInt(key) >= 48 && PApplet.parseInt(key) <= 57)
          {
            int number = PApplet.parseInt(key)-48;
            text += str(number);
          }
        }
      }
    }
    else if(clicked())
    {
      selected = true;
    }
  }
  
  public boolean mouseOver()
  {
    return checkInBounds(mouseX, mouseY);
  }
  
  public boolean clicked()
  {
    return mousePressed && mouseOver();
  }
  
  public boolean clickedOff()
  {
    return mousePressed && !mouseOver();
  }

  public boolean checkInBounds(float x, float y) {
    if (x > pos.x - size.x/2 && x < pos.x + size.x/2) {
      if (y > pos.y - size.y/2 && y < pos.y + size.y/2) {
        return true;
      }
    }
    return false;
  }

  public boolean checkInBounds(PVector p) {
    return checkInBounds(p.x, p.y);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Smart_Dots_V4" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
