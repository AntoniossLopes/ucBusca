/**
 * Raul Barbosa 2014-11-07
 */
package action;

import com.opensymphony.xwork2.ActionSupport;
import model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;
public class logoutAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    @Override
    public String execute() throws RemoteException {
        if(!session.containsKey("password"))
            return ERROR;

        this.getHeyBean().logout((String) session.get("username"));
        session.remove("username");
        session.remove("password");
        session.remove("loggedin");
        if (session.containsKey("admin"))
            session.remove("admin");
        if (session.containsKey("id"))
            session.remove("id");
        if (session.containsKey("token"))
            session.remove("token");
        if (session.containsKey("hasNotification"))
            session.remove("hasNotification");

        return SUCCESS;
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
