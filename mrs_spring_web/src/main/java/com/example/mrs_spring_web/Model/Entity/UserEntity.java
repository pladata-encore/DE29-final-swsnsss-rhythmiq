package com.example.mrs_spring_web.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "User")
@Table(name = "user")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
  
  @Id
  private String username;
  private String password;
  private String email;
  private String role;
  
  // OAuth 정보 
  private String provider;
  private String providerId;
  @Column(length = 1000)
  private String accessToken;
  @Column(length = 1000)
  private String deviceId;
}