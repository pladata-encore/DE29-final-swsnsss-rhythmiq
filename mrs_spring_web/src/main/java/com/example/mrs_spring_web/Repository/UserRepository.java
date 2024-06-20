package com.example.mrs_spring_web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mrs_spring_web.Model.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
  
    // findBy 규칙 
    // findByUsername() -> select * from user where username = 1?
    public UserEntity findByUsername(String username);
  
  }
