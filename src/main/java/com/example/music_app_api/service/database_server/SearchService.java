package com.example.music_app_api.service.database_server;

import com.example.music_app_api.entity.Search;

import java.util.List;

public interface SearchService {

    Search addSearch(Search search, String idUser);

    boolean deleteSearchAllByIdUser(String idUser);

    Search deleteSearch(Long idSearch);

    Search getSearchById(Long idSearch);

    List<Search> getSearchesByIdUser(String idUser);
}
