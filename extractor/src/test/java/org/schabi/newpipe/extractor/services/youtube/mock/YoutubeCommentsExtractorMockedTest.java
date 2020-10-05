package org.schabi.newpipe.extractor.services.youtube.mock;

import com.grack.nanojson.JsonParserException;

import org.junit.Test;
import org.schabi.newpipe.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;

public class YoutubeCommentsExtractorMockedTest {
    private static CommentsExtractor getExtractor(final String url, final String har)
            throws IOException, JsonParserException, ExtractionException {
        final DownloaderTestImpl downloader = DownloaderTestImpl.getInstance();
        downloader.mockRequestsWithHar(har);
        NewPipe.init(downloader);

        final CommentsExtractor extractor = YouTube.getCommentsExtractor(url);
        extractor.fetchPage();
        return extractor;
    }


    @Test
    public void test3753() throws Exception {
        CommentsExtractor extractor = getExtractor(
                "https://m.youtube.com/watch?v=OiuKZAkYqyE", "youtube_suggestion_4201_1.har");
        assertNotNull(extractor.getPage(extractor.getInitialPage().getNextPage()));
        DownloaderTestImpl.getInstance().clearRequestMocks();
    }
}
