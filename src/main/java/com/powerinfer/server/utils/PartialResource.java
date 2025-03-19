package com.powerinfer.server.utils;

import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class PartialResource implements Resource {
    private final Resource resource;
    private final long start;
    private final long end;

    public PartialResource(Resource resource, long start, long end) {
        this.resource = resource;
        this.start = start;
        this.end = end;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream inputStream = resource.getInputStream();
        inputStream.skip(start);
        return new LimitedInputStream(inputStream, end - start + 1) {
            @Override
            protected void raiseError(long bytesReadSoFar, long limit) throws IOException {
                throw new IOException("Stream limit exceeded. Bytes read: " + bytesReadSoFar + ", Limit: " + limit);
            }
        };
    }

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public URL getURL() throws IOException {
        return resource.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return resource.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public long contentLength() {
        return end - start + 1;
    }

    @Override
    public long lastModified() throws IOException {
        return resource.lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return resource.createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return resource.getFilename();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }
}
