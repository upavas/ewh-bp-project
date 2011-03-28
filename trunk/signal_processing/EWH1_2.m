%1st equation
i=1;
for Pt= -100:1:200
 if(Pt==0 || Pt<0)
 v(i) = 1.*exp(0.09*Pt); 
 i=i+1;
 elseif(Pt>0)
 v(i) = 4+(-3.*exp(-0.03*Pt));
 i=i+1;
 end
end
plot(v);

%get the eqns
syms vol x;
vol = 4+(-3*exp(-0.03.*x));
exp1=diff(vol,x);

%2nd eqn
clear;
i=1;
for Pt= -100:1:200
 if(Pt==0 || Pt<0)
 v(i) = (9*exp((9*Pt)/100))/100;
 i=i+1;
 elseif(Pt>0)
 v(i) = 9/(100*exp((3*Pt)/100));
 i=i+1;
 end
end
plot(v);

