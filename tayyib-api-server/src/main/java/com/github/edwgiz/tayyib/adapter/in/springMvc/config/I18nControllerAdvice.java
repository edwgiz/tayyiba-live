package com.github.edwgiz.tayyib.adapter.in.springMvc.config;

import com.github.edwgiz.tayyib.domain.model.I18n;
import com.github.edwgiz.tayyib.domain.usecase.i18n.GetBundleUsecase;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

@ControllerAdvice
class I18nControllerAdvice {

    private final GetBundleUsecase getBundleUsecase;

    I18nControllerAdvice(GetBundleUsecase getBundleUsecase) {
        this.getBundleUsecase = getBundleUsecase;
    }

    @ModelAttribute("i18n")
    public I18n i18n(Locale locale) {
        return getBundleUsecase.apply(locale);
    }
}
