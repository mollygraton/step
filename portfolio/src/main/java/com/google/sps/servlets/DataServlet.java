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
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.sps.data.Comment;
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

  private int maxNumOfComments = 3;
  private float sentimentScore = 0;
  static final String ENTITY_NAME = "Comment";
  static final String ENTITY_TIME = "timestamp";
  static final String ENTITY_CONTENT = "content";
  static final String ENTITY_SCORE = "score";
  static final String TEXT_INPUT_ID = "text-input";
  static final String NUM_INPUT_ID = "amount";

  /**
   * Get data from Datastore and return comments as a JSON file 
   */  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(ENTITY_NAME).addSort(ENTITY_TIME, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(maxNumOfComments));
    
    ArrayList<Comment> comments = new ArrayList<Comment>();

    for (Entity entity : results) {
        String content = (String) entity.getProperty(ENTITY_CONTENT);
        double sentScore = (double) entity.getProperty(ENTITY_SCORE);

        Comment currentComment = new Comment(content, sentScore);
        comments.add(currentComment);
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  /**
   * Add user input from form to Datastore 
   */  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
    String comment = request.getParameter(TEXT_INPUT_ID);
    maxNumOfComments = Integer.parseInt(request.getParameter(NUM_INPUT_ID));
    double currentTime = System.currentTimeMillis();

    Document doc =
        Document.newBuilder().setContent(comment).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();

    Entity commentEntity = new Entity(ENTITY_NAME);
    commentEntity.setProperty(ENTITY_CONTENT, comment);
    commentEntity.setProperty(ENTITY_TIME, currentTime);
    commentEntity.setProperty(ENTITY_SCORE, score);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();  
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }
}
