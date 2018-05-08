package com.hmy.bigfiledownloader.okhttp;



import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

/**
 * Created by wesley on 2018/1/30.
 */

public class GetLengthInterceptor implements Interceptor {

    public GetLengthInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Response originalResponse = chain.proceed(chain.request());
        if (chain.request().header("isJustForContentLen") != null){
            return originalResponse.newBuilder().body(
                    new ResponseBody() {
                @Override
                public MediaType contentType() {
                    return null;
                }

                @Override
                public long contentLength() {
                    long contentLength = Long.parseLong(originalResponse.header("Content-Length"));
                    return contentLength;
                }

                @Override
                public BufferedSource source() {
                    return Okio.buffer(new Source() {
                        @Override
                        public long read(Buffer sink, long byteCount) throws IOException {
                            return -1;
                        }

                        @Override
                        public Timeout timeout() {
                            return null;
                        }

                        @Override
                        public void close() throws IOException {

                        }
                    });
                }
            }).build();
        }else {
            return originalResponse;
        }
    }
}
