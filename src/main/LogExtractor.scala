
import java.io.File
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.joda.time.Interval
import scala.collection.JavaConversions._
import scala.util.{Success, Failure, Try}

class LogExtractor(homePathStr: String, branch: String = Constants.HEAD) {

  private val builder = new FileRepositoryBuilder()

  private val repository = builder
    .readEnvironment()
    .findGitDir( new File(homePathStr) )
    .build()

  private val commitId = repository.resolve(branch)
  private val git = new Git(repository)

  def getLog(interval: Interval) =
    Try {
      Try { git.fetch().call() } match {
        case Failure(e) =>
          println(s"WARNING: commits weren't fetched from origin! Some new changes may be not listed, due to ${e.getMessage}")
        case Success(_) =>
          println("Fetching complete")
      }
      val log = git.log().add(commitId).call().toIterable
      log.filter( commit => interval.contains(commit.getAuthorIdent.getWhen.getTime) )
    }

}