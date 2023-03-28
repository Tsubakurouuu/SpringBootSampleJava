package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/*セキュリティの対象外を設定*/
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
		
		//セキュリティを適用しない
        return web -> web.ignoring()
                .antMatchers("/webjars/**")
                .antMatchers("/css/**")
                .antMatchers("/js/**")
                .antMatchers("/h2-console/**");
    }
	
	/*セキュリティの各種設定*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
    	//ログイン不要ページの設定
        http.formLogin(login -> login
                .loginProcessingUrl("/login") // ログイン処理のパス
                .loginPage("/login") //ログインページ
                .failureUrl("/login?error") //失敗時
                .usernameParameter("userId")
                .passwordParameter("password")
                .defaultSuccessUrl("/user/list", true)
        ).authorizeHttpRequests(authz -> authz
                .mvcMatchers("/login").permitAll() // 直リンクOK
                .mvcMatchers("/user/signup").permitAll() // 直リンクOK
                .anyRequest().authenticated() //それ以外は直リンクNG
        );
        
        //CSRF対策を無効に設定(一時的)
        http.csrf().disable();
        return http.build();
    }
    
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
    	//インメモリ認証
        PasswordEncoder encoder = passwordEncoder();
        UserDetails user = User.withUsername("user").password(encoder.encode("user")).roles("GENERAL").build(); //userを追加
        UserDetails admin = User.withUsername("admin").password(encoder.encode("admin")).roles("ADMIN").build(); //adminを追加
        return new InMemoryUserDetailsManager(user, admin);
    }
}
