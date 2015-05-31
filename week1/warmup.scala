// run scala 2.9.x
//:cp /Users/dowling/Development/gsoc/spotlight-model-editor/target/idio-spotlight-model-0.1.0-jar-with-dependencies.jar

var spotlightModel = org.idio.dbpedia.spotlight.Main.getSpotlightModel("/Users/dowling/Development/gsoc/en_2+2/model");

var context_nnz = Vector[(Int, Int)]();
var context_count_sums = Vector[(Int, Int)]();

spotlightModel.customContextStore.contextStore.tokens.indices.foreach { j=>
  if(j % 100000 == 0){
  	println("At "+ j)
  };
  
  //var vec = Array[(String,Int)]();

  if(spotlightModel.customContextStore.contextStore.tokens(j) != null){
  	
  	context_nnz = context_nnz :+ ((j, spotlightModel.customContextStore.contextStore.counts(j).length));
  	context_count_sums = context_count_sums :+ ((j, spotlightModel.customContextStore.contextStore.counts(j).map(
  		spotlightModel.customContextStore.getCountFromQuantiziedValue).sum)
    );
  	
    //var counts = spotlightModel.customContextStore.contextStore.counts(j).map(spotlightModel.customContextStore.getCountFromQuantiziedValue);
  	//var tokens = spotlightModel.customContextStore.contextStore.tokens(j).map(lookupName);
  	//vec = tokens zip counts;
  
  } else{
  	context_nnz = context_nnz :+ ((j, 0));
  	context_count_sums = context_count_sums :+ ((j, 0));
  };
  
};


var sorted_nnz = scala.util.Sorting.stableSort(context_nnz, (e1: Tuple2[Int, Int], e2: Tuple2[Int, Int]) => e1._2 < e2._2);
var sorted_count_sums = scala.util.Sorting.stableSort(context_count_sums, (e1: Tuple2[Int, Int], e2: Tuple2[Int, Int]) => e1._2 < e2._2);

var most10_nnz = sorted_nnz.takeRight(10);
var most10_sums = sorted_count_sums.takeRight(10);
var least10_nnz = sorted_nnz.take(10);
var least10_sums = sorted_count_sums.take(10);

var convert_id_to_name = spotlightModel.customDbpediaResourceStore.resStore.uriForID;
def convert_iterable(it:Array[(Int, Int)]) : Array[(String, Int)] = {
    it map Function.tupled((id, score) => (convert_id_to_name(id), score));
};

var most10_nnz_names = convert_iterable(most10_nnz);
var most10_sums_names = convert_iterable(most10_sums);
var least10_nnz_names = convert_iterable(least10_nnz);
var least10_sums_names = convert_iterable(least10_sums);

def format_output(it:Array[(String, Int)]): Unit = {
  it map Function.tupled((name, score) => println(name + " -> "+ score));
  return Unit;
}

println("Entities with most nnz context entries:");
format_output(most10_nnz_names);
println("Entities with highest context sums:");
format_output(most10_sums_names);



