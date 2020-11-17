import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class User extends UnicastRemoteObject implements UserInterface {

    Registry myRegistry;
    Registrar service;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

