package config;

public class EndpointUserData {
    public String uniqueUserId;
    public String firebaseId;

    public EndpointUserData(String raw) {
        String[] data = raw.split(":");
        uniqueUserId = data[0];
        firebaseId = data[1];
    }

    @Override
    public String toString() {
        return String.format("%s:%s", uniqueUserId, firebaseId);
    }
}
