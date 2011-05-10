fs = 10000;t = 0:1/fs:2;x1 = (sawtooth(1*pi*t,0.12));%plot(t,x1); %wave1

%oscillations
tt = (0:1/4000:2);
xs = 0.025.*sin(1*pi*100*tt);plot(xs);
g1=0:0.0025:20;
gy=gaussmf(g1,[2 5]);
%plot(g1,gy);
sum=gy.*xs;%plot(sum);
clear tt xs g1 gy;

%this worked crap
%y = modulate(xs,120,1000,'am');plot(tt(1:150),y(1:150));
w2(1:20001)=0;
w2(11000:19000)=sum;
%plot(w2);
bp_wave=x1+w2;
plot(t,bp_wave);

%specgram(bp_wave);
%y=fft(bp_wave,512);
%plot(abs(y));


%detection method
%plot(sum);

    