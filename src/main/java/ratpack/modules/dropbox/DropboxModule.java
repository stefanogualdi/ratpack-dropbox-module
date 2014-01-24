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

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ratpack.launch.LaunchConfig;
import ratpack.modules.dropbox.internal.DefaultDropboxService;

import java.util.Locale;

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
public class DropboxModule extends AbstractModule {

  private String accessToken;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @Override
  protected void configure() {
    bind(DropboxService.class).to(DefaultDropboxService.class);
  }

  @SuppressWarnings("UnusedDeclaration")
  @Provides
  DbxClient provideDbxClient(LaunchConfig launchConfig) {
    DbxRequestConfig config = new DbxRequestConfig("RatpackDropboxModule/1.0", Locale.getDefault().toString());

    String token = accessToken == null ? launchConfig.getOther("dropbox.accessToken", "UNDEFINED") : accessToken;

    DbxClient client = new DbxClient(config, token);
    return client;
  }
}
