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

package com.mozilla.presto.hyperloglog;

import com.facebook.presto.common.type.StandardTypes;
import com.facebook.presto.spi.function.Description;
import com.facebook.presto.spi.function.ScalarFunction;
import com.facebook.presto.spi.function.ScalarOperator;
import com.facebook.presto.spi.function.SqlType;
import com.twitter.algebird.DenseHLL;
import com.twitter.algebird.HyperLogLog;
import com.twitter.algebird.HyperLogLogMonoid;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import static com.facebook.presto.common.function.OperatorType.CAST;

//import static com.facebook.presto.spi.function.OperatorType.CAST;

public final class HyperLogLogScalarFunctions
{
    private HyperLogLogScalarFunctions(){}

    @Description("Returns the approximate cardinality of a HLL")
    @ScalarFunction("hll_cardinality")
    @SqlType(StandardTypes.BIGINT)
    public static long hllCardinality(@SqlType(HyperLogLogType.TYPE) Slice hll)
    {
        return (Long) HyperLogLog.fromBytes(hll.getBytes()).approximateSize().estimate();
    }

    @Description("Create a HLL from a string")
    @ScalarFunction("hll_create")
    @SqlType(HyperLogLogType.TYPE)
    public static Slice hllCreate(@SqlType(StandardTypes.VARCHAR) Slice string, @SqlType(StandardTypes.BIGINT) long bits)
    {
        HyperLogLogMonoid monoid = new HyperLogLogMonoid((int) bits);
        DenseHLL hll = monoid.create(string.getBytes()).toDenseHLL();
        return Slices.wrappedBuffer(HyperLogLog.toBytes(hll));
    }

    @ScalarOperator(CAST)
    @SqlType(HyperLogLogType.TYPE)
    public static Slice castFromVarbinary(@SqlType(StandardTypes.VARBINARY) Slice slice)
    {
        return slice;
    }

    @ScalarOperator(CAST)
    @SqlType(StandardTypes.VARBINARY)
    public static Slice castToVarbinary(@SqlType(HyperLogLogType.TYPE) Slice slice)
    {
        return slice;
    }
}
