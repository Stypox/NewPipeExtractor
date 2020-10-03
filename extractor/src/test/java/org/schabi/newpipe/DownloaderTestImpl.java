package org.schabi.newpipe;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public final class DownloaderTestImpl extends Downloader {
    private static final String USER_AGENT
            = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:68.0) Gecko/20100101 Firefox/68.0";
    private static DownloaderTestImpl instance;

    private final OkHttpClient client;
    private final Map<String, Response> requestMocks;

    private DownloaderTestImpl(final OkHttpClient.Builder builder) {
        client = builder.readTimeout(30, TimeUnit.SECONDS).build();
        requestMocks = new HashMap<>();
    }

    /**
     * It's recommended to call exactly once in the entire lifetime of the application.
     *
     * @param builder if null, default builder will be used
     * @return a new instance of {@link DownloaderTestImpl}
     */
    public static DownloaderTestImpl init(@Nullable final OkHttpClient.Builder builder) {
        instance = new DownloaderTestImpl(
                builder != null ? builder : new OkHttpClient.Builder());
        return instance;
    }

    public static DownloaderTestImpl getInstance() {
        if (instance == null) {
            init(null);
        }
        return instance;
    }


    /**
     * @param harFilename the name of the HAR file put in the folder
     *                    {@code extractor/src/test/resources/}
     */
    public void mockRequestsWithHar(final String harFilename)
            throws FileNotFoundException, JsonParserException {
        File harFile = new File("extractor/src/test/resources/" + harFilename);
        if (!harFile.exists()) {
            harFile = new File("src/test/resources/" + harFilename);
        }
        final JsonObject har = JsonParser.object().from(new FileReader(harFile));

        for (Object o : har.getObject("log").getArray("entries")) {
            final JsonObject entry = (JsonObject) o;
            final String requestUrl = entry.getObject("request").getString("url");

            requestMocks.put(requestUrl, new Response(
                    entry.getObject("response").getInt("status"),
                    entry.getObject("response").getString("statusText"),
                    new HashMap<String, List<String>>(),
                    entry.getObject("response").getObject("content").getString("text"),
                    requestUrl));
        }
    }

    public void clearRequestMocks() {
        requestMocks.clear();
    }


    @Override
    public Response execute(@Nonnull final Request request)
            throws IOException, ReCaptchaException {
        if (requestMocks.containsKey(request.url())) {
            new Exception("Mocking request to " + request.url()).printStackTrace();
            return requestMocks.get(request.url());
        }

        final String httpMethod = request.httpMethod();
        final String url = request.url();
        final Map<String, List<String>> headers = request.headers();
        final byte[] dataToSend = request.dataToSend();

        RequestBody requestBody = null;
        if (dataToSend != null) {
            requestBody = RequestBody.create(null, dataToSend);
        }

        final okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .method(httpMethod, requestBody).url(url)
                .addHeader("User-Agent", USER_AGENT);

        for (Map.Entry<String, List<String>> pair : headers.entrySet()) {
            final String headerName = pair.getKey();
            final List<String> headerValueList = pair.getValue();

            if (headerValueList.size() > 1) {
                requestBuilder.removeHeader(headerName);
                for (String headerValue : headerValueList) {
                    requestBuilder.addHeader(headerName, headerValue);
                }
            } else if (headerValueList.size() == 1) {
                requestBuilder.header(headerName, headerValueList.get(0));
            }

        }

        final okhttp3.Response response = client.newCall(requestBuilder.build()).execute();

        if (response.code() == 429) {
            response.close();

            throw new ReCaptchaException("reCaptcha Challenge requested", url);
        }

        final ResponseBody body = response.body();
        String responseBodyToReturn = null;

        if (body != null) {
            responseBodyToReturn = body.string();
        }

        final String latestUrl = response.request().url().toString();
        return new Response(response.code(), response.message(), response.headers().toMultimap(),
                responseBodyToReturn, latestUrl);
    }
}
