package com.mantas.twotler.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;


// WebSecurityConfigurerAdapter is deprecated find answer here https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    UsersDetailsService userDetailsService;

    @Autowired
    JwtFilter jwtFilter;

//    @Override
//    public void configure(AuthenticationmanagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService);
//    }

    // this method is replacing the above configure method, if something doesn't work check here as I might be using wrong replacement
    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        if(userDetailsService.getUserDetail() != null) {
            UserDetails user = User.withDefaultPasswordEncoder()
                    .username(userDetailsService.getUserDetail().getEmail())
                    .password(userDetailsService.getUserDetail().getPassword())
                    .roles(userDetailsService.getUserDetail().getRole())
                    .build();
            JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
            users.createUser(user);

            return users;
        }
        else return null;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // MUST BE UPDATED, cors() will be removed
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers("/user/login", "/user/signup", "user/forgotPassword")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and().exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
