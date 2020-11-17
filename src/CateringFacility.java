import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.*;
import java.util.Random;

public class CateringFacility extends UnicastRemoteObject implements CateringFacilityInterface {

    Registry myRegistry;
    Registrar service;
    String CF;
    SecretKey s;
    SecretKey sCFDayi;
    String location;
    String dailyNym;



    protected CateringFacility() throws RemoteException {
    }

    protected CateringFacility(int port) throws RemoteException {
        super(port);
    }

    protected CateringFacility(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void connectToServer() throws RemoteException {
        try {
            // fire to localhostport 1099
            myRegistry = LocateRegistry.getRegistry("localhost", 1099);
            // search for CounterService
            service = (Registrar) myRegistry.lookup("Registrar");
            if (service != null) service.connect(this);
            //is connected
            //Generated secret key based on unique identifier
            this.s = service.enrollFacility(this.getCF());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDailySecret() throws RemoteException {
       this.sCFDayi= service.getDailyKey(CF,s);
    }

    public void getDailyNym() throws RemoteException{
        dailyNym = service.getDailyPseudonym(this.location,sCFDayi);
    }

    public void generateQRcode(){
        Random rand = new Random(0);
        int randomGetal = rand.nextInt();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md.update(BigInteger.valueOf(randomGetal).toByteArray());

        String datastring = randomGetal + CF +   Base64.getEncoder().encodeToString(md.digest(dailyNym.getBytes()));

        //deze ophangen dan
        System.out.println(datastring);

    }


    public void setUniqueIDCF(String CF){
        this.CF = CF;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCF() {
        return CF;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }
}
