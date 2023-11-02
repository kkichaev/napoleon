/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
//import org.glassfish.soteria.identitystores.annotation.Credentials;
//import org.glassfish.soteria.identitystores.annotation.EmbeddedIdentityStoreDefinition;

/**
 *
 * @author kkichaev
 */
//@CustomFormAuthenticationMechanismDefinition(
//        loginToContinue = @LoginToContinue(
//                loginPage = "/index.xhtml",
//                errorPage = ""
//        )
//)
//
//@EmbeddedIdentityStoreDefinition({
//    @Credentials(callerName = "david", password = "david", groups = {"foo"}),
//    @Credentials(callerName = "ed", password = "ed", groups = {"bar",}),
//    @Credentials(callerName = "michael", password = "michael", groups = {"foo"})}
// )

//@CustomFormAuthenticationMechanismDefinition(
//  loginToContinue = @LoginToContinue(loginPage = "/index.xhtml"))
@FacesConfig
@ApplicationScoped
public class ApplicationConfig {

}
