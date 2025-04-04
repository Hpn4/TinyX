package com.tinyx.repository;

import com.tinyx.home.entity.HomeTimelineMongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class SvcHomeTimelineRepository
    implements PanacheMongoRepositoryBase<HomeTimelineMongoEntity, UUID> {}
