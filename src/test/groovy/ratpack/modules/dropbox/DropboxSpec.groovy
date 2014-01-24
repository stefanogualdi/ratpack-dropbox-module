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

package ratpack.modules.dropbox

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
class DropboxSpec extends BaseModuleTestingSpec {

  final String accessToken = System.getProperty("accessToken")

  def "can connect to dropbox"() {
    given:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        render service.accessToken()
      }
    }

    expect:
    getText() == accessToken
  }

  def "can upload a file"() {
    given:
    def fooFile = file "foo.txt", "dummy text"

    and:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        service.upload(fooFile.toFile(), '/test/file.txt')
        render "uploaded"
      }
    }

    expect:
    getText() == "uploaded"
  }

  def "can list folder content"() {
    given:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        render "${service.list('/test').children.size()}"
      }
    }

    expect:
    getText() == "1"
  }

  def "can get file metadata"() {
    given:
    def metadata

    and:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        metadata = service.metadata('/test/file.txt').asFile()
        render "ok"
      }
    }

    expect:
    getText() == "ok"

    and:
    metadata.name == "file.txt"
    metadata.path == "/test/file.txt"
    metadata.numBytes == 10
    metadata.isFile()
  }

  def "can download a file"() {
    given:
    def fooFile = file "downloaded.txt"

    and:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        service.download('/test/file.txt', fooFile.toString())
        render "ok"
      }
    }

    expect:
    getText() == "ok"

    and:
    def file = new File(fooFile.toString())
    file.exists()
    file.text == "dummy text"
  }

  def "can get folder metadata"() {
    given:
    def metadata

    and:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        metadata = service.metadata('/test').asFolder()
        render "ok"
      }
    }

    expect:
    getText() == "ok"

    and:
    metadata.name == "test"
    metadata.path == "/test"
    metadata.isFolder()
  }

  def "can delete a file"() {
    given:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        service.delete('/test/file.txt')
        render "deleted"
      }

      get("list") {
        render "${service.list('/test').children.size()}"
      }
    }

    expect:
    getText() == "deleted"
    getText("list") == "0"
  }

  def "can create a folder"() {
    given:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        service.createFolder('/folder')
        render "created"
      }

      get("metadata") {
        render service.metadata('/folder').name
      }
    }

    expect:
    getText() == "created"
    getText("metadata") == "folder"
  }

  def "can delete a folder"() {
    given:
    modules {
      register new DropboxModule(accessToken: accessToken)
    }

    handlers { DropboxService service ->
      get {
        service.delete('/folder')
        render "deleted"
      }

      get("metadata") {
        render "${service.metadata('/folder')}"
      }
    }

    expect:
    getText() == "deleted"
    getText("metadata") == "null"
  }
}
