# CCID
CCID is a PCSC based daemon which interacts with USB smart card reader. It extracts the card number from health insurance card according the [eCH-0064 specification](https://www.ech.ch/de/ech/ech-0064/1.0)
## Prerequisites
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
