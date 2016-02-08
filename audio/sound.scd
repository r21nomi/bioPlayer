SynthDef(\ringtone,{
	arg freq=440, length=1.0, amp=0.5;
	var env, out;
	env = Env.perc(0.01, length);
	out = SinOsc.ar([freq,freq*1.001]) * EnvGen.kr(env, doneAction:2) * amp;
	Out.ar(0, out);
}).store;