# Bio Player  
## 概要  
植物に触ったら様々なインタラクションをするインスタレーション作品。  
  
## 準備  
- Arduino  
- Processing  
- 観葉植物  
- パーツ類    
  
## 記録  
#### 2014年6月27日  
- ##### ベンジャミンでタッチテスト  
微弱にしか反応しない  
Processingのmap()で検出した値をマッピングして増幅  
（少しはましになったが、葉の部分があまり反応しない）  
インダクターを外したら取得される値（静電容量？）の上限が増えた！！  
　→　葉の部分もしっかり反応するようになった（＾q＾）

supercoliderでオーディオのテストをする
https://gist.github.com/tado/6795733  
  
#### 2014年7月18日  
- ##### サウンド再生  
サウンドはsupercolliderで生成＆再生  
processingとの連携は下記ライブラリを使用。  
　・supercollider for processingライブラリ  
　・oscP5ライブラリ  
どちらもprocessingの「Sketch > Import Library」から追加  
植物にタッチしてそれっぽい音を出せるようにはなった。  
ただ、サウンド再生されてる間は描画処理が追いつかなくなりガクガクになる…orz  
参考：[http://yoppa.org/geidai_music13/5121.html](http://yoppa.org/geidai_music13/5121.html)

- ##### 次回課題  
サウンド再生の方法を変える。  
常時再生させておいてタッチ時にエフェクトを加えるとか。

  
## 参考  
[Toucheセンサーを使う 1 – Toucheセンサーで物体へのタッチを検出](http://yoppa.org/tau_bmaw13/4819.html?undefined&undefined)
