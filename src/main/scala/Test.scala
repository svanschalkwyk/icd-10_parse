/**
  * Created by steph on 3/31/17.
  */
import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

object Test extends App {

  val host = "elasticsearch://localhost:9200"
  // Here we create an instance of the TCP client
  val client = TcpClient.transport(ElasticsearchClientUri(host))

  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block your thread
  client.execute {
    indexInto("bands" / "artists") fields ("name" -> "coldplay") refresh(RefreshPolicy.IMMEDIATE)
  }.await

  // now we can search for the document we just indexed
  val resp = client.execute {
    search("bands" / "artists") query "coldplay"
  }.await

  println(resp)
}

