function [b,a]=filtcalc(fs,type,N,ftype,fc)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%    [b,a] = FILTCALC(fs,type,N,ftype,fc)
%
% plots filterresponse and returns filter coefficients; 
% use 'fir' , 'but' , 'ell' , and 'che' for design-type
% use 'low' , 'high' , 'stop' for filter types
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
rows=28;columns=128;  %calculates HP butterworth filter coefficients

fs_half=fs/2;

wc=fc/fs_half;          % where ws (sampling freq) := 1.0
equidistpoints=200;
%

if (type=='but') [b,a]=butter(N,wc,ftype);
  else
    if (type=='che') [b,a]=cheby1(N,0.5,wc,ftype);
    else
      if (type=='ell') [b,a]=ellip(N,0.5,100,wc,ftype);
        else
          disp('calculate FIR filter');
          [b,a]=fir1(N,wc,ftype);
        end;
    end;
end;
%
% plots manitude and phase response
%
[h,w]=freqz(b,a,equidistpoints);
mag=(abs(h)); phase=angle(h);
dbmag=20*log10(mag);

w=w/pi*fs_half;
plot_range=equidistpoints;
subplot(2,1,1);plot(w(1:plot_range),dbmag(1:plot_range));grid;
ylabel('Magnitude (dB)'); xlabel('Frequency (Hz)');
subplot(2,1,2);plot(w(1:plot_range),phase(1:plot_range));grid;
ylabel('Phase (rad)'); xlabel('Frequency (Hz)');
