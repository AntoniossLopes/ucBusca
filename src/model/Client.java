package model;

import rmiserver.RMIServerInterface;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client{
    private final int portServer = 8000;
    private RMIServerInterface server;
    private String IP;
    private String user, password, str;
    boolean value;
    Scanner sc;

    private Client() throws RemoteException {
        this.sc = new Scanner(System.in);
        System.out.println("Insert the IP of the machine: ");
        System.out.print(">>> ");
        IP = "192.168.1.5";
        while (true) {
            tryOrRetry(this.portServer);
        }
    }

    public void tryOrRetry(int port) throws RemoteException {
        int count = 0;
        while (count != 5) {
            try {
                //Registry reg = LocateRegistry.getRegistry("192.168.x.x", 1099);
                this.server = (RMIServerInterface) LocateRegistry.getRegistry(port).lookup(IP +":" + port);
                System.out.println(this.server.connected());
                mainPage();
            } catch (NotBoundException | UnmarshalException | ConnectException ex) {
                count++;
            }
        }
    }

    public boolean checkConnection() throws RemoteException {
        try {
            this.server = (RMIServerInterface) LocateRegistry.getRegistry(this.portServer).lookup("localhost:" + this.portServer);
            if (this.server != null) {
                return true;
            }
            return false;
        } catch (NotBoundException | UnmarshalException | ConnectException ex) {
            return false;
        }
    }

    public void login() throws RemoteException {
        System.out.print("Username: ");
        this.user = sc.nextLine();
        this.user = this.user.trim();
        System.out.print("Password: ");
        this.password = sc.nextLine();
        this.password = this.password.trim();
        if (checkConnection()) {
            if (!this.server.isLoggedIn(this.user)) {
                if (!this.user.equals("") && !this.password.equals("")) {
                    this.str = (String) this.server.getData(this.user, this.password, "login", null).get(1);
                    if (this.str.equals("Not found") || this.str.equals("Wrong password")) {
                        System.out.println("Something went wrong. Please try again!");
                        login();
                    } else {
                        value = true;
                        menu(value);
                    }
                }
            } else {
                System.out.println("User already logged in.");
                login();
            }
        } else {
            System.out.println("Something went wrong. Please try again!");
            login();
        }
    }

    public void giveAdmin() throws RemoteException {
        ArrayList dataArray;
        System.out.println("Type the username that you want to make admin:");
        System.out.print(">>> ");
        String word = sc.nextLine();
        word = word.trim();
        if (checkConnection() && !word.equals("")) {
            dataArray = this.server.getData(word, null, "giveadmin", null);
            System.out.println(dataArray.get(0));
        } else {
            System.out.println("Something went wrong. Please try again!");
            giveAdmin();
        }
        menu(value);
    }

    public void indexNewUrl() throws RemoteException {
        ArrayList dataArray;
        System.out.println("Type your url: ");
        System.out.print(">>> ");
        String word = sc.nextLine();
        word = word.trim();
        if (checkConnection() && !word.equals("")) {
            dataArray = this.server.getData(null, null, "newurl", word);
            System.out.println(dataArray.get(1));
        } else {
            System.out.println("Something went wrong. Please try again!");
            indexNewUrl();
        }
        menu(value);
    }

    public void connections() throws RemoteException {
        ArrayList dataArray;
        System.out.println("Type the url of the website: ");
        System.out.print(">>> ");
        String word = sc.nextLine();
        word = word.trim();
        if (checkConnection() && !word.equals("")) {
            dataArray = this.server.getData(this.user, null, "connections", word);
            if (!dataArray.isEmpty()) {
                for (Object o : dataArray) {
                    System.out.println(o);
                }
            } else {
                System.out.println("Nothing found!");
            }
            menu(value);
        } else {
            System.out.println("Something went wrong. Please try again!");
            connections();
        }
    }

    public void search() throws RemoteException {
        ArrayList dataArray;
        System.out.print("Type what you want to search: ");
        String word = sc.nextLine();
        if (checkConnection() && !word.equals("")) {
            dataArray = this.server.getData(this.user, this.password, "search", word);
            if (!dataArray.isEmpty()) {
                for (Object o : dataArray) {
                    System.out.println(o);
                }
            } else {
                System.out.println("Nothing found!");
            }
            menu(value);
        } else {
            System.out.println("Something went wrong. Please try again!");
            search();
        }
    }

    public void register() throws RemoteException {
        System.out.print("Username: ");
        this.user = sc.nextLine();
        System.out.print("Password: ");
        this.password = sc.nextLine();
        this.user = this.user.trim();
        this.password = this.password.trim();
        if (checkConnection() && !this.user.equals("") && !this.password.equals("")) {
            System.out.println("Successfully registered!");
            this.server.getData(this.user, this.password, "register", null);

        } else {
            System.out.println("Something went wrong. Please try again!");
            register();
        }
    }

    public void printAdmins(ArrayList<String> dataArray) throws RemoteException {
        if (checkConnection()) {
            if (dataArray == null) {
                System.out.println("\nNo response from multicast server. Please try again");
            } else {
                System.out.println("\nUsers with admin rights: ");
                for (int i = 0; i < dataArray.size(); i++) {
                    System.out.println(dataArray.get(i));
                }
                menu(value);
            }
        } else {
            System.out.println("Something went wrong. Please try again!");
            menu(value);
        }
        menu(value);
    }

    public void info(ArrayList<String> dataArray) throws RemoteException {
        ArrayList<String> urls_aux = new ArrayList<>();
        ArrayList<String> server_aux = new ArrayList<>();
        ArrayList<String> words_aux = new ArrayList<>();
        ArrayList<String> serverports_aux = new ArrayList<>();
        if (checkConnection()) {
            if (dataArray == null) {
                System.out.println("\nNo response from multicast server. Please try again");
            } else {
                int k = 0;
                while(k < dataArray.size())
                {
                    if(dataArray.get(k).equals("URLS"))
                    {
                        int j = 0;
                        k++;
                        int numero = Integer.parseInt((String)dataArray.get(k));
                        k++;
                        while(j < numero)
                        {
                            urls_aux.add("URL:"+(String)dataArray.get(k));
                            k++;
                            j++;
                        }

                    }
                    if(dataArray.get(k).equals("WORDS"))
                    {
                        System.out.println(dataArray.get(k));
                        int j = 0;
                        k++;
                        int numero = Integer.parseInt((String)dataArray.get(k));
                        k++;
                        while(j < numero)
                        {
                            words_aux.add("WORDS:"+(String)dataArray.get(k));
                            k++;
                            j++;
                        }

                    }
                    if(dataArray.get(k).equals("SERVERS")) {
                        int j = 0;
                        k++;
                        int numero;
                        numero = Integer.parseInt((String)dataArray.get(k));
                        k++;
                        while (j < numero) {
                            server_aux.add("SERVER ID:"+(String)dataArray.get(k));
                            k++;
                            j++;

                        }
                    }
                    if(dataArray.get(k).equals("PORTS"))
                    {
                        k++;
                        serverports_aux.add("Server PORT:"+(String) dataArray.get(k));
                        k++;
                    }
                }

                for(int i = 0 ; i < urls_aux.size();i++)
                {
                    System.out.println(urls_aux.get(i));
                }
                for(int i = 0 ; i < words_aux.size();i++)
                {
                    System.out.println(words_aux.get(i));
                }
                for(int i = 0 ; i < server_aux.size();i++)
                {
                    System.out.println(server_aux.get(i));
                }
                for(int i = 0 ; i < serverports_aux.size();i++)
                {
                    System.out.println(serverports_aux.get(i));
                }

            }
        } else {
            System.out.println("Something went wrong. Please try again!");
            menu(value);
        }
        menu(value);
    }

    public void mainPage() throws RemoteException {
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.println("3 - Skip");
        System.out.println("0 - Exit server.");
        System.out.print(">>> ");
        String option = sc.nextLine();
        if (checkConnection()) {
            switch (option) {
                case "0":
                    exit(0);
                    break;
                case "1":
                    System.out.println("Please enter your username and password.");
                    login();
                    break;
                case "2":
                    register();
                    System.out.println("Please login with your new username and password.");
                    login();
                    break;
                case "3":
                    value = false;
                    menu(value);
                    break;
                default:
                    mainPage();
            }
        } else {
            System.out.println("Something went wrong. Please try again!");
            mainPage();
        }
    }

    public void menu(boolean value) throws RemoteException {
        ArrayList data;
        String option;
        System.out.println("\n\tMENU\n1 - Search");
        if (checkConnection()) {
            if (value) {
                if (this.str.equals("true")) {
                    System.out.println("4 - Index new URL");
                    System.out.println("5 - Indexed pages in a URL");
                    System.out.println("6 - Check admins");
                    System.out.println("7 - Check history");
                    System.out.println("8 - Check most indexed pages");
                    System.out.println("9 - Give admin permissions to a user");
                    System.out.println("0 - Logout");
                    System.out.print(">>> ");
                    option = sc.nextLine();
                    switch (option) {
                        case "1":
                            search();
                            break;
                        case "4":
                            indexNewUrl();
                            break;
                        case "5":
                            connections();
                            break;
                        case "6":
                            data = this.server.getData(null, null, "admin", null);
                            printAdmins(data);
                            break;
                        case "7":
                            data = this.server.getData(this.user, null, "mysearch", null);
                            System.out.println("\nSearches: ");
                            for (int i = 0; i < data.size(); i++)
                                System.out.println((i + 1) + ": " + data.get(i));
                            menu(value);
                            break;
                        case "8":
                            data = this.server.getData(null, null, "info", null);
                            info(data);
                            break;
                        case "9":
                            giveAdmin();
                            break;
                        case "0":
                            this.server.getData(this.user, null, "logout", null);
                            mainPage();
                        default:
                            System.out.println("Wrong command! Try again.");
                            menu(value);
                    }
                } else {
                    System.out.println("5 - Indexed pages in a URL");
                    System.out.println("7 - Check history");
                    System.out.println("0 - Logout");
                    System.out.print(">>> ");
                    option = sc.nextLine();
                    switch (option) {
                        case "1":
                            search();
                            break;

                        case "5":
                            connections();
                            break;

                        case "7":
                            data = this.server.getData(this.user, null, "mysearch", null);
                            System.out.println("\nSearches: ");
                            for (int i = 0; i < data.size(); i++)
                                System.out.println((i + 1) + ": " + data.get(i));
                            menu(value);
                            break;
                        case "0":
                            this.server.getData(this.user, null, "logout", null);
                            mainPage();
                        default:
                            System.out.println("Wrong command! Try again.");
                            menu(value);
                    }
                }
            } else {
                System.out.print(">>> ");
                option = sc.nextLine();
                switch (option) {
                    case "1":
                        search();
                    default:
                        System.out.println("Wrong command! Try again.");
                        menu(value);
                }
            }
        } else {
            System.out.println("Something went wrong. Please try again!");
            menu(value);
        }
    }

    public static void main(String args[]) {
        System.getProperties().put("java.security.policy", "policy.all");

        try {
            new Client();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}