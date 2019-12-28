# Bus-Tracker-Android

## Introduction ##
The application is designed to help people commute easily by reducing their waiting time at bus stations. 
The prime motive of the application is to provide correct and current statuses of several buses plying across different routes. 
The commuters can make use of this information and prefer to either wait or choose an alternative mode of transportation thus enabling them to reach their destination within intended time frame. 
The limitations of placing the GPS systems in the buses are that installation costs are high and such systems may not be accurate in case of technical malfunction. 
However, this is eliminated when a group of users interact and help each other by exchange of necessary information on location of buses using the GPS systems on their mobile devices.

## Implementation ##
The target device is simply an Android device that is GPS enabled. 
A user logs into a system and updates his/her exact location as indicated by the GPS system along with relevant details such as bus number. 
The user at the bus stop logs in to find out if any of the buses are plying currently. 
He first selects the appropriate bus number and subsequently gets updates of that bus. 
In order to encourage users to update data, they can be given reward points whose accumulation can be redeemed in the form of free rides or other benefits when it meets a certain criterion.

The implementation has two phases:

a. To update data<br/>
b. To obtain data

These are the major modules. The interface is designed for login, bus update
retrieval and reward management.

## Complexity ##
The challenging part of the project will be to enable GPS and obtain the exact location and other details of the buses as provided by the user. 
Also to ensure that the data that is provided is accurate and not spurious. A centralized server access is needed for this purpose.
