package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;



//Open Authorization for example lets one app access certain information or features to another service with using any user related details lik username and password

// APPEXP - Google OAuth2.0 Login Process Flow
/*
    Step 1: Configure OAuth Consent Screen
        - Set App name, support email, authorized domains  -->onrender.com this called fomain name
        - Define scopes (profile,openid, email)
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
                    redirect-uri: "{baseUrl}/login/oauth2/code/google" ww have to received code in this URI google share code
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

        Step-by-Step
        1. User clicks Login with Google

           /oauth2/authorization/google/ UI manually set URL  -->Spring already has an endpoint:
        User
         |
         v
       Expense Tracker App

       2. Your app redirects to Google

       https://accounts.google.com

       3. Google sends Authorization Code

       http://localhost:8080/login/oauth2/code/google?code=ABC123XYZ

       4. Spring Boot sends code to Google

       POST https://oauth2.googleapis.com/token
       client_id
       client_secret
       authorization_code

       5. Google validate id, secret code, authorized code and uri then generated token send back to spring

       6. Google returns Access Token
       {
         "access_token": "ya29.a0AfH6SM...",
          "expires_in": 3600
      }
      7. App uses token to get user details
         GET https://www.googleapis.com/oauth2/v3/userinfo
         Authorization: Bearer ya29.a0AfH6SM...

         Google Return so spring created user session for that users
         {
           "name": "Karthick",
           "email": "karthick@gmail.com"
         }

         oauth2User.getAttribute("email");
         oauth2User.getAttribute("name");

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