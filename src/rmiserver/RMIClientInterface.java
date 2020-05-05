/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIClientInterface extends Remote {
	ArrayList mysearch(String username) throws RemoteException;
	ArrayList checkAdmins() throws RemoteException;
	ArrayList giveAdmin(String username) throws RemoteException;
	ArrayList indexNewUrl(String username, String word) throws RemoteException;
	ArrayList login(String username, String password) throws RemoteException;
	ArrayList search(String username, String password, String word) throws RemoteException;
	String register(String username, String password) throws RemoteException;
	String notification(String username) throws RemoteException;
	String getUsername()throws RemoteException;
	void checkState(String user) throws RemoteException;
	void setNotification(String username, String request) throws RemoteException;
	ArrayList info() throws RemoteException;
	ArrayList getindexesPages(String url) throws RemoteException;
	ArrayList detetaLingua(ArrayList data) throws IOException;

}
