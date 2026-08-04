package com.github.edwgiz.tayyib.domain.model;

import org.jspecify.annotations.Nullable;

public record ReverseGeoCoding(
        String displayName,
        @Nullable String iso3166_1_alpha2
) {
}
