/**
 * SoundThread
 * 音の再生をマルチスレッドで行うためのクラス
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
    // 再生
    this.scClient.play(this.voltageMax);
    // 連続再生をさけるために遅延による間引きを入れる
    delay(100);
    isPlayable = true;
  }
}