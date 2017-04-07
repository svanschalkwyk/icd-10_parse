import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.analyzers.StopAnalyzer
import com.sksamuel.elastic4s.{ElasticsearchClientUri, TcpClient}


object Create_Index extends App {
  import org.elasticsearch.common.settings.Settings
  val settings = Settings.builder().put("cluster.name", "docker-cluster").build()
  val client = TcpClient.transport(settings, ElasticsearchClientUri("elasticsearch://172.20.0.2:9300,172.20.0.3:9300"))


  //val host = "elasticsearch://localhost:9200?cluster.name=docker-cluster"
  // Here we create an instance of the TCP client
  //val client = TcpClient.transport(ElasticsearchClientUri(host))

  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block your thread
  val response = client.execute {
    createIndex {
      "places2"
    }.mappings(mapping("cities")  as (
        keywordField("id"),
        textField("name") boost 4,
        textField("content") analyzer StopAnalyzer,
        longField("long")

      )
    )
  }.await

  println(response)
}