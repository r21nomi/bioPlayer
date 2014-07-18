// play({SinOsc.ar(LFNoise0.kr(12, mul: 600, add: 1000), 0.3)})

// {Splay.ar(Ringz.ar(Impulse.ar([2, 1, 4], [0.1, 0.11, 0.12]), [0.1, 0.1, 0.5])) * EnvGen.kr(Env([1, 1, 0], [120, 10]), doneAction: 2)}.play

// {LocalOut.ar(a=CombN.ar(BPF.ar(LocalIn.ar(2)*7.5+Saw.ar([32,33],0.2),2**LFNoise0.kr(4/3,4)*300,0.1).distort,2,2,40));a}.play//#supercollider

// play{({|k|({|i|y=SinOsc;y.ar(i*k*k,y.ar(i*k**i/[4,5])*Decay.kr(Dust.kr(1/4**i),y.ar(0.1)+1*k+i,k*999))}!8).product}!16).sum}//#supercollider

// b=Buffer.read(s,"sounds/a11wlk01.wav");play{t=Impulse.kr(5);PlayBuf.ar(1,b,1,t,Demand.kr(t,0,Dseq(1e3*[103,41,162,15,141,52,124,190],4)))!2}

// play{f=LocalIn.ar(2).tanh;k=Latch.kr(f[0].abs,Impulse.kr(1/4));LocalOut.ar(f+CombC.ar(Blip.ar([4,6],100*k+50,0.9),1,k*0.3,50*f));f}//44.1kHz

// f={|t|Pbind(\note,Pseq([-1,1,6,8,9,1,-1,8,6,1,9,8]+5,319),\dur,t)};Ptpar([0,f.(1/6),12,f.(0.1672)],1).play//#supercollider reich RT @earslap

// play{x=SinOsc;y=LFNoise0;a=y.ar(8);(x.ar(Pulse.ar(1)*24)+x.ar(90+(a*90))+MoogFF.ar(Saw.ar(y.ar(4,333,666)),a*XLine.ar(1,39,99,99,0,2)))!2/3}

// Ptpar(({|i|[i*8,Pbind(\scale,[0,2,4,7,9],\degree,Pseq(32.fib.fold(0,10),4)+(2*i+i)-10,\dur,1+2**i%2/6)]}!4).flat).play // #supercollider

// Good
// {LocalOut.ar(a=DynKlank.ar(`[LocalIn.ar.clip2(LFPulse.kr([1,2,1/8]).sum/2)**100*100],Impulse.ar(10)));HPF.ar(a).clip2}.play

play{VarSaw.ar((Hasher.ar(Latch.ar(SinOsc.ar((1..4)!2),Impulse.ar([5/2,5])))*300+300).round(60),0,LFNoise2.ar(2,1/3,1/2))/5}//#supercollider