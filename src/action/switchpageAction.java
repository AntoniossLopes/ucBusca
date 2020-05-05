/**
 * Raul Barbosa 2014-11-07
 */
package action;

import com.opensymphony.xwork2.ActionSupport;
import model.HeyBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class switchpageAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null;
    private String url;

    @Override
    public String execute() throws RemoteException {
        // any username is accepted without confirmation (should check using RMI)
        url = this.getHeyBean().getLoginAuth();
        return SUCCESS;
    }

    public void setUsername(String username) {
        this.username = username; // will you sanitize this input? maybe use a prepared statement?
    }

    public String getUrl() {
        return this.url;
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
