/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ksoft.beauty;

import com.ksoft.beauty.entity.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kkichaev
 */
public class LoginFilter implements Filter{

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
//        HttpSession session = ((HttpServletRequest)sr).getSession();
//        User user = (User) session.getAttribute("user");
//        
//        if (user != null){
//            String home = "/beauty";
//            if (user.isAdmin())
//                home = "admin.xhtml";
//            
//            ((HttpServletResponse)sr1).sendRedirect(home);
//                
//        }else
            fc.doFilter(sr, sr1);
    }
}
