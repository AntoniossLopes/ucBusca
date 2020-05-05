/**
 * Raul Barbosa 2014-11-07
 */
package model;

import rmiserver.RMIClientInterface;
import rmiserver.RMIServerInterface;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/ws")

public class HeyBean extends UnicastRemoteObject implements RMIClientInterface {
    private RMIServerInterface server;
    private static String name = null; // username and password supplied by the user
    private String myUsername = null;
    private String password;
    private String message;
    private Session session;
    private String IP = "rmi://194.210.172.214";
    private static final CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<String> users = new CopyOnWriteArrayList<>();
    private int port = 8000;


    public HeyBean() throws RemoteException {
        try {
            this.server = (RMIServerInterface) LocateRegistry.getRegistry(port).lookup(IP + ":" + port);
            this.server.subscribe(this);
            System.out.println(this.server.connected());
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace(); // what happens *after* we reach this line?
        }
    }

    public String getUsername() throws RemoteException {
        return name;
    }

    public ArrayList traduz(ArrayList<String> data) throws IOException {
        return this.server.translate(data);
    }

    public void logout(String username) throws RemoteException {
        this.server.getData(username,null,"logout",null);
    }

    public ArrayList loginFB(String code) throws RemoteException {
        return this.server.LoginFacebook(code);
    }

    public String getLoginAuth() throws RemoteException {
        return this.server.LoginAuthorizationUrl();
    }

    public ArrayList mysearch(String username) throws RemoteException {
        return (ArrayList) this.server.getData(username, null, "mysearch", null);
    }

    public ArrayList checkAdmins() throws RemoteException {
        return (ArrayList) this.server.getData(null, null, "admin", null);
    }

    public ArrayList giveAdmin(String username) throws RemoteException {
        if (this.server.getData(username, null, "giveadmin", null).get(0).equals("User selected is now an admin.")) {
            for (int i = 0; i < users.size(); i++) {
                System.out.println(users.get(i) + "   " + username);
                if (users.get(i).equals(username))
                    sendMessage(username, sessions.get(i));
            }
        }
        return (ArrayList) this.server.getData(username, null, "giveadmin", null);
    }

    public ArrayList info() throws RemoteException {
        return (ArrayList) this.server.getData(null,null,"info",null);
    }

    public ArrayList getindexesPages(String url) throws RemoteException{
        return (ArrayList) this.server.getData(name,null,"connections",url);
    }

    public ArrayList indexNewUrl(String username, String word) throws RemoteException {
        return (ArrayList) this.server.getData(username, null, "newurl", word);
    }

    public ArrayList login(String username, String password) throws RemoteException {
        name = username;
        this.myUsername = name;
        return (ArrayList) this.server.getData(username, password, "login", null);
    }

    public ArrayList search(String username, String password, String word) throws RemoteException {
        return (ArrayList) this.server.getData(username, password, "search", word);
    }

    public String register(String username, String password) throws RemoteException {
        if (!username.equals("") && !password.equals("")) {
            this.server.getData(username, password, "register", null);
            return "Done";
        }
        return "Error";
    }

    public String notification(String username) throws RemoteException {
        return (String) this.server.getData(username, null, "notification", null).get(1);
    }

    public void setNotification(String username, String request) throws RemoteException {
        this.server.getData(username, null, "setnotification", request);
    }

    public void checkState(String user) throws RemoteException {
        this.server.getData(user, null, "islogged", null);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public ArrayList connectFb(String code,String username) throws RemoteException {
        return this.server.connectFacebook(code,username);
    }

    public String getConnectAuth() throws RemoteException {
        return this.server.ConnectAuthorizationUrl();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        sessions.add(session);
        for (int i = 0; i < sessions.size(); i++) {
            if (!sessions.get(i).isOpen()) {
                users.remove(i);
                sessions.remove(i);
            }
        }

    }

    @OnClose
    public void end(Session session) {

    }

    @OnMessage
    public void receiveMessage(String username) {
        name = username;
        this.myUsername = name;
        users.add(username);
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    private void sendMessage(String username, Session session) {
        try {
            session.getBasicRemote().sendText(username);
        } catch (IOException e) {
            // clean up once the WebSocket connection is closed
            try {
                session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public ArrayList detetaLingua(ArrayList data) throws IOException {
        return (ArrayList) this.server.detetaLingua(data);
    }


}
