import java.io.File

import breeze.numerics._
import breeze.linalg._

import scala.collection.mutable
import scala.io.Source

/**
 * Created by dowling on 04/06/15.
 */
class Word2VecWrapper(modelPath: String, dictPath: String) {
  var vectors: DenseMatrix[Double] = csvread(new File(modelPath))
  var dict: mutable.HashMap[String, Int] = mutable.HashMap()
  read_dict(dictPath)

  def get_similarity(first: String, second:String): Double = {
    val f = vectors(dict(first),0 to vectors.cols-1)
    val s = vectors(dict(second), 0 to vectors.cols-1)
    f * s.t
  }

  def get_similarity(first: Array[String], second: Array[String]): Double = {
    val f = first.map( s => {vectors(dict(s), 0 to vectors.cols - 1)}).reduceLeft(_ + _)
    val s = second.map( s => {vectors(dict(s), 0 to vectors.cols - 1)}).reduceLeft(_ + _)

    f * s.t
  }

  def read_dict(dictPath:String): Unit ={
    Source.fromFile(dictPath).getLines().foreach(line => {
        val contents = line.split("\t")
        dict(contents(0)) = contents(1).toInt
      }
    )
  }
}
