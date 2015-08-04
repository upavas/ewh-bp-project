USER MANUAL V0.1 (STILL INCOMPLETE)

Table of Contents




# 1. Introduction #

This software was developed in the context of the [2011 EWH Competition](http://ewh.org/index.php/programs/technology/competition/).
The main features of the software are:

1) Measure Systolic Pressure, Diastolic Pressure and Heart Rate (Cuff oscillometric method, with manual pump);

2) Save blood pressure and pulse measures into the device database;

3) View saved blood pressure and pulse measures.


# 2. Software Installation #
Ways to install software with an android device:

1) Take a pic of the QR code bellow with a [QR Reader App](http://code.google.com/p/zxing/) from your android phone. Download the software and click on it to install the application.

![http://wiki.ewh-bp-project.googlecode.com/hg/qrcode.344751.png](http://wiki.ewh-bp-project.googlecode.com/hg/qrcode.344751.png)

2) Download the latest/adequate version in this page (link) to your PC, push it to the android device (to SDCARD or internal storage) and click on it to install.

3) ADB: Having the android-sdk installed in your PC, use the command:

(In case of 1 device attached to the PC:)

adb install bp-app.apk

(In case of multiple devices attached to the PC:)

adb -s < android\_device\_serial > install bp-app.apk

4) Android Market: Still not available.

# 3. Devices #

The application should run on any Android Device with the following features: OS version 3.1+ (tested on android 4), with USB-OTG support.

The peripherals needed are: BP Box (with Male-Male USB Cable) and Blood Pressure Cuff (with manual pump and valve).

### 3.1 Tested Android Devices ###

| Device | OS version |  Working | notes |
|:-------|:-----------|:---------|:------|
| Samsung Galaxy Nexus|  Android 4       | yes      | Needs usb adapter |
| Samsung Galaxy Tab 10.1|  Android 3.1       | yes      | Needs usb adapter |
### 3.2 Expected experimental setup ###

![http://wiki.ewh-bp-project.googlecode.com/hg/photo_experimental_setup.png](http://wiki.ewh-bp-project.googlecode.com/hg/photo_experimental_setup.png)

The figure above shows the experimental setup of the mobile BP measure device. The manometer was removed from a traditional sphygmomanometer and its tube connected to the pressure sensor of the BP box. The BP box is then connected to the cell phone using a USB cable.

# 4. User Interface #

The application should run on any Android Device with the following features: OS version above 2.1, rooted (for now) and USB-OTG support.

## 4.1 Main Page ##

![http://wiki.ewh-bp-project.googlecode.com/hg/mainmenu.jpg](http://wiki.ewh-bp-project.googlecode.com/hg/mainmenu.jpg)

The main page is comprised of 3 buttons:

1.New Blood Pressure Measure - navigate to blood pressure measure page.<br>
2.View Saved Measurements - navigate to list of saved blood pressure measurements.<br>
3.Help - navigate to page that provides instructions on how to take the blood pressure measure with this aplication.<br>


<h2>4.2 Blood Pressure Measure Page</h2>

<img src='http://wiki.ewh-bp-project.googlecode.com/hg/documentation_measure_page.png' />

The measure page is composed by:<br>
<br>
1. Real time pressure Chart <br>
2. Display for Systolic/Diastolic pressures and Heart Rate<br>
3. Notes Field <br>
4. Buttons to save/discard measure.<br>

<h3>4.2.1 Real time pressure chart</h3>

The real time chart shows the changes in the cuff pressure values (mmHg) during the blood pressure measurement (time). The pressure should follow a pattern similar to the figure below.<br>
<br>
<img src='http://wiki.ewh-bp-project.googlecode.com/hg/real_time_pressure_chart.png' />

When the user pumps to pressurize the cuff, the pressure will increase.<br>
The user should stop pumping when the pressure reaches the red line in the chart, that is shown constantly at pressure = 240 mmHg.<br>
After it reaches this value the user should stop pumping and release the valve just a little bit, so the cuff de-pressures at a linear rate.<br>
Most of the times the rate of de-pressure will be exponential, the algorithm is also prepared to calculate the blood pressure in this situation. During this phase, when the pressure reaches 20 mmHg the measure will finish and the blood pressure and hear rate will be determined automatically and shown <a href='UserManual#4.2.2_Display_for_Systolic/Diastolic_pressure_and_pulse.md'>"bellow the chart"</a>.<br>
<br>
<h3>4.2.2 Display for Systolic/Diastolic pressure and pulse</h3>

The blood pressure/pulse display is bellow the real time chart in the<br>
middle of the <a href='UserManual#4.2_Blood_Pressure_Measure_Page.md'>UserManual#4.2_Blood_Pressure_Measure_Page</a>. The displayed fields are:<br>
1. SYS - systolic pressure.<br>
2. DIA - diastolic pressure.<br>
3. HR  - heart rate.<br>

<h3>4.2.3 Notes Field</h3>
After the measure is taken, the user can add notes to the measure by<br>
editing the Notes field bellow the <a href='UserManual#4.2.2_Display_for_Systolic/Diastolic_pressure_and_pulse.md'>blood pressure and pulse display</a>.<br>
Examples of notes:<br>
1. Because there is no way, in this project, for the user to identify from whom the measure is being taken, he can use this field to add a name of the patient to the measure (this is not an ideal solution<br>
and we hope we can integrate this application in other applications that already solved this problem). <br>
2. The user can use this field to comment on anything that went wrong or well, during the measure.<br>

<h3>4.2.4 Buttons to save/discard measure</h3>

At the end of the page there are buttons to save/discard measure, or to view the help page of the application.<br>
<br>
1. Save to database - to save the blood pressure and pulse values to the device database just click on the save button after the blood pressure is calculated. A message will show confirming the success of this action.<br>
<br>
2. Save to file and database - to save all the information of the measure to a csv file just check the "create csv file" checkbox and<br>
push the save button. This will also save the blood pressure and pulse<br>
values into the database as above.<br>
<br>
3. To discard the measure just push the discard button.<br>
<br>
<br>
<h3>4.2.5 Procedure to take blood pressure:</h3>

1. Remove the manometer from the sphygmomanometer and  dress the cuff into the patient left arm, the closest to the heart ;);<br>

2. Connect the BP Box sensor tip to the tube where the manometer was and the  USB cable to the other end of the box.<br>

3. Start the application on the android phone.<br>
Home->Applications-> Blood Pressure<br>

4. Push <a href='UserManual#4.1_Main_Page.md'>"New Blood Pressure measure"</a> button, to start the measure page. Connect the BP box to the android device with the other tip of the USB cable. <br>

5. Close the manual pump valve and pump to pressurize the cuff until the pressure line reaches the RED line in the chart and then stop pumping and open the valve just a little (A LITTLE! ;) ) to de-pressurize the cuff.<br>
<br>
6. The pressure line has to decrease at a linear or exponential rate until the pressure reaches 20. The blood pressure and pulse calculation<br>
will start automatically and will be displayed bellow the chart, <a href='UserManual#4.2.2_Display_for_Systolic/Diastolic_pressure_and_pulse.md'>here</a>.<br>
<br>
7. Edit the <a href='UserManual#4.2.3_Text_field_to_take_notes.md'>Notes text field</a>
and save the measure to the database of measures, otherwise push the discard button to return to the main page.<br>
<br>
If you want to study the signal click in the <a href='UserManual#4.2.4_Buttons_to_save/discard_measure.md'>"create csv file"</a> checkbox to save a csv file with<br>
calculate blood pressure and pulse, and the time series of the pressure values. The files will be saved <a href='UserManual#4.2.4_Buttons_to_save/discard_measure.md'>here</a>.<br>
<br>
<h2>4.3 List of Saved Blood Pressure Measurements</h2>

