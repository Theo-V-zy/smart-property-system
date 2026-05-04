package com.example.smartproperty.service.impl;

import com.example.smartproperty.config.AiAssistantProperties;
import com.example.smartproperty.dto.OrderAssistantRequest;
import com.example.smartproperty.dto.OrderAssistantResponse;
import com.example.smartproperty.service.OrderAssistantService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class OrderAssistantServiceImpl implements OrderAssistantService {

    private static final Logger log = LoggerFactory.getLogger(OrderAssistantServiceImpl.class);

    private static final List<String> REPAIR_SUBTYPES = List.of("水电维修", "门锁损坏", "公共设施", "其他");
    private static final List<String> LOST_SUBTYPES = List.of("证件遗失", "钥匙遗失", "贵重物品遗失", "其他");
    private static final List<String> FEEDBACK_SUBTYPES = List.of("环境卫生", "灯光昏暗", "噪音扰民", "其他");

    private final AiAssistantProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OrderAssistantServiceImpl(AiAssistantProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(properties.getTimeoutMillis()))
            .build();
    }

    @Override
    public OrderAssistantResponse assist(OrderAssistantRequest request) {
        String text = normalizeText(request.getText());
        if (shouldUseModel()) {
            try {
                return callModel(text);
            } catch (Exception exception) {
                log.warn("AI assistant fallback to local rules: {}", exception.getMessage());
                return buildFallback(text, "已检测到 AI 配置，但调用失败：" + summarizeException(exception));
            }
        }
        return buildFallback(text, "当前未检测到 AI 配置，已切换为本地规则助手。");
    }

    private boolean shouldUseModel() {
        return StringUtils.hasText(properties.getApiKey())
            && StringUtils.hasText(properties.getBaseUrl())
            && StringUtils.hasText(properties.getModel());
    }

    private OrderAssistantResponse callModel(String text) throws IOException, InterruptedException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", properties.getModel());
        payload.put("temperature", 0.3);
        payload.put("response_format", Map.of("type", "json_object"));
        payload.put("messages", List.of(
            Map.of("role", "system", "content", """
                你是智慧物业系统的报修助手。请从用户输入中提取最合适的工单信息，并且只返回 JSON。
                category 只能是 REPAIR、LOST、FEEDBACK 之一。
                subtype 必须从以下选项中选择：
                REPAIR: 水电维修、门锁损坏、公共设施、其他
                LOST: 证件遗失、钥匙遗失、贵重物品遗失、其他
                FEEDBACK: 环境卫生、灯光昏暗、噪音扰民、其他
                urgency 只能是 HIGH、MEDIUM、LOW。
                tips 输出 2 到 3 条简短建议。
                polishedDescription 需要整理成适合提交工单的中文描述。
                JSON 字段固定为 category、subtype、polishedDescription、urgency、tips。
                """),
            Map.of("role", "user", "content", text)
        ));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(trimTrailingSlash(properties.getBaseUrl()) + "/chat/completions"))
            .timeout(Duration.ofMillis(properties.getTimeoutMillis()))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + properties.getApiKey())
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException(buildHttpErrorMessage(response));
        }
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        String content = stripCodeFence(contentNode.asText(""));
        if (!StringUtils.hasText(content)) {
            throw new IOException("AI 返回内容为空");
        }
        JsonNode resultNode = objectMapper.readTree(content);

        OrderAssistantResponse result = new OrderAssistantResponse();
        result.setCategory(safeCategory(resultNode.path("category").asText("")));
        result.setSubtype(safeSubtype(result.getCategory(), resultNode.path("subtype").asText("")));
        result.setPolishedDescription(buildPolishedDescription(resultNode.path("polishedDescription").asText(""), text, result.getCategory()));
        result.setUrgency(safeUrgency(resultNode.path("urgency").asText("")));
        result.setTips(safeTips(resultNode.path("tips")));
        result.setUsedModel(true);
        result.setEngineMode("MODEL");
        result.setEngineName(resolveModelEngineName());
        result.setEngineMessage("AI 模型调用成功，已返回智能整理结果。");
        return result;
    }

    private OrderAssistantResponse buildFallback(String text, String engineMessage) {
        OrderAssistantResponse result = new OrderAssistantResponse();
        String category = detectCategory(text);
        result.setCategory(category);
        result.setSubtype(detectSubtype(category, text));
        result.setPolishedDescription(buildPolishedDescription("", text, category));
        result.setUrgency(detectUrgency(text));
        result.setTips(buildTips(category, result.getSubtype(), result.getUrgency()));
        result.setUsedModel(false);
        result.setEngineMode("RULE");
        result.setEngineName("本地规则助手");
        result.setEngineMessage(engineMessage);
        return result;
    }

    private String buildHttpErrorMessage(HttpResponse<String> response) {
        String body = response.body();
        if (StringUtils.hasText(body)) {
            try {
                JsonNode root = objectMapper.readTree(body);
                String message = root.path("error").path("message").asText("");
                if (StringUtils.hasText(message)) {
                    return "OpenAI 接口返回 " + response.statusCode() + "：" + message;
                }
            } catch (Exception ignored) {
                // Ignore parsing failures and fall back to raw status code.
            }
        }
        return "OpenAI 接口返回 " + response.statusCode();
    }

    private String summarizeException(Exception exception) {
        String message = exception.getMessage();
        if (!StringUtils.hasText(message)) {
            return "未知错误";
        }
        return message.length() > 120 ? message.substring(0, 120) + "..." : message;
    }

    private String resolveModelEngineName() {
        String baseUrl = trimTrailingSlash(properties.getBaseUrl()).toLowerCase(Locale.ROOT);
        String model = properties.getModel();
        if (baseUrl.contains("deepseek")) {
            return "DeepSeek - " + model;
        }
        if (baseUrl.contains("openai")) {
            return "OpenAI - " + model;
        }
        if (baseUrl.contains("dashscope") || baseUrl.contains("aliyuncs")) {
            return "通义千问兼容接口 - " + model;
        }
        return "外部模型接口 - " + model;
    }

    private String detectCategory(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);
        if (containsAny(normalized, "丢", "遗失", "找不到", "钱包", "钥匙", "证件", "卡", "手机")) {
            return "LOST";
        }
        if (containsAny(normalized, "脏", "卫生", "吵", "噪音", "建议", "投诉", "灯暗", "昏暗")) {
            return "FEEDBACK";
        }
        return "REPAIR";
    }

    private String detectSubtype(String category, String text) {
        String normalized = text.toLowerCase(Locale.ROOT);
        return switch (category) {
            case "LOST" -> {
                if (containsAny(normalized, "证", "身份证", "学生证", "门禁卡")) {
                    yield "证件遗失";
                }
                if (containsAny(normalized, "钥匙", "门卡")) {
                    yield "钥匙遗失";
                }
                if (containsAny(normalized, "钱包", "手机", "电脑", "首饰", "贵重")) {
                    yield "贵重物品遗失";
                }
                yield "其他";
            }
            case "FEEDBACK" -> {
                if (containsAny(normalized, "脏", "垃圾", "卫生", "异味")) {
                    yield "环境卫生";
                }
                if (containsAny(normalized, "灯", "照明", "昏暗", "黑")) {
                    yield "灯光昏暗";
                }
                if (containsAny(normalized, "吵", "噪音", "施工", "扰民")) {
                    yield "噪音扰民";
                }
                yield "其他";
            }
            default -> {
                if (containsAny(normalized, "水", "漏", "电", "空调", "插座", "灯", "马桶", "下水")) {
                    yield "水电维修";
                }
                if (containsAny(normalized, "门", "锁", "门把手")) {
                    yield "门锁损坏";
                }
                if (containsAny(normalized, "电梯", "路灯", "健身器材", "围栏", "楼道")) {
                    yield "公共设施";
                }
                yield "其他";
            }
        };
    }

    private String buildPolishedDescription(String candidate, String original, String category) {
        if (StringUtils.hasText(candidate)) {
            return appendPeriod(candidate.trim());
        }
        String prefix = switch (category) {
            case "LOST" -> "本人在小区内发生物品遗失情况，";
            case "FEEDBACK" -> "现反馈小区公共环境问题，";
            default -> "报修问题如下，";
        };
        String normalized = original.replaceAll("\\s+", " ").trim();
        return appendPeriod(prefix + normalized);
    }

    private String detectUrgency(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);
        if (containsAny(normalized, "漏水", "停电", "进不去", "打不开", "积水", "火花", "异响", "异味")) {
            return "HIGH";
        }
        if (containsAny(normalized, "尽快", "今天", "晚上", "严重", "影响")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private List<String> buildTips(String category, String subtype, String urgency) {
        List<String> tips = new ArrayList<>();
        if ("HIGH".equals(urgency)) {
            tips.add("建议尽快联系物业值班人员，并保持电话畅通。");
        }
        switch (category) {
            case "LOST" -> {
                tips.add("尽量补充遗失时间、最后出现地点和物品特征。");
                tips.add("若涉及证件或门禁卡，建议同步申请挂失处理。");
                if ("其他".equals(subtype)) {
                    tips.add("可补充物品颜色、大小和品牌，方便后续核验。");
                }
            }
            case "FEEDBACK" -> {
                tips.add("建议补充发生时段和具体楼栋位置，方便物业定位问题。");
                tips.add("如有现场照片，可一并上传提高处理效率。");
                if ("其他".equals(subtype)) {
                    tips.add("如果属于持续性问题，建议写明出现频率和影响范围。");
                }
            }
            default -> {
                tips.add("建议补充具体位置和故障现象，便于维修人员提前准备工具。");
                if ("水电维修".equals(subtype)) {
                    tips.add("若现场存在漏水或用电风险，请先做好临时隔离。");
                } else if ("其他".equals(subtype)) {
                    tips.add("可补充故障前后的变化情况，帮助物业快速判断原因。");
                } else {
                    tips.add("如有损坏部位照片，可一并上传提高处理效率。");
                }
            }
        }
        return tips.stream().limit(3).toList();
    }

    private String safeCategory(String value) {
        if ("LOST".equals(value) || "FEEDBACK".equals(value)) {
            return value;
        }
        return "REPAIR";
    }

    private String safeSubtype(String category, String value) {
        List<String> subtypeOptions = switch (category) {
            case "LOST" -> LOST_SUBTYPES;
            case "FEEDBACK" -> FEEDBACK_SUBTYPES;
            default -> REPAIR_SUBTYPES;
        };
        if (subtypeOptions.contains(value)) {
            return value;
        }
        return detectSubtype(category, value);
    }

    private String safeUrgency(String value) {
        if ("HIGH".equals(value) || "MEDIUM".equals(value) || "LOW".equals(value)) {
            return value;
        }
        return "MEDIUM";
    }

    private List<String> safeTips(JsonNode tipsNode) {
        try {
            List<String> tips = objectMapper.convertValue(tipsNode, new TypeReference<List<String>>() {});
            if (tips == null || tips.isEmpty()) {
                return List.of("建议补充更具体的地点和情况描述，方便物业快速处理。");
            }
            return tips.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .limit(3)
                .toList();
        } catch (IllegalArgumentException exception) {
            return List.of("建议补充更具体的地点和情况描述，方便物业快速处理。");
        }
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }

    private String trimTrailingSlash(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private String stripCodeFence(String content) {
        return content
            .replace("```json", "")
            .replace("```", "")
            .trim();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String appendPeriod(String text) {
        if (text.endsWith("。") || text.endsWith("！") || text.endsWith("？")) {
            return text;
        }
        return text + "。";
    }
}
