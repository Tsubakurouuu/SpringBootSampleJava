package com.example.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.formLogin(login -> login //フォーム認証の設定記述開始
			.loginProcessingUrl("/login") //ユーザー名、パスワードの送信先URL
			.loginPage("/login") //ログイン画面のURL
			.defaultSuccessUrl("/user/list", true) //ログイン成功後のリダイレクト先URL
			.failureUrl("/login?error") //ログイン失敗後のリダイレクト先URL
			.usernameParameter("userId") //ログインページのユーザーID
			.passwordParameter("password") //ログインページのパスワード
			.permitAll() //ログイン画面は未ログインでもアクセス可能
		).logout(logout -> logout
			.logoutUrl("/logout") //ログアウトの設定記述開始
			.logoutSuccessUrl("/login?logout") //ログアウト成功後のリダイレクト先URL
		).authorizeHttpRequests(authz -> authz
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // /css/**などはログイン無でもアクセス可能
			.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
			.requestMatchers("/user/signup").permitAll() //直リンクOK
			.anyRequest().authenticated() //他のURLはログイン後のみアクセス可能
		).csrf(csrf -> csrf
			.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
		).headers(headers -> headers.frameOptions().disable());
	
		
		return http.build();
	}
}
