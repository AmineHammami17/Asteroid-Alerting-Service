package com.project.asteroidalert.Services;


import com.project.asteroidalert.Client.NasaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class AsteroidAlertingService {

    private final NasaClient nasaClient;

    @Autowired
    public AsteroidAlertingService(NasaClient nasaClient) {
        this.nasaClient = nasaClient;
    }

    public void alert(){
        log.info("Alerting service called");

        final LocalDate fromDate= LocalDate.now();
        final LocalDate toDate = LocalDate.now().plusDays(7);

        log.info("Getting asteroid list for dates {} to {}",fromDate,toDate);


    }

}
