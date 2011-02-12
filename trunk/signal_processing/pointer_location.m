%pointer location


repeat = 1;

fprintf('Blood Pressure Interface\n');
fprintf('This program will run until the input value is zero\n');


while repeat == 1

    close all
    clear all
    
    x_array = [0];
    y_array = [0];
    pressure_array = [0];
    time_array = [0];

    decide = input('For USB input, press "u", for serial input press "s"\n','s');

    decide = lower(decide);

    if decide == 'u'


        initial_time = cputime;


        pressure = 2;

        while pressure > 1  %while the pressure has not reached its minimum, continue to take readings

            new =get(0,'PointerLocation');


            x = new(1,1);
            y = new(1,2);

            pressure = x;
            time = cputime - initial_time;
            
            x_array = [x_array, x];
            y_array = [y_array, y];
            
            pressure_array = [pressure_array, x];
            
            time_array = [time_array, time];
            

            plot(time,pressure);
            hold on;



            drawnow;

        end

    elseif decide == 's'

        COM_number = input('Enter the COM port number, eg 1,2,3\n');
        COM_name = ['COM' char(COM_number+48)];
        baud = input('Enter the baud rate (normal = 9600)\n');
        data_bits = input('Enter the number of data bits (normal = 8)\n');




        go = input('Press any key to read data','s');
        initial_time = cputime;


        s = serial(COM_name);
        set(s,'BaudRate',baud,'DataBits',data_bits);
        set(s,'StopBits',1);
        set(s,'ReadAsyncMode','continuous');
        fopen(s);



        pressure = 1;

        while pressure ~= 0
            string_in = fread(s,4);
            pressure = 1000*string_in(1,1) + 100*string_in(2,1) ... %assuming that pressure values of maximum 4 digits will be sent continously until the pressure = 0
                +10*string_in(3,1) + string_in(4,1);                %and assuming the numbers sent are numerical values and not ascii codes.

            pressure_array = [pressure_array, pressure];

            
            time = cputime - initial_time;
            time_array = [time_array, time];
            

            plot(time, pressure)
            hold on;
            drawnow;

        end




    else
        fprintf('Incorrect key selection');
    end
    
    pressure_array
    %Insert signal processing algorithms
    %output blood pressure stats


    repeat = input('Enter 1 to repeat or any other number to close\n');

end





