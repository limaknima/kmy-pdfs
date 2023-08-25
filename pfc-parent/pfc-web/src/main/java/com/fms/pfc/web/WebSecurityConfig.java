package com.fms.pfc.web;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
	private DataSource ds;
    
    @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;
    
    @Autowired
    private CustomLoginFailureHandler loginFailureHandler;
    
    @Autowired
    private CustomLogoutHandler logoutHandler;
    
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    	
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            .dataSource(ds)
            .usersByUsernameQuery("select user_id, password, 'true' from usr where user_id=? and DISABLED_FLAG = 'N'")
            .authoritiesByUsernameQuery("select user_id, 'true' from usr_role where user_id=?");
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/login**", "/static/**", "/css/**", "/image/**", "/js/**" ).permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin().permitAll()
						.loginPage("/login")
						.usernameParameter("user_id")
						.passwordParameter("password")
						.successHandler(loginSuccessHandler)
						.failureHandler(loginFailureHandler)
			.and()
			.logout().permitAll()
					 .addLogoutHandler(logoutHandler)
			.and()
			.sessionManagement().invalidSessionUrl("/login"); //FSGS) Faiz 179 - add invalidSessionUrl to /login
		
		http.sessionManagement().maximumSessions(1);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/forgetPass");
	}
	
}