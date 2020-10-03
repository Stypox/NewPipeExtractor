package org.schabi.newpipe.extractor.services.youtube.stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeStreamExtractor;

import static org.junit.Assert.assertFalse;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;

public class YoutubeStreamExtractorMockedTest {
    private static YoutubeStreamExtractor extractor;

    @BeforeClass
    public static void setUp() throws Exception {
        final DownloaderTestImpl downloader = DownloaderTestImpl.getInstance();
        downloader.mockRequestsWithHar("youtube_stream_har_3753.har");
        NewPipe.init(downloader);
        extractor = (YoutubeStreamExtractor) YouTube
                .getStreamExtractor("https://m.youtube.com/watch?v=FXxuFk2ZO8Y");
        extractor.fetchPage();
    }

    @AfterClass
    public static void clearDownloaderMock() {
        DownloaderTestImpl.getInstance().clearRequestMocks();
    }

    @Test
    public void testGetTitle() throws ParsingException {
        assertFalse(extractor.getName().isEmpty());
    }
}
