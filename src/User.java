import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class User extends UnicastRemoteObject implements UserInterface {

    Registry myRegistry;
    Registrar service;

    PublicKey registrarPubKey = null;

    int gsmNummer;
    String naam;
    List<byte[]> MyTokens = new ArrayList<>();
    //andere identifiers...

    protected User() throws RemoteException {
    }

    protected User(int port) throws RemoteException {
        super(port);
    }

    protected User(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
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
            if (service != null) {
                registrarPubKey = service.enrollUsers(this.getGsmNummer());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveMyTokens() throws RemoteException {
        System.out.println("Retrieving tokens");
        MyTokens = service.retrieveToken();
        for (byte[] token: MyTokens) {
            System.out.println(Base64.getEncoder().encodeToString(token));
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
}

