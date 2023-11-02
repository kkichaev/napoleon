/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.mavenproject5.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author kkich_u
 */
public class AutoLogin implements Filter{

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sr;
        HttpServletResponse res = (HttpServletResponse) sr1;
        
        if (req.getUserPrincipal() == null){
            Cookie[] ca = req.getCookies();
            String username = "";
            String password = "";
            
            for (Cookie c : ca){
                if (c.getName().equals("username"))
                    username = c.getValue();

                if (c.getName().equals("password"))
                    password = c.getValue();
            }
            
            if (username.length() > 0){
                try{
                    req.login(username, password);
                }catch(Exception e){
                    
                }
            }
        }
        
        fc.doFilter(sr, sr1);
    }
    
}
