package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Search;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.SearchRepository;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SearchServiceIml implements SearchService {

    private final SearchRepository searchRepository;
    private final UserRepository userRepository;

    @Autowired
    public SearchServiceIml(
            SearchRepository searchRepository,
            UserRepository userRepository) {
        this.searchRepository = searchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Search addSearch(Search search, String idUser) {
        try {
            Optional<User> userOptional = userRepository.findById(idUser);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not found user with ID: " + idUser);
            }

            search.setUser(userOptional.get());
            return searchRepository.save(search);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Search deleteSearch(String idSearch) {
        try {
            Search search = getSearchById(idSearch);
            searchRepository.delete(search);

            return search;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Search getSearchById(String idSearch) {
        try {
            Optional<Search> searchOptional = searchRepository.findById(idSearch);
            if (searchOptional.isEmpty()) {
                throw new NotFoundException("Not fount search with ID: " + idSearch);
            }

            return searchOptional.get();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public boolean deleteSearchAllByIdUser(String idUser) {
        try {
            Optional<User> userOptional = userRepository.findById(idUser);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not found user with ID: " + idUser);
            }

            User user = userOptional.get();
            user.getSearches().clear();
            userRepository.save(user);

            return true;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Search> getSearchesByIdUser(String idUser) {
        try {
            Optional<User> userOptional = userRepository.findById(idUser);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not found user with ID: " + idUser);
            }
            return searchRepository.getSearchesByUser(idUser);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }


}
