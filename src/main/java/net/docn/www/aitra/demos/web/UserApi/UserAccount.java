package net.docn.www.aitra.demos.web.UserApi;


public class UserAccount {
    private String userEmail;
    private String passwordhash;

    @Override
    public String toString() {
        return "UserAccount{" +
                "userEmail='" + userEmail + '\'' +
                ", password='" + passwordhash + '\'' +
                '}';
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPasswordHash() {
        return passwordhash;
    }

    public void setPasswordHash(String passwordhash) {
        this.passwordhash = passwordhash;
    }
}