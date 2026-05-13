package com.note_app.gatewayservice.proxy;

import com.note_app.commonutils.exception.ErrorMessages;
import com.note_app.commonutils.exception.NotFoundException;
import com.note_app.commonutils.exception.ServiceUnavailableException;
import com.note_app.gatewayservice.config.ServiceRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@RestController
public class ProxyController {

    private static final List<String> SKIP_HEADERS = List.of(
            "host", "content-length", "transfer-encoding", "connection"
    );

    private final RestTemplate restTemplate;
    private final ServiceRegistry registry;

    public ProxyController(RestTemplate restTemplate, ServiceRegistry registry) {
        this.restTemplate = restTemplate;
        this.registry = registry;
    }

    @RequestMapping("/api/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request,
                                        @RequestBody(required = false) byte[] body) {
        String path = request.getRequestURI();
        System.out.println("Incoming request: " + request.getMethod() + " " + path);
        String target = registry.resolve(path);
        if (target == null) {
            throw new NotFoundException(ErrorMessages.withId(ErrorMessages.GATEWAY_NO_ROUTE, path));
        }

        String query = request.getQueryString();
        System.out.println("Proxying to: " + target + path + (query != null ? "?" + query : ""));
        String url = target + path + (query != null ? "?" + query : "");

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (SKIP_HEADERS.contains(name.toLowerCase())) continue;
            headers.put(name, Collections.list(request.getHeaders(name)));
        }

        HttpEntity<?> entity;
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();

            if (request instanceof MultipartHttpServletRequest multipartRequest) {
                multipartRequest.getParameterMap().forEach((key, values) -> {
                    for (String value : values) {
                        multipartBody.add(key, value);
                    }
                });

                // Add file parts
                multipartRequest.getMultiFileMap().forEach((key, files) -> {
                    for (MultipartFile file : files) {
                        multipartBody.add(key, file.getResource());
                    }
                });
            }

           
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            headers.remove(HttpHeaders.CONTENT_TYPE);

            entity = new HttpEntity<>(multipartBody, headers);
        } else {
            entity = new HttpEntity<>(body, headers);
        }

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(url, method, entity, byte[].class);
            HttpHeaders out = new HttpHeaders();
            response.getHeaders().forEach((k, v) -> {
                if (!SKIP_HEADERS.contains(k.toLowerCase())) out.put(k, v);
            });
            return ResponseEntity.status(response.getStatusCode())
                    .headers(out)
                    .body(response.getBody());
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(filterHeaders(ex.getResponseHeaders()))
                    .body(ex.getResponseBodyAsByteArray());
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException(ErrorMessages.withId(ErrorMessages.GATEWAY_TARGET_DOWN, target));
        }
    }

    private HttpHeaders filterHeaders(HttpHeaders source) {
        HttpHeaders out = new HttpHeaders();
        if (source == null) return out;
        source.forEach((k, v) -> {
            if (!SKIP_HEADERS.contains(k.toLowerCase())) out.put(k, v);
        });
        return out;
    }
}
