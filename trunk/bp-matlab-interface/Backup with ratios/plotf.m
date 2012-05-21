%plot the fft of a function
%fs = sampling frequency
%f1 = highest frequency to be shown
function [f1 h] = plotf(datafft,fs,f1);

fmax=fs;
df=fmax/(length(datafft));
npoints1=round(f1/df);

f=0:df:f1;

h = plot(f(1:npoints1),abs(datafft(1:npoints1)));
title('Amplitude spectrum');
return
