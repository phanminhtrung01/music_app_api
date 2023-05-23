package com.example.music_app_api.controller.database;

import com.example.music_app_api.entity.Charts;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.service.database_server.ChartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/pmdv/db/chart/")
public class ChartsController {
    private final ChartsService chartsService;

    @Autowired
    public ChartsController(ChartsService chartsService) {

        this.chartsService = chartsService;
    }


    @GetMapping("all")
    public ResponseEntity<ResponseObject> getAllCharts() {
        try {
            List<Charts> charts = chartsService.getAll();

            return charts.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query charts successful!",
                                    charts)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.NO_CONTENT)
                            .body(new ResponseObject(
                                    HttpStatus.NO_CONTENT.value(),
                                    HttpStatus.NO_CONTENT.getReasonPhrase(),
                                    charts)
                            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }

    }

    @GetMapping("get")
    public ResponseEntity<ResponseObject> getChart(
            @RequestParam("idChart") String idChart) {

        try {
            Charts chart = chartsService.getChartById(idChart);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query get chart successful!",
                            chart));

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

    @PostMapping("add")
    public ResponseEntity<ResponseObject> addChart(
            @RequestBody Charts charts) {
        try {
            Charts chartsPar = chartsService.save(charts);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseObject(
                            HttpStatus.CREATED.value(),
                            "Query add chart successful!",
                            chartsPar));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<ResponseObject> deleteChart(
            @RequestParam("idChart") String idChart) {
        try {
            Charts chart = chartsService.deleteById(idChart);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove chart successful!",
                            chart));

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