1. To access this page, push the <a href='UserManual#4.1_Main_Page.md'>"View Saved Measures"</a> button in the main page.<br>
<br>
<img src='http://wiki.ewh-bp-project.googlecode.com/hg/measure_list.png' />


2. The list of saved measures is shown with the following fields:<br>
Nr - The number of the measure in the database; <br>
Date Time - The date and time when the measure was taken, in "dd/mm/yy hh:mm" format <br>
SYS - The systolic pressure of the measure;<br>
DIA - The diastolic pressure of the measure;<br>
HR - The Heart rate of the measure;<br>

The list is sorted in descended order, the last measures are shown on the top.<br>
<br>
<h2>4.4 Blood Pressure View</h2>

<img src='http://wiki.ewh-bp-project.googlecode.com/hg/measure_view.png' />

To view a saved blood pressure measure push the <a href='UserManual#4.1_Main_Page.md'>"View Saved Measures"</a> button in the main page, and the click/touch on the measure to view. These actions will open a page composed by:<br>
1. Number, created and modified date of the measure information.<br>
2. View of the blood pressure values and pulse.<br>
3. Text field to edit notes.<br>
4. Buttons to save/discard changes/delete the measure.<br>

<h3>4.4.1 Edit Note or Discard Changes</h3>
1. To edit a blood pressure note, go to a <a href='UserManual#4.4_Blood_Pressure_View.md'>"blood pressure measure view page"</a> and click over the "Notes" text field and edit the note. <br>
2. Save the note by pushing the save button. To discard the changes push the discard button<br>

<h3>4.4.2 Delete Measure</h3>

1. To delete a blood pressure measure, go to a <a href='UserManual#4.4_Blood_Pressure_View.md'>"blood pressure measure view page"</a>  and click the "delete" button. Confirm the action and the measure will<br>
be deleted from the database permanently. If there was an associated file, it will also be deleted from the sdcard folder. <br>

<h1>5. Supported Languages</h1>

The application supports the following 6 languages:<br>
<br>
<table><thead><th>locale</th><th>Language</th></thead><tbody>
<tr><td>en    </td><td>English </td></tr>
<tr><td>es    </td><td>Español </td></tr>
<tr><td>fr    </td><td>Français</td></tr>
<tr><td>pt    </td><td>Português</td></tr>
<tr><td>zh    </td><td>Chinese </td></tr>
<tr><td>hi    </td><td>Standard Hindi </td></tr>
<tr><td>ar    </td><td>Arabic Egypt </td></tr></tbody></table>

We would like to acknowledge: <br>
<b>Ning Zhang, DPhil Student in CDT in Healtcare Innovation, for the Chinese translation.</b><br>
 Sunali Bhatnagar,  DPhil Student in CDT in Healtcare Innovation, for the Standard Hindi translation. <br>
Dr.Mokhles Asal, for the Arabic(Egypt) translation.<br>
<br>
<br>
