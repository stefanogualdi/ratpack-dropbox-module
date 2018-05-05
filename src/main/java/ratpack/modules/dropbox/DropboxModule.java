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

package ratpack.modules.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.v2.DbxClientV2;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import ratpack.guice.ConfigurableModule;
import ratpack.modules.dropbox.internal.DefaultDropboxService;

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
public class DropboxModule extends ConfigurableModule<DropboxModule.Config> {

  public static class Config {
    private String accessToken;

    public String getAccessToken() {
      return accessToken;
    }

    public Config accessToken(String accessToken) {
      this.accessToken = accessToken;
      return this;
    }
  }

  private String accessToken;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @Override
  protected void configure() {
    bind(DropboxService.class).to(DefaultDropboxService.class).in(Scopes.SINGLETON);
  }

  @SuppressWarnings("UnusedDeclaration")
  @Provides
  DbxClientV2 provideDbxClient(Config config) {

    // new DbxClientV2(requestConfig, auth.getAccessToken(), auth.getHost());
    /*
    DbxRequestConfig dbxConfig = DbxRequestConfig.newBuilder("RatpackDropboxModule/1.0")
      .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
      .build();
     */

    StandardHttpRequestor requestor = new StandardHttpRequestor(StandardHttpRequestor.Config.DEFAULT_INSTANCE);
    DbxRequestConfig dbxConfig = DbxRequestConfig.newBuilder("RatpackDropboxModule/1.0")
      .withHttpRequestor(requestor)
      .build();

    String token = accessToken == null ? config.getAccessToken() : accessToken;
    DbxClientV2 client = new DbxClientV2(dbxConfig, token);

    return client;
  }
}
