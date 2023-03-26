import com.hntoplinks.crawler.repositories.ItemRepository
import com.hntoplinks.crawler.{Config, HnApiClient}
import org.h2.tools.Server
import scalikejdbc.*

import java.util.concurrent.{Executors, ScheduledExecutorService}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

@main def main() = {
  inith2()
  initdb()


  Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() => {
    for {
      bestStories <- HnApiClient.getBestStories()
      newStories <- HnApiClient.getNewStories()
      topStories <- HnApiClient.getTopStories()
    } yield {
      //combine all stories
      val allStories = (bestStories ++ newStories ++ topStories).flatten.toSeq.distinct
      allStories.map(itemId => {
        for {
          item <- HnApiClient.getItem(itemId)
        } yield {
          item.map(item => ItemRepository.save(item))
        }
    })
  }
  }, 1, 10, java.util.concurrent.TimeUnit.MINUTES)
}

def inith2(): Unit = {
  if (Config.get().getBoolean("h2.init"))
    val consoleServerPort = Config.get().getInt("h2.console.port")
    val server = Server.createWebServer("-webPort", s"${consoleServerPort}")
    server.start()
}

def initdb(): Unit = {
  val driveClassName = Config.get().getString("datasource.driver-class-name")
  val jdbcUrl        = Config.get().getString("datasource.url")
  val username       = Config.get().getString("datasource.username")
  val password       = Config.get().getString("datasource.password")
  Class.forName(driveClassName)
  ConnectionPool.singleton(jdbcUrl, username, password)
}
