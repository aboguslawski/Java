public class Account {
    private String username, password, type;

    public Account(String username, String password, String type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean compare(String username, String password){
        boolean isUser = username.equals(this.username);
        boolean isPswd = password.equals(this.password);

        return isUser && isPswd;
    }
}
