package com.github.edwgiz.tayyib.adapter.in.springMvc.config;

import com.github.edwgiz.tayyib.domain.usecase.i18n.GetBundleUsecase;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;
import java.util.Set;

@ControllerAdvice
class I18nControllerAdvice {

    private static final Set<String> RTL_LANGUAGES = Set.of(
            "ar", // Arabic script
            "fa", // Arabic script
            "pa", // Arabic script
            "ps", // Arabic script
            "ur");// Nastaliq script

    private final GetBundleUsecase getBundleUsecase;

    I18nControllerAdvice(GetBundleUsecase getBundleUsecase) {
        this.getBundleUsecase = getBundleUsecase;
    }

    @ModelAttribute
    public void addI18nModelAttributes(final Model model, final Locale locale) {
        final var i18n = getBundleUsecase.apply(locale);
        model.addAttribute("i18n", i18n);
        final var lang = locale.getLanguage();
        model.addAttribute("lang", lang);
        if (RTL_LANGUAGES.contains(lang)) {
            model.addAttribute("dir", "rtl");
        }
    }
}
