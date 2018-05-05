package ratpack.modules.dropbox

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.StandardHttpRequestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.users.FullAccount
import ratpack.modules.dropbox.internal.DefaultDropboxService
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class DropboxServiceSpec extends Specification {

  final String accessToken = System.getProperty("accessToken")

  @Shared
  DropboxService service

  def setup() {
    StandardHttpRequestor requestor = new StandardHttpRequestor(StandardHttpRequestor.Config.DEFAULT_INSTANCE);
    DbxRequestConfig dbxConfig = DbxRequestConfig.newBuilder("RatpackDropboxModule/1.0")
      .withHttpRequestor(requestor)
      .build()
    DbxClientV2 client = new DbxClientV2(dbxConfig, accessToken)

    /*
    DbxRequestConfig dbxConfig = new DbxRequestConfig("RatpackDropboxModule/1.0", Locale.getDefault().toString());
    DbxClient dbxClient = new DbxClient(dbxConfig, accessToken)
    */
    service = new DefaultDropboxService(client)
  }

  def "check account"() {
    when:
    def account = service.accountInfo()

    then:
    account.name.displayName == "Stefano Gualdi"
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
    FileMetadata f = files[0]
    f.name == 'opera.html'
  }
}
