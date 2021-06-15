// import required spark classes

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.regexp_extract
import org.apache.spark.sql.functions._
import org.apache.spark.sql.DataFrame

// define main method (Spark entry point)
object LogParser {

  def main(args: Array[String]):Unit= {
    // initialise spark conf and session
    val conf = new SparkConf().setAppName("LogParser").setMaster("local[*]")
    val spark = SparkSession.builder.config(conf).getOrCreate()

    try
      {
        // this will give dataframe with single column "value"
        val base_df = spark.read.text( ("/app/NASA_access_log_Jul95.gz"))
        base_df.printSchema()
        base_df.cache()

        // parse the input dataframe
        val parsed_df = parse_input_dataframe(base_df)

        // cleanse input dataframe
        val cleaned_df= cleanse_input_data(parsed_df)

        //let's check that we don't have any null value
        println("The count of null value : " + cleaned_df.filter((col("host").isNull) || (col("endpoint").isNull) || (col("timestamp").isNull)).count())
        // count before and after
        println("Before : " + parsed_df.count() + " | After : " + cleaned_df.count())

        /* parsing timestamp */
        // spark will return null value if its not able to convert date value
        //convert timestamp column to format spark knows
        val to_timestamp = udf[String,String](parse_clf_time)

        val logs_df = cleaned_df.withColumn("time",to_timestamp(col("timestamp"))).drop("timestamp")
        logs_df.show(10, truncate=false)
        // cache to make it faster
        logs_df.cache()

        // get top n hosts per day
        val n_top_hosts = get_top_n_hosts_per_day(logs_df)
        n_top_hosts.show()

        // get top n hosts url per day
        // TODO - you can combine top n host and urls in to single method
        val n_top_urls = get_top_n_urls_per_day(logs_df)
        n_top_urls.show()

      }catch
      {
        //TODO - this should be improved to handle specific exception instead of catching top level Throwable
        case e:Throwable  => println("unknow exception"+e.printStackTrace())
      }finally {
        // terminate spark context
        spark.stop()

    }

  }

  // convert timestamp in to format which spark knows
  def parse_clf_time(s:String):String={
    var final_var: String = ""
    try
      {
        val month_map = Map(
          "Jan" -> 1, "Feb"-> 2, "Mar"->3, "Apr"->4, "May"->5, "Jun"->6, "Jul"->7,
          "Aug"->8,  "Sep"-> 9, "Oct"->10, "Nov"-> 11, "Dec"-> 12
        )
        //println("string")
        //println(s)
        final_var =  "%3$s-%2$s-%1$s %4$s:%5$s:%6$s".format(
          s.substring(0,2),
          month_map(s.substring(3,6)),
          s.substring(7,11),
          s.substring(12,14),
          s.substring(15,17),
          s.substring(18))
      }
    catch
      {
        // handling any other exception that might come up
        //TODO - this should be improved to handle other type of execption case
        case e:Throwable  => println("some unknown"+e.printStackTrace())
      }

    final_var


  }
  def parse_input_dataframe(base_df:DataFrame): DataFrame ={

    // parsing the log file for host_pattern,url and timestamp
    val host_pattern = "(^\\S+\\.[\\S+\\.]+\\S+)\\s"
    val method_uri_protocol_pattern = "\"(\\S+)\\s(\\S+)\\s*(\\S*)\""
    val ts_pattern = "\\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} -\\d{4})]"

    val parsed_df = base_df.select(regexp_extract(col("value"), host_pattern, 1).alias("host"),
      regexp_extract(col("value"), method_uri_protocol_pattern, 2).alias("endpoint"),
      regexp_extract(col("value"), ts_pattern, 1).alias("timestamp"))

    // display top 10 records
    parsed_df.show(10, truncate=false)
    parsed_df
  }

  def cleanse_input_data (parsed_df:DataFrame): DataFrame ={
    //we have two options , replace the null value with some other meaningful value, or delete the whole line
    // we will go with the other option since those lines are with no value for us
    // we will use na sub package on a dataframe
    //TODO - this method can be enchanced to replace null values with default and aviod removing the data
     parsed_df.na.drop()
  }

  // get top n hosts per day
  def get_top_n_hosts_per_day(logs_df:DataFrame): Unit ={

    // get count of hosts visits per day
    val host_sum_df =logs_df.withColumn("day", dayofyear(col("time")))
      .withColumn("year", year(col("time")))
      .groupBy("day","year","host")
      .count()
      .sort(desc("count"))

    // create a window funtion partitioned by day and ordered by count in descending
    import org.apache.spark.sql.expressions.Window
    val w = Window.partitionBy(col("day")).orderBy(desc("count"))

    // apply the window over input dataframe to assign rank for top counts and filter the top 5 ranks
    // TODO - pass top n as input argument to spark
    host_sum_df.select(col("day"), col("host"), rank().over(w).alias("rank")).filter(col("rank") <=5)

  }

  // get top n urls per day
  def get_top_n_urls_per_day(logs_df:DataFrame): Unit ={

    // get count of url visits per day
    val urls_sum_df =logs_df.withColumn("day", dayofyear(col("time")))
      .withColumn("year", year(col("time")))
      .groupBy("day","year","endpoint")
      .count()
      .sort(desc("count"))

    // create a window funtion partitioned by day and ordered by count in descending
    import org.apache.spark.sql.expressions.Window
    val w = Window.partitionBy(col("day")).orderBy(desc("count"))

    // apply the window over input dataframe to assign rank for top counts and filter the top 5 ranks
    // TODO - pass top n as input argument to spark
    urls_sum_df.select(col("day"), col("endpoint"), rank().over(w).alias("rank")).filter(col("rank") <=5)

  }

}
