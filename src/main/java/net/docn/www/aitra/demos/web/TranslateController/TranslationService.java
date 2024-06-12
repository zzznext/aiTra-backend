package net.docn.www.aitra.demos.web.TranslateController;

import java.io.IOException;

public interface TranslationService {
    String translate(String text, String sourceLang, String targetLang,String userEmail) throws IOException;
    void saveTranslation(Translation translation);
}
