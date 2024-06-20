package com.example.mrs_spring_web.Model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDTO {
    private Long playlistId;
    private String playlistTracks;
    private String playlistTracksCount;
    private String playlistDuration;
    private String playlistUserId;
    private String playlistEnThemes;
    private String playlistThemes;
    private String playlistCoverSrc;

    private List<String> parsedThemes;
    private List<String> parsedEnThemes;
}
