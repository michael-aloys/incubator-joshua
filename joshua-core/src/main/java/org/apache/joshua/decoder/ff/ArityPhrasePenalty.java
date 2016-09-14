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
import org.apache.joshua.decoder.ff.tm.OwnerId;
import org.apache.joshua.decoder.ff.tm.OwnerMap;
import org.apache.joshua.decoder.ff.tm.Rule;
import org.apache.joshua.decoder.hypergraph.HGNode;
import org.apache.joshua.decoder.segment_file.Sentence;

import com.typesafe.config.Config;

/**
 * This feature function counts rules from a particular grammar (identified by the owner) having an
 * arity within a specific range. It expects three parameters upon initialization: the owner, the
 * minimum arity, and the maximum arity.
 * 
 * @author Matt Post post@cs.jhu.edu
 * @author Zhifei Li zhifei.work@gmail.com
 */
public class ArityPhrasePenalty extends StatelessFF {

  // when the rule.arity is in the range, then this feature is activated
  private final OwnerId owner;
  private final int minArity;
  private final int maxArity;

  public ArityPhrasePenalty(Config featureConfig, FeatureVector weights) {
    super("ArityPenalty", featureConfig, weights);
    this.owner = OwnerMap.register(this.featureConfig.getString("owner"));
    this.minArity = this.featureConfig.getInt("min_arity");
    this.maxArity = this.featureConfig.getInt("max_arity");
  }

  /**
   * Returns 1 if the arity penalty feature applies to the current rule.
   */
  private int isEligible(final Rule rule) {
    if (this.owner.equals(rule.getOwner()) && rule.getArity() >= this.minArity
        && rule.getArity() <= this.maxArity)
      return 1;

    return 0;
  }

  @Override
  public DPState compute(Rule rule, List<HGNode> tailNodes, int i, int j, SourcePath sourcePath,
      Sentence sentence, Accumulator acc) {
    acc.add(featureId, isEligible(rule));
    
    return null;
  }
}
