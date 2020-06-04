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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private int numComments = 3;

  /**
   * Get data from Datastore and return comments as a JSON file 
   */  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Input").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(numComments));
    
    ArrayList<String> comments = new ArrayList<String>();

    for (Entity entity : results) {
        comments.add((String) entity.getProperty("content"));
    }

    String json = convertToJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Add user input from form to Datastore 
   */  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
    String comment = request.getParameter("text-input");
    numComments = Integer.parseInt(request.getParameter("amount"));
    long currentTime = System.currentTimeMillis();

    Entity commentEntity = new Entity("Input");
    commentEntity.setProperty("content", comment);
    commentEntity.setProperty("timestamp", currentTime);  

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();  
    datastore.put(commentEntity);

    response.setContentType("text/html;");
    response.getWriter().println(comments);

    response.sendRedirect("/index.html");
  }

  /**
   * Converts a ServerStats instance into a JSON string using the Gson library.
   */
  private String convertToJson(ArrayList<String> strings) {
    Gson gson = new Gson();
    return gson.toJson(strings);
  }
}
