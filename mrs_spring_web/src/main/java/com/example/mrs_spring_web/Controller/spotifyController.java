package com.example.mrs_spring_web.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.mrs_spring_web.Service.SpotifyService;
import com.example.mrs_spring_web.Service.UserService;

import lombok.extern.slf4j.Slf4j;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class spotifyController {
    
    @Autowired
    public SpotifyService spotifyService;

    @Autowired
    public UserService userService;
    

    @PostMapping("/saveplaylist")
    @ResponseBody
    public void makePlaylistToSpotify(@RequestParam("playlistName") String playlistName, @RequestParam("tracklist") String[] tracklist, Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        //TODO: process POST request
        log.info(tracklist[0]);
        spotifyService.makePlaylist(userDetailsObj.getUsername(), tracklist, playlistName);
    }

    @PostMapping("/deviceid")
    @ResponseBody
    public void setDeviceid(@RequestBody String device_id, Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        //TODO: process POST request
        String username = userDetailsObj.getUsername();
        userService.setDeviceId(username, device_id);
        
    }

    @PostMapping("/playlist")
    public List<String> playPlaylist(@RequestParam("id") String playlistId, Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        return spotifyService.getTracklistByPlaylist(userDetailsObj.getUsername(), playlistId);
    }
    
    @PostMapping("/setmusic")
    public void musicplayer(@RequestBody String trackdata, Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        log.info(trackdata.toString());
        List<String> tracksList = new ArrayList<>();
        String[] tracksArray = trackdata.substring(1, trackdata.length()-1).split(", ");

        // 문자열에서 "[", "]"를 제거하고 ", "을 구분자로 사용하여 분리
        // 분리된 각 문자열을 리스트에 추가
        for (String track : tracksArray) {
            tracksList.add(track);
        }
        log.info(tracksList.toString());
        spotifyService.addTracksToQueue(userDetailsObj.getUsername(), tracksList);
    }

    @GetMapping("/getdevice")
    public String getAvaliableDevice(Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        return spotifyService.getAvaliableDevices(userDetailsObj.getUsername());
    }
    
    @PostMapping("/activatedevice")
    @ResponseBody
    public void activateDevice(Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        //TODO: process POST request
        log.info("[ActivateDevice]");
        String username = userDetailsObj.getUsername();
        spotifyService.activateDevice(username, userService.getDeviceId(username));
    }

    @GetMapping("/getplaylistart")
    @ResponseBody
    public String getPlaylistArt(@RequestParam("id") String playlistId, Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        String username = userDetailsObj.getUsername();
        Image playlistArt = spotifyService.getPlaylistArt(username, playlistId);
        return playlistArt.getUrl();
    }

    @GetMapping("/playerbar")
    public CurrentlyPlayingContext getPlayerState(Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) throws Exception{
        String username = userDetailsObj.getUsername();
        return spotifyService.getCurrentPlayState(username);
    }
    
}
