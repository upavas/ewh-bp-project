%data = vector of data
%norder = order of the filter to be used, eg 2,4,6 etc
%fs = sampling frequency
%fc = cut off frequency1


function [datafiltered]=highpassfilter(data,norder,fs,fc)

[b,a]=filtcalc(fs,'but',norder,'high',fcutoff);
datafiltered=filter(b,a,data);
%plotfft(datafiltered,fs,100); %plot to 100Hz

