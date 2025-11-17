package beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import record.KeepRecord;


/**
 *
 * @author root
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean {
    @Inject KeepRecord keepRecord;
    
    private String errorstatus; 
    
    public String getErrorStatus() {
        return keepRecord.getErrorStatus();
    }

    public void setErrorStatus(String status) {
        
        this.errorstatus = status;
    }

 
    public LoginBean() {
        
       // errorstatus="";
    }
    
   
    
  
}