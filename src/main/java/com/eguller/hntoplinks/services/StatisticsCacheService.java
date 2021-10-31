package com.eguller.hntoplinks.services;

import com.eguller.hntoplinks.models.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.lang.invoke.MethodHandles;

@ApplicationScope
@Service
public class StatisticsCacheService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Statistics statistics;
    public void setStatistics(Statistics statistics){
        if(statistics == null){
            logger.warn("Statistics is null, cache will not be updated");
            return;
        }
        this.statistics = statistics;
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
