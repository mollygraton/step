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
import java.util.Arrays;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private ArrayList<String> messages = new ArrayList<String>(Arrays.asList(
      "Welcome to my page!", "Porfolio time!", "This is my portfolio"
  ));

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = convertToJson(messages);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Converts a ServerStats instance into a JSON string using the Gson library.
   */
  private String convertToJson(ArrayList<String> strings) {
    String json = "{";
    json += "\"firstMessage\": ";
    json += "\"" + strings.get(0) + "\"";
    json += ", ";
    json += "\"secondMessage\": ";
    json += "\"" + strings.get(1) + "\"";
    json += ", ";
    json += "\"thirdMessage\": ";
    json += "\"" + strings.get(2)+ "\"";
    json += "}";

    return json;
  }
}
