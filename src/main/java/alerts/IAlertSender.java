package alerts;

public interface IAlertSender {
    void sendAlert(int teamId, String title, String subtitle);
}
