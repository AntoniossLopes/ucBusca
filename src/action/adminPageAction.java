package action;

import com.opensymphony.xwork2.ActionSupport;
import model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class adminPageAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null;
    private ArrayList data;
    private ArrayList urls;
    private ArrayList words;
    private ArrayList server_ids;
    private ArrayList server_ports;

    @Override
    public String execute() throws RemoteException {
        if(!session.containsKey("password")){
            return ERROR;
        }

        if(session.get("admin").equals(false)) {
            System.out.println("Sai aqui");
            return ERROR;
        }

        ArrayList<String> urls_aux = new ArrayList<>();
        ArrayList<String> server_aux = new ArrayList<>();
        ArrayList<String> words_aux = new ArrayList<>();
        ArrayList<String> serverports_aux = new ArrayList<>();
        // any username is accepted without confirmation (should check using RMI)
        data = this.getHeyBean().info();
        System.out.println(data);
        int i = 0;
        while(i < data.size())
        {
            if(data.get(i).equals("URLS"))
            {
                int j = 0;
                i++;
                int numero = Integer.parseInt((String)data.get(i));
                i++;
                while(j < numero)
                {
                    urls_aux.add("URL:"+(String)data.get(i));
                    i++;
                    j++;
                }

            }
            if(data.get(i).equals("WORDS"))
            {
                System.out.println(data.get(i));
                int j = 0;
                i++;
                int numero = Integer.parseInt((String)data.get(i));
                i++;
                while(j < numero)
                {
                    System.out.println(data.get(i));
                    words_aux.add("WORDS:"+(String)data.get(i));
                    i++;
                    j++;
                }

            }
            if(data.get(i).equals("SERVERS")) {
                int j = 0;
                i++;
                int numero;
                numero = Integer.parseInt((String)data.get(i));
                i++;
                while (j < numero) {
                    server_aux.add("SERVER ID:"+(String)data.get(i));
                    i++;
                    j++;

                }
            }
            if(data.get(i).equals("PORTS"))
            {
                System.out.println(data.get(i));
                i++;
                System.out.println(data.get(i));
                serverports_aux.add("Server PORT:"+(String) data.get(i));
                i++;
            }
        }
        urls = urls_aux;
        words = words_aux;
        server_ids = server_aux;
        server_ports = serverports_aux;
        return SUCCESS;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username; // will you sanitize this input? maybe use a prepared statement?
    }
    public void setWords(ArrayList words){
        this.words = words;
    }

    public ArrayList getWords() {
        return words;
    }

    public void setUrls(ArrayList urls){
        this.urls = urls;
    }

    public ArrayList getUrls() {
        return urls;
    }

    public void setServer_ids(ArrayList server_ids){
        this.server_ids = server_ids;
    }

    public ArrayList getServer_ids() {
        return server_ids;
    }

    public ArrayList getServer_ports() {
        return server_ports;
    }

    public void setServer_ports(ArrayList server_ports) {
        this.server_ports = server_ports;
    }

    public ArrayList getData() {
        return this.data;
    }

    public void setData(ArrayList data){
        this.data = data;
    }

    public HeyBean getHeyBean() {
        if(!session.containsKey("heyBean"))
            this.setHeyBean(new HeyBean());
        return (HeyBean) session.get("heyBean");
    }

    public void setHeyBean(HeyBean heyBean) {
        this.session.put("heyBean", heyBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}

