function y=interpMod(xi,yi,x)
x0 = x(1);
y0 = 0;
y1 = yi(1);
x1 = xi(1);
y(1) = 0;
j = 1;

for i=2:length(x)
    y(i) = y0 + ((x(i) - x0)*(y1 - y0)/(x1 - x0));
    if (x(i) == x1)
        if (j == length(xi))
            x0 = x1;
            x1 = x(length(x));
            y0 = y1;
            y1 = 0;
        else
            j = j + 1;
            x0 = x1;
            x1 = xi(j);
            y0 = y1;
            y1 = yi(j);
        end
    end
end