package org.schabi.newpipe.extractor.services;

public interface BaseStreamExtractorTest extends BaseExtractorTest {
    void testStreamType() throws Exception;
    void testUploaderName() throws Exception;
    void testUploaderUrl() throws Exception;
    void testUploaderAvatarUrl() throws Exception;
    void testThumbnailUrl() throws Exception;
    void testDescription() throws Exception;
    void testLength() throws Exception;
    void testTimestamp() throws Exception;
    void testViewCount() throws Exception;
    void testUploadDate() throws Exception;
    void testTextualUploadDate() throws Exception;
    void testLikeCount() throws Exception;
    void testDislikeCount() throws Exception;
    void testRelatedStreams() throws Exception;
    void testAgeLimit() throws Exception;
    void testErrorMessage() throws Exception;
    void testAudioStreams() throws Exception;
    void testVideoStreams() throws Exception;
    void testSubtitles() throws Exception;
    void testFrames() throws Exception;
}