package datafetch;

public final class UserFetch {
    private CredentialsFetch credentials;

    public UserFetch() {
    }

    public CredentialsFetch getCredentials() {
        return credentials;
    }

    public void setCredentials(final CredentialsFetch newCredentials) {
        credentials = newCredentials;
    }
}
