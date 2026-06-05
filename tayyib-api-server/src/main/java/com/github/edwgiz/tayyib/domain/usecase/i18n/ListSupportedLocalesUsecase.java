package com.github.edwgiz.tayyib.domain.usecase.i18n;


import com.github.edwgiz.tayyib.adapter.out.jdbc.I18nBundleSourcesRepository;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.JdbcUtils.tx;


@Service
public class ListSupportedLocalesUsecase {

    private final I18nBundleSourcesRepository i18NBundleSourcesRepository;
    private final DataSource dataSource;

    ListSupportedLocalesUsecase(
            final I18nBundleSourcesRepository i18NBundleSourcesRepository,
            final DataSource dataSource
    ) {
        this.i18NBundleSourcesRepository = i18NBundleSourcesRepository;
        this.dataSource = dataSource;
    }

    public List<Locale> apply() {
        return tx(dataSource, c -> i18NBundleSourcesRepository.findAllLocales(c).stream().map(Locale::of).toList());
    }
}
