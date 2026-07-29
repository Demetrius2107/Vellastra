package com.demetrius.vellastra.publish.application;

import com.demetrius.vellastra.publish.infrastructure.persistence.mapper.PublishTaskMapper;
import com.demetrius.vellastra.publish.infrastructure.persistence.po.PublishTaskPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PublishEngineService {

    private final PublishTaskMapper publishTaskMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${publish.webhook.url:}")
    private String webhookUrl;

    public PublishEngineService(PublishTaskMapper publishTaskMapper) {
        this.publishTaskMapper = publishTaskMapper;
    }

    public Long triggerPublish(Long articleId, String action) {
        PublishTaskPO task = new PublishTaskPO();
        task.setArticleId(articleId);
        task.setAction(action);
        task.setStatus("pending");
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        publishTaskMapper.insert(task);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(webhookUrl + "?articleId=" + articleId + "&action=" + action))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                task.setStatus("success");
                task.setResultLog("HTTP " + response.statusCode() + ": " + response.body());
            } catch (Exception e) {
                log.warn("Webhook 触发失败: {}", e.getMessage());
                task.setStatus("failed");
                task.setResultLog("Error: " + e.getMessage());
            }
            task.setUpdateTime(LocalDateTime.now());
            publishTaskMapper.updateById(task);
        }
        return task.getId();
    }
}
