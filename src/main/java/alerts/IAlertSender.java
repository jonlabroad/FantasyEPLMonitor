package alerts;

import data.ScoreNotification;

public interface IAlertSender {
    void SendAlert(int teamId, ScoreNotification scoreChange);
}
