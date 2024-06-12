package net.docn.www.aitra.demos.web.TranslateController;

public class TranslationRequest {
    private String userEmail;//用户Email
    private String translationSource;//翻译厂商名
    private Long sourceLangId;//原文语言标识id
    private Long targetLangId;//目标语言标识id
    private String sourceLanguage;//原文
    private String targetLanguage;//目标语言
    private String sourceText;//目标文

    // Getters and Setters
    public Long getTargetLanguageId() {
        return targetLangId;
    }
    public void setTargetLanguageId(Long targetLangId) {
        this.targetLangId = targetLangId;
    }

    public Long getSourceLanguageId() {
        return sourceLangId;
    }

    public void setSourceLanguageId(Long sourceLangId) {
        this.sourceLangId = sourceLangId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTranslationSource() {
        return translationSource;
    }

    public void setTranslationSource(String translationSource) {
        this.translationSource = translationSource;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }
}