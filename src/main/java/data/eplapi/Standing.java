package data.eplapi;

public class Standing {
    public int id;
    public String entry_name;
    public String player_name;
    public String movement;
    public boolean own_entry;
    public int rank;
    public int last_rank;
    public int rank_sort;
    public int total;
    public int matches_played;
    public int matches_won;
    public int matches_drawn;
    public int matches_lost;
    public int points_for;
    public int points_against;
    public int points_total;
    public int division;
    public int entry;

    public Standing()
    {

    }

    public Standing(Standing other) {
        this.id = other.id;
        this.entry_name = other.entry_name;
        this.player_name = other.player_name;
        this.movement = other.movement;
        this.own_entry = other.own_entry;
        this.rank = other.rank;
        this.last_rank = other.last_rank;
        this.rank_sort = other.rank_sort;
        this.total = other.total;
        this.matches_played = other.matches_played;
        this.matches_won = other.matches_won;
        this.matches_drawn = other.matches_drawn;
        this.matches_lost = other.matches_lost;
        this.points_for = other.points_for;
        this.points_against = other.points_against;
        this.points_total = other.points_total;
        this.division = other.division;
        this.entry = other.entry;
    }
}
