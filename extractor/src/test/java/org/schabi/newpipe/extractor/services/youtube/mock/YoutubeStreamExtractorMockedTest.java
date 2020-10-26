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
import org.schabi.newpipe.extractor.stream.VideoStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.schabi.newpipe.extractor.ExtractorAsserts.assertIsSecureUrl;
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

    @Test
    public void test439() throws Exception {
        StreamExtractor extractor = getExtractor(
                "https://www.youtube.com/watch?v=tcYodQoapMg&pbj=1", "youtube_stream_439.har");
        assertNotNull(extractor.getName());

        final List<VideoStream> videoStreams = extractor.getVideoStreams();
        final List<VideoStream> videoOnlyStreams = extractor.getVideoOnlyStreams();
        assertNotNull(videoStreams);
        assertNotNull(videoOnlyStreams);
        videoStreams.addAll(videoOnlyStreams);

        assertFalse(videoStreams.isEmpty());
        for (final VideoStream stream : videoStreams) {
            assertIsSecureUrl(stream.getUrl());
            assertFalse(stream.getResolution().isEmpty());

            final int formatId = stream.getFormatId();
            // see MediaFormat: video stream formats range from 0 to 0x100
            assertTrue("format id does not fit a video stream: " + formatId,
                    0 <= formatId && formatId < 0x100);
        }
    }
}
