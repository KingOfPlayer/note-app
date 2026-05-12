package com.note_app.gatewayservice.proxy;

import com.note_app.commonutils.exception.NotFoundException;
import com.note_app.commonutils.exception.ServiceUnavailableException;
import com.note_app.gatewayservice.config.ServiceRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

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
            throw new NotFoundException("Yonlendirme tanimi bulunamadi: " + path);
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

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

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
            throw new ServiceUnavailableException("Hedef servise ulasilamiyor: " + target);
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
