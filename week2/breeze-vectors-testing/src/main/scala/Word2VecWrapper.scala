import java.io.File

import breeze.numerics._
import breeze.linalg._
import com.github.tototoshi.csv._


import scala.io.Source

/**
 * Created by dowling on 04/06/15.
 */
class Word2VecWrapper(modelPath: String, dictPath: String) {
  println("Read dict..")
  var dict: Map[String, Int] = Source.fromFile(dictPath, "UTF-8").getLines().map { line =>
    val contents = line.split("\t")
    (contents(0), contents(1).toInt)
  }.toMap

  println("Read weights..")
  val matrixSource = io.Source.fromFile(modelPath)
  val lines = matrixSource.getLines()
  val rows = lines.next().substring(2).toInt
  val cols = lines.next().substring(2).toInt
  var vectors: DenseMatrix[Double] = DenseMatrix.zeros[Double](rows, cols)
  lines.zipWithIndex.foreach{case (row_str, row_idx) =>
    if(row_idx % 10000 == 0)
      println("At row " + row_idx)
    val vals = row_str.split(",").map(_.trim).map(_.toDouble)
    vals.zipWithIndex.foreach{case (value, col_idx) => vectors(row_idx, col_idx) = value}
  }
  matrixSource.close()

  //val reader = CSVReader.open(new File("sample.csv")).toStream()
  //val head_str = reader.head()
  //var vectors: DenseMatrix[Double] = DenseMatrix.zeros[Double](4000000, 100)
  //reader.zipWithIndex.foreach{ case (row, row_idx) =>
  //  row.map(_.toDouble).zipWithIndex.foreach{ case (value, col_idx) =>
  //
  //  }
  //}


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
