package org.schabi.newpipe.extractor.services.youtube.mock;

import com.grack.nanojson.JsonParserException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeStreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamExtractor;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;

public class YoutubeStreamExtractorMockedTest {
    private static StreamExtractor getExtractor(final String url, final String har)
            throws IOException, JsonParserException, ExtractionException {
        final DownloaderTestImpl downloader = DownloaderTestImpl.getInstance();
        downloader.mockRequestsWithHar(har);
        NewPipe.init(downloader);

        final StreamExtractor extractor = YouTube.getStreamExtractor(url);
        extractor.fetchPage();
        downloader.clearRequestMocks();
        return extractor;
    }

    @Test
    public void test3753() throws Exception {
        StreamExtractor extractor = getExtractor(
                "https://m.youtube.com/watch?v=FXxuFk2ZO8Y", "youtube_stream_3753.har");
        assertNotNull(extractor.getName());
    }

    @Test
    public void test4278() throws Exception {
        StreamExtractor extractor = getExtractor(
                "https://www.youtube.com/embed/tPADtMN8lkI", "youtube_stream_4278.har");
        assertNotNull(extractor.getName());
    }
}
