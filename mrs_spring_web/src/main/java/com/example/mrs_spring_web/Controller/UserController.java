package com.example.mrs_spring_web.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mrs_spring_web.Model.DTO.PlaylistDTO;
import com.example.mrs_spring_web.Service.PlaylistService;
import com.example.mrs_spring_web.Service.SpotifyService;
import com.example.mrs_spring_web.Service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;
import se.michaelthelin.spotify.model_objects.specification.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    public SpotifyService spotifyService;

    @Autowired
    public UserService userService;

    @Autowired
    public PlaylistService playlistService;


    @GetMapping("/main")
    public String main(Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj, Model model)
            throws Exception {
        User currentUser = spotifyService.getUserProfile(userDetailsObj.getUsername());
        model.addAttribute("username", currentUser.getDisplayName());
        model.addAttribute("koreatoptrackart",
                spotifyService.getPlaylistArt(userDetailsObj.getUsername(), "37i9dQZEVXbNxXF4SkHj9F").getUrl());
        model.addAttribute("globaltoptrackart",
                spotifyService.getPlaylistArt(userDetailsObj.getUsername(), "37i9dQZEVXbMDoHDwVN2tF").getUrl());
        return "main";
    }


    @GetMapping("/playlist")
    public String showplaylist(@RequestParam(value = "id", required = false) String playlistId,
            @RequestParam(value = "recommendedtracks", required = false) String recommendedtracks,
            @RequestParam(value = "tokenizedTheme", required = false) String themes,
            @RequestParam(value = "entokenizedTheme", required = false) String enthemes,
            @RequestParam(value = "playlistCoverSrc", required = false) String src,
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetailsObj, Model model) throws Exception {

        String accesstoken = userService.getAccesstoken(userDetailsObj.getUsername());
        if (playlistId != null) {
            List<String> Tracks = spotifyService.getTracklistByPlaylist(userDetailsObj.getUsername(), playlistId);
            model.addAttribute("playlistArt",
                    spotifyService.getPlaylistArt(userDetailsObj.getUsername(), playlistId).getUrl());
            model.addAttribute("tracklist", Tracks);
            Gson gson = new Gson();

        // List를 JsonArray로 변환
            JsonElement trackElement = gson.toJsonTree(Tracks);
            JsonArray tracksJson = trackElement.getAsJsonArray();
            List<String> theme = new ArrayList<>();
            theme.add("인기 차트");
            JsonElement themeElement = gson.toJsonTree(theme);
            JsonArray themesJson = themeElement.getAsJsonArray();
            List<String> entheme = new ArrayList<>();
            entheme.add("Top Tracks");
            JsonElement enthemeElement = gson.toJsonTree(entheme);
            JsonArray enthemesJson = enthemeElement.getAsJsonArray();
            model.addAttribute("tracklist2", tracksJson);
            model.addAttribute("themes", "인기 차트");
            model.addAttribute("stringThemes", themesJson);
            model.addAttribute("stringEnThemes", enthemesJson);
            model.addAttribute("trackDataList", spotifyService.getTracksdata(userDetailsObj.getUsername(), Tracks));
        } else {
            List<String> Tracks = new Gson().fromJson(recommendedtracks, new TypeToken<List<String>>() {
            }.getType());
            model.addAttribute("tracklist", Tracks);
            model.addAttribute("tracklist2", recommendedtracks);
            model.addAttribute("trackDataList", spotifyService.getTracksdata(userDetailsObj.getUsername(), Tracks));
            model.addAttribute("themes", new Gson().fromJson(themes, new TypeToken<List<String>>() {
            }.getType()));
            model.addAttribute("enthemes", new Gson().fromJson(enthemes, new TypeToken<List<String>>() {
            }.getType()));
            model.addAttribute("stringThemes", themes);
            model.addAttribute("stringEnThemes", enthemes);
            if (src != null) {
                model.addAttribute("playlistArt", src);
            }
        }
        model.addAttribute("accesstoken", accesstoken);

        User currentUser = spotifyService.getUserProfile(userDetailsObj.getUsername());
        model.addAttribute("username", currentUser.getDisplayName());
        return "playlist";
    }

    @GetMapping("/playerbar")
    public String showplayerbar(@RequestParam String trackdata, Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetailsObj, Model model) throws Exception {

        String accesstoken = userService.getAccesstoken(userDetailsObj.getUsername());
        spotifyService.getCurrentPlayState(userDetailsObj.getUsername());
        List<String> tracksList = new ArrayList<>();
        String[] tracksArray = trackdata.split(", ");

        // 문자열에서 "[", "]"를 제거하고 ", "을 구분자로 사용하여 분리
        // 분리된 각 문자열을 리스트에 추가
        for (String track : tracksArray) {
            tracksList.add(track);
        }

        log.info("show playerbar : trackdata : " + trackdata + accesstoken);
        model.addAttribute("accesstoken", accesstoken);
        model.addAttribute("tracklist", tracksList);
        model.addAttribute("trackDataList", spotifyService.getTracksdata(userDetailsObj.getUsername(), tracksList));
        return "playerbar";
    }

    @GetMapping("/makeplaylist")
    public String makeplaylist(Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj,
            Model model) throws Exception{
        log.info("[UserController] makeplaylist start!!");
        User currentUser = spotifyService.getUserProfile(userDetailsObj.getUsername());
        model.addAttribute("username", currentUser.getDisplayName());
        return "themeselect";
    }

    @GetMapping("/mylists")
    public String showUserLikePlaylist(Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj, Model model) throws Exception{
        List<PlaylistDTO> playlists = playlistService.getSavedPlaylists(userDetailsObj.getUsername());
        User currentUser = spotifyService.getUserProfile(userDetailsObj.getUsername());
        model.addAttribute("username", currentUser.getDisplayName());
        Gson gson = new Gson();

        // 각 PlaylistDTO의 playlistThemes를 파싱하여 모델에 추가
        for (PlaylistDTO playlist : playlists) {
            String themesJson = playlist.getPlaylistThemes();
            String enthemesJson = playlist.getPlaylistEnThemes();
            List<String> themes = gson.fromJson(themesJson, new TypeToken<List<String>>() {}.getType());
            List<String> enthemes = gson.fromJson(enthemesJson, new TypeToken<List<String>>() {}.getType());
            playlist.setParsedThemes(themes);
            playlist.setParsedEnThemes(enthemes);
        }
    
        model.addAttribute("playlists", playlists);
        return "mylist";
    }

    @PostMapping("/deleteuser")
    public String deleteUser(Authentication authentication, @AuthenticationPrincipal UserDetails userDetailsObj) {
        //TODO: process POST request
        userService.deleteUser(userDetailsObj.getUsername());
        return "redirect:/logout";
    }
    
}
