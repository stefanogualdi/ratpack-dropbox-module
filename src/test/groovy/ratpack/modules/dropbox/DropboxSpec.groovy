/*
 * Copyright 2015 the original author or authors.
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

import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.guice.Guice
import ratpack.test.embed.BaseDirBuilder
import ratpack.test.embed.EmbeddedApp
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Specification

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
class DropboxSpec extends Specification {

  final String accessToken = System.getProperty("accessToken")

  @AutoCleanup
  @Delegate
  BaseDirBuilder baseDir = BaseDirBuilder.tmpDir()

  @AutoCleanup
  EmbeddedApp app

  @Delegate
  TestHttpClient client

  def "simple"() {
    given:
    app = GroovyEmbeddedApp.of {
      handlers {
        get {
          render "OK"
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    getText() == "OK"
  }

  def "can connect to dropbox"() {
    given:
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken)}
      }

      handlers {
        get { DropboxService service ->
          render service.accessToken()
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    get().body.text == accessToken
  }

  def "can upload a file"() {
    given:
    def fooFile = file "foo.txt", "dummy text"

    and:
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken)}
      }

      handlers {
        get { DropboxService service ->
          service.upload(fooFile.toFile(), '/test/file.txt')
          render "uploaded"
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    getText() == "uploaded"
  }

  def "can list folder content"() {
    given:
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken) }
      }

      handlers {
        get { DropboxService service ->
          render "${service.list('/test').children.size()}"
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    getText() == "1"
  }

  def "can get file metadata"() {
    given:
    def metadata

    and:
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken) }
      }

      handlers {
        get { DropboxService service ->
          metadata = service.metadata('/test/file.txt').asFile()
          render "ok"
        }
      }
    }

    and:
    client = testHttpClient(app)

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
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken) }
      }

      handlers {
        get { DropboxService service ->
          service.download('/test/file.txt', fooFile.toString())
          render "ok"
        }
      }
    }

    and:
    client = testHttpClient(app)

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
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken) }
      }

      handlers {
        get {  DropboxService service ->
          metadata = service.metadata('/test').asFolder()
          render "ok"
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    getText() == "ok"

    and:
    metadata.name == "test"
    metadata.path == "/test"
    metadata.isFolder()
  }


  def "can delete a file"() {
    given:
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken) }
      }

      handlers {
        get { DropboxService service ->
          service.delete('/test/file.txt')
          render "deleted"
        }

        get("list") { DropboxService service ->
          render "${service.list('/test').children.size()}"
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    getText() == "deleted"
    getText("list") == "0"
  }

  def "can create a folder"() {
    given:
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken) }
      }

      handlers {
        get { DropboxService service ->
          service.createFolder('/folder')
          render "created"
        }

        get("metadata") { DropboxService service ->
          render service.metadata('/folder').name
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    getText() == "created"
    getText("metadata") == "folder"
  }

  def "can delete a folder"() {
    given:
    app = GroovyEmbeddedApp.of {
      registry Guice.registry {
        it.module DropboxModule, { c -> c.accessToken(accessToken) }
      }

      handlers {
        get { DropboxService service ->
          service.delete('/folder')
          render "deleted"
        }

        get("metadata") { DropboxService service ->
          render "${service.metadata('/folder')}"
        }
      }
    }

    and:
    client = testHttpClient(app)

    expect:
    getText() == "deleted"
    getText("metadata") == "null"
  }
}
