import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class User extends UnicastRemoteObject implements UserInterface {

    Registry myRegistry;
    Registrar registrar;
    Registry myRegistryMixing;
    MixingProxy mixingProxy;

    PublicKey registrarPubKey = null;

    int gsmNummer;
    String naam;
    List<String> MyTokens = new ArrayList<>();
    //andere identifiers...
    List<String> logs;
    String currentToken;
    String currentDataString;

    Controller controller;

    protected User() throws RemoteException {
        logs= new ArrayList<>();
        controller = new Controller();
    }

    protected User(int port) throws RemoteException {
        super(port);
    }

    protected User(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }
    public void setController(Controller c) {
        controller=c;
        controller.setUser(this);
    }

    @Override
    public void connectToServer() throws RemoteException {
        try {
            // fire to localhostport 1099
            myRegistry = LocateRegistry.getRegistry("localhost", 1099);
            // search for CounterService
            registrar = (Registrar) myRegistry.lookup("Registrar");

            myRegistryMixing = LocateRegistry.getRegistry("localhost", 1098);

            mixingProxy = (MixingProxy) myRegistryMixing.lookup("MixingProxy");
            if (registrar != null) registrar.connect(this);
            if (registrar != null) {
                registrarPubKey = registrar.enrollUsers(this.getGsmNummer());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveMyTokens() throws RemoteException {
        System.out.println("Retrieving tokens");
        MyTokens = registrar.retrieveToken();
        for (String token: MyTokens) {
            System.out.println(token);
        }
    }

    public int getGsmNummer() {
        return gsmNummer;
    }

    public void setGsmNummer(int gsmNummer) {
        this.gsmNummer = gsmNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public void scanQR(String datastring){
        currentDataString = datastring;

        sendFirstCapsule(datastring);
    }

    public void clearLogs(){
        LocalDate today = LocalDate.now();
        LocalDate twoweeks = today.minusWeeks(2);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(String s : logs){
            LocalDate dt = (LocalDate) dtf.parse(s.split(",")[0]);
            if(dt.isBefore(twoweeks)) logs.remove(s);
        }
    }

    public void sendFirstCapsule(String datastring) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = LocalDateTime.now().format(dtf);
        currentToken = MyTokens.remove(0);

        String hash = datastring.split(",")[2];

        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(",");
        sb.append(currentToken);
        sb.append(",");
        sb.append(hash);
        String capsule = sb.toString();
        System.out.println(capsule);

        sb = new StringBuilder();
        sb.append(date);
        sb.append(",");
        sb.append(currentToken);
        sb.append(",");
        sb.append(hash);
        sb.append(",");
        sb.append(datastring.split(",")[0]);
        String log = sb.toString();
        logs.add(log);

        try {
            byte[] image=mixingProxy.registerVisit(capsule);
            ByteArrayInputStream bis = new ByteArrayInputStream(image);
            BufferedImage bImage2 = ImageIO.read(bis);
     //       controller.updateImage(SwingFXUtils.toFXImage(bImage2,null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCapsule() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = LocalDateTime.now().format(dtf);

        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(",");
        sb.append(currentToken);
        sb.append(",");
        sb.append(currentDataString.split(",")[2]);
        String capsule = sb.toString();
        System.out.println(capsule);

        try {
            mixingProxy.continueVisit(capsule);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void leaveCateringFacility(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = LocalDateTime.now().format(dtf);
        for(int i = 0; i< logs.size();i++){
            String s = logs.get(i);
            if(currentToken.equals(s.split(",")[1])){
                logs.remove(s);
                logs.add(s.concat(","+date));
            }
        }
        try {
            mixingProxy.submitCapsules();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void shareLogs(){
       File file = new File("logs.lod");
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pw.close();

        try {
            FileWriter fwOutput = new FileWriter(file,true);

            for(String log : logs){
                fwOutput.write(log);
                fwOutput.write(System.lineSeparator());
            }
            fwOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

