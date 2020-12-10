import java.util.TimerTask;

public class TimedTaskBiHourly extends TimerTask {

    User user;

    public TimedTaskBiHourly(User user)
    {
        this.user = user;
    }

    public void run()  {
        System.out.println("Sending half hour confirmation capsule");
        user.sendCapsule();
    }
}

