/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.joshua.decoder.ff;

import java.util.List;

import org.apache.joshua.decoder.chart_parser.SourcePath;
import org.apache.joshua.decoder.ff.state_maintenance.DPState;
import org.apache.joshua.decoder.ff.tm.Rule;
import org.apache.joshua.decoder.hypergraph.HGNode;
import org.apache.joshua.decoder.phrase.Hypothesis;
import org.apache.joshua.decoder.segment_file.Sentence;

import com.typesafe.config.Config;

/**
 * 
 * @author Zhifei Li zhifei.work@gmail.com
 * @author Matt Post post@cs.jhu.edu
 */
public final class WordPenalty extends StatelessFF {

  private float OMEGA = -(float) Math.log10(Math.E); // -0.435
  private final boolean isCky;

  public WordPenalty(Config featureConfig, FeatureVector weights) {
    super("WordPenalty", featureConfig, weights);

    if (featureConfig.hasPath("value")) {
      OMEGA = (float) featureConfig.getDouble("value");
    }
      
    // TODO(fhieber): fix this!
    isCky = true; //decoderConfig.getConfig().getString("search_algorithm").equals("cky");
  }

  @Override
  public DPState compute(Rule rule, List<HGNode> tailNodes, int i, int j, SourcePath sourcePath,
      Sentence sentence, Accumulator acc) {

    if (rule != null) {
      // TODO: this is an inefficient way to do this. Find a better way to not apply this rule
      // to start and stop glue rules when phrase-based decoding.
      if (isCky || (rule != Hypothesis.BEGIN_RULE && rule != Hypothesis.END_RULE)) {
        acc.add(featureId, OMEGA * (rule.getTarget().length - rule.getArity()));
      }
    }
      
    return null;
  }

  @Override
  public float estimateCost(Rule rule, Sentence sentence) {
    if (rule != null)
      return weights.getOrDefault(featureId) * OMEGA * (rule.getTarget().length - rule.getArity());
    return 0.0f;
  }
}
