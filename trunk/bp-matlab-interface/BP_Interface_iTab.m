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

% Last Modified by GUIDE v2.5 10-Oct-2011 17:49:58

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
global checkbox2
global FLAG
global Pname
global Page
global Psex

checkbox2 = 0;
SBP_RATIO = 0.4;
DBP_RATIO = 0.7;
time_array = 0;
pressure_array = 0;
FLAG = 0;
Pname = '';
Page = '';
Psex = '';

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
function pushbutton1_Callback(~, ~, ~)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

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

Fs = 250; % Hz

fopen(s);           %opens the serial port

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

bp_wave_f = medfilt1(pressure_array,5);
% figure,
% plot(time,bp_wave_f), axis tight
% title('Cuff Pressure Wave after median filter'), xlabel('Time (secs)'), ylabel('Pressure (mmHg)')

time = (1:length(pressure_array))/Fs;
plot(handles.axes1,time,pressure_array);
xlabel('Time (s)');
ylabel('Pressure (mmHg)');

%% Get decreasing part of the curve


bp = bp_wave_f;
slimit = [50 2400];
inflim = slimit(1);             % inferior limit (values lower than inflim are discarded
suplim = slimit(2);
[~, indmax]=max(bp);        % calculate max value
L=length(bp);
indx1=find(bp(indmax:L)<suplim);
indx2=find(bp(indmax:L)<inflim);
bp_wave_1 = bp(indmax+indx1(1):indmax+indx2(1));        % select part of interest of bp vector
time_1 = time(indmax+indx1(1):indmax+indx2(1));    % select part of interest of time vector

%% Different approach 1
data = bp_wave_f;
fs = 256;
fc1 = 0.5;
fc2 = 3;
norder = 6;
np=length(data);
[b5 a5]=filtcalc_pp(fs,norder,'high',fc2);
f2=filter(b5,a5,data);
[d c]=filtcalc_pp(fs,norder,'low',fc1);
oscil=filter(d,c,f2);

% figure,
% hl1 = plot(time(500:end),bp_wave_f(500:end),'b');
% ax1 = gca;
% set(ax1,'XColor','b','YColor','b')
% ax2 = axes('Position',get(ax1,'Position'),'XAxisLocation','top','YAxisLocation','right','Color','none','XColor','r','YColor','r');
% hl2 = line(time(500:end),oscil(500:end),'Color','r','Parent',ax2,'LineWidth',1.5);

oscil_1 = oscil(indmax+indx1(1):indmax+indx2(1));        % select part of interest of bp vector
%t_1 = time(indmax+indx1(1):indmax+indx2(1));    % select part of interest of time vector

% figure,
% hl1 = plot(time_1(500:end),bp_wave_1(500:end),'b');
% ax1 = gca;
% set(ax1,'XColor','k','YColor','b','FontSize',20)
% xlim([time_1(500) time_1(end)])
% ylabel('Cuff Pressure (mmHg)')
% ax2 = axes('Position',get(ax1,'Position'),'YAxisLocation','right','Color','none','XColor','k','YColor','r','FontSize',20);
% xlabel('Time (secs)'); ylabel('Oscillations Amplitude (arbitrary units)')
% hl2 = line(time_1(500:end),oscil_1(500:end),'Color','r','Parent',ax2,'LineWidth',1.5);
% xlim([time_1(500) time_1(end)])

%% Get Systolic and Diastolic Blood Pressures

dt=5e-6;
%oscil_corr=oscil_bandpass;
[MAXTAB, MINTAB] = peakdet(oscil_1,dt,time_1);
FLAG11=0;
while FLAG11==0
    [~, indx] = max(MAXTAB(:,2));
    gg = time_1 == MAXTAB(indx,1); 
    if bp_wave_1(gg) > 150
        MAXTAB(indx,2)=0;
    else
        FLAG11 = 1;
    end
end

Y=interp1(MAXTAB(:,1),MAXTAB(:,2),time_1);
% figure,
% plot(time_1,oscil_1)
% hold on
% plot(MAXTAB(:,1),MAXTAB(:,2),'*r') 
% plot(time_1,Y,'g')
% axis tight
% title('Pressure Oscillations baseline-corrected'), xlabel('Time (secs)'), ylabel('Pressure (mmHg)')
% hold off

[MAP idxMAP]=max(Y(:));
ratio = 0.35:0.05:0.95;
idxsys = 0;

%for j = 1:length(ratio)
    SYS_FOUND = 0;
    i = idxMAP;
    while i~=1 && SYS_FOUND == 0
        if (Y(i)<SBP_RATIO*MAP)
            idxsys = [idxsys i];
            SYS_FOUND = 1;
        end
        i = i - 1;
    end
%end

idxdia = 0;
%for j = 1:length(ratio)
    DIA_FOUND = 0;
    i = idxMAP;
    while DIA_FOUND == 0 && i~=length(Y)
        if (Y(i)<DBP_RATIO*MAP)
            idxdia = [idxdia i];
            DIA_FOUND = 1;
        end
        i = i + 1;
    end
%end

    sys=idxsys(2);
    dias=idxdia(2);

SYSTOLIC = bp_wave_1(idxsys(2:length(idxsys)))';
DIASTOLIC = bp_wave_1(idxdia(2:length(idxdia)))';
% 
% figure,
% plot(time_1,oscil_1)
% hold on
% plot(time_1,Y)
% plot(time_1(sys(1)),Y(sys(1)),'*r');
% plot(time_1(dias(1)),Y(dias(1)),'*m');
% plot(time_1(idxMAP),Y(idxMAP),'*g');
% axis tight
% title('Pressure Oscillations baseline-corrected'), xlabel('Time (secs)'), ylabel('Pressure (mmHg)')
% legend('Pressure Oscillations','Pressure Oscillations mod','Systolic BP','Diastolic BP','MAP')

%%

% figure,
% plot(bp_wave_1,oscil_1)
% hold on
% plot(bp_wave_1,Y)
% plot(bp_wave_1(sys(1)),Y(sys(1)),'*r');
% output_txt = {[' SBP: ',num2str(bp_wSave_1(sys(1)),4)]};
% text(bp_wave_1(sys(1)),Y(sys(1)),strcat(output_txt,' \rightarrow'),'HorizontalAlignment','right')
% 
% plot(bp_wave_1(dias(1)),Y(dias(1)),'*m');
% output_txt = {[' DBP: ',num2str(bp_wave_1(dias(1)),4)]};
% text(bp_wave_1(dias(1)),Y(dias(1)),strcat(' \leftarrow',output_txt),'HorizontalAlignment','left')
% 
% plot(bp_wave_1(idxMAP),Y(idxMAP),'*g');
% output_txt = {[' MAP: ',num2str(bp_wave_1(idxMAP),4)]};
% text(bp_wave_1(idxMAP),Y(idxMAP),strcat(' \leftarrow',output_txt),'HorizontalAlignment','left')
% set(gca,'XDir','reverse')
% axis tight
% title('Pressure Oscillations baseline-corrected'), xlabel('Pressure (mmHg)'), ylabel('Pressure Oscillations(mmHg)')
% legend('Pressure Oscillations','Pressure Oscillations mod','Systolic BP','Diastolic BP','MAP')

Y = fft(oscil_1);
figure1 = figure;
[~, h] = plotf(Y,256,60);
xdata = get(h,'XData');
ydata = get(h,'YData');
[~, b] = max(ydata);
while(xdata(b)*60 < 50)
    ydata(b) = 0;
    [~, b] = max(ydata);
end

close(figure1)

SBP = round(bp_wave_1(sys(1)));
DBP = round(bp_wave_1(dias(1)));
HR = round(xdata(b)*60);
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
function popupmenu2_Callback(hObject, ~, ~)
% hObject    handle to popupmenu2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu2 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu2

global SBP_RATIO

popupcontents = get(hObject,'String');
SBP_RATIO = popupcontents{get(hObject,'Value')};


% --- Executes on selection change in popupmenu3.
function popupmenu3_Callback(hObject, eventdata, handles)
% hObject    handle to popupmenu3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu3 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu3

global DBP_RATIO

popupcontents = get(hObject,'String');
DBP_RATIO = popupcontents{get(hObject,'Value')};
