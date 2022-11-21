package application;

public class User {

    int userId;

    String password;

    int gameCount;

    int winCount;

    public User(int userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public int getGameCount() {
        return gameCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }
}
