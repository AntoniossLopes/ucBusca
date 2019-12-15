package action;

import com.opensymphony.xwork2.ActionSupport;
import model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SearchUrl extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private String message = null;
    private String search = null;
    private ArrayList data;
    private String send;
    private String username,password;

    @Override
    public String execute() throws IOException, InterruptedException, ExecutionException {
        if(session.containsKey("username")) {
            username = (String) session.get("username");
            password = (String) session.get("password");
        }
        if(search.equals("") || search == null)
        {
            return ERROR;
        }
        String nova = "";
        data = this.getHeyBean().search(username,password,search);
        data = this.getHeyBean().detetaLingua(data);
        for(int i = 0; i < data.size();i++)
        {
            String x = data.get(i) +" " +";";
            nova = nova+x;
        }

        send = nova;
        if(session.containsKey("id")) {
            String pesquisa = search.replace(" ", "+");
            String partilha = "https://www.facebook.com/dialog/feed?app_id=606045263473186&post_id="+(String)session.get("id")+"&link=https://ucBusca.uc:8443/ucBusca/search.action/?search="+pesquisa+"&redirect_uri=https://ucBusca.uc:8443/ucBusca/search.action/?search="+pesquisa;
            session.put("partilha",partilha);
            System.out.println(partilha);
            return SUCCESS;
        }

        return SUCCESS;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send){
        this.send=send;
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

    public void setSearch(String search) {
        this.search = search; // what about this input?
    }

    public String getSearch() {
        return search;
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
