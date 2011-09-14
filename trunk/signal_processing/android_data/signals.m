clear all;
close all;
filename1='bp_measure_1302793459179.csv';
M1 = csvread(filename1, 3, 0)

figure(1)
plot(M1(1:end,1),M1(1:end,2));

filename2='bp_measure_1302793670323.csv';
M2 = csvread(filename2, 3, 0)

figure(2)
plot(M2(1:end,1),M2(1:end,2));

filename3='bp_measure_1302878846335.csv';
M3 = csvread(filename3, 3, 0)

figure(3)
plot(M3(1:end,1),M3(1:end,2));
x3=find(M3<200);
x4=find(M3>40);
hold on 
plot(find(M3<200 && M3>40),M3(find(M3<200 && M3>40)));

x1=4000;
x2=8000;

slope=M3(x2)-M3(x1)/(x2-x1)

hold on 
plot(find(M3<200 && M3>40),M3(find(M3<200 && M3>40)));



figure(5)
plot(M3(1:end,2));


filename4='bp_measure_1302877112298.csv';
M4 = csvread(filename4, 3, 0)



figure(4)
plot(M4(1:end,1),M4(1:end,2));