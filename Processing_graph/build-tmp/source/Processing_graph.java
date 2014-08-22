import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 
import processing.serial.*; 
import supercollider.*; 
import oscP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Processing_graph extends PApplet {

/*
 * Touche for Arduino
 * Vidualization
 *
 */



float voltageMax; //\u96fb\u5727\u306e\u6700\u5927\u5024
float timeMax; //\u96fb\u5727\u304c\u6700\u5927\u5024\u3060\u3063\u305f\u3068\u304d\u306e\u6642\u9593

// READ ME
// Edit these values depending on your environment
float VOL_MIN  = 70;
float VOL_MAX  = 350;
float BOUNDARY = 200;
// ------------------------------------------------

// \u30c7\u30d0\u30c3\u30b0\u7528
boolean isDelay  = true;
float maxVol     = 0;
float minVol     = 200;

ArrayList<HashMap> ballList = new ArrayList<HashMap>();

public void setup() {
  //\u753b\u9762\u30b5\u30a4\u30ba
  size(displayWidth, displayHeight);
  // size(800, 500);
  frameRate(60);
  background(0);

  noLoop();
  //\u30dd\u30fc\u30c8\u3092\u8a2d\u5b9a
  PortSelected = 2;
  // PortSelected = 7;
  //\u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u3092\u521d\u671f\u5316
  SerialPortSetup();
}

public void draw() {
  // \u8d77\u52d5\u6642\u306e\u30bb\u30f3\u30b7\u30f3\u30b0\u3055\u308c\u3066\u306a\u3044\u5024\u3092\u30ab\u30a6\u30f3\u30c8\u3092\u6700\u5927\u5024\uff0f\u6700\u5c0f\u5024\u306b\u30ab\u30a6\u30f3\u30c8\u3057\u306a\u3044\u305f\u3081\u306b\u5f85\u6a5f\u51e6\u7406\u3092\u5165\u308c\u308b
  if (isDelay) {
    delay(5000);
    isDelay = false;
  }

  noStroke();
  fill(0, 255);
  rectMode(CORNER);
  rect(0, 0, width, height);

  //\u6700\u5927\u5024\u30920\u306b\u521d\u671f\u5316
  voltageMax = timeMax = 0;

  if (DataRecieved3) {
    //\u96fb\u5727\u306e\u6700\u5927\u5024\u3068\u3001\u305d\u306e\u3068\u304d\u306e\u6642\u9593\u3092\u53d6\u5f97
    for (int i = 0; i < Voltage3.length; i++) {
      // float v = map(Voltage3[i], VOL_MIN, VOL_MAX, 100, 300);
      float v = Voltage3[i];
      if (voltageMax < v) {
        voltageMax = v;
        timeMax    = Time3[i];
        // Audio
        if (voltageMax > BOUNDARY) {
          Thread t = new Thread(new SoundThread(voltageMax));
          t.start();
        }
      }
    }

    if (voltageMax > BOUNDARY) {
      HashMap<String, Float> hash = new HashMap();
      float random_x              = random(1, width);
      float random_y              = random(1, height);
      float velocity              = map(timeMax, 120, 140, 1, 1.5f);
      float radius                = map(voltageMax, VOL_MIN, VOL_MAX, 10, 2) * velocity;
      float opacity               = 255;

      hash.put("x", random_x);
      hash.put("y", random_y);
      hash.put("radius", radius);
      hash.put("opacity", opacity);
      ballList.add(hash);
    }

    fill(255, 100);

    for (int i = 0; i < ballList.size(); i++) {
      noStroke();
      float _x       = (Float)ballList.get(i).get("x");
      float _y       = (Float)ballList.get(i).get("y");
      float _radius  = (Float)ballList.get(i).get("radius");
      float _opacity = (Float)ballList.get(i).get("opacity");
      fill(255, 255, 255, _opacity);
      ellipse(_x, _y, _radius, _radius);
      float _new_y       = _y + 5;
      float _new_radius  = _radius + 10;
      float _new_opacity = _opacity - 10;
      println("_opacity = " + _opacity);
      println("_new_opacity = " + _new_opacity);

      if (_opacity < 0) {
        ballList.remove(i);
      } else {
        // ballList.get(i).put("y", _new_y);
        ballList.get(i).put("radius", _new_radius);
        ballList.get(i).put("opacity", _new_opacity);
      }
    }

    // \u30c7\u30d0\u30c3\u30b0
    if (voltageMax > maxVol) {
      maxVol = voltageMax;
    } else if (voltageMax < minVol) {
      minVol = voltageMax;
    }

    // \u30c7\u30d0\u30c3\u30b0
    println("Voltage: " + voltageMax + ", maxVol: " + maxVol);
    println("minVol: " + minVol);
  }
}

public void stop() {
  myPort.stop();
  super.stop();
}
// class DownGain implements AudioEffect {
//   float gain = 1.0;

//   DownGain(float g) {
//     gain = g;
//   }

//   void process(float[] samp) {
//     float[] out = new float[samp.length];
//     for ( int i = 0; i < samp.length; i++ ) {
//       out[i] = samp[i] * gain;
//     }
//     arraycopy(out, samp);
//   }

//   void process(float[] left, float[] right) {
//     process(left);
//     process(right);
//   }
// }

/*   =================================================================================       
 The Graph class contains functions and variables that have been created to draw 
 graphs. Here is a quick list of functions within the graph class:
 
 Graph(int x, int y, int w, int h,color k)
 DrawAxis()
 Bar([])
 smoothLine([][])
 DotGraph([][])
 LineGraph([][]) 
 
 =================================================================================*/


class Graph 
{
  float maxY = 0;
  float maxX = 0;
  int maxI = 0;
  boolean Dot=true;            // Draw dots at each data point if true
  boolean RightAxis;            // Draw the next graph using the right axis if true
  boolean ErrorFlag=false;      // If the time array isn't in ascending order, make true  
  boolean ShowMouseLines=true;  // Draw lines and give values of the mouse position

  int     xDiv=5, yDiv=5;            // Number of sub divisions
  int     xPos, yPos;            // location of the top left corner of the graph  
  int     Width, Height;         // Width and height of the graph


  int   GraphColor;
  int   BackgroundColor=color(255);  
  int   StrokeColor=color(180);     

  String  Title="Title";          // Default titles
  String  xLabel="x - Label";
  String  yLabel="y - Label";

  float   yMax=1024, yMin=0;      // Default axis dimensions
  float   xMax=10, xMin=0;
  float   yMaxRight=1024, yMinRight=0;

 // PFont   Font;                   // Selected font used for text 

    //    int Peakcounter=0,nPeakcounter=0;

  Graph(int x, int y, int w, int h, int k) {  // The main declaration function
    xPos = x;
    yPos = y;
    Width = w;
    Height = h;
    GraphColor = k;
  }


  public void DrawAxis() {

    /*  =========================================================================================
     Main axes Lines, Graph Labels, Graph Background
     ==========================================================================================  */

    fill(BackgroundColor); 
    color(0);
    stroke(StrokeColor);
    strokeWeight(1);
    int t=60;

    rect(xPos-t*1.6f, yPos-t, Width+t*2.5f, Height+t*2);            // outline
    textAlign(CENTER);
    textSize(18);
    float c=textWidth(Title);
    fill(BackgroundColor); 
    color(0);
    stroke(0);
    strokeWeight(1);
    rect(xPos+Width/2-c/2, yPos-35, c, 0);                         // Heading Rectangle  

    fill(0);
    text(Title, xPos+Width/2, yPos-37);                            // Heading Title
    textAlign(CENTER);
    textSize(14);
    text(xLabel, xPos+Width/2, yPos+Height+t/1.5f);                     // x-axis Label 

    rotate(-PI/2);                                               // rotate -90 degrees
    text(yLabel, -yPos-Height/2, xPos-t*1.6f+20);                   // y-axis Label  
    rotate(PI/2);                                                // rotate back

    textSize(10); 
    noFill(); 
    stroke(0); 
    smooth();
    strokeWeight(1);
    //Edges
    line(xPos-3, yPos+Height, xPos-3, yPos);                        // y-axis line 
    line(xPos-3, yPos+Height, xPos+Width+5, yPos+Height);           // x-axis line 

    stroke(200);
    if (yMin<0) {
      line(xPos-7, // zero line 
      yPos+Height-(abs(yMin)/(yMax-yMin))*Height, // 
      xPos+Width, 
      yPos+Height-(abs(yMin)/(yMax-yMin))*Height
        );
    }

    if (RightAxis) {                                       // Right-axis line   
      stroke(0);
      line(xPos+Width+3, yPos+Height, xPos+Width+3, yPos);
    }

    /*  =========================================================================================
     Sub-devisions for both axes, left and right
     ==========================================================================================  */

    stroke(0);

    for (int x=0; x<=xDiv; x++) {

      /*  =========================================================================================
       x-axis
       ==========================================================================================  */

      line(PApplet.parseFloat(x)/xDiv*Width+xPos-3, yPos+Height, //  x-axis Sub devisions    
      PApplet.parseFloat(x)/xDiv*Width+xPos-3, yPos+Height+5);     

      textSize(10);                                      // x-axis Labels
      String xAxis=str(xMin+PApplet.parseFloat(x)/xDiv*(xMax-xMin));  // the only way to get a specific number of decimals 
      String[] xAxisMS=split(xAxis, '.');                 // is to split the float into strings 
      text(xAxisMS[0]+"."+xAxisMS[1].charAt(0), // ...
      PApplet.parseFloat(x)/xDiv*Width+xPos-3, yPos+Height+15);   // x-axis Labels
    }


    /*  =========================================================================================
     left y-axis
     ==========================================================================================  */

    for (int y=0; y<=yDiv; y++) {
      line(xPos-3, PApplet.parseFloat(y)/yDiv*Height+yPos, // ...
      xPos-7, PApplet.parseFloat(y)/yDiv*Height+yPos);              // y-axis lines 

        textAlign(RIGHT);
      fill(20);

      String yAxis=str(yMin+PApplet.parseFloat(y)/yDiv*(yMax-yMin));     // Make y Label a string
      String[] yAxisMS=split(yAxis, '.');                    // Split string

      text(yAxisMS[0]+"."+yAxisMS[1].charAt(0), // ... 
      xPos-15, PApplet.parseFloat(yDiv-y)/yDiv*Height+yPos+3);       // y-axis Labels 


      /*  =========================================================================================
       right y-axis
       ==========================================================================================  */

      if (RightAxis) {

        color(GraphColor); 
        stroke(GraphColor);
        fill(20);

        line(xPos+Width+3, PApplet.parseFloat(y)/yDiv*Height+yPos, // ...
        xPos+Width+7, PApplet.parseFloat(y)/yDiv*Height+yPos);            // Right Y axis sub devisions

          textAlign(LEFT); 

        String yAxisRight=str(yMinRight+PApplet.parseFloat(y)/                // ...
        yDiv*(yMaxRight-yMinRight));           // convert axis values into string
        String[] yAxisRightMS=split(yAxisRight, '.');             // 

        text(yAxisRightMS[0]+"."+yAxisRightMS[1].charAt(0), // Right Y axis text
        xPos+Width+15, PApplet.parseFloat(yDiv-y)/yDiv*Height+yPos+3);   // it's x,y location

        noFill();
      }
      stroke(0);
    }
  }


  /*  =========================================================================================
   Bar graph
   ==========================================================================================  */

  public void Bar(float[] a, int from, int to) {


    stroke(GraphColor);
    fill(GraphColor);

    if (from<0) {                                      // If the From or To value is out of bounds 
      for (int x=0; x<a.length; x++) {                 // of the array, adjust them 
        rect(PApplet.parseInt(xPos+x*PApplet.parseFloat(Width)/(a.length)), 
        yPos+Height-2, 
        Width/a.length-2, 
        -a[x]/(yMax-yMin)*Height);
      }
    }

    else {
      for (int x=from; x<to; x++) {

        rect(PApplet.parseInt(xPos+(x-from)*PApplet.parseFloat(Width)/(to-from)), 
        yPos+Height-2, 
        Width/(to-from)-2, 
        -a[x]/(yMax-yMin)*Height);
      }
    }
  }
  public void Bar(float[] a ) {

    stroke(GraphColor);
    fill(GraphColor);

    for (int x=0; x<a.length; x++) {                 // of the array, adjust them 
      rect(PApplet.parseInt(xPos+x*PApplet.parseFloat(Width)/(a.length)), 
      yPos+Height-2, 
      Width/a.length-2, 
      -a[x]/(yMax-yMin)*Height);
    }
  }


  /*  =========================================================================================
   Dot graph
   ==========================================================================================  */

  public void DotGraph(float[] x, float[] y) {

    for (int i=0; i<x.length; i++) {
      strokeWeight(2);
      stroke(GraphColor);
      noFill();
      smooth();
      ellipse(
      xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width, 
      yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height, 
      2, 2
        );
    }
  }

  /*  =========================================================================================
   Streight line graph 
   ==========================================================================================  */

  public void LineGraph(float[] x, float[] y) {

    for (int i=0; i<(x.length-1); i++) {
      strokeWeight(2);
      stroke(GraphColor);
      noFill();
      smooth();
      line(xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width, 
      yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height, 
      xPos+(x[i+1]-x[0])/(x[x.length-1]-x[0])*Width, 
      yPos+Height-(y[i+1]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height);
    }
  }

  /*  =========================================================================================
   smoothLine
   ==========================================================================================  */

  public void smoothLine(float[] x, float[] y) {

    float tempyMax=yMax, tempyMin=yMin;

    if (RightAxis) {
      yMax=yMaxRight;
      yMin=yMinRight;
    } 

    int counter=0;
    int xlocation=0, ylocation=0;

    //         if(!ErrorFlag |true ){    // sort out later!

    beginShape(); 
    strokeWeight(6);
    stroke(GraphColor);
    noFill();
    smooth();
    maxY = 0;
    //find max
    for (int i=0; i<x.length; i++) {

      if (maxY < y[i])
      {

        maxY =y[i];
        maxI = i;
      }
    }



    for (int i=0; i<x.length; i++) {

      /* ===========================================================================
       Check for errors-> Make sure time array doesn't decrease (go back in time) 
       ===========================================================================*/
      if (i<x.length-1) {
        if (x[i]>x[i+1]) {

          ErrorFlag=true;
        }
      }

      /* =================================================================================       
       First and last bits can't be part of the curve, no points before first bit, 
       none after last bit. So a streight line is drawn instead   
       ================================================================================= */

      if (i==0 || i==x.length-2)line(xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width, 
      yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height, 
      xPos+(x[i+1]-x[0])/(x[x.length-1]-x[0])*Width, 
      yPos+Height-(y[i+1]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height);

      /* =================================================================================       
       For the rest of the array a curve (spline curve) can be created making the graph 
       smooth.     
       ================================================================================= */

      curveVertex( xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width, 
      yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height);

      /* =================================================================================       
       If the Dot option is true, Place a dot at each data point.  
       ================================================================================= */
      if (i == maxI) 
      {
        ellipse(
        xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width, 
        yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height, 
        20, 20
          );
      }
      if (Dot)ellipse(
      xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width, 
      yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height, 
      2, 2
        );

      /* =================================================================================       
       Highlights points closest to Mouse X position   
       =================================================================================*/

      if ( abs(mouseX-(xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width))<5 ) {


        float yLinePosition = yPos+Height-(y[i]/(yMax-yMin)*Height)+(yMin)/(yMax-yMin)*Height;
        float xLinePosition = xPos+(x[i]-x[0])/(x[x.length-1]-x[0])*Width;
        strokeWeight(1);
        stroke(240);
        // line(xPos,yLinePosition,xPos+Width,yLinePosition);
        strokeWeight(2);
        stroke(GraphColor);

        ellipse(xLinePosition, yLinePosition, 4, 4);
      }
    }  

    endShape(); 

    yMax=tempyMax; 
    yMin=tempyMin;
    float xAxisTitleWidth=textWidth(str(map(xlocation, xPos, xPos+Width, x[0], x[x.length-1])));


    if ((mouseX>xPos&mouseX<(xPos+Width))&(mouseY>yPos&mouseY<(yPos+Height))) {   
      if (ShowMouseLines) {
        // if(mouseX<xPos)xlocation=xPos;
        if (mouseX>xPos+Width)xlocation=xPos+Width;
        else xlocation=mouseX;
        stroke(200); 
        strokeWeight(0.5f);
        fill(255);
        color(50);
        // Rectangle and x position
        line(xlocation, yPos, xlocation, yPos+Height);
        rect(xlocation-xAxisTitleWidth/2-10, yPos+Height-16, xAxisTitleWidth+20, 12);

        textAlign(CENTER); 
        fill(160);
        text(map(xlocation, xPos, xPos+Width, x[0], x[x.length-1]), xlocation, yPos+Height-6);

        // if(mouseY<yPos)ylocation=yPos;
        if (mouseY>yPos+Height)ylocation=yPos+Height;
        else ylocation=mouseY;

        // Rectangle and y position
        stroke(200); 
        strokeWeight(0.5f);
        fill(255);
        color(50);

        line(xPos, ylocation, xPos+Width, ylocation);
        int yAxisTitleWidth=PApplet.parseInt(textWidth(str(map(ylocation, yPos, yPos+Height, y[0], y[y.length-1]))) );
        rect(xPos-15+3, ylocation-6, -60, 12);

        textAlign(RIGHT); 
        fill(GraphColor);//StrokeColor
        //    text(map(ylocation,yPos+Height,yPos,yMin,yMax),xPos+Width+3,yPos+Height+4);
        text(map(ylocation, yPos+Height, yPos, yMin, yMax), xPos -15, ylocation+4);
        if (RightAxis) { 

          stroke(200); 
          strokeWeight(0.5f);
          fill(255);
          color(50);

          rect(xPos+Width+15-3, ylocation-6, 60, 12);  
          textAlign(LEFT); 
          fill(160);
          text(map(ylocation, yPos+Height, yPos, yMinRight, yMaxRight), xPos+Width+15, ylocation+4);
        }
        noStroke();
        noFill();
      }
    }
  }


  public void smoothLine(float[] x, float[] y, float[] z, float[] a ) {
    GraphColor=color(188, 53, 53);
    smoothLine(x, y);
    GraphColor=color(193-100, 216-100, 16);
    smoothLine(z, a);
  }
}



int SerialPortNumber = 2;
int PortSelected     = 2;
// int SerialPortNumber = 7;
// int PortSelected     = 7;

/*   =================================================================================
 Global variables
 =================================================================================*/

int xValue, yValue, Command;
boolean Error       = true;
boolean UpdateGraph = true;
int lineGraph;
int ErrorCounter  = 0;
int TotalRecieved = 0;

/*   =================================================================================
 Local variables
 =================================================================================*/
boolean DataRecieved1 = false, DataRecieved2 = false, DataRecieved3 = false;

float[] DynamicArrayTime1, DynamicArrayTime2, DynamicArrayTime3;
float[] Time1, Time2, Time3;
float[] Voltage1, Voltage2, Voltage3;
float[] current;
float[] DynamicArray1, DynamicArray2, DynamicArray3;

float[] PowerArray        = new float[0];            // Dynamic arrays that will use the append()
float[] DynamicArrayPower = new float[0];    // function to add values
float[] DynamicArrayTime  = new float[0];

String portName;
String[] ArrayOfPorts = new String[SerialPortNumber];

boolean DataRecieved = false, Data1Recieved = false, Data2Recieved = false;
int incrament = 0;

int NumOfSerialBytes = 8;                              // The size of the buffer array
int[] serialInArray  = new int[NumOfSerialBytes];     // Buffer array
int serialCount      = 0;                                 // A count of how many bytes received
int xMSB, xLSB, yMSB, yLSB;		                // Bytes of data

Serial myPort;                                        // The serial port object


/*   =================================================================================
 A once off serail port setup function. In this case the selection of the speed,
 the serial port and clearing the serial port buffer
 =================================================================================*/

public void SerialPortSetup() {

  //  text(Serial.list().length,200,200);
  portName     = Serial.list()[PortSelected];
  ArrayOfPorts = Serial.list();
  myPort       = new Serial(this, portName, 115200);
  println(ArrayOfPorts);
  delay(50);
  myPort.clear();
  myPort.buffer(20);
}

/* ============================================================
 serialEvent will be called when something is sent to the
 serial port being used.
 ============================================================   */

public void serialEvent(Serial myPort) {

  while (myPort.available ()>0)
  {
    /* ============================================================
     Read the next byte that's waiting in the buffer.
     ============================================================   */

    int inByte = myPort.read();

    if (inByte == 0)serialCount = 0;

    if (inByte > 255) {
      println(" inByte = " + inByte);
      exit();
    }

    // Add the latest byte from the serial port to array:

    serialInArray[serialCount] = inByte;
    serialCount++;

    Error = true;
    if (serialCount >= NumOfSerialBytes ) {
      serialCount = 0;

      TotalRecieved++;

      int Checksum = 0;

      //    Checksum = (Command + yMSB + yLSB + xMSB + xLSB + zeroByte)%255;
      for (int x = 0; x < serialInArray.length - 1; x++) {
        Checksum = Checksum + serialInArray[x];
      }

      Checksum = Checksum % 255;



      if (Checksum == serialInArray[serialInArray.length - 1]) {
        Error = false;
        DataRecieved = true;
      }
      else {
        Error = true;
        //  println("Error:  "+ ErrorCounter +" / "+ TotalRecieved+" : "+float(ErrorCounter/TotalRecieved)*100+"%");
        DataRecieved = false;
        ErrorCounter++;
        println("Error:  " + ErrorCounter + " / " + TotalRecieved + " : " + PApplet.parseFloat(ErrorCounter / TotalRecieved) * 100 + "%");
      }
    }

    if (!Error) {


      int zeroByte = serialInArray[6];
      // println (zeroByte & 2);

      xLSB = serialInArray[3];
      if ( (zeroByte & 1) == 1) xLSB = 0;
      xMSB = serialInArray[2];
      if ( (zeroByte & 2) == 2) xMSB = 0;

      yLSB = serialInArray[5];
      if ( (zeroByte & 4) == 4) yLSB = 0;

      yMSB = serialInArray[4];
      if ( (zeroByte & 8) == 8) yMSB = 0;


      //   println( "0\tCommand\tyMSB\tyLSB\txMSB\txLSB\tzeroByte\tsChecksum");
      //  println(serialInArray[0]+"\t"+Command +"\t"+ yMSB +"\t"+ yLSB +"\t"+ xMSB +"\t"+ xLSB+"\t" +zeroByte+"\t"+ serialInArray[7]);

      // >=====< combine bytes to form large integers >==================< //

      Command  = serialInArray[1];

      xValue   = xMSB << 8 | xLSB;                    // Get xValue from yMSB & yLSB
      yValue   = yMSB << 8 | yLSB;                    // Get yValue from xMSB & xLSB

        // println(Command+ "  "+xValue+"  "+ yValue+" " );

      /*
How that works: if xMSB = 10001001   and xLSB = 0100 0011
       xMSB << 8 = 10001001 00000000    (shift xMSB left by 8 bits)
       xLSB =          01000011
       xLSB | xMSB = 10001001 01000011    combine the 2 bytes using the logic or |
       xValue = 10001001 01000011     now xValue is a 2 byte number 0 -> 65536
       */


      /*  ==================================================================
       Command, xValue & yValue have now been recieved from the chip
       ==================================================================  */
      switch(Command) {
        /*  ==================================================================
         Recieve array1 and array2 from chip, update oscilloscope
         ==================================================================  */
      case 1: // Data is added to dynamic arrays
        DynamicArrayTime3 = append( DynamicArrayTime3, (xValue) );
        DynamicArray3     = append( DynamicArray3, (yValue) );
        break;
      case 2: // An array of unknown size is about to be recieved, empty storage arrays
        DynamicArrayTime3 = new float[0];
        DynamicArray3     = new float[0];
        break;
      case 3:  // Array has finnished being recieved, update arrays being drawn
        Time3    = DynamicArrayTime3;
        Voltage3 = DynamicArray3;
     //   println(Voltage3.length);
        DataRecieved3 = true;
        break;
        /*  ==================================================================
         Recieve array2 and array3 from chip
         ==================================================================  */
      case 4: // Data is added to dynamic arrays
        DynamicArrayTime2 = append( DynamicArrayTime2, xValue );
        DynamicArray2     = append( DynamicArray2, (yValue-16000.0f)/32000.0f*20.0f  );
        break;
      case 5: // An array of unknown size is about to be recieved, empty storage arrays
        DynamicArrayTime2 = new float[0];
        DynamicArray2     = new float[0];
        break;
      case 6:  // Array has finnished being recieved, update arrays being drawn
        Time2         = DynamicArrayTime2;
        current       = DynamicArray2;
        DataRecieved2 = true;
        break;
        /*  ==================================================================
         Recieve a value of calculated power consumption & add it to the
         PowerArray.
         ==================================================================  */
      case 20:
        PowerArray = append( PowerArray, yValue );
        break;
      case 21:
        DynamicArrayTime  = append( DynamicArrayTime, xValue );
        DynamicArrayPower = append( DynamicArrayPower, yValue );
        break;
      }
    }
  }
  redraw();
  //    }
}
boolean isPlayable = true;

class SoundThread implements Runnable {

  float voltageMax;
  SCClient scClient;

  public SoundThread(float v){
    this.voltageMax = v;
    this.scClient   = new SCClient();
  }

  public void run() {
    if (isPlayable == false) {
      return;
    }
    isPlayable = false;
    this.scClient.play(this.voltageMax);
    delay(100);  // \u9023\u7d9a\u518d\u751f\u3092\u3055\u3051\u308b\u305f\u3081\u306b\u9045\u5ef6\u306b\u3088\u308b\u9593\u5f15\u304d\u3092\u5165\u308c\u308b
    isPlayable = true;
  }
}



class SCClient {
  Synth synth;

  SCClient() {

  }

  public void play(float voltage) {
    // \u65b0\u898f\u306b\u697d\u5668\u3092\u5b9a\u7fa9(\u307e\u3060\u751f\u6210\u306f\u3055\u308c\u305a)
    synth = new Synth("testInst111");
    // \u5f15\u6570\u3092\u8a2d\u5b9a
    synth.set("amp", 0.5f);
    synth.set("freq", map(voltage, height, 0, 20, 8000));
    // \u697d\u5668\u3092\u751f\u6210
    synth.create();
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Processing_graph" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
