import java.util.TimerTask;

public class TimedTaskBiHourly extends TimerTask {

    User user;

    public TimedTaskBiHourly(User user) {
        this.user = user;
    }

    public void run()  {
        user.sendCapsule();
    }
}

