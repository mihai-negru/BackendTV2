package datafetch;

import java.util.ArrayList;

public final class ContainsFilterFetch {
    private ArrayList<String> actors;
    private ArrayList<String> genre;

    public ContainsFilterFetch() {

    }

    public ArrayList<String> getActors() {
        return actors;
    }

    public void setActors(final ArrayList<String> newActors) {
        actors = newActors;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(final ArrayList<String> newGenre) {
        genre = newGenre;
    }
}
