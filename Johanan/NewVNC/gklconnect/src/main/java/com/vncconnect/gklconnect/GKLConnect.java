/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vncconnect.gklconnect;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.net.GuacamoleSocket;
import org.glyptodon.guacamole.net.GuacamoleTunnel;
import org.glyptodon.guacamole.net.InetGuacamoleSocket;
import org.glyptodon.guacamole.net.SimpleGuacamoleTunnel;
import org.glyptodon.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.glyptodon.guacamole.protocol.GuacamoleConfiguration;
import org.glyptodon.guacamole.servlet.GuacamoleHTTPTunnelServlet;

/**
 *
 * @author 1
 */
public class GKLConnect extends GuacamoleHTTPTunnelServlet {

    String serverAddress;
    String serverPort;
    String colorDepth;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if(request.getParameter("address") != null) {
                PrintWriter out = response.getWriter();
                String docType =
                "<!DOCTYPE html>\n";

                out.println(docType +
                   "<html>\n" +
                      "<head><title>Test</title></head>\n" +
                      "<body bgcolor = \"#f0f0f0\">\n" +
                         "<h1 align = \"center\">Test</h1>\n"
                );
                out.println(
                         "<ul>\n" +
                            "  <li><b>Address</b>: "
                            + request.getParameter("address") + "\n" +
                            "  <li><b>port</b>: "
                            + request.getParameter("port") + "\n" +
                            "  <li><b>colors</b>: "
                            + request.getParameter("colorDepth") + "\n" +
                         "</ul>\n"
                );

                out.println(
                      "</body>" +
                   "</html>"
                );
            } else {
                super.doGet(request, response); //To change body of generated methods, choose Tools | Templates.
            }
        } catch (IOException ex) {
            Logger.getLogger(GKLConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if( request.getQueryString().contains("connect") ) {
            serverAddress = request.getParameter("address");
            serverPort = request.getParameter("port");
            colorDepth = request.getParameter("colorDepth");
            request = new HttpServletRequestWrapper(request) { @Override public String getQueryString() { return "connect"; } };
        }
        super.doPost(request, response); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected GuacamoleTunnel doConnect(HttpServletRequest hsr) throws GuacamoleException {
        GuacamoleConfiguration config = new GuacamoleConfiguration();
        config.setProtocol("vnc");
        config.setParameter("hostname", serverAddress);
        config.setParameter("port", serverPort);
        config.setParameter("color-depth", colorDepth == null ? "8" : colorDepth);

        // Connect to guacd - everything is hard-coded here.
        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(new InetGuacamoleSocket("localhost", 4822), config);

        // Return a new tunnel which uses the connected socket
        return new SimpleGuacamoleTunnel(socket);
    }
    
}