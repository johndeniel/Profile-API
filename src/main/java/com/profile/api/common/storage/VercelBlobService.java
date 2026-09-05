package com.profile.api.common.storage;

import com.profile.api.common.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class VercelBlobService {

    private static final Log log = Log.get(VercelBlobService.class);
    private static final String BLOB_BASE_URL = "https://blob.vercel-storage.com";
    private static final String API_VERSION = "7";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${blob.read.write.token}")
    private String token;

    public String upload(String pathname, byte[] data, String contentType) {
        String url = BLOB_BASE_URL + "/" + pathname;
        HttpHeaders headers = createHeaders(contentType);
        HttpEntity<byte[]> request = new HttpEntity<>(data, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String blobUrl = (String) response.getBody().get("url");
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
        try {
            if (blobUrl == null || blobUrl.isBlank()) {
                return false;
            }
            HttpHeaders headers = createHeaders("application/octet-stream");
            HttpEntity<Void> request = new HttpEntity<>(headers);

            restTemplate.exchange(blobUrl, HttpMethod.DELETE, request, Void.class);
            log.info("Deleted blob: {}", blobUrl);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Blob not found for deletion (already deleted or invalid URL): {}", blobUrl);
            return false;
        } catch (Exception e) {
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
