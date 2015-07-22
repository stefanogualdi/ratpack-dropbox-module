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

package ratpack.modules.dropbox.internal;

import com.dropbox.core.*;
import com.google.inject.Inject;
import ratpack.modules.dropbox.DropboxService;

import java.io.*;

import static ratpack.util.Exceptions.uncheck;

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
public class DefaultDropboxService implements DropboxService {

  private final DbxClient dbxClient;

  @Inject
  DefaultDropboxService(DbxClient dbxClient) {
    this.dbxClient = dbxClient;
  }

  public String accessToken() {
    return dbxClient.getAccessToken();
  }

  public DbxAccountInfo accountInfo() {
    DbxAccountInfo info = null;
    try {
      info = dbxClient.getAccountInfo();
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return info;
  }

  public DbxEntry metadata(String path) {
    DbxEntry entry = null;
    try {
      entry = dbxClient.getMetadata(path);
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return entry;
  }

  public DbxEntry.WithChildren list(String path) {
    DbxEntry.WithChildren listing = null;
    try {
      listing = dbxClient.getMetadataWithChildren(path);
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return listing;
  }

  public DbxEntry.Folder createFolder(String path) {
    DbxEntry.Folder folder = null;
    try {
      folder = dbxClient.createFolder(path);
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return folder;
  }

  public DbxEntry.File upload(File fileToUpload, String uploadFilename) {
    DbxEntry.File uploadedFile = null;

    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(fileToUpload);
    } catch (FileNotFoundException e) {
      throw uncheck(e);
    }

    try {
      uploadedFile = dbxClient.uploadFile(uploadFilename, DbxWriteMode.add(), fileToUpload.length(), inputStream);
    } catch (DbxException | IOException e) {
      throw uncheck(e);
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        throw uncheck(e);
      }
    }

    return uploadedFile;
  }

  public DbxEntry.File download(String filename, String downloadedFilename) {
    DbxEntry.File downloadedFile = null;

    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(downloadedFilename);
      downloadedFile = download(filename, outputStream);
      outputStream.close();
    } catch (IOException e) {
      throw uncheck(e);
    }

    return downloadedFile;
  }

  public DbxEntry.File download(String filename, OutputStream outputStream) {
    DbxEntry.File downloadedFile = null;

    try {
      downloadedFile = dbxClient.getFile(filename, null, outputStream);
    } catch (IOException | DbxException e) {
      throw uncheck(e);
    }

    return downloadedFile;
  }

  public void delete(String filename) {
    try {
      dbxClient.delete(filename);
    } catch (DbxException e) {
      throw uncheck(e);
    }
  }
}
