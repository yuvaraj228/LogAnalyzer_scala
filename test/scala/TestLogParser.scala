//TODO - testing is pending

class TestLogParser extends FlatSpec with getTestSparkSession{

  "cleanse_input_data" should "drop the nulls in the df" in {
    val input_df = TestData.getInputDfWithNull
    val expected_df = TestData.getExpectedDfWithoutNull
    val return_df = LogParser.cleanse_input_data(input_df)

    assert(return_df.collect().sameElements(expected_df.collect()))
  }

  "parse_input_dataframe" should "should retrun host,endpoint and timestamp for base_df" in {
    val base_df = TestData.getBaseDf
    val expected_parsed_df = TestData.getExpectedParsedDf
    val return_df = LogParser.parse_input_dataframe(base_df)

    assert(return_df.collect().sameElements(expected_parsed_df.collect()))

  }

  "parse_clf_time" should "should timestamp to format which spark understands" in {
    val input_string = "[01 / Aug / 1995: 00: 00: 23 -0400]"
    val expected_string = "1995-07-01 00:00:23"
    val return_string = LogParser.parse_clf_time(input_string)

    assert(input_string == return_string)

  }


  "get_top_n_hosts_per_day" should "should print top n hosts per day" in {
    val input_df = TestData.getLogsDf
    val expected_df = TestData.expectedTopNHostsDf
    val return_string = LogParser.get_top_n_hosts_per_day(input_df)

    assert(return_df.collect().sameElements(expected_df.collect()))

  }

  "get_top_n_urls_per_day" should "should print top n urls per day" in {
    val input_df = TestData.getLogsDf
    val expected_df = TestData.getExpectedTopNUrls
    val return_string = LogParser.get_top_n_hosts_per_day(input_df)

    assert(return_df.collect().sameElements(expected_df.collect()))

  }

  // TODO you can include few more test scenarios
  //TODO test cases to validate the exception handling



}