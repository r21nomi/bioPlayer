/*
 * Touche for Arduino
 * Vidualization
 *
 */

import ddf.minim.*;
import ddf.minim.effects.*;
import java.util.*;

Minim minim;
AudioPlayer player;
DownGain down;

float voltageMax; //電圧の最大値
float timeMax; //電圧が最大値だったときの時間
// float VOL_MIN = 110;
// float VOL_MAX = 130;
float VOL_MIN  = 100;
float VOL_MAX  = 500;
float BOUNDARY = 200;

// デバッグ用
boolean isDelay = true;
float maxVol    = 0;
float minVol    = 200;

float DGAIN   = 100;

ArrayList<HashMap> ballList = new ArrayList<HashMap>();

void setup() {
  //画面サイズ
  // size(displayWidth, displayHeight);
  size(800, 500);

  minim  = new Minim(this);
  player = minim.loadFile("rain.mp3");
  down   = new DownGain(DGAIN);
  player.play();

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

  background(63);
//  fill(255);

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
        player.removeEffect(down);  // エフェクトを削除
        DGAIN = voltageMax / 10;
        down  = new DownGain(DGAIN);
        player.addEffect(down);  // 新たにエフェクトを追加
      }
    }

    // if (voltageMax < VOL_MAX) {
    if (voltageMax > BOUNDARY) {
      HashMap<String, Float> hash = new HashMap();
      float random_x              = random(1, width);
      float velocity              = map(timeMax, 120, 140, 1, 1.5);
      float radius                = map(voltageMax, VOL_MIN, VOL_MAX, 30, 5) * velocity;

      hash.put("x", random_x);
      hash.put("y", 0.0);
      hash.put("radius", radius);
      ballList.add(hash);
      // println(radius);
      // player.addEffect(down);
    } else {
      // player.removeEffect(0);
    }

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

    //電圧の最大値と、その時の時間を表示
    // println("Time: " + timeMax);
    // println("Voltage: " + voltageMax);

    // デバッグ
    println("Voltage: " + voltageMax + ", maxVol: " + maxVol);
    println("minVol: " + minVol);
  }
}

void stop() {
  myPort.stop();
  super.stop();
}

// Audio
class DownGain implements AudioEffect {
  float gain = 1.0;

  DownGain(float g) {
    gain = g;
  }

  void process(float[] samp) {
    float[] out = new float[samp.length];
    for ( int i = 0; i < samp.length; i++ ) {
      out[i] = samp[i] * gain;
    }
    arraycopy(out, samp);
  }

  void process(float[] left, float[] right) {
    process(left);
    process(right);
  }
}