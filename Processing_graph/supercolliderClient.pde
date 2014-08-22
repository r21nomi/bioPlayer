import supercollider.*;
import oscP5.*;

class SCClient {
  Synth synth;

  SCClient() {

  }

  void play(float voltage) {
    // 新規に楽器を定義(まだ生成はされず)
    synth = new Synth("testInst111");
    // 引数を設定
    synth.set("amp", 0.5);
    synth.set("freq", map(voltage, height, 0, 20, 8000));
    // 楽器を生成
    synth.create();
  }
}