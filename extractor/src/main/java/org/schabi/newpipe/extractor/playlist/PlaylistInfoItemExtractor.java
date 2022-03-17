package org.schabi.newpipe.extractor.playlist;

import org.schabi.newpipe.extractor.InfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

public interface PlaylistInfoItemExtractor extends InfoItemExtractor {

    /**
     * Get the uploader name
     * @return the uploader name
     */
    String getUploaderName() throws ParsingException;

    /**
     * Get the number of streams
     * @return the number of streams
     */
    long getStreamCount() throws ParsingException;
}
