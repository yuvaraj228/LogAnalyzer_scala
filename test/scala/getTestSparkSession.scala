trait getTestSparkSession{

  import org.apache.spark.sql.SparkSession

  val testSparkSession = SparkSession.builder.master("local").appName("logparsertest").getOrCreate()

}
