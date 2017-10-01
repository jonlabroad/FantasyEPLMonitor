package Data.EPLAPI;

public class FootballerScoreDetailElement {
    public ScoreExplain minutes = new ScoreExplain();
    public ScoreExplain goals_scored = new ScoreExplain();
    public ScoreExplain bonus = new ScoreExplain();
    public ScoreExplain clean_sheets = new ScoreExplain();
    public ScoreExplain assists = new ScoreExplain();

    public FootballerScoreDetailElement Compare(FootballerScoreDetailElement other) {
        if (other == null) {
            return this;
        }
        FootballerScoreDetailElement diff = new FootballerScoreDetailElement();
        diff.minutes = minutes.diff(other.minutes);
        diff.goals_scored = goals_scored.diff(other.goals_scored);
        diff.bonus = bonus.diff(other.bonus);
        diff.clean_sheets = clean_sheets.diff(other.clean_sheets);
        diff.assists = assists.diff(other.assists);
        return diff;
    }
}
