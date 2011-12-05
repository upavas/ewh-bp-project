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
        
        uipanel2txt = 'Medida da PS:  BOMBEAR ACIMA DA LINHA VERMELHA!';
        %uipanel3txt = 'Define the SBP and DBP ratios (oscillometric method)';
        uipanel3txt = 'Lingua';
        uipanel4txt = 'Last BP measurement';
        uipanel5txt = 'Save BP measure';
        uipanel6txt = 'Patient credentials';
        uipanel7txt = 'Session records';
        uipanel8txt = 'About the Low-cost BP monitor device';
        
        text1txt = 'Braco usado';
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
        text18txt = 'The BP team: Carlos Arteta, Joao Domingos, Marco Pimentel, Mauro Santos';
        
        %popmenu1txt = {'Ingles'; 'Portugues'; 'Frances'; 'Standard Indiano'; 'Nepales'; 'Espanhol'; 'Chines'; 'Amarico'; 'Egipto Arabico'};
        popmenu1txt = {'Ingles'; 'Portugues'; 'Frances'; 'Standard Indiano'; 'Nepales'};
        popmenu3txt = {'Adulto'; 'Pediatrico'; 'Recem nascido'; 'Adulto Grande'; 'Adulto Muito Grande'};
        popmenu4txt = {'Esquerdo'; 'Direito'}
        
        %checkbox1txt = '.csv file';
        
        pushbutton1txt = 'GUARDAR';
        pushbutton2txt = 'NOVA MEDIDA DE PS';
        pushbutton3txt = 'Actualizar';
        
        columnname_1 = 'Time';
        columnname_2 = 'Systolic BP';       %Systolic Blood Pressure
        columnname_3 = 'Diastolic BP';      %Diastolic Blood Pressure
        columnname_4 = 'Heart Rate';
        columnname_5 = 'Blood Pressure';
        
        tab1 = 'BP Measurements';      % Blood Pressure Measurements
        tab2 = 'Session Record';
        tab3 = 'About';
        
        cnames = {'Name','Age','Sex','Time','Cuff Size','Used Arm','SBP RATIO','DBP RATIO','Systolic BP','Diastolic BP','Heart Rate'};
    end
end

