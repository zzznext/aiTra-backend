package net.docn.aitra.web.TranslateApi;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.docn.aitra.web.TranslateController.TranslationService;
import net.docn.aitra.web.generator.domain.Translations;
import net.docn.aitra.web.generator.mapper.TranslationsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("YoudaoTranslationService")
public class YoudaoTranslationService implements TranslationService {

    @Autowired
    private TranslationsMapper translationsMapper;
    @Value("${youdao.api.url}")
    private String youdaoUrl;

    @Value("${youdao.api.appKey}")
    private String appKey;

    @Value("${youdao.api.appSecret}")
    private String appSecret;


    @Override
    public String translate(String text, String sourceLang, String targetLang,String useremail) throws IOException {
        Map<String, String> params = new HashMap<>();
        String salt = UUID.randomUUID().toString();
        long curtime = Instant.now().getEpochSecond();
        String timestampString = String.valueOf(curtime);
        String signStr = appKey + truncate(text) + salt + curtime + appSecret;
        String sign = getDigest(signStr);

        params.put("q", text);
        params.put("from", sourceLang);
        params.put("to", targetLang);
        params.put("appKey", appKey);
        params.put("salt", salt);
        params.put("sign", sign);
        params.put("signType", "v3");
        params.put("curtime", timestampString);

        String jsonResponse = requestForHttp(youdaoUrl, params);
        return extractTranslatedText(jsonResponse);
    }




    @Override
    public void saveTranslation(Translations translation) {
        try {
            int result = translationsMapper.insert(translation);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : q.substring(0, 10) + len + q.substring(len - 10, len);
    }

    public static String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String requestForHttp(String url, Map<String, String> params) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        StringBuilder requestBody = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (requestBody.length() != 0) {
                requestBody.append('&');
            }
            requestBody.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                    .append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("HTTP request failed with status code: " + response.getStatusCode());
        }
    }

    private String extractTranslatedText(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode translationNode = rootNode.path("translation");
        if (translationNode.isArray() && translationNode.size() > 0) {
            return translationNode.get(0).asText();
        } else {
            throw new IOException("Invalid response from translation API");
        }
    }
}
