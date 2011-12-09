%-----------------------------------|
% BP Interface: Oxford University   |
%-----------------------------------|
% Configure the HyperTerminal application for the following
% default Configuration:
% - Baud Rate = 19200
% - Data Bits = 8
% - Parity Type = None
% - Stop Bits = One

% Open constructor:
% tabpanel('BP_Interface_iTab','mainTab');

function varargout = BP_Interface_iTab(varargin)
% BP_INTERFACE MATLAB code for BP_Interface.fig
%      BP_INTERFACE, by itself, creates a new BP_INTERFACE or raises the existing
%      singleton*.
%
%      H = BP_INTERFACE returns the handle to a new BP_INTERFACE or the handle to
%      the existing singleton*.
%
%      BP_INTERFACE('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in BP_INTERFACE.M with the given input arguments.
%
%      BP_INTERFACE('Property','Value',...) creates a new BP_INTERFACE or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before BP_Interface_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to BP_Interface_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help BP_Interface

% Last Modified by GUIDE v2.5 07-Dec-2011 10:18:39

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @BP_Interface_OpeningFcn, ...
                   'gui_OutputFcn',  @BP_Interface_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before BP_Interface is made visible.
function BP_Interface_OpeningFcn(hObject, ~, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to BP_Interface (see VARARGIN)

% Choose default command line output for BP_Interface
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

set(gcf,'CloseRequestFcn',@my_closefcn)

% UIWAIT makes BP_Interface wait for user response (see UIRESUME)
% uiwait(handles.figure1);

global port
global SBP_RATIO
global DBP_RATIO
global time_array
global pressure_array
global Fs
global FLAG
global Pname
global Page
global Psex
global PcuffSize
global Parm
global numMeasures
global mySessionData
global numSessionRecords
global language
global study_number
global Psmokes
global Pmeasure_num

%%%%%%%%%%%%%%%%%%%%%%%%%%%%SET VARIABLES HERE%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

study_number = 1;
SBP_RATIO = 0.4;
DBP_RATIO = 0.7;
Fs = 75; % Hz

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

language = char(java.util.Locale.getDefault().getLanguage());
setLanguage(language)

time_array = 0;
pressure_array = 0;
FLAG = 0;
Pname = '';
Page = '';
Psex = 'Male';
PcuffSize = 'Adult';
Parm = 'Left';
Psmokes = 'No';
Pmeasure_num = 1;

numMeasures = 0;
numSessionRecords = 100;

mySessionData = cell(numSessionRecords,11);
for x = 1:numSessionRecords
    for z = 1:11
        mySessionData{x,z} = '            -';
    end
end

set(handles.uitable1,'data',mySessionData);

infos = instrhwinfo('serial');                                              %instrument control toolbox! instrhwinfo: information about available hardware
ports = size(infos.SerialPorts);
if ports(1,1) == 0
    msgbox('No serial port was found in the system. Please connect the BP monitor device and restart the Interface.','Help Box:','error');
    return
end

uports = infos.SerialPorts;
t_port = 1;
while t_port <= ports(1,1)
    port = uports{t_port};
    %port = '/dev/ttyS101';     %Linux
    
    s = serial(port);
    set(s, 'InputBufferSize', 1024);                %number of bytes in input buffer
    set(s, 'BaudRate', 19200);
    set(s, 'Parity', 'none');
    set(s, 'DataBits', 8);
    set(s, 'StopBit', 1);
    set(s, 'Timeout',10);
    set(s, 'RequestToSend','on');
    
    fopen(s);                                       %opens the serial port
    
    fwrite(s,119);                                  % fprintf(s,'%s','w');
    fwrite(s,104);                                  % fprintf(s,'%s','h');
    fwrite(s,111);                                  % fprintf(s,'%s','o');
    
    a = fread(s);                                   % reads the data from the serial port and stores it to the matrix a
    
    fclose(s);
    delete(s);
    
    t = 1;
    rsp = '';
    buffer_len = size(a);
    
    while t <= 10 && buffer_len(1,1) >= 10                                  % 'ADPS' has 4 chars: answer from the PCB
        rsp = strcat(rsp,char(a(t,1)));                                     % 'iceicebaby' has 10 chars
        t = t+1;
    end
    
    if strcmp(rsp,'iceicebaby')
        return
    elseif t_port == ports(1,1)
        msgbox('There are (virtual) serial devices connected to the computer but none is the BP device! Please connect the BP monitor device and restart the Interface.','Help Box:','error');
        return
    end
    
    t_port = t_port+1;
end


% --- Outputs from this function are returned to the command line.
function varargout = BP_Interface_OutputFcn(~, ~, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in pushbutton1.
function pushbutton1_Callback(~, ~, handles)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

%global Cont_or_Quit
global port
global SBP_RATIO
global DBP_RATIO
global FLAG
global time_array
global pressure_array
global time
global Fs
global SBP
global DBP
global HR

cla(handles.axes1,'reset')

s = serial(port);
set(s, 'InputBufferSize', 1024);                                %number of bytes in inout buffer
set(s, 'BaudRate', 19200);
set(s, 'Parity', 'none');
set(s, 'DataBits', 8);
set(s, 'StopBit', 1);
set(s, 'Timeout',10);
set(s, 'RequestToSend','on');

fopen(s);                                           %opens the serial port

initial_time = cputime;
b = [];
k = 1;
c = 0;
t = 1;
set(gcf,'currentcharacter','C')
%Cont_or_Quit = get(gcf,'currentcharacter');

j = 0;
myFLAG = 1;

try 
time_array = [];
pressure_array = [];
    
while t
    
    Cont_or_Quit = get(gcf,'currentcharacter');
    
    if (Cont_or_Quit ~='C')
        break;
    end
    
    a = fread(s); %reads the data from the serial port and stores it to the matrix a
    
    c = [c; a];
    
    %a=max(a);  % in this particular example, I'm plotting the maximum value of the 256B input buffer
    i = 1;
    a = [b ; a];
    
    jjj = 1;
    %i = 1;
    while i <= length(a)
%         switch(state)
%             case 1
%                 if a(i) == 0
%                     state = state + 1;
%                     i = i + 1;
%                 else
%                     FLAG = 0;
%                     aux = 999;
%                     aux1 = aux;
%                     i = i + 1;
%                 end
%                 
%             case 2
%                 if (a(i) == 0 && FLAG == 1)
%                     x = aux;
%                     y = aux1;
%                     
%                     
%                     %%% aux2 = convert(x,y);
%                     
%                     switch x
%                         case 0
%                             val = y;
%                         case 1
%                             val = (2^8) + y;
%                         case 2
%                             val = (2^9) + y;
%                         case 3
%                             val = (2^8 + 2^9) + y;
%                     end
%                     
%                     y_pressure=((val*3.72/(1024*5))-0.04)/0.018 * 7.5;
%                     
%                     time = cputime - initial_time;
%                     
%                     plot(handles.axes1,time,y_pressure)%, '-')
%                     hold on;
%                     
%                     drawnow;
%                     
%                     time_array = [time_array; time];
%                     pressure_array = [pressure_array; y_pressure];
%                     
%                     
%                     %ypressure = aux2;
%                     %time = cputime - initial_time;
%                     
%                     FLAG = 0;
%                     aux = 999;
%                     aux1 = aux;
%                     state = state + 1;
%                     i = i + 1;
%                 elseif a(i) == 0
%                     state = state + 1;
%                     i = i + 1;
%                 else
%                     if aux1 == 0
%                         state = 3;
%                     else
%                         state = 1;
%                         i = i + 1;
%                     end
%                 end
%                 
%             case 3
%                 if a(i) < 4
%                     state = state + 1;
%                     aux = a(i);
%                     i = i + 1;
%                 else
%                     state = 1;
%                     i = i + 1;
%                 end
%             case 4
%                 if ~(aux == 0 && a(i) == 0)
%                     state = 1;
%                     FLAG = 1;
%                     aux1 = a(i);
%                     i = i + 1;
%                 else
%                     state = 3;
%                     i = i + 1;
%                 end
%         end
               
        if (a(i) == 0 && a(i + 1) == 0) && (a(i + 4) == 0 && a(i + 5) == 0)
            x = a(i + 2);
            y = a(i + 3);
            
            val = bitshift(double(x), 8) + double(y);                 %to convert the wanted number (that we receive), we do: the sum of the most significant byte changed to the right with the one less significant
            
            y_pressure=((val*3.72/(1024*5))-0.04)/0.018 * 7.5;
            
            time = cputime - initial_time;
            
            time_array = [time_array; time];
            pressure_array = [pressure_array; y_pressure];
            
            i = i + 4;
            
            if (jjj == 64 || jjj == 128 || jjj == 192 || jjj == 256)
                plot(handles.axes1,k,y_pressure, '*')
                hold on
                
                hline = refline([0 200]);
                set(hline,'Color','r')
                ylim([0 250]);

                drawnow
            end
            
            k = k + 1;
            
            jjj = jjj + 1;
            
            if i >= length(a)-4
                b = a(i:end);
                a=0;
                break;
            end
        else
            i = i + 1;
            if i >= length(a)-4
                b = a(i:end);
                a = 0;
                break;
            end
        end
    end
    j = j + 1;
    %y_down = [y_down pressure_array(256*j - 192) pressure_array(256*j - 128) pressure_array(256*j - 64)];
    %t_down = [t_down time_array(256*j - 192) time_array(256*j - 128) time_array(256*j - 64)]; 
    %hhh = [(k*(j-1)/2)+128 k];
    %yyy = [pressure_array(((j-1)*k/2)+128) y_pressure];
    %plot(handles.axes1,hhh,yyy, '*')
    %plot(handles.axes1,k,y_pressure, '*')
    %plot(handles.axes1,k*j/2,pressure_array(j*k/2),'*')
    %hold on
    
    %plot(handles.axes1,round((k*(j-1)/2)+128),y_pressure(round((k*(j-1)/2)+128)), '*')
    %hold on
    a = 0;
    %drawnow;
    
end

hold off

fclose(s);
delete(s);
myFLAG = 1;

if (max(pressure_array) < 180)
    errordlg('Acquire new signal! Pressure must be above 180 mmHg.');
    return
end

% bp_wave_f = medfilt1(pressure_array,5);
% 
% plot(handles.axes1,time,bp_wave_f), axis tight
% title('Cuff Pressure Wave after median filter'), xlabel('Time (secs)'), ylabel('Pressure (mmHg)')

%--------------------------------------------------------------------------
%Signal Procesing!
bp_wave_f = medfilt1(pressure_array,5);
% figure,
% plot(time,bp_wave_f), axis tight
% title('Cuff Pressure Wave after median filter'), xlabel('Time (secs)'), ylabel('Pressure (mmHg)')

time = (1:length(pressure_array))/Fs;
plot(handles.axes1,time,pressure_array);
xlabel('Time (s)');
ylabel('Pressure (mmHg)');

pressure_array = bp_wave_f;

[SBP,DBP,HR] = SignalProcessing(pressure_array,time,SBP_RATIO,DBP_RATIO);

set(handles.edit1,'String',DBP);
set(handles.edit2,'String',HR);
set(handles.edit3,'String',SBP);

FLAG = 1;

catch exception
    if ~myFLAG
        fclose(s);
        delete(s);
    end
    %cla(handles.axes1,'reset')
    time_array = [];
    pressure_array = [];
end

function pushbutton1_CreateFcn(~, ~, ~)
% hObject    handle to pushbutton3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% --- Executes on button press in pushbutton2.
function pushbutton2_Callback(~, ~, ~)
% hObject    handle to pushbutton2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

global time_manual_device
% global SBP_RATIO
% global DBP_RATIO
% global pressure_array
% global time
% global SBP
% global DBP
% global HR
% 
% [SBP,DBP,HR] = SignalProcessing(pressure_array,time,SBP_RATIO,DBP_RATIO);
% 
% set(handles.edit1,'String',SBP);
% set(handles.edit2,'String',DBP);
% set(handles.edit3,'String',HR);

time_manual_device = datestr(now);

function pushbutton2_CreateFcn(~, ~, ~)
% hObject    handle to pushbutton3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% --- Executes on button press in pushbutton3.
function pushbutton3_Callback(hObject, ~, handles)
% hObject    handle to pushbutton3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Choose default command line output for BP_Interface
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

global Pname
global Page
global Psex
global PcuffSize
global Parm
global SBP_RATIO
global DBP_RATIO
global FLAG
global time_array
global pressure_array
global SBP
global DBP
global HR
global numMeasures
global mySessionData
global numSessionRecords
global study_number
global Pmeasure_num
global Psmokes
global time_manual_device
global Ptraditional_SBP
global Ptraditional_DBP
global Ptraditional_HR
global Pstatus
global Pmedication
global Pobservations
global Parmcircunf
global Pobservername

if FLAG == 0
    msgbox('A new acquisition needs to be performed before saving the values!','Help Box:','warn');
    return
else
    if (size(Pname,1) == 0 || size(PcuffSize,1) == 0)
        msgbox('Compulsory patient details must be given in order to save the acquired measurements.','Help Box:','error');
        return
    end
    FLAG = 0;
end

numMeasures = numMeasures+1;
if numMeasures > numSessionRecords
    numMeasures = 1;
    for x = 2:numSessionRecords
        for z = 2:11
            mySessionData{x,z} = '            -';
        end
    end
end

mySessionData{numMeasures,1} = Pname;
if isempty(Page)
    Page = '            -';
end
if isnan(Page)
    Page = '            -';
end
mySessionData{numMeasures,2} = Page;
if isempty(Psex)
    Psex = '            -';
end
mySessionData{numMeasures,3} = Psex;
mySessionData{numMeasures,4} = datestr(clock);
mySessionData{numMeasures,5} = PcuffSize;
mySessionData{numMeasures,6} = Parm;
mySessionData{numMeasures,7} = SBP_RATIO;
mySessionData{numMeasures,8} = DBP_RATIO;
mySessionData{numMeasures,9} = SBP;
mySessionData{numMeasures,10} = DBP;
mySessionData{numMeasures,11} = HR;

set(handles.uitable1,'data',mySessionData);

if isempty(Parmcircunf)
    Parmcircunf = '            -';
end
if isnan(Parmcircunf)
    Parmcircunf = '            -';
end
if isempty(Pstatus)
    Pstatus = '            -';
end
if isempty(Pmedication)
    Pmedication = '            -';
end
if isempty(Pobservations)
    Pobservations = '            -';
end

Foldername = 'BP Data';
if (exist(Foldername) ~= 7)
    mkdir(pwd,Foldername);
end
oldFolder = cd(sprintf('%s%s%s%s',pwd,filesep,'BP Data',filesep));

M = [time_array pressure_array];
FileName = [num2str(study_number) '_' Pname '_' num2str(now) '.csv'];       %With datestr(now), : cannot be saved in the file name.
csvwrite(FileName,M);

if isunix
    FileName = ['Record_Session' num2str(now) '.csv'];
    %if (exist(FileName) == 0)
    %d = {'Study Number','Observer Name','Name/ID','Measure Number (same patient)','Age','Sex','Cuff Size','Used arm','Arm Circumference (cm)','Smoker?','Time of measurement (TD)','Systolic BP (TD)','Diastolic BP (TD)','Heart Rate (TD)','Time of measurement','SBP Ratio','DBP Ratio','Systolic BP','Diastolic BP','Heart Rate','Patient status (medical condition)','Anti-hypertensive medication','Observations/Comments'; ...
    d = [study_number,Pobservername,Pname,Pmeasure_num,Page,Psex,PcuffSize,Parm,Parmcircunf,Psmokes,time_manual_device,Ptraditional_SBP,Ptraditional_DBP,Ptraditional_HR,datestr(clock),SBP_RATIO,DBP_RATIO,SBP,DBP,HR,Pstatus,Pmedication,Pobservations];
    %any2csv(d,'|',1);
    csvwrite(FileName, d);
    %xlswrite(FileName, d, 1);
    % else
    %     d = {study_number,Pobservername,Pname,Pmeasure_num,Page,Psex,PcuffSize,Parm,Parmcircunf,Psmokes,time_manual_device,Ptraditional_SBP,Ptraditional_DBP,Ptraditional_HR,datestr(clock),SBP_RATIO,DBP_RATIO,SBP,DBP,HR,Pstatus,Pmedication,Pobservations};
    %     A = xlsread(FileName);
    %     range = ['A' num2str(size(A,1)+2)];
    %     xlswrite(FileName, d, 1, range);
    % end
elseif ispc
    FileName = ['Record_Session ' num2str(now) '.xls'];       %With datestr(now), : cannot be saved in the file name.
    if (exist(FileName) == 0)
        d = {'Study Number','Observer Name','Name/ID','Measure Number (same patient)','Age','Sex','Cuff Size','Used arm','Arm Circumference (cm)','Smoker?','Time of measurement (TD)','Systolic BP (TD)','Diastolic BP (TD)','Heart Rate (TD)','Time of measurement','SBP Ratio','DBP Ratio','Systolic BP','Diastolic BP','Heart Rate','Patient status (medical condition)','Anti-hypertensive medication','Observations/Comments'; ...
            study_number,Pobservername,Pname,Pmeasure_num,Page,Psex,PcuffSize,Parm,Parmcircunf,Psmokes,time_manual_device,Ptraditional_SBP,Ptraditional_DBP,Ptraditional_HR,datestr(clock),SBP_RATIO,DBP_RATIO,SBP,DBP,HR,Pstatus,Pmedication,Pobservations};
        xlswrite(FileName, d, 1);
    else
        d = {study_number,Pobservername,Pname,Pmeasure_num,Page,Psex,PcuffSize,Parm,Parmcircunf,Psmokes,time_manual_device,Ptraditional_SBP,Ptraditional_DBP,Ptraditional_HR,datestr(clock),SBP_RATIO,DBP_RATIO,SBP,DBP,HR,Pstatus,Pmedication,Pobservations};
        A = xlsread(FileName);
        range = ['A' num2str(size(A,1)+2)];
        xlswrite(FileName, d, 1, range);
    end
end

cd(oldFolder)
clear oldFolder;

function pushbutton3_CreateFcn(~, ~, ~)
% hObject    handle to pushbutton3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


function edit1_Callback(~, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit1 as text
%        str2double(get(hObject,'String')) returns contents of edit1 as a double

% --- Executes during object creation, after setting all properties.
function edit1_CreateFcn(hObject, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit2_Callback(~, ~, ~)
% hObject    handle to edit2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit2 as text
%        str2double(get(hObject,'String')) returns contents of edit2 as a double

% --- Executes during object creation, after setting all properties.
function edit2_CreateFcn(hObject, ~, ~)
% hObject    handle to edit2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit3_Callback(~, ~, ~)
% hObject    handle to edit3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit3 as text
%        str2double(get(hObject,'String')) returns contents of edit3 as a double

% --- Executes during object creation, after setting all properties.
function edit3_CreateFcn(hObject, ~, ~)
% hObject    handle to edit3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit4_Callback(hObject, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit1 as text
%        str2double(get(hObject,'String')) returns contents of edit1 as a double
global Pname

Pname = get(hObject,'String');

% --- Executes during object creation, after setting all properties.
function edit4_CreateFcn(hObject, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit5_Callback(hObject, ~, ~)
% hObject    handle to edit2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit2 as text
%        str2double(get(hObject,'String')) returns contents of edit2 as a double
global Page

Page = str2double(get(hObject,'String'));

% --- Executes during object creation, after setting all properties.
function edit5_CreateFcn(hObject, ~, ~)
% hObject    handle to edit2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit6_Callback(hObject, ~, ~)
% hObject    handle to edit3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit3 as text
%        str2double(get(hObject,'String')) returns contents of edit3 as a double
global Ptraditional_DBP

Ptraditional_DBP = str2double(get(hObject,'String'));

% --- Executes during object creation, after setting all properties.
function edit6_CreateFcn(hObject, ~, ~)
% hObject    handle to edit3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit7_Callback(hObject, ~, ~)
% hObject    handle to edit4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit4 as text
%        str2double(get(hObject,'String')) returns contents of edit4 as a double
global Ptraditional_HR

Ptraditional_HR = str2double(get(hObject,'String'));

% --- Executes during object creation, after setting all properties.
function edit7_CreateFcn(hObject, ~, ~)
% hObject    handle to edit4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit8_Callback(hObject, ~, ~)
% hObject    handle to edit5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit5 as text
%        str2double(get(hObject,'String')) returns contents of edit5 as a double
global Ptraditional_SBP

Ptraditional_SBP = str2double(get(hObject,'String'));

% --- Executes during object creation, after setting all properties.
function edit8_CreateFcn(hObject, ~, ~)
% hObject    handle to edit5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit9_Callback(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit7 as text
%        str2double(get(hObject,'String')) returns contents of edit7 as a double
global Pstatus

Pstatus = get(hObject,'String');

% --- Executes during object creation, after setting all properties.
function edit9_CreateFcn(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit10_Callback(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit7 as text
%        str2double(get(hObject,'String')) returns contents of edit7 as a double
global Pmedication

Pmedication = get(hObject,'String');

% --- Executes during object creation, after setting all properties.
function edit10_CreateFcn(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit11_Callback(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit7 as text
%        str2double(get(hObject,'String')) returns contents of edit7 as a double
global Parmcircunf

Parmcircunf = str2double(get(hObject,'String'));

% --- Executes during object creation, after setting all properties.
function edit11_CreateFcn(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit12_Callback(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit7 as text
%        str2double(get(hObject,'String')) returns contents of edit7 as a double
global Pobservations

Pobservations = get(hObject,'String');

% --- Executes during object creation, after setting all properties.
function edit12_CreateFcn(hObject, ~, ~)
% hObject    handle to edit7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit13_Callback(hObject, ~, ~)
% hObject    handle to edit6 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit6 as text
%        str2double(get(hObject,'String')) returns contents of edit6 as a double
global Pobservername

Pobservername = get(hObject,'String');

% --- Executes during object creation, after setting all properties.
function edit13_CreateFcn(hObject, ~, ~)
% hObject    handle to edit6 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes during object creation, after setting all properties.
function text1_CreateFcn(~, ~, ~)
% hObject    handle to text1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% --- Executes during object creation, after setting all properties.
function text3_CreateFcn(~, ~, ~)
% hObject    handle to text1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% --- Executes during object creation, after setting all properties.
function text13_CreateFcn(~, ~, ~)
% hObject    handle to text1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% --- Executes during object creation, after setting all properties.
function text15_CreateFcn(~, ~, ~)
% hObject    handle to text1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called


% --- Executes on selection change in popupmenu1.
function popupmenu1_Callback(hObject, ~, ~)
% hObject    handle to popupmenu1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu1 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu1

global language
global lang

popupcontents = cellstr(get(hObject,'String'));
lang = popupcontents{get(hObject,'Value')};

if strcmp(lang,getLabel(language,'english'))
    language = 'en';
elseif strcmp(lang,getLabel(language,'portuguese'))
    language = 'pt';
elseif strcmp(lang,getLabel(language,'nepalese'))
    language = 'ne';
% elseif strcmp(lang,getLabel(language,'hindi'))
%     language = 'hi';
% elseif strcmp(lang,getLabel(language,'french'))
%     language = 'fr';
% elseif strcmp(lang,getLabel(language,'spanish'))
%     language = 'es';
% elseif strcmp(lang,getLabel(language,'chinese'))
%     language = 'zh';
% elseif strcmp(lang,getLabel(language,'ethiopian'))
%     language = 'am';
% elseif strcmp(lang,getLabel(language,'arabic_egypt'))
%     language = 'ar';
end
setLanguage(language);

function popupmenu1_CreateFcn(~, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
% if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
%     set(hObject,'BackgroundColor','white');
% end

% % --- Executes on selection change in popupmenu1.
% function popupmenu1_Callback(hObject, ~, handles)
% % hObject    handle to popupmenu1 (see GCBO)
% % eventdata  reserved - to be defined in a future version of MATLAB
% % handles    structure with handles and user data (see GUIDATA)
% 
% % Hints: contents = cellstr(get(hObject,'String')) returns popupmenu1 contents as cell array
% %        contents{get(hObject,'Value')} returns selected item from popupmenu1
% 
% global SBP_RATIO
% % global DBP_RATIO
% % global pressure_array
% % global time
% % global SBP
% % global DBP
% % global HR
% 
% popupcontents = cellstr(get(hObject,'String'));
% SBP_RATIO = popupcontents{get(hObject,'Value')};
% 
% % [SBP,DBP,HR] = SignalProcessing(pressure_array,time,SBP_RATIO,DBP_RATIO);
% 
% % set(handles.edit1,'String',SBP);
% % set(handles.edit2,'String',DBP);
% % set(handles.edit3,'String',HR);
% 
% % --- Executes on selection change in popupmenu2.
% function popupmenu2_Callback(hObject, ~, handles)
% % hObject    handle to popupmenu2 (see GCBO)
% % eventdata  reserved - to be defined in a future version of MATLAB
% % handles    structure with handles and user data (see GUIDATA)
% 
% % Hints: contents = cellstr(get(hObject,'String')) returns popupmenu2 contents as cell array
% %        contents{get(hObject,'Value')} returns selected item from popupmenu2
% 
% % global SBP_RATIO
% global DBP_RATIO
% % global pressure_array
% % global time
% % global SBP
% % global DBP
% % global HR
% 
% popupcontents = cellstr(get(hObject,'String'));
% DBP_RATIO = popupcontents{get(hObject,'Value')};
% 
% % [SBP,DBP,HR] = SignalProcessing(pressure_array,time,SBP_RATIO,DBP_RATIO);
% 
% % set(handles.edit1,'String',SBP);
% % set(handles.edit2,'String',DBP);
% % set(handles.edit3,'String',HR);

% --- Executes on selection change in popupmenu3.
function popupmenu2_Callback(hObject, ~, ~)
% hObject    handle to popupmenu3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu3 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu3
global PcuffSize

popupcontents = cellstr(get(hObject,'String'));
PcuffSize = popupcontents{get(hObject,'Value')};

% --- Executes on selection change in popupmenu5.
function popupmenu3_Callback(hObject, ~, ~)
% hObject    handle to popupmenu5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu5 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu5

global Psex

popupcontents = cellstr(get(hObject,'String'));
Psex = popupcontents{get(hObject,'Value')};

% --- Executes on selection change in popupmenu4.
function popupmenu4_Callback(hObject, ~, ~)
% hObject    handle to popupmenu4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu4 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu4
global Parm

popupcontents = cellstr(get(hObject,'String'));
Parm = popupcontents{get(hObject,'Value')};

function popupmenu4_CreateFcn(hObject, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

% --- Executes on selection change in popupmenu4.
function popupmenu5_Callback(hObject, ~, ~)
% hObject    handle to popupmenu4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu4 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu4
global Pmeasure_num

popupcontents = cellstr(get(hObject,'String'));
Pmeasure_num = popupcontents{get(hObject,'Value')};

function popupmenu5_CreateFcn(hObject, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
 if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
     set(hObject,'BackgroundColor','white');
 end

% --- Executes on selection change in popupmenu4.
function popupmenu6_Callback(hObject, ~, ~)
% hObject    handle to popupmenu4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu4 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu4
global Psmokes

popupcontents = cellstr(get(hObject,'String'));
Psmokes = popupcontents{get(hObject,'Value')};


    function setLanguage(language)
        
        handles = guidata(gcf);
        
        set(handles.text1,'String',getLabel(language,'text1txt'));
        set(handles.text2,'String',getLabel(language,'text2txt'));
        set(handles.text3,'String',getLabel(language,'text3txt'));
        set(handles.text4,'String',getLabel(language,'text4txt'));
        set(handles.text5,'String',getLabel(language,'text5txt'));
        set(handles.text6,'String',getLabel(language,'text5txt'));
        set(handles.text7,'String',getLabel(language,'text7txt'));
        set(handles.text8,'String',getLabel(language,'text5txt'));
        set(handles.text9,'String',getLabel(language,'text5txt'));
        set(handles.text10,'String',getLabel(language,'text7txt'));
        set(handles.text11,'String',getLabel(language,'text2txt'));
        set(handles.text12,'String',getLabel(language,'text3txt'));
        set(handles.text13,'String',getLabel(language,'text4txt'));
        set(handles.text14,'String',getLabel(language,'text14txt'));
        set(handles.text15,'String',getLabel(language,'text15txt'));
        set(handles.text16,'String',getLabel(language,'text16txt'));
        set(handles.text17,'String',getLabel(language,'text17txt'));
        set(handles.text18,'String',getLabel(language,'text18txt'));
        set(handles.text19,'String',getLabel(language,'text19txt'));
        set(handles.text20,'String',getLabel(language,'text20txt'));
        set(handles.text211,'String',getLabel(language,'text20txt'));
        set(handles.text21,'String',getLabel(language,'text21txt'));
        set(handles.text22,'String',getLabel(language,'text22txt'));
        set(handles.text23,'String',getLabel(language,'text23txt'));
        set(handles.text24,'String',getLabel(language,'text24txt'));
        set(handles.text25,'String',getLabel(language,'text25txt'));
        set(handles.text26,'String',getLabel(language,'text26txt'));
        set(handles.text27,'String',getLabel(language,'text27txt'));
        set(handles.text29,'String',getLabel(language,'text29txt'));
        set(handles.text30,'String',getLabel(language,'text30txt'));
        set(handles.text31,'String',getLabel(language,'text31txt'));
        
        set(handles.uipanel1,'Title',getLabel(language,'uipanel1txt'));
        set(handles.uipanel2,'Title',getLabel(language,'uipanel2txt'));
        set(handles.uipanel3,'Title',getLabel(language,'uipanel3txt'));
        set(handles.uipanel4,'Title',getLabel(language,'uipanel4txt'));
        set(handles.uipanel5,'Title',getLabel(language,'uipanel5txt'));
        set(handles.uipanel6,'Title',getLabel(language,'uipanel6txt'));
        set(handles.uipanel7,'Title',getLabel(language,'uipanel7txt'));
        set(handles.uipanel8,'Title',getLabel(language,'uipanel8txt'));
        set(handles.uipanel9,'Title',getLabel(language,'uipanel9txt'));
        
%         %set(handles.checkbox1,'String',getLabel(language,'checkbox1txt'));
        
        set(handles.uitable1,'ColumnName',getLabel(language,'cnames'));
        
        set(handles.pushbutton1,'String',getLabel(language,'pushbutton1txt'));
        set(handles.pushbutton2,'String',getLabel(language,'pushbutton2txt'));
        set(handles.pushbutton3,'String',getLabel(language,'pushbutton3txt'));
        
        set(handles.popupmenu1,'String',getLabel(language,'popmenu1txt'));
        set(handles.popupmenu2,'String',getLabel(language,'popmenu2txt'));
        set(handles.popupmenu3,'String',getLabel(language,'popmenu3txt'));
        set(handles.popupmenu4,'String',getLabel(language,'popmenu4txt'));
        set(handles.popupmenu5,'String',getLabel(language,'popmenu5txt'));
        
        % set(handles.tp291b36a5_1f32_483a_9eba_dffa9082686d,'String',getLabel(language,'tab1'));
        % set(handles.tp4d0a0d52_59e1_4953_8dc7_e59f4a18bc72,'String',getLabel(language,'tab2'));
        % set(handles.tp314274bc_f25d_46bc_be76_2c3e86bb68a6,'String',getLabel(language,'tab3'));
        
        drawnow;
        


%%%For robustness:
% global t_aquisicao
% global flag
% global t_aquisi_aux
%     
% t_aquisi_aux = t_aquisicao;
% t_aquisicao = get(hObject,'String');
% t_aquisicao(t_aquisicao == ',') = '.';                                      %change commas to dots
% t_aquisicao = str2double(t_aquisicao);
% if (t_aquisicao <= 0 || isnan(t_aquisicao) == 1)
%     h1 = msgbox('Aquisition time (s or ms) must be an integer or decimal value superior to 0!','Help Box:','error');
%     t_aquisicao = t_aquisi_aux;                             
%     set(handles.t_aquisicao,'String',t_aquisicao);
% else
%     flag = 1;
% end

% if (strcmp(escala_temporal,'ms') == 1 && t_aquisicao1 < 0.5)
%     h1 = msgbox('Aquisition time value must be higher than 100ms (lower possible value)!','Help Box:','error');
%     t_aquisicao = 100;
%     set(handles.t_aquisicao,'String',t_aquisicao);
% elseif (t_aquisicao1 > 65535)
%     h1 = msgbox('The maximum aquisition time value allowed by the program is 6553 seconds ~ 109 minutes!','Help Box:','error');
%     t_aquisicao = out/10;                                                   %� o �ltmo out recebido! neste caso ele volta a meter o valor do �ltimo out!
%     set(handles.t_aquisicao,'String',t_aquisicao)
% end
