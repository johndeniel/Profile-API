package com.profile.api.common.config;

import com.profile.api.common.logging.CentralizedLoggingFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

@Service
public class VercelBlobService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(VercelBlobService.class);
    private static final String BLOB_BASE_URL = "https://blob.vercel-storage.com";
    private static final String API_VERSION = "7";

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${blob.read.write.token}")
    private String token;

    public String upload(String pathname, byte[] data, String contentType) {
        String url = BLOB_BASE_URL + "/" + pathname;
        HttpHeaders headers = createHeaders(contentType);
        HttpEntity<byte[]> request = new HttpEntity<>(data, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String blobUrl = (String) response.getBody().get("url");
            if (blobUrl == null || blobUrl.isBlank()) {
                throw new RuntimeException("Upload succeeded but Vercel returned no URL for: " + pathname);
            }
            log.info("Uploaded blob: {} -> {}", pathname, blobUrl);
            return blobUrl;
        }

        throw new RuntimeException("Failed to upload blob: " + pathname);
    }

    public String uploadWithRandomSuffix(String pathname, byte[] data, String contentType) {
        String ext = pathname.contains(".") ? pathname.substring(pathname.lastIndexOf(".")) : "";
        String name = pathname.contains(".") ? pathname.substring(0, pathname.lastIndexOf(".")) : pathname;
        String uniquePathname = name + "-" + UUID.randomUUID().toString().substring(0, 8) + ext;
        return upload(uniquePathname, data, contentType);
    }

    public byte[] download(String pathname) {
        String url = BLOB_BASE_URL + "/" + pathname;
        HttpHeaders headers = createHeaders("application/octet-stream");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, request, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            log.info("Downloaded blob: {}", pathname);
            return response.getBody();
        }

        throw new RuntimeException("Failed to download blob: " + pathname);
    }

    public boolean delete(String blobUrl) {
        if (blobUrl == null || blobUrl.isBlank()) {
            return false;
        }
        try {
            String requestBody = "{\"urls\":[\"" + blobUrl + "\"]}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BLOB_BASE_URL + "/delete"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Deleted blob: {}", blobUrl);
                return true;
            } else {
                log.warn("Failed to delete blob (status {}): {} - {}", response.statusCode(), blobUrl, response.body());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to delete blob: {}", blobUrl, e);
            return false;
        }
    }

    private HttpHeaders createHeaders(String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("x-api-version", API_VERSION);
        headers.setContentType(MediaType.parseMediaType(contentType));
        return headers;
    }
}
