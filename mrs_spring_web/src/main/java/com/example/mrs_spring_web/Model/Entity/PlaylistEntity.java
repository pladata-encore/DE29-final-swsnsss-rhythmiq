package com.example.mrs_spring_web.Model.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity(name = "Playlist")
@Table(name = "playlist")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistEntity {
    @Id
    @GeneratedValue
    private Long playlistId;
    @Column(length = 10000)
    private String playlistTracks;
    private String playlistTracksCount;
    private String playlistDuration;
    private String playlistUserId;
    private String playlistThemes;
    private String playlistEnThemes;
    private String playlistCoverSrc;
}
