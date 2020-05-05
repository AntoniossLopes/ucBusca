package action;

import com.opensymphony.xwork2.ActionSupport;
import model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class translateAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private String message = null;
    private String search = null;
    private ArrayList data;
    private String receive;
    private String username,password;

    @Override
    public String execute() throws IOException, InterruptedException, ExecutionException {
        ArrayList nova = new ArrayList();
        System.out.println("WOW"+receive);
        String auxiliar =receive;
        String[] iterador = auxiliar.split(";");
        for(int i = 0 ; i < iterador.length;i++)
        {
            nova.add(iterador[i]);
        }
        System.out.println(nova);
        data = this.getHeyBean().traduz(nova);
        System.out.println(data);
        return SUCCESS;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive){
        this.receive=receive;
    }

    public ArrayList getData() {
        return data;
    }

    public void setData(ArrayList data){
        this.data=data;
    }

    public void setUsername(String username){this.username=username;}

    public String getUsername(){return username;}

    public void setPassword(String password){this.password=password;}

    public String getPassword(){return password;}

    public HeyBean getHeyBean() {
        if(!session.containsKey("heyBean")) {
            try {
                this.setHeyBean(new HeyBean());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
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
