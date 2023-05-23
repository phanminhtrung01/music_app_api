package com.example.music_app_api.controller.database;

import com.example.music_app_api.entity.Search;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.service.database_server.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/pmdv/db/search/")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {

        this.searchService = searchService;
    }

    @PostMapping("add")
    public ResponseEntity<ResponseObject> addSearch(
            @RequestBody Map<String, String> map) {
        try {
            String key = map.get("key");
            String idUser = map.get("id_user");
            Search search = new Search();
            search.setKey(key);
            Search searchPar = searchService.addSearch(search, idUser);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseObject(
                            HttpStatus.CREATED.value(),
                            "Query add search successful!",
                            searchPar));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @DeleteMapping("delete/all")
    public ResponseEntity<ResponseObject> deleteAllSearch(
            @RequestParam("idUser") String idUser) {
        try {
            boolean checkDelSearch = searchService.deleteSearchAllByIdUser(idUser);

            if (checkDelSearch) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseObject(
                                HttpStatus.OK.value(),
                                "Query remove comment successful!",
                                true));
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Query remove comment failed!",
                            false));

        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @GetMapping("get/searches_by_user")
    public ResponseEntity<ResponseObject> getSearchesByUser(
            @RequestParam("idUser") String idUser) {

        try {
            List<Search> searches = searchService.getSearchesByIdUser(idUser);

            if (!searches.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseObject(
                                HttpStatus.OK.value(),
                                "Query get searches by user successful!",
                                searches));
            }

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            HttpStatus.NOT_FOUND.getReasonPhrase(),
                            searches));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }

    }
}
