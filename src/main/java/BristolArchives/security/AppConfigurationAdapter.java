package BristolArchives.security;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
@EnableWebSecurity
@Configuration
@Order(1)
public class AppConfigurationAdapter extends WebSecurityConfigurerAdapter {
    public AppConfigurationAdapter() {
        super();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/admin*")
                .authorizeRequests()
                .anyRequest()
                .hasRole("ADMIN")

                .and()
                .formLogin()
                .loginPage("/loginAdmin")
                .loginProcessingUrl("/admin_login")
                .failureUrl("/loginAdmin?error=loginError")
                .defaultSuccessUrl("/adminPage")

                .and()
                .logout()
                .logoutUrl("/admin_logout")
                .logoutSuccessUrl("/protectedLinks")
                .deleteCookies("JSESSIONID")

                .and()
                .exceptionHandling()
                .accessDeniedPage("/403")

                .and()
                .csrf().disable();
    }
}
