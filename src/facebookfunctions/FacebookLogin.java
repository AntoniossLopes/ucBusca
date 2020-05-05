package facebookfunctions;

import com.opensymphony.xwork2.ActionSupport;
import model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class FacebookLogin extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    public String code = null;

    public String execute() throws RemoteException {
        ArrayList result = new ArrayList();
        System.out.println(code);
        result = this.getHeyBean().loginFB(code);
        session.put("username", result.get(0));
        session.put("password", result.get(2));
        session.put("loggedin", true); // this marks the user as logged in
        session.put("id",result.get(2));
        session.put("token",result.get(3));
        session.put("admin",result.get(1));
        session.put("hasNotification", true);
        return SUCCESS;
    }

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

    public String getCode() { return code; }


    public void setCode(String code) { this.code = code; }


    public void setHeyBean(HeyBean heyBean) {
        this.session.put("heyBean", heyBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
