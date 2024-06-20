package com.example.mrs_spring_web.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mrs_spring_web.Model.DTO.PlaylistDTO;
import com.example.mrs_spring_web.Model.Entity.PlaylistEntity;
import com.example.mrs_spring_web.Repository.PlaylistRepository;


@Service
public class PlaylistService {
    @Autowired
    public PlaylistRepository playlistRepository;

    public List<PlaylistDTO> getSavedPlaylists(String userId) {
        List<PlaylistEntity> entities = playlistRepository.findAllByPlaylistUserId(userId);
        List<PlaylistDTO> dtos = new ArrayList<>();
        for (PlaylistEntity playlistEntity : entities) {
            PlaylistDTO playlistDTO = new PlaylistDTO();
            playlistDTO.setPlaylistDuration(playlistEntity.getPlaylistDuration());
            playlistDTO.setPlaylistId(playlistEntity.getPlaylistId());
            playlistDTO.setPlaylistThemes(playlistEntity.getPlaylistThemes());
            playlistDTO.setPlaylistTracks(playlistEntity.getPlaylistTracks());
            playlistDTO.setPlaylistUserId(playlistEntity.getPlaylistUserId());
            playlistDTO.setPlaylistTracksCount(playlistEntity.getPlaylistTracksCount());
            playlistDTO.setPlaylistCoverSrc(playlistEntity.getPlaylistCoverSrc());
            playlistDTO.setPlaylistEnThemes(playlistEntity.getPlaylistEnThemes());
            dtos.add(playlistDTO);
        }
        return dtos;
    }

    public void savePlaylist(PlaylistDTO playlistDTO) {
        PlaylistEntity playlistEntity = new PlaylistEntity();
        playlistEntity.setPlaylistDuration(playlistDTO.getPlaylistDuration());
        playlistEntity.setPlaylistThemes(playlistDTO.getPlaylistThemes());
        playlistEntity.setPlaylistTracks(playlistDTO.getPlaylistTracks());
        playlistEntity.setPlaylistUserId(playlistDTO.getPlaylistUserId());
        playlistEntity.setPlaylistTracksCount(playlistDTO.getPlaylistTracksCount());
        playlistEntity.setPlaylistCoverSrc(playlistDTO.getPlaylistCoverSrc());
        playlistEntity.setPlaylistEnThemes(playlistDTO.getPlaylistEnThemes());
        playlistRepository.save(playlistEntity);
    }

    public void deletePlaylist(Long playlistId) {
        playlistRepository.deleteById(playlistId);
    }
}
