import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class User extends UnicastRemoteObject implements UserInterface {

    Registry myRegistry;
    Registrar registrar;
    Registry myRegistryMixing;
    MixingProxy mixingProxy;
    Registry myRegistryMatching;
    MatchingService matchingService;

    PublicKey registrarPubKey = null;

    int gsmNummer;
    String naam;
    List<String> MyTokens = new ArrayList<>();
    //andere identifiers...
    List<String> logs;
    String currentToken;
    String currentDataString;

    Controller controller;

    Timer timer;

    protected User() throws RemoteException {
        logs = new ArrayList<>();
        controller = new Controller();
    }

    protected User(int port) throws RemoteException {
        super(port);
    }

    protected User(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    public void setController(Controller c) {
        controller = c;
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

            myRegistryMatching = LocateRegistry.getRegistry("localhost", 1097);

            matchingService = (MatchingService) myRegistryMatching.lookup("MatchingService");

            if (registrar != null) registrar.connect(this);
            if (registrar != null) {
                registrarPubKey = registrar.enrollUsers(this.getGsmNummer());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveMyTokens() {
        System.out.println("Retrieving tokens");
        try {
            MyTokens = registrar.retrieveToken();
        } catch (RemoteException e) {
            e.printStackTrace();
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

    public void scanQR(String datastring) {
        currentDataString = datastring;

        sendFirstCapsule(datastring);
    }

    public void clearLogs() {
        LocalDate today = LocalDate.now();
        LocalDate twoweeks = today.minusWeeks(2);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (String s : logs) {
            LocalDate dt = LocalDate.parse(s.split(",")[0], dtf);
            if (dt.isBefore(twoweeks)) logs.remove(s);
        }
        System.out.println("local logs cleared");
    }

    public void sendFirstCapsule(String datastring) {
        if (MyTokens.size() > 0) {
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
                int width = 100;
                int height = 100;

                byte[] image = mixingProxy.registerVisit(capsule);

                if (image != null) {
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    Random random = new Random(Byte.toUnsignedInt(image[0]));
                    int alpha = 255;
                    r += Byte.toUnsignedInt(image[64 + random.nextInt(64)]);
                    g += Byte.toUnsignedInt(image[128 + random.nextInt(64)]);
                    b += Byte.toUnsignedInt(image[192 + random.nextInt(64)]);

                    int pixel = (alpha << 24) | (r << 16) | (g << 8) | b;
                    int[] pixels = new int[width * height];
                    Arrays.fill(pixels, pixel);

                    WritableImage img = new WritableImage(width, height);
                    PixelWriter pw = img.getPixelWriter();
                    pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), pixels, 0, width);
                    controller.updateImage(img);

                    //start up bihourly task
                    LocalDateTime biHourly = LocalDateTime.now();
                    timer = new Timer();
                    timer.schedule(new TimedTaskBiHourly(this), Date.from(biHourly.atZone(ZoneId.systemDefault()).toInstant()), 10000);
                } else {
                    FileInputStream inputstream = new FileInputStream("StopCovid19Cat.jpg");
                    Image img = new Image(inputstream);
                    controller.updateImage(img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileInputStream inputstream = new FileInputStream("StopCovid19Cat.jpg");
                Image img = new Image(inputstream);
                controller.updateImage(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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

    public void leaveCateringFacility() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = LocalDateTime.now().format(dtf);
        for (int i = 0; i < logs.size(); i++) {
            String s = logs.get(i);
            if (currentToken.equals(s.split(",")[1])) {
                logs.remove(s);
                logs.add(s.concat("," + date));
            }
        }
        try {
            mixingProxy.submitCapsules();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        timer.cancel();
    }

    public void shareLogs() {
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
            FileWriter fwOutput = new FileWriter(file, true);

            for (String log : logs) {
                fwOutput.write(log);
                fwOutput.write(System.lineSeparator());
            }
            fwOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Logs shared");
    }

    public void fetchCriticalLogs() {
        try {
            List<String> infectedLogs = matchingService.requestInfectedLogs();
            List<String> acknowledgedTokens = new ArrayList<>();
            for (String infectedLog : infectedLogs) {
                for (String localLog : logs) {
                    String hash = infectedLog.split(",")[0];
                    String localHash = localLog.split(",")[2];

                    if (hash.equals(localHash)) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                        LocalDateTime begin1 = LocalDateTime.parse(localLog.split(",")[0], dtf);

                        LocalDateTime eind1 = LocalDateTime.parse(localLog.split(",")[4], dtf);

                        LocalDateTime begin2 = LocalDateTime.parse(infectedLog.split(",")[1], dtf);

                        LocalDateTime eind2 = LocalDateTime.parse(infectedLog.split(",")[2], dtf);

                        if (begin1.isBefore(eind2) && eind1.isAfter(begin2)) {
                            acknowledgedTokens.add(localLog.split(",")[1].split(";")[1]);
                            break;
                        }
                    }
                }
            }
            if (acknowledgedTokens.size() > 0) {
                System.out.println("infection detected");
                controller.setInfected();
                mixingProxy.acknowledge(acknowledgedTokens);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flushCapsules(){
        try {
            mixingProxy.flushCapsules();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

