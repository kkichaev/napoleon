/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5;

import java.util.Set;
import static java.util.UUID.randomUUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.RememberMeCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.http.HttpServletRequest;
import static org.omnifaces.util.Servlets.getRemoteAddr;

/**
 *
 * @author kkich_u
 */
@ApplicationScoped
public class TheRememberMeStore implements RememberMeIdentityStore{
    @Inject
    HttpServletRequest request;
            
    @Override
    public CredentialValidationResult validate(RememberMeCredential rmc) {
        return new CredentialValidationResult("wer");
    }

    @Override
    public String generateLoginToken(CallerPrincipal cp, Set<String> set) {
        String ipAddress = getRemoteAddr(request);
        return  randomUUID().toString(); 
    }

    @Override
    public void removeLoginToken(String string) {
        
    }
    
}
