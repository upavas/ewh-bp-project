function [b,a]=filtcalc_pp(fs,N,ftype,fc)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%    [b,a] = FILTCALC(fs,N,ftype,fc)
%
% plots filterresponse and returns filter coefficients; 
% use 'fir' , 'but' , 'ell' , and 'che' for design-type
% use 'low' , 'high' , 'stop' for filter types
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

rows=28; columns=128;  %calculates HP butterworth filter coefficients

fs_half=fs/2;

wc=fc/fs_half;          % where ws (sampling freq) := 1.0
equidistpoints=200;
%

 [b,a]=butter_pp(N,wc,ftype);
 