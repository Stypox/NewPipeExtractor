// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package org.schabi.newpipe.extractor.services.bandcamp.extractors;

import static org.schabi.newpipe.extractor.Image.HEIGHT_UNKNOWN;
import static org.schabi.newpipe.extractor.Image.WIDTH_UNKNOWN;
import static org.schabi.newpipe.extractor.services.bandcamp.extractors.BandcampExtractorHelper.getArtistDetails;
import static org.schabi.newpipe.extractor.services.bandcamp.extractors.BandcampExtractorHelper.getImagesFromImageId;
import static org.schabi.newpipe.extractor.utils.Utils.replaceHttpWithHttps;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

import org.jsoup.Jsoup;
import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.Image.ResolutionLevel;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.services.bandcamp.extractors.streaminfoitem.BandcampDiscographStreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

public class BandcampChannelExtractor extends ChannelExtractor {

    private JsonObject channelInfo;

    public BandcampChannelExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    @Override
    public List<Image> getAvatars() {
        return getImagesFromImageId(channelInfo.getLong("bio_image_id"), false);
    }

    @Nonnull
    @Override
    public List<Image> getBanners() throws ParsingException {
        /*
         * Mobile API does not return the header or not the correct header.
         * Therefore, we need to query the website
         */
        try {
            final String html = getDownloader()
                            .get(replaceHttpWithHttps(channelInfo.getString("bandcamp_url")))
                            .responseBody();

            return Stream.of(Jsoup.parse(html).getElementById("customHeader"))
                    .filter(Objects::nonNull)
                    .flatMap(element -> element.getElementsByTag("img").stream())
                    .map(element -> element.attr("src"))
                    .filter(url -> !url.isEmpty())
                    .map(url -> new Image(
                            replaceHttpWithHttps(url), HEIGHT_UNKNOWN, WIDTH_UNKNOWN,
                            ResolutionLevel.UNKNOWN))
                    .collect(Collectors.toUnmodifiableList());

        } catch (final IOException | ReCaptchaException e) {
            throw new ParsingException("Could not download artist web site", e);
        }
    }

    /**
     * Bandcamp discontinued their RSS feeds because it hadn't been used enough.
     */
    @Override
    public String getFeedUrl() {
        return null;
    }

    @Override
    public long getSubscriberCount() {
        return -1;
    }

    @Override
    public String getDescription() {
        return channelInfo.getString("bio");
    }

    @Override
    public String getParentChannelName() {
        return null;
    }

    @Override
    public String getParentChannelUrl() {
        return null;
    }

    @Nonnull
    @Override
    public List<Image> getParentChannelAvatars() {
        return Collections.emptyList();
    }

    @Override
    public boolean isVerified() throws ParsingException {
        return false;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() throws ParsingException {

        final StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        final JsonArray discography = channelInfo.getArray("discography");

        for (int i = 0; i < discography.size(); i++) {
            // A discograph is as an item appears in a discography
            final JsonObject discograph = discography.getObject(i);

            if (!discograph.getString("item_type").equals("track")) {
                continue;
            }

            collector.commit(new BandcampDiscographStreamInfoItemExtractor(discograph, getUrl()));
        }

        return new InfoItemsPage<>(collector, null);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return null;
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader)
            throws IOException, ExtractionException {
        channelInfo = getArtistDetails(getId());
    }

    @Nonnull
    @Override
    public String getName() {
        return channelInfo.getString("name");
    }
}
