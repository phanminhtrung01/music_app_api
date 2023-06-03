package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
    @Query(
            value = """
                    select * from history_search
                    where id_user = ?1
                    """, nativeQuery = true)
    List<Search> getSearchesByUser(String idUser);
}
