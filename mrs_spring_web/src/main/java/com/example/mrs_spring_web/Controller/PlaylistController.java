package com.example.mrs_spring_web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.mrs_spring_web.Model.DTO.PlaylistDTO;
import com.example.mrs_spring_web.Service.PlaylistService;
import com.example.mrs_spring_web.Service.S3Service;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
@RequestMapping("/api/v1")
public class PlaylistController {

    @Autowired
    public PlaylistService playlistService;

    @Autowired
    public S3Service s3Service;


    @PostMapping("/likeplaylist")
    public void saveplaylist(@RequestBody PlaylistDTO playlistDTO, Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        //TODO: process POST request
        log.info("savepltodb");
        playlistDTO.setPlaylistUserId(userDetailsObj.getUsername());
        playlistService.savePlaylist(playlistDTO);
    }

    @PostMapping("/deleteplaylist")
    public void deleteplaylist(@RequestBody PlaylistDTO playlistDTO, Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        //TODO: process POST request
        playlistService.deletePlaylist(playlistDTO.getPlaylistId());
    }
    
    @PostMapping("/imagetos3")
    public String imageUploadToS3(@RequestParam("file") MultipartFile file) throws Exception{
        log.info(file.toString());
        //TODO: process POST request
        return s3Service.upload(file, "PlaylistImages");
    }
    

}
