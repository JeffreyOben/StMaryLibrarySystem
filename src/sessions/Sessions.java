package sessions;

import models.User;

public class Sessions {

    private static User currentUser = null;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static String getRole() {
        return (currentUser != null) ? currentUser.getRole() : null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}