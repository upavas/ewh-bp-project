function y = myfft(x,fs)

N = length(x);
w = fs*(0:(N-1))/N;







plot(w,abs(y)), title('Spectrum of frequencies')

