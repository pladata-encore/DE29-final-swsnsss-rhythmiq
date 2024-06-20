package com.example.mrs_spring_web.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mrs_spring_web.Model.Entity.UserEntity;
import com.example.mrs_spring_web.Repository.PlaylistRepository;
import com.example.mrs_spring_web.Repository.UserRepository;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public PlaylistRepository playlistRepository;

    public String getAccesstoken (String username){
        UserEntity userEntity = userRepository.findByUsername(username);
        return userEntity.getAccessToken();
    }
    
    public void setDeviceId (String username, String deviceId){
        UserEntity userEntity = userRepository.findByUsername(username);
        userEntity.setDeviceId(deviceId);
        userRepository.save(userEntity);
    }

    public String getDeviceId (String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        return userEntity.getDeviceId();
    }

    public void deleteUser (String userId) {
        userRepository.deleteById(userId);
        playlistRepository.deleteAllByPlaylistUserId(userId);
    }

}
