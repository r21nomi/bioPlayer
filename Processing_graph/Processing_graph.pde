/*
 * Touche for Arduino
 * Vidualization
 *
 */

import java.util.*;

float voltageMax; //電圧の最大値
float timeMax; //電圧が最大値だったときの時間

// READ ME
// Edit these values depending on your environment
float VOL_MIN  = 70;
float VOL_MAX  = 350;
float BOUNDARY = 200;
// ------------------------------------------------

// デバッグ用
boolean isDelay  = true;
float maxVol     = 0;
float minVol     = 200;

ArrayList<HashMap> ballList = new ArrayList<HashMap>();

void setup() {
  //画面サイズ
  size(displayWidth, displayHeight);
  // size(800, 500);
  frameRate(60);
  background(0);

  noLoop();
  //ポートを設定
  PortSelected = 2;
  // PortSelected = 7;
  //シリアルポートを初期化
  SerialPortSetup();
}

void draw() {
  // 起動時のセンシングされてない値をカウントを最大値／最小値にカウントしないために待機処理を入れる
  if (isDelay) {
    delay(5000);
    isDelay = false;
  }

  noStroke();
  fill(0, 255);
  rectMode(CORNER);
  rect(0, 0, width, height);

  //最大値を0に初期化
  voltageMax = timeMax = 0;

  if (DataRecieved3) {
    //電圧の最大値と、そのときの時間を取得
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
      float velocity              = map(timeMax, 120, 140, 1, 1.5);
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

    // デバッグ
    if (voltageMax > maxVol) {
      maxVol = voltageMax;
    } else if (voltageMax < minVol) {
      minVol = voltageMax;
    }

    // デバッグ
    println("Voltage: " + voltageMax + ", maxVol: " + maxVol);
    println("minVol: " + minVol);
  }
}

void stop() {
  myPort.stop();
  super.stop();
}