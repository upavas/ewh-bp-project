clear all
close all
x1=csvread('bp-joao-h1.csv');

x2=csvread('bp-joao-h2.csv');

x_p=x1(:,2);
figure(1)
plot(x_p)
title('h1')
figure(2)
plot(x2(:,2))
title('h2')