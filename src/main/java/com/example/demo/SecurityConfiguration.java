package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


// APPEXP - Google OAuth2.0 Login Process Flow
/*
    Step 1: Configure OAuth Consent Screen
        - Set App name, support email, authorized domains
        - Define scopes (openid, profile, email)
        - Add test users if app is in testing mode

    Step 2: Create OAuth Client ID in Google Cloud
        - Application type: Web Application
        - Authorized redirect URI: http://localhost:8080/login/oauth2/code/google
        - Copy Client ID and Client Secret

    Step 3: Add Configuration in application.yml
        spring:
          security:
            oauth2:
              client:
                registration:
                  google:
                    client-id: <YOUR_CLIENT_ID>
                    client-secret: <YOUR_CLIENT_SECRET>
                    scope: openid, profile, email
                    redirect-uri: "{baseUrl}/login/oauth2/code/google"
                provider:
                  google:
                    issuer-uri: https://accounts.google.com

    Step 4: SecurityConfig Class
        - Define SecurityFilterChain bean
        - Permit /login and static resources
        - Enable .oauth2Login() with loginPage("/login")
        - Set defaultSuccessUrl("/Main", true)

    Step 5: Vaadin LoginViewImpl
        - Add "Login with Google" button
        - Redirect to /oauth2/authorization/google

    Step 6: Vaadin Main View (AppExpenseMainViewImpl)
        - Annotated with @Route("Main")
        - Fetch authenticated user details from SecurityContext
        - Display welcome message with Google profile info
*/

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                    .csrf(csrf -> csrf.disable())
//                    .authorizeHttpRequests(auth -> auth
//                            .requestMatchers("/", "/login", "/VAADIN/**", "/images/**",
//                                    "/icons/**", "/styles/**").permitAll()
//                            .anyRequest().authenticated()
//                    )
//

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login",
                                "/oauth2/**",
                                "/VAADIN/**",
                                "/frontend/**",
                                "/webjars/**",
                                "/themes/**",
                                "/vaadinServlet/**",
                                "/vaadinPush/**",
                                "/images/**",
                                "/icons/**",
                                "/styles/**",
                                "/favicon.ico",
                                "/manifest.json"
                        ).permitAll()
                        .anyRequest().authenticated()
                )



                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/finance", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }
}