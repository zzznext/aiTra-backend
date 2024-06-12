package net.docn.www.aitra.demos.web.TranslateApi;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.docn.www.aitra.demos.web.TranslateController.Translation;
import net.docn.www.aitra.demos.web.TranslateController.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("TencentTranslationService")
public class TencentTranslationService implements TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TencentTranslationService.class);

    @Value("${tencent.api.url}")
    private String tencentUrl;

    @Value("${tencent.api.SecretId}")
    private String tencentSecretId;

    @Value("${tencent.api.SecretKey}")
    private String tencentSecretKey;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String translate(String text, String sourceLang, String targetLang, String userEmail) throws IOException {
        String body = String.format("{\"SourceText\":\"%s\",\"Source\":\"%s\",\"Target\":\"%s\",\"ProjectId\":0}", text, sourceLang, targetLang);
        String region = "ap-beijing";
        String token = "";

        try {
            String response = doRequest(tencentSecretId, tencentSecretKey, "tmt", "2018-03-21", "TextTranslate", body, region, token);
            String translatedText = extractTranslatedText(response);
           // saveTranslation(new Translation(text, translatedText, sourceLang, targetLang, userEmail));
            return translatedText;
        } catch (Exception e) {
            throw new IOException("Translation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveTranslation(Translation translation) {
        try (Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)) {
            String query = "INSERT INTO Translations (user_email, provider_name, source_text, translated_text, source_lang_name, target_lang_name, status, translated_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, translation.getUserEmail());
                preparedStatement.setString(2, translation.getProviderName());
                preparedStatement.setString(3, translation.getSourceText());
                preparedStatement.setString(4, translation.getTranslatedText());
                preparedStatement.setString(5, translation.getSourceLangName());
                preparedStatement.setString(6, translation.getTargetLangName());
                preparedStatement.setString(7, translation.getStatus());
                preparedStatement.setTimestamp(8, java.sql.Timestamp.valueOf(translation.getTranslatedAt()));
                preparedStatement.setTimestamp(9, java.sql.Timestamp.valueOf(translation.getCreatedAt()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String extractTranslatedText(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode errorNode = rootNode.path("Response").path("Error");

        if (!errorNode.isMissingNode()) {
            String errorCode = errorNode.path("Code").asText();
            String errorMessage = errorNode.path("Message").asText();
            throw new IOException("API Error - Code: " + errorCode + ", Message: " + errorMessage);
        }

        JsonNode translationNode = rootNode.path("Response").path("TargetText");
        if (translationNode.isTextual()) {
            return translationNode.asText();
        } else {
            throw new IOException("Invalid response from translation API");
        }
    }

    private String doRequest(String secretId, String secretKey, String service, String version, String action, String body, String region, String token) throws Exception {
        HttpHeaders headers = buildHeaders(secretId, secretKey, service, version, action, body, region, token);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // 打印cURL命令
        printCurlCommand(tencentUrl, headers, body);

        ResponseEntity<String> response = restTemplate.exchange(tencentUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new Exception("HTTP request failed with status code: " + response.getStatusCode());
        }
    }

    private HttpHeaders buildHeaders(String secretId, String secretKey, String service, String version, String action, String body, String region, String token) throws Exception {
        String host = "tmt.tencentcloudapi.com";
        String contentType = "application/json";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String auth = getAuth(secretId, secretKey, host, contentType, timestamp, body, service);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Host", host);
        headers.set("X-TC-Timestamp", timestamp);
        headers.set("X-TC-Version", version);
        headers.set("X-TC-Action", action);
        headers.set("X-TC-Region", region);
        headers.set("X-TC-Token", token);
        headers.set("X-TC-RequestClient", "APIExplorer");
        headers.set("X-TC-Language", "zh-CN");
        headers.set("Authorization", auth);
        return headers;
    }

    private String getAuth(String secretId, String secretKey, String host, String contentType, String timestamp, String body, String service) throws NoSuchAlgorithmException, InvalidKeyException {
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:" + contentType + "\nhost:" + host + "\nx-tc-action:texttranslate\n";
        String signedHeaders = "content-type;host;x-tc-action";

        String hashedRequestPayload = sha256Hex(body.getBytes(StandardCharsets.UTF_8));
        String canonicalRequest = "POST\n" + canonicalUri + "\n" + canonicalQueryString + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(Long.parseLong(timestamp) * 1000));

        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String hashedCanonicalRequest = sha256Hex(canonicalRequest.getBytes(StandardCharsets.UTF_8));
        String stringToSign = "TC3-HMAC-SHA256\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmac256(secretDate, service);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature = printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();

        return "TC3-HMAC-SHA256 Credential=" + secretId + "/" + credentialScope + ", SignedHeaders=" + signedHeaders + ", Signature=" + signature;
    }

    private static String sha256Hex(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data);
        return printHexBinary(digest).toLowerCase();
    }

    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    private static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(HEX_CODE[(b >> 4) & 0xF]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }

    private static byte[] hmac256(byte[] key, String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    private void printCurlCommand(String url, HttpHeaders headers, String body) {
        StringBuilder curlCommand = new StringBuilder("curl -X POST ");
        curlCommand.append("\"").append(url).append("\" ");

        headers.forEach((key, value) -> {
            curlCommand.append("-H \"").append(key).append(": ").append(String.join("", value)).append("\" ");
        });

        curlCommand.append("-d '").append(body).append("'");

        logger.info("cURL command: {}", curlCommand);
    }
}
