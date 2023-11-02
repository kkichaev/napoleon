/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.beauty.entity;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kkichaev
 */
@Entity
public class User implements Serializable{
    @Id
    @GeneratedValue
    private Long id;
    
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String loginAction(){
        //status = "login STATUS";
        //return "index";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
         
        if (login.equals("admin")){
            //isAdmin = true;
            session.setAttribute("user", this);
            return "ADMIN";
        }
        else if (login.equals("login")){
            session.setAttribute("user", this);
            return "USER";
        }
        
        return "FAIL";
    }
}
