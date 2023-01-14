import com.hntoplinks.crawler.Config
import org.h2.tools.Server
import scalikejdbc._
@main def main() = {
  inith2()
  initdb()
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
