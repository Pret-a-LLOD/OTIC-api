package sid.otic.app.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import sid.otic.web.config.WebSecurityConfig;

@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfig {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
    }

}
