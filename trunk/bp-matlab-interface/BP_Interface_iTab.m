function varargout = BP_Interface_iTab(varargin)
% BP_INTERFACE_ITAB MATLAB code for BP_Interface_iTab.fig
%      BP_INTERFACE_ITAB, by itself, creates a new BP_INTERFACE_ITAB or raises the existing
%      singleton*.
%
%      H = BP_INTERFACE_ITAB returns the handle to a new BP_INTERFACE_ITAB or the handle to
%      the existing singleton*.
%
%      BP_INTERFACE_ITAB('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in BP_INTERFACE_ITAB.M with the given input arguments.
%
%      BP_INTERFACE_ITAB('Property','Value',...) creates a new BP_INTERFACE_ITAB or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before BP_Interface_iTab_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to BP_Interface_iTab_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help BP_Interface_iTab

% Last Modified by GUIDE v2.5 26-Oct-2011 15:36:24

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
    'gui_Singleton',  gui_Singleton, ...
    'gui_OpeningFcn', @BP_Interface_iTab_OpeningFcn, ...
    'gui_OutputFcn',  @BP_Interface_iTab_OutputFcn, ...
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


% --- Executes just before BP_Interface_iTab is made visible.
function BP_Interface_iTab_OpeningFcn(hObject, ~, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to BP_Interface_iTab (see VARARGIN)

% Choose default command line output for BP_Interface_iTab
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes BP_Interface_iTab wait for user response (see UIRESUME)
% uiwait(handles.figure1);

global SBP_RATIO
global DBP_RATIO
global time_array
global pressure_array
global Fs
global checkbox2
global FLAG
global Pname
global Page
global Psex
global numMeasures
global mySessionData
global numSessionRecords

checkbox2 = 0;
SBP_RATIO = 0.4;
DBP_RATIO = 0.7;
time_array = 0;
Fs = 250; % Hz
pressure_array = 0;
FLAG = 0;
Pname = '';
Page = '';
Psex = '';

numMeasures = 0;
numSessionRecords = 100;

mySessionData = cell(numSessionRecords,9);
for x = 1:numSessionRecords
    for z = 1:9
    mySessionData{x,z} = '           -';
    end
end

set(handles.uitable1,'data',mySessionData);

infos = instrhwinfo('serial');                                              %instrument control toolbox! instrhwinfo: information about available hardware
coms = size(infos.SerialPorts);
if (coms(1,1) ~= 1)
    msgbox('No COM port or more than one COM port was found in the system. In case there is more than one, choose the COM port to be used!','Help Box:','warn');
    return
end

% --- Outputs from this function are returned to the command line.
function varargout = BP_Interface_iTab_OutputFcn(~, ~, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in checkbox2.
function checkbox2_Callback(hObject, ~, ~)
% hObject    handle to checkbox2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox2

global checkbox2

checkbox2 = get(hObject,'Value');

% --- Executes on button press in pushbutton1.
function pushbutton1_Callback(hObject, ~, handles)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Choose default command line output for BP_Interface_iTab
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

global checkbox2
global Pname
global Page
global Psex
global SBP_RATIO
global DBP_RATIO
global FLAG
global time
global pressure_array
global SBP
global DBP
global HR
global numMeasures
global mySessionData
global numSessionRecords

if FLAG == 0
    msgbox('A new acquisition needs to be performed before saving the values!','Help Box:','warn');
    return
else
    if size(Pname,1) == 0
        msgbox('A name must be given to the patient in order to save the acquired BP measurements!','Help Box:','error');
        return
    end
    FLAG = 0;
end

numMeasures = numMeasures+1;
if numMeasures > numSessionRecords
    numMeasures = 1;
    for x = 2:numSessionRecords
        for z = 2:9
            mySessionData{x,z} = '           -';
        end
    end
end

mySessionData{numMeasures,1} = Pname;
if isnan(str2double(Page))
    Page = '           -';
end
mySessionData{numMeasures,2} = Page;
if isempty(Psex)
    Psex = '           -';
end
mySessionData{numMeasures,3} = Psex;
mySessionData{numMeasures,4} = datestr(clock);
mySessionData{numMeasures,5} = SBP_RATIO;
mySessionData{numMeasures,6} = DBP_RATIO;
mySessionData{numMeasures,7} = SBP;
mySessionData{numMeasures,8} = DBP;
mySessionData{numMeasures,9} = HR;

set(handles.uitable1,'data',mySessionData);

Foldername = 'BP Data';
if (exist(Foldername) ~= 7)
    mkdir(pwd,Foldername);
end
oldFolder = cd(sprintf('%s%s%s%s',pwd,filesep,'BP Data',filesep));

if checkbox2 == 1
    M = [time' pressure_array];
    FileName = [Pname 'Data.csv'];
    csvwrite(FileName,M);
end

Filename = 'Record.xls';

if (exist(Filename) == 0)
    d = {'Name','Age','Sex','Time','SBP Ratio','DBP Ratio','Systolic BP','Diastolic BP','Heart Rate';Pname,Page,Psex,datestr(clock),SBP_RATIO,DBP_RATIO,SBP,DBP,HR};
    xlswrite(Filename, d, 1);
else
    d = {Pname,Page,Psex,datestr(clock),SBP_RATIO,DBP_RATIO,SBP,DBP,HR};
    A = xlsread(Filename);
    range = ['A' num2str(size(A,1)+2)];
    xlswrite(Filename, d, 1, range);
end

cd(oldFolder)
clear oldFolder;

% --- Executes on button press in pushbutton2.
function pushbutton2_Callback(~, ~, handles)
% hObject    handle to pushbutton2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

%global Cont_or_Quit
global SBP_RATIO
global DBP_RATIO
global FLAG
global time
global pressure_array
global Fs
global SBP
global DBP
global HR

infos = instrhwinfo('serial');                                              %instrument control toolbox! instrhwinfo: information about available hardware
coms = size(infos.SerialPorts);
if (coms(1,1) ~= 1)
    msgbox('No COM port or more than one COM port was found in the system. In case there is more than one, choose the COM port to be used!','Help Box:','error');
    return
end

cla(handles.axes1,'reset')

infos = instrhwinfo('serial');
port = char(infos.SerialPorts); %assigns the object s to serial port COM3
s = serial(port);
set(s, 'InputBufferSize', 1024); %number of bytes in inout buffer
set(s, 'FlowControl', 'hardware');
set(s, 'BaudRate', 9600);
set(s, 'Parity', 'none');
set(s, 'DataBits', 8);
set(s, 'StopBit', 1);
set(s, 'Timeout',10);

fopen(s);           %opens the serial port
%%-------------------------------------------------------------------------
% %     fprintf(s,'%s','w');
% %     fprintf(s,'%s','h');
% %     fprintf(s,'%s','o');
%     fwrite(s,119);
%     fwrite(s,104);
%     fwrite(s,111);
% 
%     a = fread(s); %reads the data from the serial port and stores it to the matrix a
%     if strcmp(a,'sana') == 0
%         msgbox('The connected device is not the BP measure device! Please connect the correct device.','Help Box:','error');
%         fclose(s);
%         delete(s);
%         return
%     end
%%-------------------------------------------------------------------------

%x = 0;
initial_time = cputime;
b = [];
k = 1;
c = 0;
t = 1;
set(gcf,'currentcharacter','C')
%Cont_or_Quit = get(gcf,'currentcharacter');

% state = 1;
% lb = 0;
% i = 1;
% %FLAG = 0;
% aux = 999; x = 999;
% aux1 = 999; y = 999;
% 
% aux2 = 0;
% 
% l = 0;
% y_down = [];
% t_down = [];
j = 0;
myFLAG = 1;

try 
time_array = [];
pressure_array = [];
    
while t
    
    Cont_or_Quit = get(gcf,'currentcharacter');
    
    if (Cont_or_Quit~='C')
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
%     plot(handles.axes1,k*j/2,pressure_array(j*k/2),'*')
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

[SBP,DBP,HR] = SignalProcessing(pressure_array,Fs,SBP_RATIO,DBP_RATIO,handles);

set(handles.edit3,'String',SBP);
set(handles.edit4,'String',DBP);
set(handles.edit5,'String',HR);

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

function edit1_Callback(hObject, ~, ~)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit1 as text
%        str2double(get(hObject,'String')) returns contents of edit1 as a double

global Pname

Pname = get(hObject,'String');

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

function edit4_Callback(~, ~, ~)
% hObject    handle to edit4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit4 as text
%        str2double(get(hObject,'String')) returns contents of edit4 as a double

% --- Executes during object creation, after setting all properties.
function edit4_CreateFcn(hObject, ~, ~)
% hObject    handle to edit4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function edit6_Callback(hObject, ~, ~)
% hObject    handle to edit6 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit6 as text
%        str2double(get(hObject,'String')) returns contents of edit6 as a double

global Psex

Psex = get(hObject,'String');

% --- Executes during object creation, after setting all properties.
function edit6_CreateFcn(hObject, ~, ~)
% hObject    handle to edit6 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


function edit2_Callback(hObject, ~, ~)
% hObject    handle to edit2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit2 as text
%        str2double(get(hObject,'String')) returns contents of edit2 as a double

global Page

Page = str2double(get(hObject,'String'));


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

function edit5_Callback(~, ~, ~)
% hObject    handle to edit5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit5 as text
%        str2double(get(hObject,'String')) returns contents of edit5 as a double



% --- Executes during object creation, after setting all properties.
function edit5_CreateFcn(hObject, ~, ~)
% hObject    handle to edit5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes during object creation, after setting all properties.
function text13_CreateFcn(~, ~, ~)
% hObject    handle to text13 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% --- Executes during object creation, after setting all properties.
function text1_CreateFcn(~, ~, ~)
% hObject    handle to text1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% --- Executes during object creation, after setting all properties.
function text3_CreateFcn(~, ~, ~)
% hObject    handle to text3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% --- Executes on selection change in popupmenu2.
function popupmenu2_Callback(hObject, ~, handles)
% hObject    handle to popupmenu2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu2 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu2

% Choose default command line output for BP_Interface_iTab
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

global SBP_RATIO
global DBP_RATIO
global pressure_array
global Fs
global SBP
global DBP
global HR

popupcontents = get(hObject,'String');
SBP_RATIO = popupcontents{get(hObject,'Value')};

[SBP,DBP,HR] = SignalProcessing(pressure_array,Fs,SBP_RATIO,DBP_RATIO,handles);

set(handles.edit3,'String',SBP);
set(handles.edit4,'String',DBP);
set(handles.edit5,'String',HR);


% --- Executes on selection change in popupmenu3.
function popupmenu3_Callback(hObject, ~, handles)
% hObject    handle to popupmenu3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu3 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu3

% Choose default command line output for BP_Interface_iTab
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

global SBP_RATIO
global DBP_RATIO
global pressure_array
global Fs
global SBP
global DBP
global HR

popupcontents = get(hObject,'String');
DBP_RATIO = popupcontents{get(hObject,'Value')};

[SBP,DBP,HR] = SignalProcessing(pressure_array,Fs,SBP_RATIO,DBP_RATIO,handles);

set(handles.edit3,'String',SBP);
set(handles.edit4,'String',DBP);
set(handles.edit5,'String',HR);

