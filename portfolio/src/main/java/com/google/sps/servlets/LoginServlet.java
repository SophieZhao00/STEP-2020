// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        UserService userService = UserServiceFactory.getUserService();

        // Only logged-in users can see the form
        if (userService.isUserLoggedIn()) {
            String logoutUrl = userService.createLogoutURL("/index.html");

            response.getWriter().println("<form action=\"/data\" method=\"POST\">");
            response.getWriter().println("<textarea name=\"text-input\"></textarea>");
            response.getWriter().println("<br/><br/>");
            response.getWriter().println("<button>Submit</button>");
            response.getWriter().println("</form>");
            
            response.getWriter().println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
        } else {
            String loginUrl = userService.createLoginURL("/comment.html");

            response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a> to leave a comment.</p>");
        }
    }
}