package com.example.recycling.config;

import com.example.recycling.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    // Create security config and set any required services
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    /*
    * Sets up security for requests
    **/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF
        http.csrf().disable()
                // If not found send 401
                .exceptionHandling().authenticationEntryPoint(new Send401NotRedirect())
                .and()
                // Process logins at /api/login
                .formLogin().loginProcessingUrl("/api/login")
                .and()
                // Process logouts at /api/logout by deleting session cookie
                .logout().logoutUrl("/api/logout").deleteCookies("JSESSIONID");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Set userService as how it resolves users
        auth.userDetailsService(userService);
    }

    private static class Send401NotRedirect implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
            // Send a 401, this is called from configure
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
