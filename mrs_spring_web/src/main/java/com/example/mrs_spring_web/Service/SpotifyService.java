package com.example.mrs_spring_web.Service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mrs_spring_web.Model.Entity.UserEntity;
import com.example.mrs_spring_web.Repository.UserRepository;
import com.google.gson.JsonArray;

import lombok.extern.slf4j.Slf4j;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;

@Slf4j
@Service
public class SpotifyService {
    @Autowired
    public UserRepository userRepository;

    private static final URI redirectUri = SpotifyHttpManager.makeUri("스포티파이 앱의 redirecturi를 입력해주세요.");
    private static final String CLIENT_ID = "스포티파이 앱의 clientid를 입력해주세요.";
    private static final String CLIENT_SECRET = "스포티파이 앱의 clientsecret를 입력해주세요.";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
                                                .setClientId(CLIENT_ID)
                                                .setClientSecret(CLIENT_SECRET)
                                                .setRedirectUri(redirectUri)
                                                .build();

    public List<String> getTracklistByPlaylist(String userName, String playlistId) throws IOException, ParseException, SpotifyWebApiException {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        PlaylistTrack[] playlistTracks = spotifyApi.getPlaylist(playlistId).build().execute().getTracks().getItems();
        List<String> trackLists = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTracks) {
            trackLists.add(playlistTrack.getTrack().getUri());
        }
        return trackLists;
    }

    public void addTracksToQueue(String userName, List<String> tracks) throws IOException, ParseException, SpotifyWebApiException {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        JsonArray trackUris = new JsonArray();
        for (String track : tracks) {
            trackUris.add(track);
        }
        log.info("[StartTracks]"+trackUris.toString());
        spotifyApi.startResumeUsersPlayback().uris(trackUris).device_id(user.getDeviceId()).build().execute();
    }

    public User getUserProfile(String userName) throws IOException, ParseException, SpotifyWebApiException {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        User userprofile = spotifyApi.getCurrentUsersProfile().build().execute();
        return userprofile;
    }

    public void makePlaylist(String userName, String[] trackList, String playlistName) throws IOException, ParseException, SpotifyWebApiException{
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        Playlist newPlaylist = spotifyApi.createPlaylist(userName, playlistName).build().execute();
        spotifyApi.addItemsToPlaylist(newPlaylist.getId(), trackList).build().execute();
    }
    
    public String getAvaliableDevices(String userName) throws Exception {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        return spotifyApi.getUsersAvailableDevices().build().execute()[1].getId();
    }
    
    public void activateDevice(String userName, String deviceId) throws Exception {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        JsonArray deviceids = new JsonArray();
        deviceids.add(deviceId);
        log.info("[activateDevice]"+deviceids.toString());
        spotifyApi.transferUsersPlayback(deviceids).build().execute();
    }

    public Image getPlaylistArt(String userName, String playlistId) throws Exception {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        Image[] playlistArts = spotifyApi.getPlaylistCoverImage(playlistId).build().execute();
        return playlistArts[0];
    }

    public List<Track> getTracksdata(String userName, List<String> trackids) throws Exception {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        List<Track> trackDataList = new ArrayList<>();
        for (String trackid : trackids) {
            Track trackdata = spotifyApi.getTrack(trackid.substring(14)).build().execute();
            trackDataList.add(trackdata);
        }
        return trackDataList;
    }

    public List<String> getArtistName(String userName, Track track) throws Exception {
        List<String> artistNames = new ArrayList<>();

        ArtistSimplified[] artists = track.getArtists();
        for (ArtistSimplified artist: artists) {
            artistNames.add(artist.getName());
        }
        return artistNames;

    }

    public CurrentlyPlayingContext getCurrentPlayState(String userName) throws Exception {
        UserEntity user = userRepository.findByUsername(userName);
        spotifyApi.setAccessToken(user.getAccessToken());
        CurrentlyPlayingContext currentlyPlayingContext = spotifyApi.getInformationAboutUsersCurrentPlayback().build().execute();
        return currentlyPlayingContext;
    }
}
