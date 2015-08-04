# Introduction #

The system for acquisition of the BP data is based on a low-cost peripheral that connects a regular BP cuff with a mobile phone. In order to minimize the cost of the BP device, the peripheral hardware design contains just enough elements to transmit the pressure signal to the mobile device, which then performs the digital signal processing.



# Details #

As it is common for electronic BP devices, the air pressure in the inflatable cuff is converted into an analogue electric signal by a pressure sensor. The transducer used in the device presented in this work is the MPXV5050GP from Freescale Semiconductors, an on-chip integrated and temperature compensated pressure sensor. The analogue output signal from the sensor is divided in two channels: 1) a raw pressure channel, and 2) a filtered/amplified channel; each channel is sampled at 50Hz with a 12-bit resolution (0.07 mmHg per bit in the raw channel) using a PIC18F14K50 from Microchip. The data containing discrete pressure values is sent to the mobile phone through a USB cable using the USB 2.0 module integrated in the chosen microcontroller. The resulting peripheral device is a 50x20mm PCB, with a connector for the air tube from the cuff and a Type-A male USB connector.

In order to communicate with the peripheral, the mobile phone must support the USB ‘On-the-Go’ (OTG) supplement of the USB 2.0 standard, which allows the phone to act as a USB Host to the external device. Furthermore, when using this feature the peripheral can be powered from the mobile phone. USB Host mode feature is available from Android version 3.1 onwards and is present in some smartphones nowadays.

![http://wiki.ewh-bp-project.googlecode.com/hg/circuit.png](http://wiki.ewh-bp-project.googlecode.com/hg/circuit.png)

![http://wiki.ewh-bp-project.googlecode.com/hg/OSource%20BP%20device%20close%20shot.jpg](http://wiki.ewh-bp-project.googlecode.com/hg/OSource%20BP%20device%20close%20shot.jpg)

![http://wiki.ewh-bp-project.googlecode.com/hg/OSource%20BP%20device%20with%20mphone.jpeg](http://wiki.ewh-bp-project.googlecode.com/hg/OSource%20BP%20device%20with%20mphone.jpeg)

![http://wiki.ewh-bp-project.googlecode.com/hg/OSource%20BP%20device%20with%20tablet.png](http://wiki.ewh-bp-project.googlecode.com/hg/OSource%20BP%20device%20with%20tablet.png)