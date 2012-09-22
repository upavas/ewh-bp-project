clear all
clc

%run('clean');
clear all;
close all;

s = serial('COM3'); %assigns the object s to serial port

set(s, 'InputBufferSize', 512); %number of bytes in inout buffer
set(s, 'FlowControl', 'hardware');
set(s, 'BaudRate', 9600);
set(s, 'Parity', 'none');
set(s, 'DataBits', 8);
set(s, 'StopBit', 1);
set(s, 'Timeout',10);
%clc;

disp(get(s,'Name'));
prop(1)=(get(s,'BaudRate'));
prop(2)=(get(s,'DataBits'));
prop(3)=(get(s, 'StopBit'));
prop(4)=(get(s, 'InputBufferSize'));

disp(['Port Setup Done!!',num2str(prop)]);

fopen(s);           %opens the serial port

disp('Running');
x=0;
initial_time = cputime;
time_array = 0;
pressure_array = 0;
b = [];
k = 1;
t = 1;
set(gcf,'currentcharacter','C')
Cont_or_Quit = get(gcf,'currentcharacter');
disp('Exit loop by pressing any key but ''C'',')
disp(['with figure: ',num2str(gcf)',' focused.'])


while t
    
    Cont_or_Quit = get(gcf,'currentcharacter');
    
    if (Cont_or_Quit~='C')
        break;
    end
    
    a = fread(s); %reads the data from the serial port and stores it to the matrix a
    
    %a=max(a);  % in this particular example, I'm plotting the maximum value of the 256B input buffer
    i = 1;
    a = [b ; a];
    while i <= length(a)
        if (a(i) == 0 && a(i + 1) == 0) && (a(i + 4) == 0 && a(i + 5) == 0)
            x = a(i + 2);
            y = a(i + 3);
            
            switch x
            case 0
                val = y;
            case 1
                val = (2^8) + y;
            case 2
                val = (2^9) + y;
            case 3
                val = (2^8 + 2^9) + y;
            end
            
            y_pressure=((val*5/(1024*5))-0.04)/0.018 * 7.5;
            
            time = cputime - initial_time;
                      
            plot(k,y_pressure, '*')
            hold on;
            
            
            time_array = [time_array; time];
            pressure_array = [pressure_array; y_pressure];
            
            i = i + 4;
            
            k = k + 1;
            
            if i >= length(a)-4
                b = a(i:end);
                a=0;
                break;
            end
        else
            i = i + 1;
            if i >= length(a)-4
                b = a(i:end);
                a=0;
                break;
            end
        end
    end
    
    a=0;
    drawnow;
    
end

fclose(s);
delete(s);       
        
        
           
                
        
        
        
       
                
                
                
                
                
             