/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author kkich_u
 */

@ApplicationScoped
public class TheIdentityStore implements IdentityStore{

    @Override
    public CredentialValidationResult validate(Credential credential) {
        UsernamePasswordCredential upc = (UsernamePasswordCredential)credential;
        return new CredentialValidationResult(upc.getCaller());
    }
    
}
