import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame,Row}

object TestData extends getTestSparkSession{

  val inputDataListWithNull:List [List[Any]] = (List("10.128.2.1","29/Nov/2017:06:58:55","GET /login.php HTTP/1.1",200),
    List("10.128.2.1","","GET /login.php HTTP/1.1",200))

  val input_schema:StructType = StructType(
    List(
      StructField("IP",StringType,true),
      StructField("Time",StringType,false),
      StructField("URL",StringType,true),
      StructField("Status",IntegerType,true),
    )

  )

  def getInputDfWithNull:DataFrame = {
    testSparkSession.createDateFrame(
      testSparkSession.sparkContext,parallelize(inputDataListWithNull.map(Row.fromSeq(_))),
      input_schema
    )

  }


  val expectedDataList:List [List[Any]] = (List("10.128.2.1","29/Nov/2017:06:58:55","GET /login.php HTTP/1.1",200))

  def getExpectedDfWithoutNull:DataFrame = {
    testSparkSession.createDateFrame(
      testSparkSession.sparkContext,parallelize(expectedDataList.map(Row.fromSeq(_))),
      input_schema
    )

  }


  val baseDataList:List [List[Any]] = (List("10.128.2.1","29/Nov/2017:06:58:55","GET /login.php HTTP/1.1",200),
    List("10.128.2.1","30/Nov/2017:06:58:55","GET /login.php HTTP/1.1",200))


  def getBaseDf:DataFrame = {
    testSparkSession.createDateFrame(
      testSparkSession.sparkContext,parallelize(baseDataList.map(Row.fromSeq(_))),
      input_schema
    )

  }

  val expectedParsedData:List [Any] = ("10.128.2.1","29/Nov/2017:06:58:55","/login.php"))

  def getExpectedParsedDf:DataFrame = {
    testSparkSession.createDateFrame(
      testSparkSession.sparkContext,parallelize(expectedParsedData.map(Row.fromSeq(_))),
      input_schema
    )

  }


  val logsDataList:List [List[Any]] = (List("10.128.2.1","1995-07-01 00:00:23","/login.php"),
    List("10.128.2.1","1995-08-01 00:00:23","/login.php"))


  def getLogsDf:DataFrame = {
    testSparkSession.createDateFrame(
      testSparkSession.sparkContext,parallelize(logsDataList.map(Row.fromSeq(_))),
      input_schema
    )

  }

  val expectedTopNHostsDf:List [Any] = (("193","10.128.2.1","1"),("193","11.128.2.1","2"))

  def getExpectedTopNHosts:DataFrame = {
    testSparkSession.createDateFrame(
      testSparkSession.sparkContext,parallelize(expected_top_n_hosts_df.map(Row.fromSeq(_))),
      input_schema
    )

  }

  val expectedTopNUrlsDf:List [Any] = (("193","/login.php","1"),("193","/login2.php","2"))

  def getExpectedTopNUrls:DataFrame = {
    testSparkSession.createDateFrame(
      testSparkSession.sparkContext,parallelize(expected_top_n_urls_df.map(Row.fromSeq(_))),
      input_schema
    )

  }

}