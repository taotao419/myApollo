package com.example.my.apollo.internals;

import java.util.Properties;

import com.example.my.apollo.core.ConfigConsts;
import com.example.my.apollo.enums.ConfigSourceType;
import com.google.common.base.Joiner;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RemoteConfigRepository
 */
public class RemoteConfigRepository extends AbstractConfigRepository {

    private static final Logger logger = LoggerFactory.getLogger(RemoteConfigRepository.class);
    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);
    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");
    private static final Escaper pathEscaper = UrlEscapers.urlPathSegmentEscaper();
    private static final Escaper queryParamEscaper = UrlEscapers.urlFormParameterEscaper();

    @Override
    public Properties getConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUpstreamRepository(ConfigRepository upstreamConfigRepository) {
        // TODO Auto-generated method stub

    }

    @Override
    public ConfigSourceType getSourceType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void sync() {
        // TODO Auto-generated method stub

    }

}