package ratpack.modules.dropbox

import com.dropbox.core.DbxClient
import com.dropbox.core.DbxEntry
import com.dropbox.core.DbxRequestConfig
import ratpack.modules.dropbox.internal.DefaultDropboxService
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class DropboxServiceSpec extends Specification {

  final String accessToken = System.getProperty("accessToken")

  @Shared
  DropboxService service

  def setup() {
    DbxRequestConfig dbxConfig = new DbxRequestConfig("RatpackDropboxModule/1.0", Locale.getDefault().toString());
    DbxClient dbxClient = new DbxClient(dbxConfig, accessToken)
    service = new DefaultDropboxService(dbxClient)
  }

  def "check token"() {
    when:
    def token = service.accessToken()

    then:
    token == accessToken
  }

  @Ignore
  def "list a folder"() {
    when:
    def files = service.list('/test1').children

    then:
    files.size() > 0
  }

  @Ignore
  def "check for file name"() {
    when:
    def files = service.list('/test1').children

    then:
    DbxEntry.File f = files[0]
    f.name == 'opera.html'
  }
}
