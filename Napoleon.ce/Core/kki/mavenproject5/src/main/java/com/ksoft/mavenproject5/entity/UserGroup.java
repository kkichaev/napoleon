/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author kkichaev
 */
@Entity
public class UserGroup {
    @Id
    public String username;
    
    @Id
    public String groupname;
}
