classdef PortugueseStrings
    properties (Constant)

        english = 'Ingles';
        french = 'Frances';
        portuguese = 'Portugues';
        spanish = 'Espanhol';
        nepalese = 'Nepales';
        indian = 'Standard Indiano';
        chinese = 'Chines';
        ethiopian = 'Amarico';
        arabic_egypt = 'Egipto Arabico';
        
        uipanel1txt = 'Medida currente de PS:  BOMBEAR ACIMA DA LINHA VERMELHA!';
        %uipanel31txt = 'Define the SBP and DBP ratios (oscillometric method)';
        uipanel4txt = 'Lingua';
        uipanel2txt = 'BP measurement with low-cost BP monitor';
        uipanel6txt = 'Save BP measure';
        uipanel3txt = 'Patient credentials';
        uipanel8txt = 'Session record details';
        uipanel9txt = 'About the Low-cost BP monitor device';
        uipanel5txt = 'BP measurement with traditional device';
        uipanel7txt = 'Patient data (not compulsory)';
        
        text19txt = 'Used Arm';
        %text21txt = 'SBP RATIO';
        %text31txt = 'DBP RATIO';
        text2txt = 'SBP';       %Systolic Blood Pressure 
        text3txt = 'DBP';       %Diastolic Blood Pressure 
        text4txt = 'HR';        %Heart Rate 
        text5txt = 'mmHg';      %Millimeters (mm) of mercury (Hg)
        text7txt = 'bpm';
        %text10txt = 'DO NOT press a keyboard key unless you want to finish the acquisition';
        text1txt = 'Press ENTER to finish the acquisition';
        text20txt = 'Compulsory';       %* Compulsory
        text14txt = 'Name / ID';
        text15txt = 'Age';
        text16txt = 'Sex';
        text18txt = 'Cuff Size';
        text29txt = 'University of Oxford: Engineering World Health';
        text30txt = 'This programme is intended to be used only with the Low-cost Blood Pressure Monitor device developed by the Oxford BP team. The device is not intended for medical use, pending validation and simultaneous optimization. The BP team, Oxford University or Oxford Chapter shall not be held accountable for any outcome of medical assessment or any other kind of evaluation with this device and/or interface.';
        text31txt = 'The BP team: Carlos Arteta, Joao Domingos, Marco Pimentel, Mauro Santos';
        text17txt = 'Measurement Number';
        text21txt = 'Patient Status (functional limitations)';
        text22txt = 'Patient smokes?';
        text23txt = 'Anti-hypertensive medication';
        text24txt = 'Arm circunference';
        text25txt = 'Observations / Comments';
        text26txt = 'Observer Name';
        text27txt = 'cm';       %centimeters
        
        %popmenu5txt = {'Ingles'; 'Portugues'; 'Nepales'; 'Standard Indiano'; 'Frances'; 'Espanhol'; 'Chines'; 'Amarico'; 'Egipto Arabico'};
        popmenu5txt = {'Ingles'; 'Portugues'; 'Nepales'};
        popmenu3txt = {'Adulto'; 'Pediatrico'; 'Recem nascido'; 'Adulto grande'; 'Adulto muito grande'};
        popmenu2txt = {'Direito'; 'Esquerdo'}
        popmenu1txt = {'Masculino'; 'Feminino'}
        popmenu4txt = {'1'; '2'; '3'; '4'; '5'}
        
        checkbox1txt = 'file';
        
        pushbutton3txt = 'GUARDAR';
        pushbutton1txt = 'NOVA MEDIDA DE PS';
        %pushbutton3txt = 'Actualizar';
        pushbutton2txt = 'Extrair tempo de medida';
        
        tab1 = 'BP Measurements';      % Blood Pressure Measurements
        tab11 = 'BP Measurements 2';
        tab2 = 'Session Record';
        tab3 = 'About';
        
        warning1 = 'No serial port was found in the system. Please connect the BP monitor device and restart the Interface.';
        warning2 = 'Help Box:';
        warning3 = 'There are (virtual) serial devices connected to the computer but none is the BP device! Please connect the BP monitor device and restart the Interface.';
        warning4 = 'Please, acquire a new signal! Pressure must be above 180 mmHg.';
        warning5 = 'A new acquisition needs to be performed before saving the values!';
        warning6 = 'Compulsory patient details must be given in order to save the acquired measurements.';
        
        cnames_xls = {'Study Number','Observer Name','Name/ID','Measure Number (same patient)','Age','Sex','Cuff Size','Used arm','Arm Circumference (cm)','Smoker?','Time of measurement (TD)','Systolic BP (TD)','Diastolic BP (TD)','Heart Rate (TD)','Time of measurement','SBP Ratio','DBP Ratio','Systolic BP','Diastolic BP','Heart Rate','Patient status (medical condition)','Anti-hypertensive medication','Observations/Comments'};  %TD stands for Traditional Device
        cnames = {'Name/ID','Age','Sex','Time of measurement','Cuff Size','Used arm','SBP Ratio','DBP Ratio','Systolic BP','Diastolic BP','Heart Rate'};
    
    end
end

