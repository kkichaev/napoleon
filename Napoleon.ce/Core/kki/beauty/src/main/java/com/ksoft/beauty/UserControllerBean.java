/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.beauty;

import com.ksoft.beauty.entity.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kkich_u
 */
@Stateless
public class UserControllerBean {
    @PersistenceContext
    private EntityManager em;
    
    public void addUser(User user){
        em.persist(user);
    }
}
