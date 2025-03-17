package com.github.edwgiz.tayyib.adapter.in.dgs.fetcher;

import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.Nutrient;
import com.github.edwgiz.tayyib.adapter.commons.dgs.dto.UploadNutrientResult;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@DgsComponent
public class NutrientFetcher {

    private static final Logger log = LoggerFactory.getLogger(NutrientFetcher.class);

    private final JdbcClient jdbcClient;

    public NutrientFetcher(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @DgsQuery
    public List<Nutrient> shows(@InputArgument String titleFilter) {
        // language=PostgreSQL
        return jdbcClient.sql("""
                        SELECT
                          id,         --> 1
                          parent_id,  --> 2
                          name,       --> 3
                          description --> 4
                        FROM
                          nutrient
                        WHERE
                          parent_id=? --< 1
                        """)
                .param(1, new UUID(0L, 0L))
                .query((rs, rowNum) -> new Nutrient(
                        rs.getObject(1, UUID.class),
                        rs.getObject(2, UUID.class),
                        rs.getString(3),
                        rs.getString(4),
                        List.of()))
                .list();
    }


    @DgsMutation
    @Transactional
    public UploadNutrientResult uploadFdcNutrients(
            @SuppressWarnings("DgsInputArgumentValidationInspector") @InputArgument final MultipartFile file)
            throws IOException {
        int created = 0;
        int skipped = 0;

        try (final var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), US_ASCII))) {
            final var records = CSVFormat.DEFAULT.parse(reader).stream().skip(1L).iterator();
            final var sqlInsert = jdbcClient.sql("""
                    INSERT INTO nutrient (
                      id,
                      parent_id,
                      fdc_nutrient_id,
                      nutrient_nbr,
                      name,
                      description)
                    VALUES (?, ?, ?, ?, ?, '')
                    ON CONFLICT DO NOTHING
                    """);
            final var parentId = new UUID(-1L, -1L); // void
            while (records.hasNext()) {
                final var rec = records.next();
                final var name = rec.get(1);
                final var nutrientNbr = rec.get(3) instanceof String str && isNotBlank(str) ? new BigDecimal(str) : null;
                final var fdcNutrientId = parseInt(rec.get(0));
                final var count = sqlInsert
                        .param(1, randomUUID())
                        .param(2, parentId)
                        .param(3, fdcNutrientId)
                        .param(4, nutrientNbr)
                        .param(5, name)
                        //String unitName = rec.get(2);
                        .update();
                if (count > 0) {
                    created++;
                } else {
                    log.info("Skipped: {}", name);
                    skipped++;
                }
            }
        }
        return new UploadNutrientResult(created, skipped);
    }
}

