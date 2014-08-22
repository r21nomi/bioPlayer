s.dumpOSC;

(
SynthDef("testInst111",{
	arg freq=440, length=1.0, amp=0.5;
	var env, out;
	env = Env.perc(0.01, length);
	out = SinOsc.ar([freq,freq*1.001]) * EnvGen.kr(env, doneAction:2) * amp;
	Out.ar(0, out);
}).store;
)

/*s.dumpOSC;
s.boot;

(
var buf = Buffer.read(s, path);
var path = thisProcess.nowExecutingPath.dirname +/+ "audio/audio.wav";

 SynthDef("playBufMono", {| out = 0, bufnum = 0, rate = 1 |
  var scaledRate, player;
  scaledRate = rate * BufRateScale.kr(bufnum);
  player = PlayBuf.ar(1, bufnum, scaledRate, doneAction:2);
  Out.ar(out, player)
 }).add;

buf = Buffer.read(s, path);

SynthDef("testInst111",{
	// s.sync; // wait for the server
	Synth(\playBufMono, [\out, 0, \bufnum, buf.bufnum, \rate, 1]).play
}).store
)*/