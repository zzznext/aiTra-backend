package net.docn.www.aitra.demos.web.TranslateController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TranslationServiceFactory {
    @Autowired
    private ApplicationContext context;

    public TranslationService getService(String apiName) {
        switch (apiName.toLowerCase()) {
            case "youdao":
                return context.getBean("YoudaoTranslationService", TranslationService.class);
            case "tencent":
                return context.getBean("TencentTranslationService", TranslationService.class);
            case "baidu":
                return context.getBean("BaiduTranslationService", TranslationService.class);
            // 添加更多翻译服务
            default:
                throw new IllegalArgumentException("API: " + apiName + "敬请期待");
        }
    }
}
