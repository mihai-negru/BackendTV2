package datafetch;

public final class ActionFetch {
    private String type;
    private String page;
    private String movie;
    private String feature;
    private CredentialsFetch credentials;
    private String startsWith;
    private FilterActionFetch filters;
    private int count;
    private int rate;

    public ActionFetch() {

    }

    public String getType() {
        return type;
    }

    public void setType(final String newType) {
        type = newType;
    }

    public String getPage() {
        return page;
    }

    public void setPage(final String newPage) {
        page = newPage;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(final String newMovie) {
        movie = newMovie;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(final String newFeature) {
        feature = newFeature;
    }

    public CredentialsFetch getCredentials() {
        return credentials;
    }

    public void setCredentials(final CredentialsFetch newCredentials) {
        credentials = newCredentials;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(final String newStartsWith) {
        startsWith = newStartsWith;
    }

    public FilterActionFetch getFilters() {
        return filters;
    }

    public void setFilters(final FilterActionFetch newFilters) {
        filters = newFilters;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int newCount) {
        count = newCount;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(final int newRate) {
        rate = newRate;
    }
}
