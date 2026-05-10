package com.note_app.commonutils.authguard;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(AuthGuardAspect.class)
public class AuthGuardAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthGuardAspect authGuardAspect() {
        return new AuthGuardAspect();
    }
}