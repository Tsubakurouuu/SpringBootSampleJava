package com.example.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/**セキュリティの各種設定*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(login -> login //フォーム認証の設定記述開始
        		.loginProcessingUrl("/login") //ユーザー名、パスワードの送信先URL
        		.loginPage("/login") //ログイン画面のURL
        		.failureUrl("/login?error") //ログイン失敗後のリダイレクト先URL
        		.usernameParameter("userId") //ログインページのユーザーID
        		.passwordParameter("password") //ログインページのパスワード
        		.defaultSuccessUrl("/user/list", true) //ログイン成功後のリダイレクト先URL
        		.permitAll() //ログイン画面は未ログインでもアクセス可能
        ).authorizeHttpRequests(authz -> authz
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll() // "/css/**"などはログインなしでもアクセス可能
                .requestMatchers("/login").permitAll() //直リンクOK
                .requestMatchers("/user/signup").permitAll() //直リンクOK
//                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated() //それ以外は直リンクNG
        );
        
        //CSRF対策を無効に設定(一時的)
        http.csrf().disable();
        
        return http.build();
    }
    
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();
        UserDetails user = User.withUsername("user").password(encoder.encode("user")).roles("GENERAL").build();
        UserDetails admin = User.withUsername("admin").password(encoder.encode("admin")).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(user, admin);
    }

}