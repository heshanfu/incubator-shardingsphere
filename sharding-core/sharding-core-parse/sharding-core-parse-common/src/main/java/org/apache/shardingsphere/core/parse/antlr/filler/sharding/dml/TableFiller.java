/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.parse.antlr.filler.sharding.dml;

import lombok.Setter;
import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData;
import org.apache.shardingsphere.core.parse.antlr.filler.SQLSegmentFiller;
import org.apache.shardingsphere.core.parse.antlr.filler.ShardingRuleAwareFiller;
import org.apache.shardingsphere.core.parse.antlr.sql.segment.common.TableSegment;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.SQLStatement;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dml.SelectStatement;
import org.apache.shardingsphere.core.parse.parser.context.table.Table;
import org.apache.shardingsphere.core.rule.ShardingRule;

/**
 * Table filler.
 *
 * @author duhongjun
 */
@Setter
public final class TableFiller implements SQLSegmentFiller<TableSegment>, ShardingRuleAwareFiller {
    
    private ShardingRule shardingRule;
    
    @Override
    public void fill(final TableSegment sqlSegment, final SQLStatement sqlStatement, final ShardingTableMetaData shardingTableMetaData) {
        boolean fill = false;
        String tableName = sqlSegment.getName();
        if (shardingRule.contains(tableName) || shardingRule.isBroadcastTable(tableName) || shardingRule.findBindingTableRule(tableName).isPresent()
                || shardingRule.getShardingDataSourceNames().getDataSourceNames().contains(shardingRule.getShardingDataSourceNames().getDefaultDataSourceName())) {
            fill = true;
        } else {
            if (!(sqlStatement instanceof SelectStatement) && sqlStatement.getTables().isEmpty()) {
                fill = true;
            }
        }
        if (fill) {
            sqlStatement.getTables().add(new Table(sqlSegment.getName(), sqlSegment.getAlias()));
            sqlStatement.getSQLTokens().add(sqlSegment.getToken());
        }
    }
}
