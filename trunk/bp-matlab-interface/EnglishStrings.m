classdef EnglishStrings
    properties (Constant)
        language = 'Language';
        english = 'English';
        french = 'French';
        portuguese = 'Portuguese';
        spanish = 'Spanish';
        nepalese = 'Nepali';
        hindi = 'Standard Hindi';
        chinese = 'Chinese';
        ethiopian = 'Amharic';
        arabic_egypt = 'Arabic Egypt';
        
        uipanel2txt = 'Current BP measure:  PUMP OVER THE RED LINE!';
        %uipanel3txt = 'Define the SBP and DBP ratios (oscillometric method)';
        uipanel3txt = language;
        uipanel4txt = 'Last BP measurement';
        uipanel5txt = 'Save BP measure';
        uipanel6txt = 'Patient credentials';
        uipanel7txt = 'Session records';
        uipanel8txt = 'About the Low-cost BP monitor device';
        
        text1txt = 'Used Arm';
        text22txt = 'SBP RATIO';
        text33txt = 'DBP RATIO';
        text2txt = 'Observations'
        text3txt = 'Observer name';
        text4txt = 'SBP';       %Systolic Blood Pressure 
        text5txt = 'DBP';       %Diastolic Blood Pressure 
        text6txt = 'HR';        %Heart Rate 
        text7txt = 'mmHg';      %Millimeters (mm) of mercury (Hg)
        text9txt = 'bpm';
        text10txt = 'DO NOT press a keyboard key unless you want to finish the acquisition';
        text11txt = 'Compulsory';       %* Compulsory
        text12txt = 'Name';
        text13txt = 'Age';
        text14txt = 'Sex';
        text15txt = 'Cuff Size';
        text16txt = 'University of Oxford: Engineering World Health';
        text17txt = 'This programme is intended to be used only with the Low-cost Blood Pressure Monitor device developed by the Oxford BP team. The device is not intended for medical use, pending validation and simultaneous optimization. The BP team, Oxford University or Oxford Chapter shall not be held accountable for any outcome of medical assessment or any other kind of evaluation with this device and/or interface.';
        text18txt = 'The BP team: Carlos Arteta, João Domingos, Marco Pimentel, Mauro Santos';
        
        %popmenu1txt = {english; portuguese; french; indian; nepalese; spanish; chinese; ethiopian; arabic_egypt};
        popmenu1txt = {english; portuguese; french; indian; nepalese};
        popmenu3txt = {'Adult'; 'Paediatric'; 'Newborn'; 'Large Adult'; 'Extra Large Adult'};
        popmenu4txt = {'Left'; 'Right'}
        
        %checkbox1txt = '.csv file';
        
        pushbutton1txt = 'SAVE';
        pushbutton2txt = 'NEW BP MEASUREMENT';
        pushbutton3txt = 'Update';
        
        columnname_1 = 'Time';
        columnname_2 = 'Systolic BP';       %Systolic Blood Pressure
        columnname_3 = 'Diastolic BP';      %Diastolic Blood Pressure
        columnname_4 = 'Heart Rate';
        columnname_5 = 'Blood Pressure';
        
        tab1 = 'BP Measurements';      % Blood Pressure Measurements
        tab2 = 'Session Record';
        tab3 = 'About';
        
        cnames = {'text12txt','text13txt','text14txt','columnname_1','text15txt','text1txt','text22txt','text33txt','columnname_2','columnname_3','columnname_4'};
    end
end

