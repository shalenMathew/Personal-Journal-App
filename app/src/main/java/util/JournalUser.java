package util;

import android.app.Application;

public class JournalUser extends Application {

private String username;
private String userId;
private static JournalUser instance;

    public JournalUser() {
    }

public static JournalUser getInstance(){

        if(instance==null){
            instance = new JournalUser();
        }
        return instance;

}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
