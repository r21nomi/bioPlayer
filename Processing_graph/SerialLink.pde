import processing.serial.*;

// 自身の環境のポートを指定
int SerialPortNumber = 2;
int PortSelected     = 1;

int xValue, yValue, Command;
boolean Error       = true;
boolean UpdateGraph = true;
int lineGraph;
int ErrorCounter  = 0;
int TotalRecieved = 0;

boolean DataRecieved1 = false,
        DataRecieved2 = false,
        DataRecieved3 = false;

float[] DynamicArrayTime1, DynamicArrayTime2, DynamicArrayTime3;
float[] Time1, Time2, Time3;
float[] Voltage1, Voltage2, Voltage3;
float[] current;
float[] DynamicArray1, DynamicArray2, DynamicArray3;

float[] PowerArray        = new float[0]; // Dynamic arrays that will use the append()
float[] DynamicArrayPower = new float[0]; // function to add values
float[] DynamicArrayTime  = new float[0];

String portName;
String[] ArrayOfPorts = new String[SerialPortNumber];

boolean DataRecieved  = false,
        Data1Recieved = false,
        Data2Recieved = false;

int incrament        = 0;
int NumOfSerialBytes = 8; // The size of the buffer array
int[] serialInArray  = new int[NumOfSerialBytes]; // Buffer array
int serialCount      = 0; // A count of how many bytes received
int xMSB, xLSB, yMSB, yLSB; // Bytes of data

Serial myPort; // The serial port object

// シリアル通信の初期化関数
void SerialPortSetup() {
  portName     = Serial.list()[PortSelected];
  ArrayOfPorts = Serial.list();
  myPort       = new Serial(this, portName, 115200);
  println(ArrayOfPorts);  // ポートのリストを列挙
  delay(50);
  myPort.clear();
  myPort.buffer(20);
}

// シリアルイベント
// シリアルポートから何か情報を受けとると呼びだされる
void serialEvent(Serial myPort) {

  while (myPort.available() > 0) {

    // シリアルのバッファから次のバイト列を読み出す
    int inByte = myPort.read();

    if (inByte == 0) {
      serialCount = 0;
    }
    if (inByte > 255) {
      println(" inByte = " + inByte);
      exit();
    }

    // シリアルポートから取得された最新のバイトを配列に追加
    serialInArray[serialCount] = inByte;
    serialCount++;

    Error = true;
    if (serialCount >= NumOfSerialBytes ) {
      serialCount = 0;
      TotalRecieved++;
      int Checksum = 0;

      // Checksum = (Command + yMSB + yLSB + xMSB + xLSB + zeroByte) % 255;
      for (int x = 0; x < serialInArray.length - 1; x++) {
        Checksum = Checksum + serialInArray[x];
      }
      Checksum = Checksum % 255;

      if (Checksum == serialInArray[serialInArray.length - 1]) {
        Error        = false;
        DataRecieved = true;
      }
      else {
        Error        = true;
        DataRecieved = false;
        ErrorCounter++;
        println("Error:  " + ErrorCounter + " / " + TotalRecieved + " : " + float(ErrorCounter / TotalRecieved) * 100 + "%");
      }
    }

    if (!Error) {
      int zeroByte = serialInArray[6];
      xLSB = serialInArray[3];
      if ( (zeroByte & 1) == 1) xLSB = 0;
      xMSB = serialInArray[2];
      if ( (zeroByte & 2) == 2) xMSB = 0;
      yLSB = serialInArray[5];
      if ( (zeroByte & 4) == 4) yLSB = 0;
      yMSB = serialInArray[4];
      if ( (zeroByte & 8) == 8) yMSB = 0;

      // バイト(8bit)単位に分割されたデータを合成して、16bit(0〜1024)の値を復元する
      Command = serialInArray[1];
      xValue  = xMSB << 8 | xLSB; // xMSBとxLSBの値から、xValueを合成
      yValue  = yMSB << 8 | yLSB; // yMSBとyLSBの値から、yValueを合成

      // Command, xValue, yValueの3つの値がArduinoから取得完了
      // Commandの値によって、動作をふりわけ
      switch(Command) {

        case 1: // 配列にデータを追加
          DynamicArrayTime3 = append(DynamicArrayTime3, xValue);
          DynamicArray3     = append(DynamicArray3, yValue);
          break;
        case 2: // 想定外のサイズの配列を受けとった場合、配列を空に初期化
          DynamicArrayTime3 = new float[0];
          DynamicArray3     = new float[0];
          break;
        case 3: // 配列の受信完了、値を追加した配列をグラフ描画用の配列にコピーして、グラフを描画する
          Time3         = DynamicArrayTime3;
          Voltage3      = DynamicArray3;
          DataRecieved3 = true;
          break;
      }
    }
  }
  redraw();
}