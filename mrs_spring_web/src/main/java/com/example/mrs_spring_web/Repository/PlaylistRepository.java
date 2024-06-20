package com.example.mrs_spring_web.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.mrs_spring_web.Model.Entity.PlaylistEntity;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long>{
    public List<PlaylistEntity> findAllByPlaylistUserId(String userId);
    public PlaylistEntity findByPlaylistId(Long playlistId);
    @Transactional
    @Modifying
    public void deleteAllByPlaylistUserId(String userId);
}
