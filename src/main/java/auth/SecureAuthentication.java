package auth;

import beans.LoginBean;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import java.io.Serializable;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import record.KeepRecord;

@Named
@RequestScoped
public class SecureAuthentication implements HttpAuthenticationMechanism, Serializable {

    @Inject
    IdentityStoreHandler handler;
    CredentialValidationResult result;
    AuthenticationStatus status;
   
    @Inject
    LoginBean lbean;
    @Inject KeepRecord keepRecord;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext ctx) throws AuthenticationException {
        
        try {
            // LOGOUT Handling
            if(request.getRequestURI().contains("Logout"))
            {
                request.logout();
                request.getSession().invalidate();
                keepRecord.reset(); // Ensure KeepRecord.reset() clears all session state
                
                response.sendRedirect("Login.jsf");
                return ctx.doNothing();
            }
            
            // LOGIN Attempt Handling
            if(request.getParameter("username")!=null && request.getParameter("password")!=null)
            {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                
                Credential credential = new UsernamePasswordCredential(username, new Password(password));
                result = handler.validate(credential);
                
                if (result.getStatus().equals(Status.VALID)) {
                    // Successful Login logic
                    
                    keepRecord.setPrincipal(result.getCallerPrincipal());
                    keepRecord.setRoles(result.getCallerGroups());
                    keepRecord.setUsername(username);
                    keepRecord.setCredential(credential);
                    keepRecord.setResult(result);
                    keepRecord.setErrorStatus(""); // Clear any previous error

                    // *** CORRECTED: Use the container's login mechanism directly ***
                    status = ctx.notifyContainerAboutLogin(result); 
                    
                    // Role-Based Redirection
                    if (result.getCallerGroups().contains("Admin")) {
                        request.getRequestDispatcher("admin/Admin.jsf").forward(request, response);
                        return status;
                    } else if (result.getCallerGroups().contains("User")) {
                        request.getRequestDispatcher("user/User.jsf").forward(request, response);
                        return status;
                    }
                }
                
                // Failed Login logic (Status.INVALID or Status.NOT_VALIDATED)
                else {
                    keepRecord.setErrorStatus("Either Username or Password is wrong !");
                    response.sendRedirect("Login.jsf");
                    return ctx.doNothing();
                }
            }
            
            // Subsequent Request Handling (Session Re-authentication)
            if(keepRecord.getPrincipal()!=null)
            {
                result = handler.validate(keepRecord.getCredential());
                status = ctx.notifyContainerAboutLogin(result);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ctx.doNothing();
    }
    
    // ** REQUIRED METHOD for HttpAuthenticationMechanism **
    @Override
    public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        return httpMessageContext.doNothing();
    }
}