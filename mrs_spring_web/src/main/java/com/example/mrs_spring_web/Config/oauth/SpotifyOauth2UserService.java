package com.example.mrs_spring_web.Config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.mrs_spring_web.Config.auth.PrincipalDetails;
import com.example.mrs_spring_web.Model.Entity.UserEntity;
import com.example.mrs_spring_web.Repository.UserRepository;
import com.example.mrs_spring_web.Service.SpotifyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpotifyOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  public @Lazy BCryptPasswordEncoder bCryptPasswordEncoder;
  
  @Autowired
  public SpotifyService spotifyService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // TODO Auto-generated method stub
    OAuth2UserService delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);   
    String accessToken = userRequest.getAccessToken().getTokenValue();
    String provider = userRequest.getClientRegistration().getClientId(); // google 
    String providerId = oAuth2User.getAttribute("id");
    String username = providerId;
    UserEntity userEntity = userRepository.findByUsername(username);
    log.info(oAuth2User.getAttributes().toString());
    // 기존 사용자 
    if (userEntity != null) {
      userEntity.setAccessToken(accessToken);
      userRepository.save(userEntity);
      return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }

    // 신규 가입 
    String password = bCryptPasswordEncoder.encode(username+"mtp!@~");
    String email = oAuth2User.getAttribute("email");
    String role = "USER";

    UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setPassword(password);
    user.setEmail(email);
    user.setProvider(provider);
    user.setProviderId(providerId);
    user.setRole(role);
    user.setAccessToken(accessToken);
    userRepository.save(user);

    return new PrincipalDetails(user, oAuth2User.getAttributes());
  }


}