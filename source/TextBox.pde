class TextBox
{
  PVector pos;
  PVector size;
  String text;

  boolean selected;
  
  boolean cursorOn = true;
  float cursorFlashTick = 0;
  float cursorFlashTime = 0.75;
  
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
    
    text(text, pos.x - tWidth/2.0, pos.y + tSize/2.0);
    
    if(selected)
    {
      showCursor(tSize);
    }
    
     update(); 
  }
  
  void setText(String txt)
  {
    text = txt;
  }
  
  int getValue()
  {
    return Integer.parseInt(text);
  }
  
  void showCursor(float tSize)
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
      float x = pos.x + tWidth/2.0 + margin;
      line(x, pos.y - tSize/2.0, x, pos.y + tSize/2.0);
    }
  }
  
  void setMax(int max)
  {
    maxValue = max;
  }
  
  void setMin(int min)
  {
    minValue = min;
  }
  
  void setMinMax(int min, int max)
  {
    setMin(min);
    setMax(max);
  }
  
  String getMinMaxString()
  {
    return "(" + minValue + " to " + maxValue + ")";
  }
  
  void update()
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
          if(int(key) >= 48 && int(key) <= 57)
          {
            int number = int(key)-48;
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
  
  boolean mouseOver()
  {
    return checkInBounds(mouseX, mouseY);
  }
  
  boolean clicked()
  {
    return mousePressed && mouseOver();
  }
  
  boolean clickedOff()
  {
    return mousePressed && !mouseOver();
  }

  boolean checkInBounds(float x, float y) {
    if (x > pos.x - size.x/2 && x < pos.x + size.x/2) {
      if (y > pos.y - size.y/2 && y < pos.y + size.y/2) {
        return true;
      }
    }
    return false;
  }

  boolean checkInBounds(PVector p) {
    return checkInBounds(p.x, p.y);
  }
}