package com.github.edwgiz.tayyib.domain.usecase.i18n;


import com.github.edwgiz.tayyib.adapter.out.jdbcClient.I18nBundleSourcesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ListSupportedLocalesUsecase {

    private final I18nBundleSourcesRepository i18NBundleSourcesRepository;

    ListSupportedLocalesUsecase(final I18nBundleSourcesRepository i18NBundleSourcesRepository) {
        this.i18NBundleSourcesRepository = i18NBundleSourcesRepository;
    }

    public List<Locale> apply() {
        return i18NBundleSourcesRepository.findAllLocales().stream().map(Locale::of).toList();
    }
}
