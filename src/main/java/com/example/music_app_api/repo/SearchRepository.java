package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, String> {
    @Query(
            value = """
                    SELECT s FROM Search s
                    JOIN s.user u
                    WHERE u.idUser = ?1
                    """)
    List<Search> getSearchesByUser(String idUser);
}
