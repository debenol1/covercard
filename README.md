# CCID
CCID is a PCSC based daemon which interacts with USB smart card reader. It extracts the card number from health insurance card according the [eCH-0064 specification](https://www.ech.ch/de/ech/ech-0064/1.0)
## Prerequisites
- Local USER to run the service
- JRE 17 or higher
- Maven
- Have the necessary libpcsc libraries installed:
    - libpcsc-perl
    - libpcsclite-dev
    - libpcsclite1
    - pcsc-tools
    - pcscd
## Build
Check out the git repository:

	git clone https://github.com/debenol1/covercard.git

Build the product:

   mvn clean compile assembly:single	

## Installation
Copy the compiled binaries to the destination folder. First plug in the physical Card Terminal. Then start the CCID daemon by running the following command:

	java -jar /DESTINATION_FOLDER/ccid/ccid.jar

The generated logs are stored in the relative subfolder **logs**

	/DESTINATION_PATH/ccid/logs/application-YYYYMMDD.log

The installation of the java binaries is done. **IMPORTANT: connect the card terminal now**
## Create CCID startup script
Create a file under /DESTINATION_FOLDER/ccid by running the following command:

	sudo vi /DESTINATION_FOLDER/ccid/ccid.sh

Paste the following Code in your Service_Name.sh.
Modify the SERVICE_NAME(Name of your Service), PATH_TO_JAR(Absolute Path to you jar File), and choose a PID_PATH_NAME(just replace Service_Name to your Service_Name keeping -pid at end ) for the file you are going to use to store your service ID.Only Changes needed are to the first 3 variables:

	#!/bin/sh 
	SERVICE_NAME=CCID 
	PATH_TO_JAR=/DESTINATION_FOLDER/ccid/ccid.jar 
	PID_PATH_NAME=/tmp/ccid-pid 
	case $1 in 
	start)
   	  	echo "Starting $SERVICE_NAME ..."
  		if [ ! -f $PID_PATH_NAME ]; then 
      	nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >>/dev/null &      
                   echo $! > $PID_PATH_NAME  
       	echo "$SERVICE_NAME started ..."         
  	else 
       	echo "$SERVICE_NAME is already running ..."
  	fi
	;;
	stop)
  		if [ -f $PID_PATH_NAME ]; then
         PID=$(cat $PID_PATH_NAME);
         echo "$SERVICE_NAME stoping ..." 
         kill $PID;         
         echo "$SERVICE_NAME stopped ..." 
         rm $PID_PATH_NAME       
  		else          
         echo "$SERVICE_NAME is not running ..."   
  	fi    
	;;    
	restart)  
  	if [ -f $PID_PATH_NAME ]; then 
      PID=$(cat $PID_PATH_NAME);    
      echo "$SERVICE_NAME stopping ..."; 
      kill $PID;           
      echo "$SERVICE_NAME stopped ...";  
      rm $PID_PATH_NAME     
      echo "$SERVICE_NAME starting ..."  
      nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &            
      echo $! > $PID_PATH_NAME  
      echo "$SERVICE_NAME started ..."    
  	else           
      echo "$SERVICE_NAME is not running ..."    
     fi     ;;
 	esac

Write and quit the above file and give execution permisions :
ex. sudo chmod +x /DESTINATION_PATH/ccid/ccid.sh

To test the ccid.jar execute the following commands:

	/usr/local/bin/./Service_Name.sh start

Now take a look at the processes:

	ps -ef --forest

You should see something similar:

	USER       PID       1 48 18:27 ?        00:00:01 java -jar /DESTINATION_PATH/ccid/ccid.jar /tmp

To stop or restart the program execute the following:

	/usr/local/bin/./Service_Name.sh stop
	/usr/local/bin/./Service_Name.sh restart

## Create CCID service
In order to get the CCID daemon started automaticallv, create a service configuration file:

	sudo vi /etc/systemd/user/ccid.service
	
Enter the following lines:

	[[Unit]
 	Description = CCID controller to manage pscsd based card terminal 
 	
	[Service]
 	Type = forking
 	Restart=always
 	RestartSec=1
 	SuccessExitStatus=143 
 	ExecStart = /DESTINATION_FOLDER//ccid/ccid.sh start
 	ExecStop = /DESTINATION_FOLDER/ccid/ccid.sh stop
 	ExecReload = /DESTINATION_FOLDER/ccid/ccid.sh reload
 	StandardOutput=file:%h/log_file
	[Install]
 	WantedBy=multi-user.target

Reload the systemd daemon:

	systemctl --user daemon-reload


Activate the service:

	systemctl --user enable ccid.service

Start the service:

	systemctl --user start ccid.service

Show the status:

	systemctl --user status ccid.service

You should see something like:

	systemctl --user status ccid.service
	● ccid.service - CCID controller to manage pscsd based card terminal
     Loaded: loaded (/etc/xdg/systemd/user/ccid.service; enabled; preset: enabled)
     Active: active (running) since Mon 2025-03-24 20:50:37 CET; 1s ago
    Process: 54313 ExecStart=/opt//ccid/ccid.sh start (code=exited, status=0/SUCCESS)
  	 Main PID: 54314 (java)
      Tasks: 21 (limit: 38148)
     Memory: 62.7M (peak: 64.3M)
        CPU: 836ms
     CGroup: /user.slice/user-1008.slice/user@1008.service/app.slice/ccid.service
             └─54314 java -jar /opt/ccid/io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar /tmp

	Mär 24 20:50:37 chbs177 systemd[2068]: Starting ccid.service - CCID controller to manage pscsd based card terminal...
	Mär 24 20:50:37 chbs177 systemd[2068]: Started ccid.service - CCID controller to manage pscsd based card terminal.

To inspect the log:

	journalctl --user -u ccid.service

Finito. Have a nice day

