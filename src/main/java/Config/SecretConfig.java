package Config;

import java.util.HashMap;

public class SecretConfig {
    public HashMap<Integer, User> users = new HashMap<Integer, User>();
    public String platformApplicationArn = "";

    public void AddUser(int teamId, String teamName, String phoneNumber) {
        User user = new User();
        user.teamId = teamId;
        user.teamName = teamName;
        user.alertPhoneNumber = phoneNumber;
        users.put(teamId, user);
    }

    public User GetUserByTeamId(int teamId) {
        return users.get(teamId);
    }
}
