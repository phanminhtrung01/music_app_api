package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Charts;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.ChartRepository;
import com.example.music_app_api.repo.SongRepository;
import com.example.music_app_api.service.database_server.ChartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChartsServiceImpl implements ChartsService {

    private final ChartRepository chartRepository;
    private final SongRepository songRepository;

    @Autowired
    public ChartsServiceImpl(
            ChartRepository chartRepository,
            SongRepository songRepository) {
        this.chartRepository = chartRepository;
        this.songRepository = songRepository;
    }

    @Override
    public List<Charts> getAll() {
        try {
            return chartRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Charts getChartById(String idChart) {
        try {
            Optional<Charts> chartsOptional = chartRepository.findById(idChart);
            if (chartsOptional.isEmpty()) {
                throw new NotFoundException("Not fount chart with ID: " + idChart);
            }

            return chartsOptional.get();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Charts save(Charts chart) {
        try {
            chartRepository.save(chart);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return chart;
    }

    @Override
    public Charts deleteById(String idChart) {
        try {
            Charts charts = getChartById(idChart);
            chartRepository.delete(charts);

            return charts;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
