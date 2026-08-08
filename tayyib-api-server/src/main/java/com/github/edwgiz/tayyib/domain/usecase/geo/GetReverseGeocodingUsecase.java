package com.github.edwgiz.tayyib.domain.usecase.geo;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.edwgiz.tayyib.adapter.out.httpClient.NominatimClient;
import com.github.edwgiz.tayyib.domain.model.ReverseGeoCoding;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import static java.util.concurrent.TimeUnit.DAYS;


@Service
public class GetReverseGeocodingUsecase {

    private final Cache<GetReverseGeocodingArgs, ReverseGeoCoding> cache;
    private final Function<GetReverseGeocodingArgs, ReverseGeoCoding> reverseGeoCodingFunction;


    GetReverseGeocodingUsecase(final NominatimClient nominatimClient) {
        this.reverseGeoCodingFunction = new Function<>() {
            @Override
            public @Nullable ReverseGeoCoding apply(GetReverseGeocodingArgs key) {
                return nominatimClient.reverse(key.latitude, key.longitude, key.acceptLanguage());
            }
        };
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(7, DAYS)
                .build();
    }


    public @Nullable ReverseGeoCoding apply(final GetReverseGeocodingArgs args) {
        return cache.get(args, reverseGeoCodingFunction);
    }


    public record GetReverseGeocodingArgs(
            double latitude,
            double longitude,
            @Nullable String acceptLanguage
    ) {
    }
}
