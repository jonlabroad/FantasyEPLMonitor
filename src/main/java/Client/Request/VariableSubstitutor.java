package Client.Request;

public class VariableSubstitutor {
    private static final String ENTRY_ID_STRING = "ENTRY_ID";
    private static final String EVENT_ID_STRING = "EVENT_ID";
    private static final String LEAGUE_ID_STRING = "LEAGUE_ID";

    public static String Substitute(String inputString, int entryId, int eventId) {
        String withEntry = SubstituteEntryId(inputString, entryId);
        return SubstituteEventId(withEntry, eventId);
    }

    public static String SubstituteLeague(String inputString, int leagueId) {
        return Substitute(inputString, LEAGUE_ID_STRING, leagueId);
    }

    private static String SubstituteEventId(String inputString, int entryId) {
        return Substitute(inputString, EVENT_ID_STRING, entryId);
    }

    private static String SubstituteEntryId(String inputString, int entryId) {
        return Substitute(inputString, ENTRY_ID_STRING, entryId);
    }

    private static String Substitute(String inputString, String name, Integer value) {
        String toReplace = String.format("{%s}", name);
        return inputString.replace(toReplace, value.toString());
    }
}
