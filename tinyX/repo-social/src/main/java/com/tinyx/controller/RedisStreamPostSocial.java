package com.tinyx.controller;

import com.tinyx.service.SocialService;
import io.quarkus.runtime.Startup;
import jakarta.ejb.Schedule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.scheduler.Scheduled;
@Startup
@ApplicationScoped
public class RedisStreamPostSocial {
    @Inject
    SocialService service;
    public RedisStreamPostSocial(){}

    public void process(){}


}
