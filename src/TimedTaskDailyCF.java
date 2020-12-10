import java.rmi.RemoteException;
import java.util.TimerTask;

public class TimedTaskDailyCF extends TimerTask {

    CateringFacility cf;

    public TimedTaskDailyCF(CateringFacility c){
        cf=c;
    }


    public void run()  {
        try {
            cf.getDailySecret();
            cf.getDailyNym();
            cf.generateQRcode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

