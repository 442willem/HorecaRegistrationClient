# HorecaRegistrationClient

How to run:

-Catering facility:
  When executing the cateringFacilityApp.java file a catering facility (in this case KastartBVBA) will be registered with the server. 
  The console will print out the daily QR-code in the form of a datastring.

-User:
  When executing the UserApp.java a GUI will be launched for a user (in this case "Willem" with phone number 102).
  Paste the datastring in the textfield in the gui to "scan" a QR code.
  Press enter or leave to enter leave a facility
  Press share logs to print out your personal logs to a file

-Doctor:
  When executing the DoctorApp.java file the last shared logs will be read, marked as infected and sent to the server
  This means that a user must generate a log file before the doctor can be executed
  
In order to run any of the files, the javafx libraries need to be included in the external libraries folder of the project.
The following VM options also need to be added to the run configurations of the 3 apps:

--module-path "PATH-TO-LIBRARY" --add-modules javafx.controls,javafx.fxml

