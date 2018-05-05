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

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.users.FullAccount;
import com.google.inject.Inject;
import ratpack.modules.dropbox.DropboxService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ratpack.util.Exceptions.uncheck;

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
public class DefaultDropboxService implements DropboxService {

  private final DbxClientV2 dbxClient;

  @Inject
  DefaultDropboxService(DbxClientV2 dbxClient) {
    this.dbxClient = dbxClient;
  }

  public FullAccount accountInfo() {
    FullAccount info = null;
    try {
      info = dbxClient.users().getCurrentAccount();
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return info;
  }

  public Metadata metadata(String path) {
    Metadata entry = null;
    try {
      entry = dbxClient.files().getMetadata(path);
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return entry;
  }

  public List<Metadata> list(String path) {
    List<Metadata> result = new ArrayList<>();
    try {
      ListFolderResult listing = dbxClient.files().listFolderBuilder(path).start();
      for (Metadata child : listing.getEntries()) {
        result.add(child);
      }
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return result;
  }

  public FolderMetadata createFolder(String path) {
    FolderMetadata folder = null;
    try {
      CreateFolderResult f = dbxClient.files().createFolderV2(path);
      folder = f.getMetadata();
    } catch (DbxException e) {
      throw uncheck(e);
    }
    return folder;
  }

  public FileMetadata upload(File fileToUpload, String uploadFilename) {

    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(fileToUpload);
    } catch (FileNotFoundException e) {
      throw uncheck(e);
    }

    // Path p = Paths.get(uploadFilename);

    FileMetadata metadata = null;
    try {
      metadata = dbxClient.files().uploadBuilder(uploadFilename).uploadAndFinish(inputStream);
    } catch (DbxException e) {
      throw uncheck(e);
    } catch (IOException e) {
      throw uncheck(e);
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        throw uncheck(e);
      }
    }

    return metadata;
    /*
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
    */
  }

  public FileMetadata download(String filename, String downloadedFilename) {
    FileMetadata downloadedFile = null;

    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(downloadedFilename);
      downloadedFile = download(filename, outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      throw uncheck(e);
    } finally {
      try {
        outputStream.close();
      } catch (IOException e) {
        throw uncheck(e);
      }
    }

    return downloadedFile;
  }

  public FileMetadata download(String filename, OutputStream outputStream) {
    FileMetadata downloadedFile = null;

    try {
      downloadedFile = dbxClient.files().downloadBuilder(filename).download(outputStream);
    } catch (IOException | DbxException e) {
      throw uncheck(e);
    }

    return downloadedFile;
  }

  public String downloadAsString(String filename) {
    String theData = null;

    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      download(filename, os);
      theData = new String(os.toByteArray(), "UTF-8");
      os.flush();
      os.close();
    } catch (IOException e) {
      throw uncheck(e);
    }

    return theData;
  }

  public void delete(String filename) {
    try {
      dbxClient.files().deleteV2(filename);
    } catch (DbxException e) {
      throw uncheck(e);
    }
  }
}
