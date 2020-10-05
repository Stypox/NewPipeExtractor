package org.schabi.newpipe.extractor.services.youtube.mock;

import com.grack.nanojson.JsonParserException;

import org.junit.Test;
import org.schabi.newpipe.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.suggestion.SuggestionExtractor;

import java.io.IOException;

import static org.schabi.newpipe.extractor.ServiceList.YouTube;

public class YoutubeSuggestionExtractorMockedTest {
    private static void getSearchSuggestions(final String har, final String... queries)
            throws IOException, JsonParserException, ExtractionException {
        final DownloaderTestImpl downloader = DownloaderTestImpl.getInstance();
        downloader.mockRequestsWithHar(har);
        NewPipe.init(downloader);

        final SuggestionExtractor extractor = YouTube.getSuggestionExtractor();
        for (final String query : queries) {
            extractor.suggestionList(query);
        }
        downloader.clearRequestMocks();
    }

    @Test
    public void test4201_1() throws Exception {
        getSearchSuggestions("youtube_suggestion_4201_1.har", "los bitchos", "clowncore");
    }

    @Test
    public void test4201_2() throws Exception {
        getSearchSuggestions("youtube_suggestion_4201_2.har", "los bitchos");
    }

    @Test
    public void test4201_3() throws Exception {
        getSearchSuggestions("youtube_suggestion_4201_3.har", "los bitchos", "clowncore");
    }
}
