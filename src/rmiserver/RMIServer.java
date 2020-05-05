/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;


import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
    private final int portServer = 8000;
    private final int portMulticast = 4000;
    private InetAddress address;
    private MulticastSocket socket;
    private boolean value;
    ArrayList queue;
    ArrayList<RMIClientInterface> loggedUsers;
    private final String apiKey = "606045263473186";
    private final String apiSecret = "85229599decbd2a8fee846cb46c20446";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";


    public RMIServer() throws IOException {
        super();
        socket = new MulticastSocket(portServer);
        address = InetAddress.getByName("224.0.224.0");
        socket.joinGroup(address);
        loggedUsers = new ArrayList();
    }

    public static void main(String args[]) throws IOException {
        Registry registry;
        Scanner sc = new Scanner(System.in);
        String IP ;
		System.out.println("Insert the IP of the machine: ");
		System.out.print(">>> ");
		IP = sc.nextLine();

        RMIServer server = new RMIServer();
        try {
            registry = LocateRegistry.getRegistry(server.portServer);
            while (registry.lookup(IP + ":" + server.portServer) != null) {
                System.out.println("Waiting for Backup Server to crash.");
            }
        } catch (RemoteException re) {
            registry = LocateRegistry.createRegistry(server.portServer);
            registry.rebind(IP + ":" + server.portServer, server);
            System.out.println("Server ready.");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(RMIClientInterface user) {
        if (!this.loggedUsers.contains(user)) {
            this.loggedUsers.add(user);
        }
    }

    public boolean isLoggedIn(String user) {
        boolean value = false;
        if (!loggedUsers.isEmpty()) {
            for (Object logged : loggedUsers) {
                if (user.equals(logged)) {
                    value = true;
                } else {
                    value = false;
                }
            }
        }
        return value;
    }

    public String connected() {
        return "\nWelcome to ucBusca!";
    }

    public String sendData(String data) {
        boolean clean = false;
        byte[] bufferSender, bufferReceiver;
        String dataReceived = null;
        DatagramPacket packetSender, packetReceiver;
        bufferSender = data.getBytes();
        packetSender = new DatagramPacket(bufferSender, bufferSender.length, address, portMulticast);
        if (!value) {
            try {
                bufferReceiver = new byte[99999];
                packetReceiver = new DatagramPacket(bufferReceiver, bufferReceiver.length);
                socket.send(packetSender);
                this.queue.remove(data);
                socket.setSoTimeout(30000);
                socket.receive(packetReceiver);
                socket.setSoTimeout(100);
                while (!clean) {
                    try {
                        dataReceived = new String(packetReceiver.getData(), 0, packetReceiver.getLength());
                        socket.receive(packetReceiver);
                        System.out.println(dataReceived);
                    } catch (IOException x) {
                        clean = true;
                    }
                }
                return dataReceived;
            } catch (IOException e) {
                System.out.println("Failed to send request. Multicast servers are down!");
            }
        } else {
            try {
                socket.send(packetSender);
            } catch (IOException e) {
                System.out.println("Failed to send request. Multicast servers are down!");
            }
        }
        return null;
    }


    public ArrayList<String> getData(String user, String password, String type, String request) {
        String dataSent, dataReceived;
        ArrayList<String> newData = null;
        queue = new ArrayList();
        switch (type) {
            case "login":  // Done
                dataSent = "type|login;user|" + user + ";password|" + password + ";";
                System.out.println("SENT: " + dataSent);
                value = false;
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                if (newData.get(1).equals("Not found") || newData.get(1).equals("Wrong password")) {
                    System.out.println("Login failed");
                }
                break;

            case "register": // Done
                dataSent = "type|register;user|" + user + ";password|" + password + ";";
                System.out.println("SENT: " + dataSent);
                this.queue.add(dataSent);
                sendData(dataSent);
                break;

            case "newurl": // Done
                dataSent = "type|newurl;url|" + request + ";";
                System.out.println("SENT: " + dataSent);
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                break;

            case "search": // Done
                newData = dataHandler(request, type);
                dataSent = "type|search;";
                if ((user != null))
                    dataSent = dataSent.concat("user|" + user + ";");
                for (int i = 0; i < newData.size(); i++) {
                    dataSent = dataSent.concat("word|" + newData.get(i) + ";");
                }
                newData.clear();
                System.out.println("SENT: " + dataSent);
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, "handle");
                break;

            case "admin": // Done
                dataSent = "type|admin;";
                System.out.println("SENT: " + dataSent);
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                break;

            case "giveadmin": // Done
                dataSent = "type|giveadmin;user|" + user + ";state|";
                System.out.println("SENT: " + dataSent);
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                try {
                    int x = 0;
                    try {
                        while (!user.equals(this.loggedUsers.get(x).getUsername())) {
                            x++;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        this.loggedUsers.get(0).setNotification(user, "Your permissions were changed while you were away");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case "logout": // Done
                dataSent = "type|logout;user|" + user + ";";
                System.out.println("SENT: " + dataSent);
                value = true;
                this.queue.add(dataSent);
                sendData(dataSent);
                break;

            case "mysearch": // Done
                dataSent = "type|mysearch;user|" + user + ";";
                this.queue.add(dataSent);
                System.out.println("SENT: " + dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                break;

            case "connections":
                dataSent = "type|connections;user|" + user + ";" + "page|" + request + ";";
                System.out.println("SENT: " + dataSent);
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                break;

            case "info":
                dataSent = "type|info;";
                System.out.println("SENT: " + dataSent);
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                break;
            case "notification":
                dataSent = "type|notification;user|" + user + ";";
                System.out.println("SENT: " + dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                break;
            case "islogged":
                dataSent = "type|islogged;user|" + user + ";status|true;";
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
            case "setnotification":
                dataSent = "type|setnotification;user|" + user + ";request|" + request + ";";
                System.out.println("SENT: " + dataSent);
                sendData(dataSent);
                break;
            case "searchFb":
                dataSent = "type|searchidfb;id|"+request;
                System.out.println("SENT:"+dataSent);
                this.queue.add(dataSent);
                dataReceived = sendData(dataSent);
                newData = dataHandler(dataReceived, type);
                break;

            case "associate":
                String[] aux = request.split(" ");
                dataSent = "type|associate;user|"+user+";idfb|"+aux[0] +";token|"+aux[1]+";";
                this.queue.add(dataSent);
                sendData(dataSent);
                break;

        }
        return newData;
    }

    public ArrayList<String> dataHandler(String data, String type) {
        ArrayList<String> newDataArray = new ArrayList<>();
        //System.out.println(data);
        if (data == null) {
            return null;
        } else {
            if (type.equals("admin")) {
                String[] dataArray = data.split(";");
                for (int i = 0; i < dataArray.length / 2; i++) {
                    String[] nome = dataArray[2 * i].split("\\|");
                    String[] status = dataArray[1 + 2 * i].split("\\|");
                    if (status[1].equals("true"))
                        newDataArray.add("Admin user " + (i + 1) + ":" + nome[1]);
                    else
                        newDataArray.add("User " + (i + 1) + ":" + nome[1]);
                }
            }
            if (type.equals("newurl") || type.equals("notification") || type.equals("islogged")) {
                String[] dataArray = data.split(";");
                for (int i = 0; i < dataArray.length / 2; i++) {
                    String[] nome = dataArray[2 * i].split("\\|");
                    String[] status = dataArray[1 + 2 * i].split("\\|");
                    newDataArray.add(nome[1]);
                    newDataArray.add(status[1]);
                }
            }
            if(type.equals("connections")) {
                String[] dataArray = data.split(";");
                for(int i = 0; i < dataArray.length;i++)
                {
                    String[] url = dataArray[i].split("\\|");
                    newDataArray.add(url[1]);
                }
            }
            if (type.equals("search")) {
                String[] dataArray = data.split(" ");
                for (int i = 0; i < dataArray.length; i++)
                    newDataArray.add(dataArray[i]);
                System.out.println(newDataArray);
            }
            if (type.equals("handle")) {
                String[] dataArray = data.split(";");
                for (int i = 0; i < dataArray.length - 2; i++) {
                    String[] info = dataArray[i + 2].split("\\|", 2);
                    if (i % 3 == 0) {
                        newDataArray.add("\nTitle: " + info[1]);
                    }
                    if (i % 3 == 1) {
                        newDataArray.add("URL: " + info[1]);
                    }
                    if (i % 3 == 2) {
                        newDataArray.add("Text: " + info[1]);
                    }
                }
            }
            if (type.equals("login")){
                String[] dataArray = data.split(";");
                if (dataArray.length == 2) {
                    String[] nome = dataArray[0].split("\\|");
                    String[] status = dataArray[1].split("\\|");
                    newDataArray.add(nome[1]);
                    newDataArray.add(status[1]);
                }
                else{
                    String[] nome = dataArray[0].split("\\|");
                    String[] status = dataArray[1].split("\\|");
                    String[] facebookID = dataArray[2].split("\\|");
                    String[] tokenID = dataArray[3].split("\\|");
                    newDataArray.add(nome[1]);
                    newDataArray.add(status[1]);
                    newDataArray.add(facebookID[1]);
                    newDataArray.add(tokenID[1]);
                }
            }


            if (type.equals("giveadmin")) {
                String[] dataArray = data.split("\\|");
                newDataArray.add(dataArray[1]);
            }
            if (type.equals("info")) {
                String[] dataArray = data.split("\\^");
                String[] lista_urls = dataArray[1].split(";");
                String[] lista_words = dataArray[2].split(";");
                String[] lista_server_ids = dataArray[3].split(";");
                String[] porta_server = dataArray[4].split(";");

                String[] tamanhourls = lista_urls[1].split("\\|");
                int numUrls = Integer.parseInt(tamanhourls[1]);

                String[] tamanhowords = lista_words[1].split("\\|");
                int numWords = Integer.parseInt(tamanhowords[1]);

                newDataArray.add("URLS");
                newDataArray.add(tamanhourls[1]);
                for (int i = 0; i < numUrls; i++) {
                    String[] info = lista_urls[i + 2].split("\\|");
                    newDataArray.add(info[1]);
                }
                System.out.println("DONE 1");
                newDataArray.add("WORDS");
                newDataArray.add(tamanhowords[1]);
                for(int i=0; i < numWords;i++)
                {
                    String[] info = lista_words[i + 2].split("\\|");
                    newDataArray.add(info[1]);
                }
                System.out.println("DONE 2");
                newDataArray.add("SERVERS");
                int j = lista_server_ids.length - 1;
                newDataArray.add(String.valueOf(j));
                for(int i = 1; i < lista_server_ids.length;i++)
                {
                    String[] info = lista_server_ids[i].split("\\|");
                    newDataArray.add(info[1]);
                }
                System.out.println("DONE 3");
                newDataArray.add("PORTS");
                String[] aux = porta_server[1].split("\\|");
                newDataArray.add(aux[1]);
                System.out.println("DONE 4");
            }
            if (type.equals("searchFb")) {
                System.out.println("searchFB handler:"+data);
                String [] dataArray = data.split(";");
                String [] verif = dataArray[1].split("\\|");
                if(verif[0].equals("message")){
                    newDataArray.add(verif[1]);
                }
                else{
                    newDataArray.add(verif[1]);
                    String[] status = dataArray[2].split("\\|");
                    String[] fbID = dataArray[3].split("\\|");
                    String[] token = dataArray[4].split("\\|");
                    newDataArray.add(status[1]);
                    newDataArray.add(fbID[1]);
                    newDataArray.add(token[1]);
                }
            }
            if(type.equals("mysearch")){
                String [] dataArray = data.split(";");
                for(int i = 0; i< dataArray.length; i++){
                    String[] search = dataArray[i].split("\\|");
                    newDataArray.add(search[1]);
                }
                System.out.println(newDataArray);
            }

        }
        return newDataArray;
    }

    public String LoginAuthorizationUrl() {
        OAuth20Service service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .callback("https://ucBusca.uc:8443/ucBusca/loginFacebook.action") // Do not change this.
                .scope("public_profile,publish_pages")
                .build(FacebookApi.instance());

        String url = service.getAuthorizationUrl();
        System.out.println(url);
        return url;
    }

    public String ConnectAuthorizationUrl(){
        OAuth20Service service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .callback("https://ucBusca.uc:8443/ucBusca/connectFacebook.action") // Do not change this.
                .scope("public_profile,publish_pages")
                .build(FacebookApi.instance());

        String url = service.getAuthorizationUrl();
        System.out.println(url);
        return url;
    }

    public ArrayList<String> LoginFacebook(String code) {
        ArrayList<String> newData = null;
        Token EMPTY_TOKEN = null;
        OAuth20Service service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .callback("https://ucBusca.uc:8443/ucBusca/loginFacebook.action") // Do not change this.
                .scope("public_profile,publish_pages")
                .build(FacebookApi.instance());

        final OAuth2AccessToken accessToken;

        try {
            accessToken = service.getAccessToken(code);

            // VAI BUSCAR USER ID AND NAME para inserir na base de dados PROTECTED_RESOURCE_URL = link on estao guardadas as infos dos users
            OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, request);

            //RESPOSTA DO API PARA IR BUSCAR O USER E O ID
            Response response = service.execute(request);
            System.out.println(response.getBody());

            JSONObject obj = (JSONObject) JSONValue.parse(response.getBody());

            newData = getData(null,null,"searchFb",(String)obj.get("id"));

            if(newData.get(0).equals("not found"))
            {
                ArrayList<String> nova = new ArrayList<>();
                getData((String)obj.get("name"),(String)obj.get("id"),"register",null);
                String auxol = (String)obj.get("id")+" "+accessToken.getAccessToken();
                getData((String)obj.get("name"),null,"associate",auxol);
                nova.add((String)obj.get("name"));
                nova.add(null);
                nova.add((String)obj.get("id"));
                nova.add(accessToken.getAccessToken());
                //logged  nao faz nada, apenas ativa o comando para o user dar login no rmi
                ArrayList logged = new ArrayList();
                logged = getData((String)obj.get("name"),(String)obj.get("id"),"login",null);
                return nova;
            }
            else{
                ArrayList logged = new ArrayList();
                logged = getData((String)obj.get("name"),(String)obj.get("id"),"login",null);
                return logged;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> connectFacebook(String code,String username)
    {
        ArrayList<String> newData = null;
        Token EMPTY_TOKEN = null;
        OAuth20Service service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .callback("https://ucBusca.uc:8443/ucBusca/connectFacebook.action") // Do not change this.
                .scope("public_profile,publish_pages")
                .build(FacebookApi.instance());

        final OAuth2AccessToken accessToken;
        System.out.println("ENTEI AQUI CRL");
        try {
            accessToken = service.getAccessToken(code);

            // VAI BUSCAR USER ID AND NAME para inserir na base de dados PROTECTED_RESOURCE_URL = link on estao guardadas as infos dos users
            OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, request);

            //RESPOSTA DO API PARA IR BUSCAR O USER E O ID
            Response response = service.execute(request);
            System.out.println(response.getBody());

            JSONObject obj = (JSONObject) JSONValue.parse(response.getBody());

            newData = getData(null,null,"searchFb",(String)obj.get("id"));

            if(newData.get(0).equals("not found")) {
                ArrayList<String> nova = new ArrayList<>();
                String auxol = (String) obj.get("id") + " " + accessToken.getAccessToken();
                getData(username, null, "associate", auxol);
                nova.add((String) obj.get("id"));
                nova.add(accessToken.getAccessToken());

                return nova;
            }
            else{
                ArrayList<String> nova = new ArrayList<>();
                nova.add("ERROR");
                return nova;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList translate(ArrayList<String> data) throws IOException {
        ArrayList<String> nova = new ArrayList();
        Yandex tradutor = new Yandex() ;

        Map<String,String> todaslinguas = tradutor.getLinguas();
        for(int i = 0 ; i < data.size();i++)
        {
            if(i % 4 == 0){
                String input = data.get(i);
                if(!input.equals("") || !input.equals(" ")) {
                    String linguaoriginal = tradutor.detect(input);
                    String linguarTraduzir = tradutor.getTraducao(todaslinguas, "portuguese");

                    String traduzida = tradutor.translate(input, linguaoriginal, linguarTraduzir);
                    traduzida = traduzida.substring(3,traduzida.length()-1);
                    nova.add(traduzida);
                    System.out.println(traduzida);
                }
                else{
                    nova.add(input);
                }
            }

            if(i % 4 == 1){
                String input = data.get(i);
                nova.add(input);
            }

            if(i % 4 == 2){
                String input = data.get(i);
                if(!input.equals("") || !input.equals(" ")) {
                    String linguaoriginal = tradutor.detect(input);
                    String linguarTraduzir = tradutor.getTraducao(todaslinguas, "portuguese");

                    String traduzida = tradutor.translate(input, linguaoriginal, linguarTraduzir);
                    traduzida = traduzida.substring(1,traduzida.length()-1);
                    nova.add(traduzida);
                    System.out.println(traduzida);
                }
                else{
                    nova.add(input);
                }
            }
            if(i % 4 == 3)
            {
                String input = data.get(i);
                nova.add(input);
            }
        }
        return nova;
    }

    public ArrayList<String> detetaLingua(ArrayList<String> data) throws IOException {
        ArrayList<String> nova = new ArrayList();
        Yandex tradutor = new Yandex() ;

        Map<String,String> todaslinguas = tradutor.getLinguas();
        for(int i = 0 ; i < data.size();i++)
        {
            if(i % 3 == 0){
                String input = data.get(i);
                nova.add(input);
            }

            if(i % 3 == 1){
                String input = data.get(i);
                nova.add(input);
            }

            if(i % 3 == 2){
                String input = data.get(i);
                if(!input.equals("") || !input.equals(" ")) {
                    String linguaoriginal = tradutor.detect(input);
                    nova.add(input);
                    nova.add("Language:"+linguaoriginal);
                }
                else{
                    nova.add(input);
                }
            }
        }
        return nova;
    }
}

