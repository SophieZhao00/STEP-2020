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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles (returns) comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // fetch data from datastore
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        // get a specific number of comments
        int max = getMaxNum(request);
        List<String[]> comments = new ArrayList<>();
        Iterator iterator = results.asIterable().iterator();
        Entity entity;
        while(iterator.hasNext() && max != 0) {
            entity = (Entity) iterator.next();
            String email = (String) entity.getProperty("email");
            String text = (String) entity.getProperty("text");
            String[] ele = {email, text};
            comments.add(ele);
            max--;
        }

        // return comments
        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(comments));
    }

    /** Returns the number entered by the user, or -1 for infinity. */
    private int getMaxNum(HttpServletRequest request) {
        String numString = request.getParameter("maxNum");
        int num = Integer.parseInt(numString);
        if(num == 0) {
            return -1;
        }
        return num;
  }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get data
        String newComment = getParameter(request, "text-input", "");
        long timestamp = System.currentTimeMillis();
        UserService userService = UserServiceFactory.getUserService();
        String email = userService.getCurrentUser().getEmail();

        // combine data
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("text", newComment);
        commentEntity.setProperty("timestamp", timestamp);
        commentEntity.setProperty("email", email);

        // put data entity into datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        response.sendRedirect("/comment.html");
    }

    /**
     * @return the request parameter, or the default value if the parameter
     *         was not specified by the client
     */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
