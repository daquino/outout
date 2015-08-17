package outout.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import outout.security.StatelessAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private Environment environment;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers().frameOptions().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/account/create").permitAll()
                .antMatchers("/authenticate").permitAll()
                .antMatchers("/console/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(new AuthenticationManagerBeanDefinitionParser.NullAuthenticationProvider())
        .addFilterBefore(statelessAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    private Filter statelessAuthenticationFilter() {
        return new StatelessAuthenticationFilter(environment.getProperty("token.secret"));
    }
}
