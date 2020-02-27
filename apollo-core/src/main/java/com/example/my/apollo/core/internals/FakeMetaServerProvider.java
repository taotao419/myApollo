package com.example.my.apollo.core.internals;

import com.example.my.apollo.core.enums.Env;
import com.example.my.apollo.core.spi.MetaServerProvider;

/**
 * FakeMetaServerProvider
 */
public class FakeMetaServerProvider implements MetaServerProvider {

    @Override
    public int getOrder() {
        return 101;
    }

    @Override
    public String getMetaServerAddress(Env targetEnv) {
        return null;
    }
}