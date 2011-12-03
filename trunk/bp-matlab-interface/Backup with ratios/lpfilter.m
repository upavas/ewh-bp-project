%data = vector of data
%norder = order of the filter to be used, eg 2,4,6 etc
%fs = sampling frequency
%fc = cut off frequency
function f=lpfilter(data,norder, fs, fc)
 
np=length(data);
[b a]=filtcalc_pp(fs,norder,'low',fc);
f=filter(b,a,data);

% samplesarray=0:1:length(data)-1;
% t=samplesarray'/360; %sample rate 360Hz
% np=3000;
% figure
% subplot(211)
% plot(tdata(N1:N2),data(N1:N2));
% title('Original signal v. time');
% subplot(212)
% plot(tdata(N1:N2),f(N1:N2));
% title('Filtered signal v. time');