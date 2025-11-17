package auth;

import beans.LoginBean;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
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
import static jwtrest.Constants.AUTHORIZATION_HEADER;
import static jwtrest.Constants.BEARER;
import jwtrest.JWTCredential;
import jwtrest.TokenProvider;
import record.KeepRecord;
import jakarta.security.enterprise.CallerPrincipal;

@Named
@RequestScoped
public class SecureAuthentication implements HttpAuthenticationMechanism, Serializable {

    @Inject
    IdentityStoreHandler handler;
    CredentialValidationResult result;
    AuthenticationStatus status;
    @Inject
    TokenProvider tokenProvider;
    @Inject
    LoginBean lbean;
    @Inject KeepRecord keepRecord;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext ctx) throws AuthenticationException {
        
        try {
            // 1. LOGOUT Handling
            if (request.getRequestURI().contains("Logout")) {
                request.logout();
                request.getSession().invalidate();
                keepRecord.reset();
                response.sendRedirect("Login.jsf");
                
                // FIX: Must return SUCCESS after a manual redirect to end the process cleanly
                return AuthenticationStatus.SUCCESS; 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String token = extractToken(ctx);
        
        // 2. JWT (REST) Authentication Flow
        if (token != null) {
            return validateToken(token, ctx);
        }
        
        try {
            // 3. Form-Based (JSF) Authentication Flow
            if (request.getParameter("username") != null && request.getParameter("password") != null) {
                
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                Credential credential = new UsernamePasswordCredential(username, new Password(password));
                result = handler.validate(credential);

                if (result.getStatus() == Status.VALID) {
                    keepRecord.setErrorStatus("");
                    
                    // A. Create JWT and notify container (authStatus will be SUCCESS)
                    AuthenticationStatus authStatus = createToken(result, ctx); 

                    // B. Store session data for JSF navigation/internal use
                    keepRecord.setPrincipal(result.getCallerPrincipal());
                    keepRecord.setRoles(result.getCallerGroups());
                    keepRecord.setCredential(credential);

                    // C. Role-Based Redirection (JSF navigation)
                    if (result.getCallerGroups().contains("Admin")) {
                        request.getRequestDispatcher("admin/Admin.jsf").forward(request, response);
                        // Return SUCCESS after forward
                        return AuthenticationStatus.SUCCESS;
                    } 
                    else if (result.getCallerGroups().contains("User")) { 
                        request.getRequestDispatcher("user/User.jsf").forward(request, response);
                        // Return SUCCESS after forward
                        return AuthenticationStatus.SUCCESS;
                    }
                    
                    return authStatus; 

                } else {
                    // Failed Login logic
                    keepRecord.setErrorStatus("Either Username or Password is wrong !");
                    response.sendRedirect("Login.jsf");
                    
                    // FIX: Must return SUCCESS after a manual redirect to end the process cleanly
                    return AuthenticationStatus.SUCCESS;
                }
            }
            
            // 4. Existing Session Maintenance (For subsequent JSF page requests)
            if (keepRecord.getPrincipal() != null) {
                result = handler.validate(keepRecord.getCredential());
                return ctx.notifyContainerAboutLogin(result);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. Protected Resource Check (if no credentials were provided)
        if (ctx.isProtected()) {
            return ctx.responseUnauthorized();
        }
        
        return ctx.doNothing();
    }

    // Required minimal implementation for HttpAuthenticationMechanism
    @Override
    public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        // This method should also avoid returning doNothing if the response is committed, 
        // but for a minimal implementation, we rely on validateRequest to signal completion.
        return httpMessageContext.doNothing();
    }

    // JWT Creation: Creates token, stores it, adds header, and notifies the container
    private AuthenticationStatus createToken(CredentialValidationResult result, HttpMessageContext context) {
        String jwt = tokenProvider.createToken(result.getCallerPrincipal().getName(), result.getCallerGroups(), false);
        keepRecord.setToken(jwt);
        context.getResponse().addHeader(AUTHORIZATION_HEADER, BEARER + jwt);
        System.out.println("Token Value: " + jwt);

        return context.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
    }

    // JWT Validation: Validates token, extracts claims, and notifies container
    private AuthenticationStatus validateToken(String token, HttpMessageContext context) {
        try {
            if (tokenProvider.validateToken(token)) {
                JWTCredential credential = tokenProvider.getCredential(token);
                
                CallerPrincipal callerPrincipal = new CallerPrincipal(credential.getPrincipal());
                
                return context.notifyContainerAboutLogin(callerPrincipal, credential.getAuthorities());
            }
            return context.responseUnauthorized();
        } catch (ExpiredJwtException eje) {
            return context.responseUnauthorized();
        } catch (SignatureException e) {
            return context.responseUnauthorized();
        }
    }

    // Extracts JWT from Authorization HTTP header
    private String extractToken(HttpMessageContext context) {
        String authorizationHeader = context.getRequest().getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(BEARER.length()).trim();
            keepRecord.setToken(token); 
            return token;
        }
        return null;
    }
}