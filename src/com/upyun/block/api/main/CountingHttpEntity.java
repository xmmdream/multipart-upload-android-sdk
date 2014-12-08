package com.upyun.block.api.main;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import com.belladati.httpclientandroidlib.HttpEntity;
import com.belladati.httpclientandroidlib.entity.HttpEntityWrapper;


public class CountingHttpEntity extends HttpEntityWrapper {
	

    public static interface ProgressListener {
        void transferred(long transferedBytes, long totalBytes);
    }

    static class CountingOutputStream extends FilterOutputStream {

        private final ProgressListener listener;
        private long transferred;
        private long totalBytes;

        CountingOutputStream(final OutputStream out, final ProgressListener listener, final long bytesSended, final long totalBytes) {
            super(out);
            this.listener = listener;
            this.transferred = bytesSended;
            this.totalBytes = totalBytes;
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            //// NO, double-counting, as super.write(byte[], int, int) delegates to write(int).
            //super.write(b, off, len);
            out.write(b, off, len);            
            this.transferred += len;
            this.listener.transferred(this.transferred, this.totalBytes);
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred, this.totalBytes);
        }
    }

    private final ProgressListener listener;
    private final long bytesSended;
    private final long totalBytes;

    public CountingHttpEntity(final HttpEntity entity, final ProgressListener listener, final long bytesSended, final long totalBytes) {
        super(entity);
        this.listener = listener;
        this.bytesSended = bytesSended;
        this.totalBytes = totalBytes;
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        this.wrappedEntity.writeTo(out instanceof CountingOutputStream? out: new CountingOutputStream(out, this.listener, this.bytesSended, this.totalBytes));
    }

}
