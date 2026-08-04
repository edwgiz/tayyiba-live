package com.github.edwgiz.tayyib.adapter.out.httpClient;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.edwgiz.tayyib.adapter.out.httpClient.NominatimClient.ReverseResponseDto.Address;
import com.github.edwgiz.tayyib.domain.model.ReverseGeoCoding;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;


@Component
public class NominatimClient {

    private final RestClient restClient;


    NominatimClient(final RestClient.Builder builder) {
        restClient = builder.build();
    }


    public @Nullable ReverseGeoCoding reverse(double latitude, double longitude, @Nullable String acceptLanguage) {
        final var requestSpec = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("nominatim.openstreetmap.org")
                        .path("/reverse")
                        .queryParam("format", "jsonv2")
                        .queryParam("layer", "address,poi,manmade")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build());
        if (acceptLanguage != null) {
            requestSpec.header(ACCEPT_LANGUAGE, acceptLanguage);
        }
        return requestSpec.exchange((_, clientResponse) -> {
            if (clientResponse.getStatusCode() != HttpStatus.OK) {
                return null;
            }
            final var responseDto = clientResponse.bodyTo(ReverseResponseDto.class);
            if (responseDto == null) {
                return null;
            }

            var iso3166_1_alpha2 = (String) null;
            if (responseDto.address() instanceof Address(final var countryCode) && countryCode != null) {
                iso3166_1_alpha2 = trimToNull(countryCode.toUpperCase(Locale.ROOT));
            }
            return new ReverseGeoCoding(trimToEmpty(responseDto.displayName()), iso3166_1_alpha2);
        });
    }


    public record ReverseResponseDto(
            @JsonProperty("display_name")
            @Nullable String displayName,
            @Nullable Address address
    ) {
        public record Address(
                @JsonProperty("country_code")
                @Nullable String countryCode
        ) {
        }
    }

}
