package com.example.mrs_spring_web.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.mrs_spring_web.Config.handler.LoginAuthFailureHandler;
import com.example.mrs_spring_web.Config.handler.LoginAuthSuccessHandler;
import com.example.mrs_spring_web.Config.handler.LogoutAuthSuccesshandler;
import com.example.mrs_spring_web.Config.oauth.SpotifyOauth2UserService;



@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // @Secured 어노테이션 활성화, @PreAuthorize 어노테이션 활성화  
public class SecurityConfig {

  @Bean // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
  public BCryptPasswordEncoder encodePwd() {
    return new BCryptPasswordEncoder();
  }
  @Autowired
  private LoginAuthSuccessHandler loginAuthSuccessHandler;
  @Autowired
  private LoginAuthFailureHandler loginAuthFailureHandler;
  @Autowired
  private LogoutAuthSuccesshandler logoutAuthSuccesshandler;
  @Autowired
  private SpotifyOauth2UserService spotifyOauth2UserService;
  /**
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain DefaultfilterChain(HttpSecurity http) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable);
    http
      .authorizeHttpRequests(authorize -> authorize
          .requestMatchers("/user/**").authenticated() // 인증이되면 접근 가능 
          .requestMatchers("/manager/**").hasAnyAuthority("ADMIN", "MANAGER") // 인증&인가가 되면 접근 가능 
          .requestMatchers("/admin/**").hasAnyAuthority("ADMIN") // 인증&인가가 되면 접근 가능
          .anyRequest().permitAll() // 누구나 접근 가능 
      )
      .oauth2Login(oauth2 -> oauth2 
          // 1. 코드받기(인증) 성공시, 2. 엑세스토큰(권한) + 사용자프로필정보
          // 3. 사용자프로필 정보를 가져옴 4. 그 정보를 토대로 회원가입 진행  
          // .loginPage("/loginForm")
          .defaultSuccessUrl("/user/main")
          .userInfoEndpoint(userInfo -> userInfo
            .userService(this.spotifyOauth2UserService)
          )
          .successHandler(loginAuthSuccessHandler)
          // .defaultSuccessUrl("/user/index")
          // 로그인 실패시
          .failureHandler(loginAuthFailureHandler)
          // 그외의 모든 url path는 누구나 접근 가능
          .permitAll()
      )
      .logout(logout -> logout
          // 로그아웃 요청 url path
          .logoutUrl("/logout")
          // 로그아웃 성공시
          .logoutSuccessHandler(logoutAuthSuccesshandler)
          .permitAll());


    return http.build();
  }

}
