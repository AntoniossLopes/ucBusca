import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class User  onde sera guardado a info de cada user, e os estados de login e logout, e assim como se e admin ou nao
 */

public class User implements Serializable {
    private int id;
    private String nome;
    private String password;
    private boolean Admin;
    private boolean state;
    private String fbToken;
    private String fbID;
    private ArrayList<String> searchHistory = new ArrayList<>();
    private String notification;


    public User(int id, String nome, String password) {
        if (id == 0) {
            this.id = id;
            this.nome = nome;
            this.password = password;
            this.fbToken = null;
            this.fbID = null;
            Admin = true;
        } else {
            this.id = id;
            this.nome = nome;
            this.password = password;
            this.fbToken = null;
            this.fbID = null;
            Admin = false;
        }
    }

    public void login() {
        this.state = true;
    }

    public void logout() {
        this.state = false;
    }

    public int getId() {
        return id;
    }

    public void setFbID(String fbId){this.fbID = fbId;}

    public void setFbToken(String fbToken){this.fbToken = fbToken;}

    public String getFbID() {
        return fbID;
    }

    public String getFbToken() {
        return fbToken;
    }


    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public boolean isAdmin() {
        return Admin;
    }

    public void makeAdmin() {
        Admin = true;
    }

    public void insertSearch(String search) {
        searchHistory.add(search);
    }

    public ArrayList<String> getSearchHistory() {
        return searchHistory;
    }
}
