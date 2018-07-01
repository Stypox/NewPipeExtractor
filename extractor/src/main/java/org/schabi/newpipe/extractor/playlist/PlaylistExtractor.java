package org.schabi.newpipe.extractor.playlist;

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.uih.ListUIHFactory;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.uih.ListUIHandler;

public abstract class PlaylistExtractor extends ListExtractor<StreamInfoItem> {

    public PlaylistExtractor(StreamingService service, ListUIHandler urlIdHandler) {
        super(service, urlIdHandler);
    }

    public abstract String getThumbnailUrl() throws ParsingException;
    public abstract String getBannerUrl() throws ParsingException;

    public abstract String getUploaderUrl() throws ParsingException;
    public abstract String getUploaderName() throws ParsingException;
    public abstract String getUploaderAvatarUrl() throws ParsingException;

    public abstract long getStreamCount() throws ParsingException;
}