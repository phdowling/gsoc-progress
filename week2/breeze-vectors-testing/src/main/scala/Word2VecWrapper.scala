import java.io.File

import breeze.numerics._
import breeze.linalg._


import scala.io.Source

/**
 * Created by dowling on 04/06/15.
 */
class Word2VecWrapper(modelPath: String, dictPath: String) {
  var dict: Map[String, Int] = Source.fromFile(dictPath, "UTF-8").getLines().map { line =>
    val contents = line.split("\t")
    (contents(0), contents(1).toInt)
  }.toMap

  var vectors: DenseMatrix[Double] = csvread(new File(modelPath))


  def lookup(token: String): Transpose[DenseVector[Double]]={
    // look up vector, if it isn't there, simply ignore the word
    // TODO: is this good standard behaviour?
    if(dict.contains(token)){
      vectors(dict(token), ::)
    }else{
      DenseVector.zeros[Double](vectors.cols).t
    }
  }

  def get_similarity(first: String, second:String): Double = {
    // todo: do we need 1 - (lookup(first) * lookup(second).t) ?

    lookup(first) * lookup(second).t
  }

  def get_similarity(first: Array[String], second: Array[String]): Double = {
    val f = first.map(lookup).reduceLeft(_ + _)
    val s = second.map(lookup).reduceLeft(_ + _)

    f * s.t
  }

}
