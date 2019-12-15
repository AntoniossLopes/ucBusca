/**
 * Raul Barbosa 2014-11-07
 */
package model;

import rmiserver.RMIServerInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class HeyBean {
    private RMIServerInterface server;
    private String username; // username and password supplied by the user
    private String password;
    private String message;
    private String IP = "rmi://172.20.10.9";
    private int port = 8000;


    public HeyBean() {
        try {
            server =  (RMIServerInterface) LocateRegistry.getRegistry(port).lookup(IP +":" + port);
            System.out.println(this.server.connected());
        }
        catch(NotBoundException| RemoteException e) {
            e.printStackTrace(); // what happens *after* we reach this line?
        }
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
        return (ArrayList) this.server.getData(username,null,"mysearch",null);
    }

    public ArrayList checkAdmins() throws RemoteException{
        return (ArrayList) this.server.getData(null,null,"admin",null);
    }

    public ArrayList giveAdmin(String word) throws RemoteException {
        return (ArrayList) this.server.getData(word,null,"giveadmin",null);
    }

    public ArrayList info() throws RemoteException {
        return (ArrayList) this.server.getData(null,null,"info",null);
    }

    public ArrayList getindexesPages(String url) throws RemoteException{
        return (ArrayList) this.server.getData(username,null,"connections",url);
    }

    public ArrayList indexNewUrl(String username, String word) throws RemoteException {
        return (ArrayList) this.server.getData(username,null,"newurl",word);
    }

    public ArrayList login(String username, String password) throws RemoteException {
        return (ArrayList) this.server.getData(username, password, "login", null);
    }

    public ArrayList search(String username,String password,String word) throws RemoteException {
        return (ArrayList) this.server.getData(username,password,"search",word);
    }

    public String register(String username, String password) throws RemoteException {
        if(!username.equals("") && !password.equals("")){
            this.server.getData(username,password,"register",null);
            return "Done";
        }
        return "Error";
    }

    public ArrayList connectFb(String code,String username) throws RemoteException {
        return this.server.connectFacebook(code,username);
    }

    public String getConnectAuth() throws RemoteException {
        return this.server.ConnectAuthorizationUrl();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public ArrayList detetaLingua(ArrayList data) throws IOException {
        return (ArrayList) this.server.detetaLingua(data);
    }

}
