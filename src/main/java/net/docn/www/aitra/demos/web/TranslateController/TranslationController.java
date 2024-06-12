package net.docn.www.aitra.demos.web.TranslateController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/translate")
public class TranslationController {
    private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);
    @Autowired
    private TranslationServiceFactory translationServiceFactory;

    @Autowired
    private TranslationRepository translationRepository;
    @PostMapping
    public Map<String, String> translate(@RequestBody TranslationRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            TranslationService translationService = translationServiceFactory.getService(request.getTranslationSource());
            String translatedText = translationService.translate(request.getSourceText(), request.getSourceLanguage(), request.getTargetLanguage(), request.getUserEmail());
            response.put("translatedText", translatedText);
            // 创建并保存翻译记录
            Translation translation = new Translation();
            translation.setUserEmail(request.getUserEmail()); // 向数据库存用户id
            translation.setProviderName(request.getTranslationSource()); // 存翻译厂商名
            translation.setSourceText(request.getSourceText()); // 存原文
            translation.setTranslatedText(translatedText); // 存翻译后文
            translation.setSourceLangName(request.getSourceLanguage()); // 存原文语言id
            translation.setTargetLangName(request.getTargetLanguage()); // 存目标语言id
            translation.setStatus("COMPLETED"); // 翻译状态
            translation.setTranslatedAt(LocalDateTime.now()); // 使用当前时间
            translation.setCreatedAt(LocalDateTime.now()); // 使用当前时间
            if (request.getUserEmail() == null || request.getUserEmail().isEmpty())  {
                response.put("error", "Translation failed");
            }else {
                translationService.saveTranslation(translation);
            }
        } catch (IOException e) {
            logger.error("Translation error", e);
            response.put("error", "Translation failed");
        }
        return response;
    }


    @GetMapping("/history")
    public List<Translation> getTranslationHistory(@RequestParam String userEmail) {
        return translationRepository.getTranslationsByUserEmail(userEmail);
    }
}
