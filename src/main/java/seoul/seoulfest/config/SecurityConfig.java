package seoul.seoulfest.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.jwt.JwtFilter;
import seoul.seoulfest.auth.oauth2.handler.OAuth2FailureHandler;
import seoul.seoulfest.auth.oauth2.handler.OAuth2SuccessHandler;
import seoul.seoulfest.auth.oauth2.service.CustomOAuth2UserService;
import seoul.seoulfest.util.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final List<String> CORS_WHITELIST = List.of(
		"http://localhost:5173",
		"http://127.0.0.1:5500"
	);
	private static final List<String> WHITELIST = List.of(
		"/login",
		"/api/register",
		"/api/login",
		"/api/token/**",
		"/ws-stomp/**"
	);

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;
	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

		http
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
			);

		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(WHITELIST.toArray(new String[0])).permitAll()
				.requestMatchers("/api/auth/semi/**").hasRole("SEMI_USER")
				.requestMatchers("/api/auth/all-user/**").hasAnyRole("SEMI_USER", "USER")
				.requestMatchers("/api/auth/user/**").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/api/auth/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			);

		http
			.addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));
		return http.build();
	}

	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(CORS_WHITELIST);
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
