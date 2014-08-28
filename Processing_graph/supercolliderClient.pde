/**
 * SCClient
 * supercolliderで音の再生を行うためのクラス
 */

import supercollider.*;
import oscP5.*;

class SCClient {
  Synth synth;

  SCClient() {

  }

  void play(float voltage) {
    // 新規に楽器を定義(まだ生成はされない)
    synth = new Synth("testInst111");
    // 引数を設定
    synth.set("amp", 0.5);
    // 受け取ったvoltageの値によって再生される音が変化する
    synth.set("freq", map(voltage, height, 0, 20, 8000));
    // 楽器を生成
    synth.create();
  }
}