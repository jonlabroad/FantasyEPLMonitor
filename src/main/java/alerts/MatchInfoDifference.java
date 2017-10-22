package alerts;

import java.util.ArrayList;

public class MatchInfoDifference {
    public ArrayList<MatchInfoDifferenceType> types = new ArrayList<MatchInfoDifferenceType>();
    public ArrayList<String> additionalText = new ArrayList<String>();

    public void AddDifference(MatchInfoDifferenceType type, String extraText) {
        types.add(type);
        additionalText.add(extraText);
    }

}
