   function my_closefcn(~,~)
   % User-defined close request function 
   % to display a question dialog box
   
   global port

      selection = questdlg('Do you want to close this session?',...
         'Close Request Function',...
         'Yes','No','Yes'); 
      switch selection, 
         case 'Yes',
             
             s = serial(port);
             set(s, 'InputBufferSize', 1024);                %number of bytes in input buffer
             set(s, 'BaudRate', 19200);
             set(s, 'Parity', 'none');
             set(s, 'DataBits', 8);
             set(s, 'StopBit', 1);
             set(s, 'Timeout',10);
             set(s, 'RequestToSend','on');

             fopen(s);                                       %opens the serial port

             fwrite(s,115);                                  % fprintf(s,'%s','s');
             fwrite(s,116);                                  % fprintf(s,'%s','t');
             fwrite(s,111);                                  % fprintf(s,'%s','o');
             fwrite(s,112);                                  % fprintf(s,'%s','p');

             fclose(s);
             delete(s);

             delete(gcf)
            
         case 'No'      
         return 
      end
   end