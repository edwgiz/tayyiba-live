package com.github.edwgiz.tayyib.adapter.in.commons.rendering;

import com.github.edwgiz.tayyib.domain.model.I18n;


public final class I18nRenderingUtils {

    public static String renderHadithHref(final I18n.Fasting.HadithReference hadithReference) {
        return "https://sunnah.com/" + hadithReference.collection() + ":" + hadithReference.hadith();
    }


    private I18nRenderingUtils() {
    }
}
