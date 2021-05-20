/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.sql.planner.iterative.rule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.trino.metadata.TableHandle;
import io.trino.plugin.tpch.TpchTableHandle;
import io.trino.plugin.tpch.TpchTransactionHandle;
import io.trino.spi.connector.SchemaTableName;
import io.trino.spi.type.BigintType;
import io.trino.sql.planner.assertions.PlanMatchPattern;
import io.trino.sql.planner.iterative.rule.test.BaseRuleTest;
import org.testng.annotations.Test;

import java.util.Optional;

import static io.trino.sql.planner.iterative.rule.test.RuleTester.CONNECTOR_ID;

public class TestRemoveEmptyDelete
        extends BaseRuleTest
{
    @Test
    public void testDoesNotFire()
    {
        tester().assertThat(new RemoveEmptyDelete())
                .on(p -> p.tableDelete(
                        new SchemaTableName("sch", "tab"),
                        p.tableScan(
                                new TableHandle(CONNECTOR_ID, new TpchTableHandle("nation", 1.0), TpchTransactionHandle.INSTANCE, Optional.empty()),
                                ImmutableList.of(),
                                ImmutableMap.of()),
                        p.symbol("a", BigintType.BIGINT)))
                .doesNotFire();
    }

    @Test
    public void test()
    {
        tester().assertThat(new RemoveEmptyDelete())
                .on(p -> p.tableDelete(
                        new SchemaTableName("sch", "tab"),
                        p.values(),
                        p.symbol("a", BigintType.BIGINT)))
                .matches(
                        PlanMatchPattern.values(ImmutableMap.of("a", 0)));
    }
}
