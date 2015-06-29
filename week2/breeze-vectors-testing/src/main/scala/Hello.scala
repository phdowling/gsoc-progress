/**
 * Created by dowling on 04/06/15.
 */
import breeze.linalg._

import java.io.File


object Hello{

  def main (args: Array[String]) {
    println("Hello World!")
    val data_dir = args(0)
    val modelpath = data_dir + ".syn0.csv"
    val dictpath = data_dir + ".wordids.txt"
    val w2v = new Word2VecWrapper(modelpath, dictpath)
    println(w2v.get_similarity("affiliated", "advancement"))
    println(w2v.get_similarity("affiliated people".split(" "), "advancement houses".split(" ")))

  }

}
