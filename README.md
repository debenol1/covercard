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

   java -jar io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Optionally the daemon can be started with the -v parameter. The generated logs are stored in the relative subfolder log.
## Create CCID startup script
Create a file under /DESTINATION_FOLDER/ccid by running the following command:

	sudo vi /DESTINATION_FOLDER/ccid/ccid.sh

Paste the following Code in your Service_Name.sh.
Modify the SERVICE_NAME(Name of your Service), PATH_TO_JAR(Absolute Path to you jar File), and choose a PID_PATH_NAME(just replace Service_Name to your Service_Name keeping -pid at end ) for the file you are going to use to store your service ID.Only Changes needed are to the first 3 variables:

	#!/bin/sh 
	SERVICE_NAME=Your_Service_Name 
	PATH_TO_JAR=/usr/Name_of_User/MyJavaApplication.jar 
	PID_PATH_NAME=/tmp/Service_Name-pid 
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



## Create CCID service
In order to get the CCID daemon started automaticallv, create a service configuration file:

   sudo vi /etc/systemd/system/ccid.service
	
Enter the following lines:

	[Unit]
	Description=CCID

	[Service]
	User=USER_WITH_SUDOERS_RIGHTS
	WorkingDirectory=/BASE_DIRECTORY
	ExecStart=java -jar io.ccid.covercard-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
	Restart=always

	[Install]
	WantedBy=multi-user.target

	
Activate the service:

	sudo systemctl daemon-reload
	sudo systemctl start ccid.service
	sudo systemctl enable ccid.service
	
If you want to disable the service enter:
        sudo systemctl disable ccid.service
