/**
 * Raul Barbosa 2014-11-07
 */
package action;

import com.opensymphony.xwork2.ActionSupport;
import model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
public class LoginAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    @Override
    public String execute() throws RemoteException {
        if(session.containsKey("password"))
            return ERROR;

        if(this.username == null||(this.username.equals(""))||this.password == null || this.password.equals(""))
            return ERROR;
        // any username is accepted without confirmation (should check using RMI)
        ArrayList recebi = this.getHeyBean().login(this.username,this.password);
        String notification = this.getHeyBean().notification(this.username);
        String status = (String)recebi.get(1);
        String id = (String)recebi.get(2);
        String token = (String)recebi.get(3);
        if (status.equals("Not found") || status.equals("Wrong password")) {
            return LOGIN;
        }
        else{
            session.put("username", username);
            session.put("password", password);
            session.put("loggedin", true); // this marks the user as logged in
            session.put("admin",status);
            session.put("notification", notification);
            if(id.equals("null"))
            {
                String url = this.getHeyBean().getConnectAuth();
                session.put("url",url);
                return SUCCESS;
            }
            session.put("id",id);
            session.put("token",token);
            return SUCCESS;
        }
    }

    public void setUsername(String username) {
        this.username = username; // will you sanitize this input? maybe use a prepared statement?
    }

    public void setPassword(String password) {
        this.password = password; // what about this input?
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

    public void setHeyBean(HeyBean heyBean) {
        this.session.put("heyBean", heyBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
