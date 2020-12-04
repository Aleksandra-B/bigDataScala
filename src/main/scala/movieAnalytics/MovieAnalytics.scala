package movieAnalytics

import java.io.{BufferedWriter, FileWriter}

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

import scala.io.Source

case class Movies(userId: Int, itemId: Int, rating: Int, timestamp: Long)

case class MoviesStatJson(hist_film: Seq[Int], hist_all: Seq[Int])

object MoviesStatJson {
  implicit val decodeMoviesStat: Decoder[MoviesStatJson] =
    Decoder.forProduct2("hist_film", "hist_all")(MoviesStatJson.apply)

  implicit val encodeMoviesStat: Encoder[MoviesStatJson] =
    Encoder.forProduct2("hist_film", "hist_all")(u => (u.hist_film, u.hist_all))
}

object FileReader {
  val sep = "\\s+"

  def readInFile(fileName: String): List[Movies] = {

    val bufferedSource = Source.fromFile(fileName)

    val names: List[Movies] = bufferedSource.getLines.toList.map { line =>
      line.trim.split(sep) match {
        case Array(uId, iId, rating, ts) =>
          Movies(uId.toInt, iId.toInt, rating.toInt, ts.toLong)
      }
    }
    bufferedSource.close
    names
  }
}

object WriterJson {
  val file = "src/out/movies_spark_res.json"
  val file2 = "src/out/movies_spark_res_circe.json"
  val writer = new BufferedWriter(new FileWriter(file))
  val writer2 = new BufferedWriter(new FileWriter(file2))

  def writeMoviesToJson(m: Seq[Int], m2: Seq[Int]): Unit = {
    writer.write("{\n  \"hist_film\" : [\n")
    m.take(4).foreach(k => writer.write("\t" + k + ",\n"))
    writer.write("\t" + m.last + "\n\t],\n  \"hist_all\" : [\n")
    m2.take(4).foreach(k => writer.write("\t" + k + ",\n"))
    writer.write("\t" + m2.last + "\n\t]\n}")
    writer.close()
  }

  def writeJsonToFile(m: Json): Unit = {
    writer2.write(m.toString())
    writer2.close()
  }
}

object MovieAnalytics extends App {
  val filename = "src/main/resources/ml-100k/u.data"
  val variant = 1540

  def getStatistics(movies: List[Movies], variant: Option[Int]): Seq[Int] = {
    val stat = variant match {
      case Some(v) =>
        movies.filter(m => m.itemId.equals(v)).map(_.rating)
      case _ => movies.map(_.rating)
    }
    stat.groupBy(identity).mapValues(_.size).toSeq.sortBy(_._1).map(_._2)
  }

  val moviesList = FileReader.readInFile(filename)
  val stat =
    moviesList.filter(movies => movies.itemId.equals(variant)).map(_.rating)
  val statMovie = getStatistics(moviesList, Some(variant))
  val statAll = getStatistics(moviesList, None)

  val moviesStat = MoviesStatJson(statMovie, statAll)
  val moviesStatJson = moviesStat.asJson

  println(moviesStatJson)

  movieAnalytics.WriterJson.writeMoviesToJson(statMovie, statAll)
  WriterJson.writeJsonToFile(moviesStatJson)
}
