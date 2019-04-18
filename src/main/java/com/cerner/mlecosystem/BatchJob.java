/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cerner.mlecosystem;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;

/**
 * Skeleton for a Flink Batch Job.
 *
 * <p>For a tutorial how to write a Flink batch application, check the tutorials and examples on the
 * <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, change the main class in the
 * POM.xml file to this class (simply search for 'mainClass') and run 'mvn clean package' on the
 * command line.
 */
public class BatchJob {

  public static void main(String[] args) throws Exception {

    ParameterTool params = ParameterTool.fromArgs(args);
    // set up the batch execution environment
    final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    env.getConfig().setGlobalJobParameters(params);

    if (!params.has("input")) {
      System.out.println("Invalid number of args, no input");
      System.exit(2);
    }
    if (!params.has("output")) {
      System.out.println("Invalid number of args, no output");
      System.exit(2);
    }
    DataSet<String> text = env.readTextFile(params.get("input"));
    text.flatMap(new Tokenizer()).groupBy(0).sum(1).writeAsCsv(params.get("output"), "\n", " ");
    // execute program
    env.execute("Flink Batch Java API Skeleton");
  }

  public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

    private static final long serialVersionUID = 1L;

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
      String[] tokens = value.toLowerCase().split("\\W+");
      for (String token : tokens) {
        if (token.length() > 0) {
          out.collect(Tuple2.of(token, 1));
        }
      }
    }
  }
}
