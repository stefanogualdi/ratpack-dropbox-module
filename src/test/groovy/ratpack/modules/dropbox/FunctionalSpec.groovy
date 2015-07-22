package ratpack.modules.dropbox

import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.guice.Guice
import ratpack.test.embed.BaseDirBuilder
import spock.lang.AutoCleanup
import spock.lang.Specification

/**
 * @author Stefano Gualdi <stefano.gualdi@gmail.com>
 */
class FunctionalSpec extends Specification {

  final String userId = System.getProperty("userId")
  final String accessToken = System.getProperty("accessToken")

  @AutoCleanup
  @Delegate
  BaseDirBuilder baseDir = tmpDir()

  def fooFile

  @AutoCleanup
  @Delegate
  GroovyEmbeddedApp app = of {
    registry Guice.registry {
      it.module DropboxModule, { c -> c.accessToken(accessToken) }
    }

    handlers {
      get { render("OK") }
      get("token") { DropboxService service ->
        render service.accessToken()
      }
      get("info") { DropboxService service ->
        render service.accountInfo().userId.toString()
      }
      get("upload") { DropboxService service ->
        service.upload(fooFile.toFile(), '/test2/file.txt')
        render "uploaded"
      }
      get("download") { DropboxService service ->
        def data = service.downloadAsString('/test2/file.txt')
        render data
      }
      get("delete") { DropboxService service ->
        service.delete('/test2/file.txt')
        render "deleted"
      }
      get("list") { DropboxService service ->
        render "${service.list('/test2').children.size()}"
      }
    }
  }

  void "simple"() {
    given:
    def response = httpClient.get()

    expect:
    response.statusCode == 200
    response.body.text == "OK"
  }

  void "get the token"() {
    given:
    def response = httpClient.get("token")

    expect:
    response.statusCode == 200
    response.body.text == accessToken
  }

  void "get the account info"() {
    given:
    def response = httpClient.get("info")

    expect:
    response.statusCode == 200
    response.body.text == userId
  }

  void "can upload a file"() {
    given:
    fooFile = file "foo.txt", "dummy text"

    and:
    def response = httpClient.get("upload")

    expect:
    response.statusCode == 200
    response.body.text == "uploaded"
  }

  void "can download as string"() {
    given:
    def response = httpClient.get("download")

    expect:
    response.statusCode == 200
    response.body.text == "dummy text"
  }

  def "can delete a file"() {
    when:
    def response = httpClient.get("delete")

    then:
    response.statusCode == 200
    response.body.text == "deleted"

    when:
    response = httpClient.get("list")

    then:
    response.statusCode == 200
    response.body.text == "0"
  }
}
