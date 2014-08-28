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

/**
 * Touche for Arduino
 * Vidualization
 *
 */



float voltageMax; //\u96fb\u5727\u306e\u6700\u5927\u5024
float timeMax;    //\u96fb\u5727\u304c\u6700\u5927\u5024\u3060\u3063\u305f\u3068\u304d\u306e\u6642\u9593

/**
 * README
 * Edit these values depending on your environment */
float VOL_MIN  = 70;
float VOL_MAX  = 350;
float BOUNDARY = 200;
/* ----------------------------------------------- */

// \u30c7\u30d0\u30c3\u30b0\u7528
boolean isDelay = true;
float maxVol    = 0;
float minVol    = 200;

ArrayList<HashMap> ballList = new ArrayList<HashMap>();

public void setup() {
  // \u753b\u9762\u30b5\u30a4\u30ba
  size(displayWidth, displayHeight);
  frameRate(60);
  background(0);

  noLoop();
  // \u30dd\u30fc\u30c8\u3092\u8a2d\u5b9a\uff08\u74b0\u5883\u306b\u3088\u3063\u3066\u30a4\u30f3\u30c7\u30c3\u30af\u30b9\u304c\u7570\u306a\u308b\u306e\u3067for\u30eb\u30fc\u30d7\u3067\u4e00\u89a7\u3092\u8868\u793a\u3055\u305b\u3066\u78ba\u8a8d\u3059\u308b\u3068\u826f\u3044\uff09
  PortSelected = 2;
  // \u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u3092\u521d\u671f\u5316
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

  // \u6700\u5927\u5024\u30920\u306b\u521d\u671f\u5316
  voltageMax = timeMax = 0;

  if (DataRecieved3) {
    // \u96fb\u5727\u306e\u6700\u5927\u5024\u3068\u3001\u305d\u306e\u3068\u304d\u306e\u6642\u9593\u3092\u53d6\u5f97
    for (int i = 0; i < Voltage3.length; i++) {
      float v = Voltage3[i];
      if (voltageMax < v) {
        voltageMax = v;
        timeMax    = Time3[i];

        // \u96fb\u5727\u304c\u95be\u5024\u3092\u8d85\u3048\u305f\u3089\u30b5\u30a6\u30f3\u30c9\u3092\u518d\u751f
        if (voltageMax > BOUNDARY) {
          Thread t = new Thread(new SoundThread(voltageMax));
          t.start();
        }
      }
    }

    // \u96fb\u5727\u304c\u95be\u5024\u3092\u8d85\u3048\u305f\u3089\u63cf\u753b\u30ea\u30b9\u30c8\u306b\u30d1\u30fc\u30c6\u30a3\u30af\u30eb\u3092\u8ffd\u52a0
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

      // \u30d1\u30fc\u30c6\u30a3\u30af\u30eb\u306e\u63cf\u753b
      fill(255, 255, 255, _opacity);
      ellipse(_x, _y, _radius, _radius);

      float _new_y       = _y + 5;
      float _new_radius  = _radius + 10;
      float _new_opacity = _opacity - 10;

      if (_opacity < 0) {
        // \u900f\u660e\u306b\u306a\u3063\u305f\u3089\u524a\u9664
        ballList.remove(i);
      } else {
        // \u900f\u660e\u306b\u306a\u308a\u306a\u304c\u3089\u62e1\u5927
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


// \u81ea\u8eab\u306e\u74b0\u5883\u306e\u30dd\u30fc\u30c8\u3092\u6307\u5b9a
int SerialPortNumber = 2;
int PortSelected     = 2;

int xValue, yValue, Command;
boolean Error       = true;
boolean UpdateGraph = true;
int lineGraph;
int ErrorCounter  = 0;
int TotalRecieved = 0;

boolean DataRecieved1 = false,
        DataRecieved2 = false,
        DataRecieved3 = false;

float[] DynamicArrayTime1, DynamicArrayTime2, DynamicArrayTime3;
float[] Time1, Time2, Time3;
float[] Voltage1, Voltage2, Voltage3;
float[] current;
float[] DynamicArray1, DynamicArray2, DynamicArray3;

float[] PowerArray        = new float[0]; // Dynamic arrays that will use the append()
float[] DynamicArrayPower = new float[0]; // function to add values
float[] DynamicArrayTime  = new float[0];

String portName;
String[] ArrayOfPorts = new String[SerialPortNumber];

boolean DataRecieved  = false,
        Data1Recieved = false,
        Data2Recieved = false;

int incrament        = 0;
int NumOfSerialBytes = 8; // The size of the buffer array
int[] serialInArray  = new int[NumOfSerialBytes]; // Buffer array
int serialCount      = 0; // A count of how many bytes received
int xMSB, xLSB, yMSB, yLSB; // Bytes of data

Serial myPort; // The serial port object

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u306e\u521d\u671f\u5316\u95a2\u6570
public void SerialPortSetup() {
  portName     = Serial.list()[PortSelected];
  ArrayOfPorts = Serial.list();
  myPort       = new Serial(this, portName, 115200);
  println(ArrayOfPorts);  // \u30dd\u30fc\u30c8\u306e\u30ea\u30b9\u30c8\u3092\u5217\u6319
  delay(50);
  myPort.clear();
  myPort.buffer(20);
}

// \u30b7\u30ea\u30a2\u30eb\u30a4\u30d9\u30f3\u30c8
// \u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u304b\u3089\u4f55\u304b\u60c5\u5831\u3092\u53d7\u3051\u3068\u308b\u3068\u547c\u3073\u3060\u3055\u308c\u308b
public void serialEvent(Serial myPort) {

  while (myPort.available() > 0) {

    // \u30b7\u30ea\u30a2\u30eb\u306e\u30d0\u30c3\u30d5\u30a1\u304b\u3089\u6b21\u306e\u30d0\u30a4\u30c8\u5217\u3092\u8aad\u307f\u51fa\u3059
    int inByte = myPort.read();

    if (inByte == 0) {
      serialCount = 0;
    }
    if (inByte > 255) {
      println(" inByte = " + inByte);
      exit();
    }

    // \u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u304b\u3089\u53d6\u5f97\u3055\u308c\u305f\u6700\u65b0\u306e\u30d0\u30a4\u30c8\u3092\u914d\u5217\u306b\u8ffd\u52a0
    serialInArray[serialCount] = inByte;
    serialCount++;

    Error = true;
    if (serialCount >= NumOfSerialBytes ) {
      serialCount = 0;
      TotalRecieved++;
      int Checksum = 0;

      // Checksum = (Command + yMSB + yLSB + xMSB + xLSB + zeroByte) % 255;
      for (int x = 0; x < serialInArray.length - 1; x++) {
        Checksum = Checksum + serialInArray[x];
      }
      Checksum = Checksum % 255;

      if (Checksum == serialInArray[serialInArray.length - 1]) {
        Error        = false;
        DataRecieved = true;
      }
      else {
        Error        = true;
        DataRecieved = false;
        ErrorCounter++;
        println("Error:  " + ErrorCounter + " / " + TotalRecieved + " : " + PApplet.parseFloat(ErrorCounter / TotalRecieved) * 100 + "%");
      }
    }

    if (!Error) {
      int zeroByte = serialInArray[6];
      xLSB = serialInArray[3];
      if ( (zeroByte & 1) == 1) xLSB = 0;
      xMSB = serialInArray[2];
      if ( (zeroByte & 2) == 2) xMSB = 0;
      yLSB = serialInArray[5];
      if ( (zeroByte & 4) == 4) yLSB = 0;
      yMSB = serialInArray[4];
      if ( (zeroByte & 8) == 8) yMSB = 0;

      // \u30d0\u30a4\u30c8(8bit)\u5358\u4f4d\u306b\u5206\u5272\u3055\u308c\u305f\u30c7\u30fc\u30bf\u3092\u5408\u6210\u3057\u3066\u300116bit(0\u301c1024)\u306e\u5024\u3092\u5fa9\u5143\u3059\u308b
      Command = serialInArray[1];
      xValue  = xMSB << 8 | xLSB; // xMSB\u3068xLSB\u306e\u5024\u304b\u3089\u3001xValue\u3092\u5408\u6210
      yValue  = yMSB << 8 | yLSB; // yMSB\u3068yLSB\u306e\u5024\u304b\u3089\u3001yValue\u3092\u5408\u6210

      // Command, xValue, yValue\u306e3\u3064\u306e\u5024\u304cArduino\u304b\u3089\u53d6\u5f97\u5b8c\u4e86
      // Command\u306e\u5024\u306b\u3088\u3063\u3066\u3001\u52d5\u4f5c\u3092\u3075\u308a\u308f\u3051
      switch(Command) {

        case 1: // \u914d\u5217\u306b\u30c7\u30fc\u30bf\u3092\u8ffd\u52a0
          DynamicArrayTime3 = append(DynamicArrayTime3, xValue);
          DynamicArray3     = append(DynamicArray3, yValue);
          break;
        case 2: // \u60f3\u5b9a\u5916\u306e\u30b5\u30a4\u30ba\u306e\u914d\u5217\u3092\u53d7\u3051\u3068\u3063\u305f\u5834\u5408\u3001\u914d\u5217\u3092\u7a7a\u306b\u521d\u671f\u5316
          DynamicArrayTime3 = new float[0];
          DynamicArray3     = new float[0];
          break;
        case 3: // \u914d\u5217\u306e\u53d7\u4fe1\u5b8c\u4e86\u3001\u5024\u3092\u8ffd\u52a0\u3057\u305f\u914d\u5217\u3092\u30b0\u30e9\u30d5\u63cf\u753b\u7528\u306e\u914d\u5217\u306b\u30b3\u30d4\u30fc\u3057\u3066\u3001\u30b0\u30e9\u30d5\u3092\u63cf\u753b\u3059\u308b
          Time3         = DynamicArrayTime3;
          Voltage3      = DynamicArray3;
          DataRecieved3 = true;
          break;
      }
    }
  }
  redraw();
}
/**
 * SoundThread
 * \u97f3\u306e\u518d\u751f\u3092\u30de\u30eb\u30c1\u30b9\u30ec\u30c3\u30c9\u3067\u884c\u3046\u305f\u3081\u306e\u30af\u30e9\u30b9
 */

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
    // \u518d\u751f
    this.scClient.play(this.voltageMax);
    // \u9023\u7d9a\u518d\u751f\u3092\u3055\u3051\u308b\u305f\u3081\u306b\u9045\u5ef6\u306b\u3088\u308b\u9593\u5f15\u304d\u3092\u5165\u308c\u308b
    delay(100);
    isPlayable = true;
  }
}
/**
 * SCClient
 * supercollider\u3067\u97f3\u306e\u518d\u751f\u3092\u884c\u3046\u305f\u3081\u306e\u30af\u30e9\u30b9
 */




class SCClient {
  Synth synth;

  SCClient() {

  }

  public void play(float voltage) {
    // \u65b0\u898f\u306b\u697d\u5668\u3092\u5b9a\u7fa9(\u307e\u3060\u751f\u6210\u306f\u3055\u308c\u306a\u3044)
    synth = new Synth("testInst111");
    // \u5f15\u6570\u3092\u8a2d\u5b9a
    synth.set("amp", 0.5f);
    // \u53d7\u3051\u53d6\u3063\u305fvoltage\u306e\u5024\u306b\u3088\u3063\u3066\u518d\u751f\u3055\u308c\u308b\u97f3\u304c\u5909\u5316\u3059\u308b
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
