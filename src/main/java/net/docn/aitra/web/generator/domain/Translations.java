package net.docn.aitra.web.generator.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import net.docn.aitra.web.TranslateController.Translation;

import java.time.LocalDateTime;

@Data
@TableName("translations")
public class Translations {
    private Long translationId;
    private String userEmail;
    private String providerName;
    private String sourceText;
    private String translatedText;
    private String sourceLangName;
    private String targetLangName;
    private String status;
    private LocalDateTime translatedAt;
    private LocalDateTime createdAt;

    public Translations(){

    }
    public Translations(Long translationId, String userEmail, String providerName, String sourceText, String translatedText, String sourceLangName, String targetLangName, String status, LocalDateTime translatedAt, LocalDateTime createdAt) {
        this.translationId = translationId;
        this.userEmail = userEmail;
        this.providerName = providerName;
        this.sourceText = sourceText;
        this.translatedText = translatedText;
        this.sourceLangName = sourceLangName;
        this.targetLangName = targetLangName;
        this.status = status;
        this.translatedAt = translatedAt;
        this.createdAt = createdAt;
    }



    public Long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(Long translationId) {
        this.translationId = translationId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getSourceLangName() {
        return sourceLangName;
    }

    public void setSourceLangName(String sourceLangName) {
        this.sourceLangName = sourceLangName;
    }

    public String getTargetLangName() {
        return targetLangName;
    }

    public void setTargetLangName(String targetLangName) {
        this.targetLangName = targetLangName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTranslatedAt() {
        return translatedAt;
    }

    public void setTranslatedAt(LocalDateTime translatedAt) {
        this.translatedAt = translatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
