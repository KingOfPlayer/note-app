package com.note_app.commonutils.authguard;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.UnauthorizedException;

@Aspect
@Component
public class AuthGuardAspect {

    @Before("@annotation(authGuard)")
    public void handleAuth(AuthGuard authGuard) {

        if (authGuard != null) {

            System.out.println("Guard Triggered! Role needed: " + authGuard.value());

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                String userIdHeader = request.getHeader("X-User-Id");
                if (userIdHeader == null || userIdHeader.isEmpty()) {
                    throw new BadRequestException("User ID header is missing or empty.");
                }

                String userRoleHeader = request.getHeader("X-User-Role");
                if (userRoleHeader == null || userRoleHeader.isEmpty()) {
                    throw new BadRequestException("User Role header is missing or empty.");
                }

                // 3. Logic: Compare header to the annotation value
                UserRoles[] requiredRoles = authGuard.value();

                System.out.println("Checking Header: " + userRoleHeader);
                System.out.println("Required Roles: "
                        + String.join(", ", java.util.Arrays.stream(requiredRoles).map(UserRoles::name).toList()));

                if (userRoleHeader == null || !java.util.Arrays.stream(requiredRoles)
                        .anyMatch(role -> role.name().equalsIgnoreCase(userRoleHeader))) {
                    System.out.println("Access Denied for Role: " + userRoleHeader);
                    throw new UnauthorizedException("You do not have the required role to access this resource.");
                }
                System.out.println("Access Granted for Role: " + userRoleHeader);
                // add to request attributes for later use in controllers
                request.setAttribute("userId", userIdHeader);
                request.setAttribute("userRole", userRoleHeader);

                /*
                 * try {
                 * joinPoint.proceed();
                 * } catch (Throwable e) {
                 * throw new
                 * InternalServerException("An error occurred while processing the request.");
                 * }
                 */

            } else {
                throw new BadRequestException("Unable to retrieve request attributes.");
            }
        }
    }
}