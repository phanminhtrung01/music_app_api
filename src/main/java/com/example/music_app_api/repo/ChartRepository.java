package com.example.music_app_api.repo;


import com.example.music_app_api.entity.Charts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartRepository extends JpaRepository<Charts, String> {
}
