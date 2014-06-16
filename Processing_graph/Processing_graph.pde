/*
 * Touche for Arduino
 * Vidualization
 *
 */
 
import ddf.minim.*;
import ddf.minim.signals.*;

import java.util.*;

Minim minim;
AudioOutput out;
SineWave sine;
 
float voltageMax; //電圧の最大値
float timeMax; //電圧が最大値だったときの時間

ArrayList<HashMap> ballList = new ArrayList<HashMap>();
 
void setup() {
  //画面サイズ
  size(displayWidth, displayHeight);
  
  minim = new Minim(this);
  
  noLoop();
  //ポートを設定
  PortSelected=7; 
  //シリアルポートを初期化
  SerialPortSetup();
}
 
void draw() {
  background(63);
//  fill(255);
 
  //最大値を0に初期化
  voltageMax = timeMax = 0;
 
  if ( DataRecieved3 ) {
    //電圧の最大値と、そのときの時間を取得
    for (int i = 0; i < Voltage3.length; i++) {
      if (voltageMax < Voltage3[i]) {
        voltageMax = Voltage3[i];
        timeMax = Time3[i];
      }
    }
    
    if (voltageMax < 180) {
      HashMap<String, Float> hash = new HashMap();
      float random_x = random(1, width);
      float radius   = 200/voltageMax * 2;
      hash.put("x", random_x);
      hash.put("y", 0.0);
      hash.put("radius", radius);
      ballList.add(hash);
      println(radius);
    }
    
    for (int i = 0; i < ballList.size(); i++) {
      noStroke();
      float _x = (Float)ballList.get(i).get("x");
      float _y = (Float)ballList.get(i).get("y");
      float _radius = (Float)ballList.get(i).get("radius");
      ellipse(_x, _y, _radius, _radius);
      float _new_y = _y + 10;
      if (_new_y > height) {
        ballList.remove(i);
      } else {
        ballList.get(i).put("y", _new_y); 
      }
    }
 
    //時間と電圧の範囲(最小値と最大値)を表示
    println("Time range: " +  min(Time3) + " - " + max(Time3), 20, 20);
    println("Voltage range: " +  min(Voltage3) + " - " + max(Voltage3), 20, 40);
 
    //電圧の最大値と、その時の時間を表示
    println("Time: " + timeMax, 20, 80);
    println("Voltage: " + voltageMax, 20, 100);
  }
}
 
void stop() {
  myPort.stop();
  super.stop();
}
