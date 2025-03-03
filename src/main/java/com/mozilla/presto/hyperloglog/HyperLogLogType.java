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
import com.facebook.presto.common.function.SqlFunctionProperties;
import com.facebook.presto.common.type.AbstractVariableWidthType;
import com.facebook.presto.common.type.SqlVarbinary;
import com.facebook.presto.common.type.TypeSignature;
import io.airlift.slice.Slice;

public class HyperLogLogType extends AbstractVariableWidthType {

    public static final HyperLogLogType HYPER_LOG_LOG = new HyperLogLogType();
    public static final String TYPE = "HLL";

    private HyperLogLogType() {
        super(new TypeSignature(HyperLogLogType.TYPE), Slice.class);
    }

    protected HyperLogLogType(TypeSignature signature) {
        super(signature, Slice.class);
    }

    @Override
    public void appendTo(Block block, int position, BlockBuilder blockBuilder) {
        if (block.isNull(position)) {
            blockBuilder.appendNull();
        } else {
            block.writeBytesTo(position, 0, block.getSliceLength(position), blockBuilder);
            blockBuilder.closeEntry();
        }
    }

    @Override
    public Object getObjectValue(SqlFunctionProperties properties, Block block, int position) {
        if (block.isNull(position)) {
            return null;
        }

        return new SqlVarbinary(block.getSlice(position, 0, block.getSliceLength(position)).getBytes());
    }

    public Slice getSlice(Block block, int position) {
        return block.getSlice(position, 0, block.getSliceLength(position));
    }

    public void writeSlice(BlockBuilder blockBuilder, Slice value) {
        writeSlice(blockBuilder, value, 0, value.length());
    }

    public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length) {
        blockBuilder.writeBytes(value, offset, length).closeEntry();
    }

}
