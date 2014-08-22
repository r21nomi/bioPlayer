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
    delay(100);  // 連続再生をさけるために遅延による間引きを入れる
    isPlayable = true;
  }
}