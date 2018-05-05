package org.mlt.eso.stores.file;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import java.util.UUID;

public class AggregateKeyFunnel implements Funnel<AggregateKey> {
    @Override
    public void funnel(AggregateKey from, PrimitiveSink into) {
        UUID key = from.getId();
        into.putLong(key.getMostSignificantBits());
        into.putLong(key.getLeastSignificantBits());
        into.putLong(from.getVersion());
    }
}
