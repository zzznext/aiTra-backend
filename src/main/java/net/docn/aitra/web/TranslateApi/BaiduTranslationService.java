package net.docn.aitra.web.TranslateApi;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.docn.aitra.web.TranslateController.TranslationService;
import net.docn.aitra.web.generator.domain.Translations;
import net.docn.aitra.web.generator.mapper.TranslationsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("BaiduTranslationService")
public class BaiduTranslationService implements TranslationService {

    @Autowired
    private TranslationsMapper translationsMapper;
    private static final Logger logger = LoggerFactory.getLogger(BaiduTranslationService.class);

    @Value("${baidu.api.url}")
    private String baiduUrl;

    @Value("${baidu.api.accessToken}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String translate(String text, String sourceLang, String targetLang, String userEmail) throws IOException {
        logger.info("Translation Source: Baidu");
        logger.info("Source Language: {}", sourceLang);
        logger.info("Target Language: {}", targetLang);
        logger.info("Source Text: {}", text);

        try {
            String translatedText = translateText(text, sourceLang, targetLang);
            return translatedText;
        } catch (IOException e) {
            logger.error("Translation error", e);
            throw new IOException("Translation failed: " + e.getMessage(), e);
        }
    }


    @Override
    public void saveTranslation(Translations translation) {
        try {
            int result = translationsMapper.insert(translation);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String translateText(String text, String from, String to) throws IOException {
        // 将ja转换为jp
        if ("ja".equals(from)) {
            from = "jp";
        }
        if ("ja".equals(to)) {
            to = "jp";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", text);
        requestBody.put("from", from);
        requestBody.put("to", to);

        String param = new ObjectMapper().writeValueAsString(requestBody);
        String url = String.format("%s?access_token=%s", baiduUrl, accessToken);

        HttpEntity<String> entity = new HttpEntity<>(param, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return extractTranslatedText(response.getBody());
            } else {
                logger.error("HTTP request failed with status code: {}", response.getStatusCode());
                throw new IOException("HTTP request failed with status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            handleErrorResponse(responseBody);
            throw new IOException("Translation API error: " + responseBody);
        }
    }

    private void handleErrorResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode errorCodeNode = rootNode.path("error_code");
        JsonNode errorMsgNode = rootNode.path("error_msg");

        if (!errorCodeNode.isMissingNode() && !errorMsgNode.isMissingNode()) {
            int errorCode = errorCodeNode.asInt();
            String errorMsg = errorMsgNode.asText();
            logger.error("Translation API error: code = {}, message = {}", errorCode, errorMsg);

            String solution = getSolutionForErrorCode(errorCode);
            throw new IOException("Translation API error: code = " + errorCode + ", message = " + errorMsg + ". Solution: " + solution);
        } else {
            throw new IOException("Unknown error response from translation API");
        }
    }

    private String getSolutionForErrorCode(int errorCode) {
        switch (errorCode) {
            case 1:
                return "请重试";
            case 2:
                return "请重试";
            case 4:
                return "请重试";
            case 6:
                return "请确认您调用的接口已经被赋权。企业认证生效时间为1小时左右，使用需要企业认证的服务，请等待生效后重试";
            case 18:
                return "请降低您的调用频率";
            case 19:
                return "请检查当前可用字符/次数包额度";
            case 100:
                return "请检查请求参数是否正确，可能的原因是token拉取失败，无效的access token参数等";
            case 110:
                return "token有效期为30天，注意需要定期更换，也可以每次请求都拉取新token";
            case 111:
                return "token有效期为30天，注意需要定期更换，也可以每次请求都拉取新token";
            case 31001:
                return "请重试";
            case 31005:
                return "请检查当前可用字符/次数包额度";
            case 31006:
                return "请重试";
            default:
                return "未知错误，请检查您的请求并重试";
        }
    }

    private String extractTranslatedText(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode resultNode = rootNode.path("result");
        if (resultNode.isMissingNode()) {
            throw new IOException("No translation result found in the response");
        }
        JsonNode translatedTextNode = resultNode.path("trans_result").get(0).path("dst");
        if (translatedTextNode.isMissingNode()) {
            throw new IOException("No translated text found in the response");
        }
        return translatedTextNode.asText();
    }
}
