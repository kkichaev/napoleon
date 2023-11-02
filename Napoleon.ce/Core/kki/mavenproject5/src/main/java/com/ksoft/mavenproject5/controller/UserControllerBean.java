/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5.controller;

import com.ksoft.mavenproject5.entity.UserGroup;
import com.ksoft.mavenproject5.entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author kkichaev
 */
@Stateless
public class UserControllerBean {
    @PersistenceContext
    EntityManager em;
    
    public void createUser(String username, String password){
        User user = new User();
        user.username = username;
        user.password = DigestUtils.sha256Hex(password);
        UserGroup g = new UserGroup();
        g.username = user.username;
        g.groupname = "user";
        
        em.persist(user);
        em.persist(g);
    }
    
    public List<User> getUsers(){
        return em.createQuery("select u from User u").getResultList();
    }
}
