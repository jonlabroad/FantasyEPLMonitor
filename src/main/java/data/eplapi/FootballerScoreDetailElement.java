package data.eplapi;

public class FootballerScoreDetailElement {
    public ScoreExplain minutes = new ScoreExplain();
    public ScoreExplain goals_scored = new ScoreExplain();
    public ScoreExplain bonus = new ScoreExplain();
    public ScoreExplain clean_sheets = new ScoreExplain();
    public ScoreExplain assists = new ScoreExplain();
    public ScoreExplain yellow_cards = new ScoreExplain();
    public ScoreExplain red_cards = new ScoreExplain();
    public ScoreExplain penalties_missed = new ScoreExplain();
    public ScoreExplain goals_conceded = new ScoreExplain();
    public ScoreExplain saves = new ScoreExplain();
    public ScoreExplain penalties_saved = new ScoreExplain();
    public ScoreExplain own_goals = new ScoreExplain();


    public FootballerScoreDetailElement compare(FootballerScoreDetailElement other) {
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

    public void set(FootballerScoreDetailElement other) {
        minutes = other.minutes;
        goals_scored = other.goals_scored;
        bonus = other.bonus;
        clean_sheets = other.clean_sheets;
        assists = other.assists;
        yellow_cards = other.yellow_cards;
        red_cards = other.red_cards;
        penalties_missed = other.penalties_missed;
        goals_conceded = other.goals_conceded;
        saves = other.saves;
        penalties_saved = other.penalties_saved;
        own_goals = other.own_goals;
    }
}
