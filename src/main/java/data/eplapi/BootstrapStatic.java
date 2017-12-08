package data.eplapi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BootstrapStatic {
    @SerializedName("current-event")
    public int currentEvent;
    public List<Event> events = new ArrayList<>();

    // There's a lot more to this but it's not important, yet
}
