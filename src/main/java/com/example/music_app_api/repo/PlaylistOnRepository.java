package com.example.music_app_api.repo;

import com.example.music_app_api.entity.PlaylistOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistOnRepository extends JpaRepository<PlaylistOnline, String> {

}
