package com.example.music_app_api.service.database_server;


import com.example.music_app_api.entity.Charts;

import java.util.List;

public interface ChartsService {
    List<Charts> getAll();

    Charts getChartById(String idChart);

    Charts save(Charts chart);

    Charts deleteById(String idChart);
}
