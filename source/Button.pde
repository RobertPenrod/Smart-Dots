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

  void show() {
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
      
      text(text, pos.x - textWidth/2.0, pos.y + textHeight/2.0 - 2 * textMargin);
    }
  }

  void show(boolean selected) {
    if (selected) {
      strokeWeight(10);
      stroke(0, 200, 0);
      fill(200);
      rect(pos.x - size.x/2, pos.y - size.y/2, size.x, size.y);
    } else {
      show();
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