package com.app.nexora.iam.user.config;

import com.app.nexora.iam.user.application.port.out.UserRepository;
import com.app.nexora.iam.user.application.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class UserConfiguration {

    @Bean
    Clock applicationClock() {
        return Clock.systemUTC();
    }

    @Bean
    UserService userService(UserRepository userRepository, Clock applicationClock) {
        return new UserService(userRepository, applicationClock);
    }
}
