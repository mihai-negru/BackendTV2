package datafetch;

import java.util.ArrayList;

public final class MovieFetch {
    private String name;
    private int year;
    private int duration;
    private ArrayList<String> genres;
    private ArrayList<String> actors;
    private ArrayList<String> countriesBanned;

    public MovieFetch() {

    }

    public String getName() {
        return name;
    }

    public void setName(final String newName) {
        name = newName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(final int newYear) {
        year = newYear;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(final int newDuration) {
        duration = newDuration;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(final ArrayList<String> newGenres) {
        genres = newGenres;
    }

    public ArrayList<String> getActors() {
        return actors;
    }

    public void setActors(final ArrayList<String> newActors) {
        actors = newActors;
    }

    public ArrayList<String> getCountriesBanned() {
        return countriesBanned;
    }

    public void setCountriesBanned(final ArrayList<String> newCountriesBanned) {
        countriesBanned = newCountriesBanned;
    }
}
