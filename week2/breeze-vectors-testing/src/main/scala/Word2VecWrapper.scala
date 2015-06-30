import java.io.File

import breeze.numerics._
import breeze.linalg._
import com.github.tototoshi.csv._


import scala.io.Source

/**
 * Created by dowling on 04/06/15.
 */

def cosine_similarity(vector1: Transpose[DenseVector[Double]], vector2: Transpose[DenseVector[Double]]): Double = {
  (vector1 * vector2.t) /
    (sqrt(vector1 * vector1.t) * sqrt(vector2 * vector2.t))
}


class Word2VecWrapper(modelPath: String, dictPath: String) {
  println("Read dict..")
  var dict: Map[String, Int] = Source.fromFile(dictPath, "UTF-8").getLines().map { line =>
    val contents = line.split("\t")
    (contents(0), contents(1).toInt)
  }.toMap

  println("Read weights..")
  val vectors: DenseMatrix[Double] = read_weights_csv

  def read_weights_csv: DenseMatrix[Double] = {
    val matrixSource = io.Source.fromFile(modelPath)
    val lines = matrixSource.getLines()
    val rows = lines.next().substring(2).toInt
    val cols = lines.next().substring(2).toInt
    println("Allocating " + rows + "x" + cols + " matrix..")
    val vectors: DenseMatrix[Double] = DenseMatrix.zeros[Double](rows, cols)
    println("Reading CSV and writing to matrix...")
    lines.zipWithIndex.foreach { case (row_str, row_idx) =>
      if (row_idx % 10000 == 0)
        println("At row " + row_idx)
      val values = row_str.split(",").map(_.trim).map(_.toDouble)
      values.zipWithIndex.foreach { case (value, col_idx) => vectors(row_idx, col_idx) = value }
    }
    matrixSource.close()
    vectors
  }


  def lookup(token: String): Transpose[DenseVector[Double]]={
    // look up vector, if it isn't there, simply ignore the word
    // TODO: is this good standard behaviour?
    if(dict.contains(token)){
      vectors(dict(token), ::)
    }else{
      println("Warning: token " + token + " not in dictionary! Lookup returning null vector.")
      DenseVector.zeros[Double](vectors.cols).t
    }
  }

  def get_similarity(first: String, second:String): Double = {
    // todo: do we need 1 - (lookup(first) * lookup(second).t) ?

    cosine_similarity(lookup(first), lookup(second))
  }

  def get_similarity(first: Array[String], second: Array[String]): Double = {
    val f = first.map(lookup).reduceLeft(_ + _)
    val s = second.map(lookup).reduceLeft(_ + _)

    cosine_similarity(f, s)
  }

}
