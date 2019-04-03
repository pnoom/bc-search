package BristolArchives.admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

    @Configuration
    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                        .authorizeRequests()
                        .antMatchers("/submitCsv").hasRole("ADMIN")
                    .and()
                        .formLogin()
                        .loginPage("/loginPage")
                        .permitAll()
                        .defaultSuccessUrl("/submitCsv", true)
                    .and()
                        .logout()
                        .permitAll();
                    //.and()
                      //  .formLogin().defaultSuccessUrl("/index", true);
        }

        @Bean
        @Override
        public UserDetailsService userDetailsService() {
            UserDetails user =
                    User.withDefaultPasswordEncoder()
                            .username("admin")
                            .password("1234")
                            .roles("ADMIN")
                            .build();

            return new InMemoryUserDetailsManager(user);
        }
    }

