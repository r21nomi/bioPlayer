// class DownGain implements AudioEffect {
//   float gain = 1.0;

//   DownGain(float g) {
//     gain = g;
//   }

//   void process(float[] samp) {
//     float[] out = new float[samp.length];
//     for ( int i = 0; i < samp.length; i++ ) {
//       out[i] = samp[i] * gain;
//     }
//     arraycopy(out, samp);
//   }

//   void process(float[] left, float[] right) {
//     process(left);
//     process(right);
//   }
// }