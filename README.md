# CCID Controller
CCID is a PCSC based daemon which interacts with USB smart card reader. It extracts the card number from health insurance card according the [eCH-0064 specification](https://www.ech.ch/de/ech/ech-0064/1.0)
## Prerequisites
- JRE 17 or higher
- Maven 3.9.5 or higher
- Have the necessary libpcsc libraries installed:
    - libpcsclite1
    - pcsc-tools
    - pcscd
- Card Terminal that can be accessed by PCSD

## Card Terminal
Plug in the card terminal and run the following command:

	lsusb

Check the resulting list. You should see something like:

	Bus 003 Device 011: ID 072f:b100 Advanced Card Systems, Ltd ACR39U

> [!NOTE]  
> If you can't detect the card terminal fix it before proceeding. Keep an eye on your USB subsystem

## Build
Change to the `PROJECT_FOLDER` where the source code will be compiled:

	cd PROJECT_FOLDER

Check out the git repository:

	git clone https://github.com/debenol1/covercard.git

Build the product:

	mvn clean compile assembly:single	

## Installation
Create the `DESTINATION_FOLDER`:

	sudo mkdir /DESTINATION_FOLDER

Copy the assembled jar file to the `DESTINATION_FOLDER`

	sudo cp PROJECT_FOLDER/io.ccid.covercard/target/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies /DESTINATION_FOLDER

Create the logs directory:

	sudo mkdir /DESTINATION_FOLDER/logs
        sudo chown -R a+w /DESTIONATON_FOLDER/logs

Test the java binary:

	java -jar DESTINATION_FOLDER/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar

The CCID daemon should boot and display the following lines:

	2025-03-25T19:54:57.249656102Z main INFO Starting configuration XmlConfiguration[location=jar:file:/opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar!/log4j2.xml, lastModified=2025-03-23T21:36:25Z]...
	2025-03-25T19:54:57.250616381Z main INFO Start watching for changes to jar:file:/opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar!/log4j2.xml every 0 seconds
	2025-03-25T19:54:57.250779805Z main INFO Configuration XmlConfiguration[location=jar:file:/opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar!/log4j2.xml, lastModified=2025-03-23T21:36:25Z] started.
	2025-03-25T19:54:57.252269515Z main INFO Stopping configuration org.apache.logging.log4j.core.config.DefaultConfiguration@1563da5...
	2025-03-25T19:54:57.252645697Z main INFO Configuration org.apache.logging.log4j.core.config.DefaultConfiguration@1563da5 stopped.
	2025-03-25T19:54:57.267943779Z main INFO Starting configuration XmlConfiguration[location=jar:file:/opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar!/log4j2.xml, lastModified=2025-03-23T21:36:25Z]...
	2025-03-25T19:54:57.268069364Z main INFO Start watching for changes to jar:file:/opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar!/log4j2.xml every 0 seconds
	2025-03-25T19:54:57.268173300Z main INFO Configuration XmlConfiguration[location=jar:file:/opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar!/log4j2.xml, lastModified=2025-03-23T21:36:25Z] started.
	2025-03-25T19:54:57.268297259Z main INFO Stopping configuration org.apache.logging.log4j.core.config.DefaultConfiguration@6ae5aa72...
	2025-03-25T19:54:57.268424376Z main INFO Configuration org.apache.logging.log4j.core.config.DefaultConfiguration@6ae5aa72 stopped.
	20:54:57.307 [main] DEBUG ch.framsteg.io.ccid.covercard.Launcher - TerminalFactory for type PC/SC from provider SunPCSC
	20:54:57.312 [main] DEBUG ch.framsteg.io.ccid.covercard.Launcher - [1] terminals found
	20:54:57.312 [main] DEBUG ch.framsteg.io.ccid.covercard.Launcher - ACS ACR39U ICC Reader 00 00

Insert an insurance card into the terminal, you should see the card number in the active Window. You can check the log file:

	cat /DESTINATION_FOLDER/logs/application-yyyymmdd.log

Now press Ctrl/C to stop the CCID daemon. The manual startup is working. We have to care about the automatic startup

> [!NOTE]  
> If you can't start the io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar fix it before proceeding. Be sure to have installed the right java runtime environment

## Create CCID startup script
Create a file under `/DESTINATION_FOLDER/ccid` by running the following command:

	sudo vi /DESTINATION_FOLDER/ccid/ccid.sh

Paste the following code in your `ccid.sh`.

	#!/bin/sh 
        java -jar /opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Write and quit the above file. Set the necessary execution permisions:

	sudo chmod +x /DESTINATION_PATH/ccid/ccid.sh

To test the `ccid.jar` execute the following commands:

	/DESTINATION_FOLDER/ccid/ccid.sh start

Now take a look at the processes:

	ps -ef --forest

You should see something similar:

	USER       PID       1 48 18:27 ?        00:00:01 java -jar /DESTINATION_PATH/ccid/ccid.jar /tmp

To stop or restart the program execute the following:

	/DESTINATION_FOLDER/ccid/ccid.sh stop
	/DESTINATION_FOLDER/ccid/ccid.sh restart

Altough we have now a startup/shutdown script the process is still evoked manually. To get the controller up and running automatically we need to reigster it

> [!NOTE]  
> If you can't start/stop the application by using the script fix it before proceeding

## Autostart
The CCID daemon writes the read health card number to the active Window. The underlying Java/AWT/Robot depends on the X Window System and hence cannot be controlled as a headless systemd service. On the contrary the startup/stop script of the CCID controller must be registered within your desktop environment (XFCE/Cinnamon/Mate etc.). Be sure to have the startup script triggered by the login action.

Thats it. Now you have installed a multi user CCID terminal controller 

Have a nice day!

