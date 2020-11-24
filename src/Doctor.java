import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.*;

public class Doctor extends UnicastRemoteObject implements DoctorInterface {
    Registry myRegistry;
    MatchingService matchingService;

    KeyPair pair;

    List<String> allLogs=new ArrayList<>();

    int id;

    protected Doctor() throws RemoteException {
        KeyPairGenerator keyGen=null;
        SecureRandom random=null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            id = random.nextInt();
            keyGen.initialize(2048, random);
            pair = keyGen.generateKeyPair();
            System.out.println("pair:"+pair);
            System.out.println("publickey:"+ Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
            System.out.println("privatekey:"+Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    protected Doctor(int port) throws RemoteException {
        super(port);
    }

    protected Doctor(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }


    @Override
    public void connectToServer() throws RemoteException {
        try {
            myRegistry = LocateRegistry.getRegistry("localhost", 1097);
            matchingService = (MatchingService) myRegistry.lookup("MatchingService");

            if (matchingService != null) matchingService.connect(this);
            else System.out.println("matchingservice is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return pair.getPublic();
    }

    @Override
    public int getId() throws RemoteException {
        return id;
    }

    public void sendLogs() throws RemoteException{
        List<byte[]> signedLogs = new ArrayList<>();
        for(String log : allLogs) {
            Signature dsa = null;
            try {
                dsa = Signature.getInstance("SHA1WithRSA");

                dsa.initSign(pair.getPrivate());

                byte[] tekst = log.getBytes();
                dsa.update(tekst);

                signedLogs.add(dsa.sign());

            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
            }
        }

        matchingService.forwardLogs(id, allLogs,signedLogs);
        allLogs.clear();
    }


    public void readLogs() {
        File file = new File("logs.lod");
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                allLogs.add(line);
            }
            Collections.shuffle(allLogs);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}