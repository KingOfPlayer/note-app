package com.note_app.gatewayservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.note_app.commonutils.authguard.AuthGuard;
import com.note_app.commonutils.authguard.UserRoles;

@RestController
public class TokenHealthController {
  @GetMapping("/token/health")
  @AuthGuard(UserRoles.USER)
  public ResponseEntity<String> health(
      @RequestHeader(value = "X-User-Id", required = false) String userId,
      @RequestHeader(value = "X-User-Role", required = false) String userRole,
      @RequestHeader(value = "X-User-Email", required = false) String userEmail) {
    return ResponseEntity.ok("Gateway Service is healthy - Access granted for userId: " + userId + " with role: " + userRole + " and email: " + userEmail);
  }
}
