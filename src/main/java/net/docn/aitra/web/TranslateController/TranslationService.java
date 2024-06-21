package net.docn.aitra.web.TranslateController;

import net.docn.aitra.web.generator.domain.Translations;

import java.io.IOException;

public interface TranslationService {
    String translate(String text, String sourceLang, String targetLang,String userEmail) throws IOException;

    void saveTranslation(Translations translation);
}
