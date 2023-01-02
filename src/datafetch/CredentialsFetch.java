package datafetch;

public final class CredentialsFetch {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private int balance;

    public CredentialsFetch() {

    }

    public String getName() {
        return name;
    }

    public void setName(final String newName) {
        name = newName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String newPassword) {
        password = newPassword;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String newAccountType) {
        accountType = newAccountType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String newCountry) {
        country = newCountry;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(final int newBalance) {
        balance = newBalance;
    }
}
