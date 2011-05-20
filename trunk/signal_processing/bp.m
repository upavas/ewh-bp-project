clear all
close all

x2=csvread('mauro_test_folio_1.csv');

y_p1_o=abs(x2(:,3)-255);
x_p1=x2(:,2);
figure(1)
plot(y_p1_o,'o')
title('Y')
figure(2)
plot(x_p1,'o')
title('X')
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
figure(6)
plot(y_p1_converted,'o')
title('bits')

 y_p1_volts=y_p1_converted.*5./1024;
 
 vs=5;

 y_pressure=(y_p1_volts./vs-0.04)./0.018;

 y_pressure_mm_hg=y_pressure*7.5;
figure(7)
plot(y_p1_volts)
title('y volts')

figure(8)
plot(y_pressure)
title('y pressure KPa')

figure(9)
plot(y_pressure_mm_hg)
title('y pressure KPa')
 
