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

import com.dropbox.core.DbxEntry;
import java.io.File;

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
public interface DropboxService {

  String accessToken();

  DbxEntry metadata(String path);

  DbxEntry.WithChildren list(String path);

  DbxEntry.Folder createFolder(String path);

  DbxEntry.File upload(File fileToUpload, String uploadFilename);

  DbxEntry.File download(String filename, String downloadedFilename);

  void delete(String filename);
}