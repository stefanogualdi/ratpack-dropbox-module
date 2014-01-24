/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools;

import com.dropbox.core.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * @author Dropbox Team
 */
public class Auth {
  public static void main(String[] args) throws IOException, DbxException {
    // Get your app key and secret from the Dropbox developers website.
    final String appKey = System.getenv("API_KEY");
    final String appSecret = System.getenv("API_SECRET");

    DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);

    DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

    // Have the user sign in and authorize your app.
    String authorizeUrl = webAuth.start();
    System.out.println("1. Go to: " + authorizeUrl);
    System.out.println("2. Click \"Allow\" (you might have to log in first)");
    System.out.println("3. Copy the authorization code.");

    System.out.println("Enter code here: ");
    String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

    DbxAuthFinish authFinish = webAuth.finish(code);

    System.out.print("The access token is: ");
    System.out.println(authFinish.accessToken);
  }
}