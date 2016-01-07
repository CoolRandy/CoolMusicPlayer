package com.android.volley.toolbox;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by admin on 2016/1/7.
 */
public class InflatingEntity extends HttpEntityWrapper{

    public InflatingEntity(HttpEntity wrappedEntity) {
        super(wrappedEntity);
    }

    @Override
    public InputStream getContent() throws IOException {
        return new GZIPInputStream(wrappedEntity.getContent());
    }

    @Override
    public long getContentLength() {
        return wrappedEntity.getContentLength();
    }
}
