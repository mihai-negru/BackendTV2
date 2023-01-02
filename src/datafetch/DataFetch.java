package datafetch;

import java.util.ArrayList;

public final class DataFetch {
    private ArrayList<UserFetch> users;
    private ArrayList<MovieFetch> movies;
    private ArrayList<ActionFetch> actions;

    public DataFetch() {

    }

    public ArrayList<UserFetch> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<UserFetch> newUsers) {
        users = newUsers;
    }

    public ArrayList<MovieFetch> getMovies() {
        return movies;
    }

    public void setMovies(final ArrayList<MovieFetch> newMovies) {
        movies = newMovies;
    }

    public ArrayList<ActionFetch> getActions() {
        return actions;
    }

    public void setActions(final ArrayList<ActionFetch> newActions) {
        actions = newActions;
    }
}
