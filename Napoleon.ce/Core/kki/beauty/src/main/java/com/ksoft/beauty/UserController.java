/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.beauty;

import com.ksoft.beauty.entity.User;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author kkichaev
 */

@Named
@RequestScoped
public class UserController {
    @Inject 
    private User user;
    
    @Inject
    private UserControllerBean userControllerBean;
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String save(){
        userControllerBean.addUser(user);
        return "index";
    }
    
}
