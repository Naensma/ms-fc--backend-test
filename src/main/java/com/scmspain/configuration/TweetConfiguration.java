package com.scmspain.configuration;

import com.scmspain.controller.TweetController;
import com.scmspain.dao.ITweetDao;
import com.scmspain.dao.H2TweetDao;
import com.scmspain.services.TweetService;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class TweetConfiguration {
    @Bean
    public ITweetDao getTweetDao(EntityManager entityManager){
        return new H2TweetDao(entityManager);
    }

    @Bean
    public TweetService getTweetService(ITweetDao tweetDao, MetricWriter metricWriter) {
        return new TweetService(tweetDao, metricWriter);
    }

    @Bean
    public TweetController getTweetConfiguration(TweetService tweetService) {
        return new TweetController(tweetService);
    }
}
