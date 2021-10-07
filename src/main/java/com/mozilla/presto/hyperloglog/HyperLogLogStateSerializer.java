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

import com.facebook.presto.common.block.Block;
import com.facebook.presto.common.block.BlockBuilder;
import com.facebook.presto.common.type.Type;
import com.facebook.presto.spi.function.AccumulatorStateSerializer;
import com.twitter.algebird.HyperLogLog;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

public class HyperLogLogStateSerializer
        implements AccumulatorStateSerializer<HyperLogLogState>
{
    @Override
    public Type getSerializedType()
    {
        return HyperLogLogType.HYPER_LOG_LOG;
    }

    @Override
    public void serialize(HyperLogLogState state, BlockBuilder out)
    {
        if (state.getHyperLogLog() == null) {
            out.appendNull();
        }
        else {
            Slice slice = Slices.wrappedBuffer(HyperLogLog.toBytes(state.getHyperLogLog()));
            HyperLogLogType.HYPER_LOG_LOG.writeSlice(out, slice);
        }
    }

    @Override
    public void deserialize(Block block, int index, HyperLogLogState state)
    {
        if (!block.isNull(index)) {
            Slice slice = HyperLogLogType.HYPER_LOG_LOG.getSlice(block, index);
            state.setHyperLogLog(HyperLogLog.fromBytes(slice.getBytes()).toDenseHLL());
        }
    }
}
