package Alerts;

import Data.ScoreNotification;

public interface IAlertSender {
    void SendAlert(int teamId, ScoreNotification scoreChange);
}
