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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles (returns) comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private List<String> comments;

    @Override
    public void init() {
        comments = new ArrayList<>();
        comments.add("Comment 1.");
        comments.add("Comment 2.");
        comments.add("Comment 3.");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = convertToJson(comments);
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    /**
     * Converts a list of comemnts into a JSON string using the Gson library.
     */
    private String convertToJson(List<String> comments) {
        Gson gson = new Gson();
        String json = gson.toJson(comments);
        return json;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String newComment = getParameter(request, "text-input", "");
        comments.add(newComment);
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
