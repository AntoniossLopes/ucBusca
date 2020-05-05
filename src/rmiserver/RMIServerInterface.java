/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIServerInterface extends Remote {
	String connected() throws RemoteException;
	ArrayList getData(String user, String password, String type, String request) throws RemoteException;
	void subscribe(RMIClientInterface user) throws RemoteException;
	String LoginAuthorizationUrl() throws RemoteException;
	String ConnectAuthorizationUrl() throws RemoteException;
	ArrayList<String> LoginFacebook(String code) throws RemoteException;
	ArrayList translate(ArrayList<String> data) throws IOException;
	ArrayList<String> detetaLingua(ArrayList<String> data) throws IOException;
	ArrayList<String> connectFacebook(String code,String username) throws RemoteException;
	boolean isLoggedIn(String user) throws RemoteException;

}
