package com.ikeyit.trade.controller;


import com.ikeyit.trade.dto.HomeStatsDTO;
import com.ikeyit.trade.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class StatsController {

    @Autowired
    StatsService statsService;


    @GetMapping("/home_stats")
    public HomeStatsDTO getHomeStats() {
        return statsService.getHomeStats();
    }
}
