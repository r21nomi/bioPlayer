/*
 * Touche for Arduino
 * Vidualization
 *
 */

import java.util.*;

float voltageMax; //電圧の最大値
float timeMax; //電圧が最大値だったときの時間
float VOL_MIN  = 130;
float VOL_MAX  = 230;
float BOUNDARY = 180;

// デバッグ用
boolean isDelay  = true;
float maxVol     = 0;
float minVol     = 200;

SCClient scClient;
boolean playAble = true;

ArrayList<HashMap> ballList = new ArrayList<HashMap>();

void setup() {
  //画面サイズ
  size(displayWidth, displayHeight);
  // size(800, 500);
  frameRate(60);
  background(0);
  scClient = new SCClient();

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
  fill(0, 30);
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
        if (playAble && voltageMax > BOUNDARY) {
          playAble = false;
          scClient.play(voltageMax);
          delay(100);  // 連続再生をさけるために遅延による間引きを入れる
          playAble = true;
        }
      }
    }

    if (voltageMax > BOUNDARY) {
      HashMap<String, Float> hash = new HashMap();
      float random_x              = random(1, width);
      float velocity              = map(timeMax, 120, 140, 1, 1.5);
      float radius                = map(voltageMax, VOL_MIN, VOL_MAX, 30, 5) * velocity;

      hash.put("x", random_x);
      hash.put("y", 0.0);
      hash.put("radius", radius);
      ballList.add(hash);
    }

    fill(255, 100);

    for (int i = 0; i < ballList.size(); i++) {
      noStroke();
      float _x      = (Float)ballList.get(i).get("x");
      float _y      = (Float)ballList.get(i).get("y");
      float _radius = (Float)ballList.get(i).get("radius");
      ellipse(_x, _y, _radius, _radius);
      float _new_y  = _y + 10;

      if (_new_y > height) {
        ballList.remove(i);
      } else {
        ballList.get(i).put("y", _new_y);
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