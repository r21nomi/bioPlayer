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
float VOL_MIN = 110;
float VOL_MAX = 130;

float DGAIN   = 1;

ArrayList<HashMap> ballList = new ArrayList<HashMap>();

void setup() {
  //画面サイズ
  // size(displayWidth, displayHeight);
  size(800, 500);

  minim  = new Minim(this);
  player = minim.loadFile("rain.mp3");
  down   = new DownGain(DGAIN);
  // player.addEffect(down);
  player.play();
  player.addEffect(down);
  player.addEffect(down);
  player.addEffect(down);

  noLoop();
  //ポートを設定
  PortSelected = 3;
  // PortSelected = 7;
  //シリアルポートを初期化
  SerialPortSetup();
}

void draw() {
  background(63);
//  fill(255);

  //最大値を0に初期化
  voltageMax = timeMax = 0;

  if (DataRecieved3) {
    //電圧の最大値と、そのときの時間を取得
    for (int i = 0; i < Voltage3.length; i++) {
      if (voltageMax < Voltage3[i]) {
        voltageMax = Voltage3[i];
        timeMax    = Time3[i];
      }
    }

    if (voltageMax < VOL_MAX) {
      HashMap<String, Float> hash = new HashMap();
      float random_x              = random(1, width);
      float velocity              = map(timeMax, 120, 140, 1, 1.5);
      float radius                = map(voltageMax, VOL_MIN, VOL_MAX, 30, 5) * velocity;

      hash.put("x", random_x);
      hash.put("y", 0.0);
      hash.put("radius", radius);
      ballList.add(hash);
      println(radius);
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
      float _new_y = _y + 10;

      if (_new_y > height) {
        ballList.remove(i);
      } else {
        ballList.get(i).put("y", _new_y);
      }
    }

    //時間と電圧の範囲(最小値と最大値)を表示
    // println("Time range: " +  min(Time3) + " - " + max(Time3), 20, 20);
    // println("Voltage range: " +  min(Voltage3) + " - " + max(Voltage3), 20, 40);

    //電圧の最大値と、その時の時間を表示
    println("Time: " + timeMax);
    println("Voltage: " + voltageMax);
  }
}

void stop() {
  myPort.stop();
  super.stop();
}

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