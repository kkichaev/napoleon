/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AutoApplySession;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author kkichaev
 */
//@AutoApplySession
@ApplicationScoped
//@RememberMe
@RememberMe(cookieSecureOnly = false, // Remove this when login is served over HTTPS.
		cookieMaxAgeSeconds = 60 * 60 * 24 * 14) // 14 days. )
public class CustomAuthentication implements HttpAuthenticationMechanism{

    @Inject
    private IdentityStoreHandler identityStore;
 
    
    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMsgContext) throws AuthenticationException {
        Credential credential = httpMsgContext.getAuthParameters().getCredential();
        
        if (credential != null)
            return httpMsgContext.notifyContainerAboutLogin(identityStore.validate(credential));
        
        return httpMsgContext.doNothing();
    }
    
}
