package com.github.edwgiz.tayyib.adapter.out.httpClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;


@SpringBootTest(classes = {HttpClientConfiguration.class, NominatimClient.class}, webEnvironment = NONE)
class NominatimClientTest {

    @Autowired
    private NominatimClient nominatimClient;


    @Test
    void sanity() {
        final var actual = nominatimClient.reverse(52.5487429714954, -1.81602098644987, "n-US,en;q=0.9");
        assertNotNull(actual);
        assertEquals("104, Pilkington Avenue, Maney, Sutton Coldfield, Birmingham, West Midlands, England, B72 1LH, United Kingdom", actual.displayName());
        assertEquals("GB", actual.iso3166_1_alpha2());
    }

}