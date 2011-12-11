function [SBP,DBP,HR] = SignalProcessing(bp_wave_f,time,SBP_RATIO,DBP_RATIO)

%% Get decreasing part of the curve
bp = bp_wave_f;
slimit = [50 200];
inflim = slimit(1);         % inferior limit (values lower than inflim are discarded
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
    if bp_wave_1(gg) > 125
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

SBP = round(bp_wave_1(idxsys(2:length(idxsys))));
DBP = round(bp_wave_1(idxdia(2:length(idxdia))));
HR = round(xdata(b)*60);

end

