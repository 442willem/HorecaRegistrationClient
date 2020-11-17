import java.rmi.RemoteException;
import java.util.TimerTask;

public class TimedTask  extends TimerTask {

    CateringFacility cf;

    public void run()  {
        try {
            cf.getDailySecret();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

