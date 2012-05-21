clear all
close all

x2=csvread('bp-joao-1.csv');



y_p1_o=abs(x2(:,2)-255);
x_p1=x2(:,1);
% figure(1)
% plot(y_p1_o,'o')
% title('Y')
% figure(2)
% plot(x_p1,'o')
% title('X')
y_p1=medfilt1(y_p1_o,10);       




y_p1_converted=zeros(size(y_p1));
for i=1:length(y_p1)
    
    x=0;
    if (x_p1(i)==1)
        x=2^8;
    elseif (x_p1(i)==2)
        x=2^9;
    elseif (x_p1(i)==3)
        
        x=2^8+2^9;
    end
    
    y_p1_converted(i)=y_p1(i)+x;
    
end
% figure(6)
% plot(y_p1_converted,'o')
% title('bits')

 y_p1_volts=y_p1_converted.*5./1024;
 
 vs=5;

 y_pressure=(y_p1_volts./vs-0.04)./0.018;

 y_pressure_mm_hg=y_pressure*7.5;
% figure(7)
% plot(y_p1_volts)
% title('y volts')
% 
% figure(8)
% plot(y_pressure)
% title('y pressure KPa')

figure(9)
plot(y_pressure_mm_hg)
title('y pressure KPa')

figure(10)
y_pressure_mm_hg = y_pressure_mm_hg(2471:length(y_pressure_mm_hg));
plot(y_pressure_mm_hg)
title('Only the deflation of the cuff before filtering')

figure(11)
y_pressure_mm_hg_fft = fft(y_pressure_mm_hg);
plotf(y_pressure_mm_hg_fft,256,100) %randomly chose 256 as the sampling frequency
title('Specturm of Deflation pressure')

figure(12)
y_pressure_mm_hg_detrend = detrend(y_pressure_mm_hg);
plot(y_pressure_mm_hg_detrend)
title('Detrend Signal')

figure(13)
y_pressure_mm_hg_detrend_lowpass = lpfilter(y_pressure_mm_hg_detrend, 6,256,10);
plot(y_pressure_mm_hg_detrend_lowpass)
title('Detrend & low pass filtered Signal')

figure(14)
y_pressure_mm_hg_detrend_lowpass_fft = fft(y_pressure_mm_hg_detrend_lowpass);
plotf(y_pressure_mm_hg_detrend_lowpass_fft,256,100)
title('Specturm of Deflation pressure without DC and low passed')

figure(15)
y_pressure_mm_hg_detrend_bandpass = bpfilter(y_pressure_mm_hg_detrend, 6,256,1,40);
y_pressure_mm_hg_detrend_bandpass_fft = fft(y_pressure_mm_hg_detrend_bandpass);
plotf(y_pressure_mm_hg_detrend_bandpass_fft,256,100)
title('Specturm of Deflation pressure without DC and band passed')

figure(16)
plot(y_pressure_mm_hg_detrend_bandpass)
title('Detrend & band pass filtered Signal')

% y_pressure_mm_hg_detrend_filter = bpfilter(y_pressure_mm_hg_detrend,6,256,1, 100);
% y_pressure_mm_hg_fft_detrend = fft(y_pressure_mm_hg_detrend_filter);
% plotf(y_pressure_mm_hg_fft_detrend,256,100)
% title('Specturm of Deflation pressure without DC and low frequencies')
% 
% 
% 
% figure(13)
% 
% plot(y_pressure_mm_hg_detrend)
% title('Filtered Signal')



