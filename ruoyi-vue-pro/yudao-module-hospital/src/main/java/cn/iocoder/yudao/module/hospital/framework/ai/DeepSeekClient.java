package cn.iocoder.yudao.module.hospital.framework.ai;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * DeepSeek API 客户端（OpenAI 兼容接口）
 */
@Component
@Slf4j
public class DeepSeekClient {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL = "deepseek-chat";

    @Value("${spring.ai.deepseek.api-key:}")
    private String apiKey;

    private RestTemplate restTemplate;

    /**
     * 发送 chat 请求，返回 JSON 字符串；失败时返回 null
     */
    public String chat(String systemPrompt, String userMessage) {
        if (StrUtil.isBlank(apiKey)) {
            log.warn("[DeepSeekClient] API Key 未配置，跳过 AI 调用");
            return null;
        }
        try {
            RestTemplate client = getRestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", MODEL);
            Map<String, Object> responseFormat = new HashMap<>();
            responseFormat.put("type", "json_object");
            body.put("response_format", responseFormat);

            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            body.put("messages", Arrays.asList(systemMsg, userMsg));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = client.exchange(API_URL, HttpMethod.POST, entity, Map.class);
            if (response.getBody() == null) {
                return null;
            }
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices == null || choices.isEmpty()) {
                return null;
            }
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null) {
                return null;
            }
            return (String) message.get("content");
        } catch (Exception e) {
            log.warn("[DeepSeekClient] AI 调用失败: {}", e.getMessage());
            return null;
        }
    }

    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);
            restTemplate = new RestTemplate(factory);
        }
        return restTemplate;
    }

}
