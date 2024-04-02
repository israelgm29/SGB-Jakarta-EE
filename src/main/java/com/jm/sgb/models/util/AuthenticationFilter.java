/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jm.sgb.models.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author JM
 */
@WebFilter("/faces/views/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sr;
        HttpServletResponse resp = (HttpServletResponse) sr1;

        String action = req.getServletPath();
        if ("/".equals(action) || "/Login".equals(action) || "/Login.xhtml".equals(action)) {
            fc.doFilter(sr, sr1);
        } else {
            Object isLoggedObj = req.getSession().getAttribute("isLoggedIn");
            if (isLoggedObj != null) {
                boolean isLoggedIn = (Boolean) isLoggedObj;
                if (isLoggedIn) {
                    fc.doFilter(sr, sr1);
                    return;
                }
            }
//            http://localhost:8080/filter_demo_war_exploded/
            String path = req.getContextPath() + "/";
            resp.sendRedirect(path);
        }
//        System.out.println("AuthenticationFilter ---2-------");
    }



}
