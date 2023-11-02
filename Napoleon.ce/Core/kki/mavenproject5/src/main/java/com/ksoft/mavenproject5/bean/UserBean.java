/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5.bean;

import com.ksoft.mavenproject5.controller.UserControllerBean;
import com.ksoft.mavenproject5.entity.User;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.core.Context;

/**
 *
 * @author kkichaev
 */
@Named
@RequestScoped
public class UserBean implements Serializable{
    @NotEmpty
    private String username;
    
    @NotEmpty
    private String password;
    
    @Inject
    private UserControllerBean userController;
    
    @Inject
    private HttpServletRequest request;
    
    @Inject
    private SecurityContext securityContext;
    
    @Context
    private HttpServletResponse responce;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void createUser(){
        userController.createUser(username, password);
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(String.format("createUser(%s, %s);", username, password));
        context.addMessage(null, msg);
    }
    
    public List<User> getUsers(){
        return userController.getUsers();
    }
    
    public void login(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        
//        try{
//            request.login(username, password);
//            
//            FacesMessage msg = new FacesMessage("Login success!");
//            context.addMessage(null, msg);
//            
//            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//            Cookie c = new Cookie("username", username);
//            c.setPath(request.getContextPath());
//            response.addCookie(c);
//            
//            c = new Cookie("password", username);
//            c.setPath(request.getContextPath());
//            
//            response.addCookie(c);
//        }catch(Exception e){
//            FacesMessage msg = new FacesMessage("Login failure! " + e.getMessage() + " " + e.getStackTrace().toString());
//            context.addMessage(null, msg);
//        }

        Credential credential = new UsernamePasswordCredential(
          username, new Password(password));
        AuthenticationStatus status = securityContext
          .authenticate(
            request, response, withParams().credential(credential));
    }
    
    public String getCurrentUser(){
        FacesContext context = FacesContext.getCurrentInstance();
        Principal p = request.getUserPrincipal();
        if ( p == null)
            return "Незарегестрирован!";
        else
            return p.getName();
    }
}
