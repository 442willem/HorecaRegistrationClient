import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Registrar extends Remote  {

     void connect(CateringFacilityInterface cf) throws RemoteException;

     void connect(UserInterface ui) throws RemoteException;
}
