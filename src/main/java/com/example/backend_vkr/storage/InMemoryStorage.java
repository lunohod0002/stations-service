package com.example.backend_vkr.storage;


import com.example.backend_vkr.models.AttractionInfoResponse;
import com.example.backend_vkr.models.AttractionResponse;
import com.example.backend_vkr.models.StationResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, StationResponse> stations = new ConcurrentHashMap<>();
    public final Map<Long, AttractionInfoResponse> attractions = new ConcurrentHashMap<>();

    public final AtomicLong stationSequence = new AtomicLong(0);
    public final AtomicLong attractionSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {
        // Создаем несколько авторов
        StationResponse station1 = new StationResponse(stationSequence.incrementAndGet(), "Краснопресненская","1","Красная","Адрес тест","1980 г","Построена на месте революционных событий","1", new ArrayList<>());
        StationResponse station2 = new StationResponse(stationSequence.incrementAndGet(), "Чеховская","9","Серая","Адрес тест 2","1980 г","Построена на месте революционных событий","3",new ArrayList<>());
        stations.put(station1.id(), station1);
        stations.put(station2.id(), station2);

        long attractionId1 = attractionSequence.incrementAndGet();
        AttractionResponse attraction1 = new AttractionResponse(attractionId1, "Московский Зоопарк", 200,"УЛ Баррикадная");
        AttractionInfoResponse attractionInfo1=new AttractionInfoResponse(attractionId1,"Московский Зоопарк", "УЛ Баррикадная","8:00-12:00","Самый большой зоопарк в Европе",1200,"1");
        attractions.put(attractionId1,attractionInfo1);

        long attractionId2 = attractionSequence.incrementAndGet();
        AttractionResponse attraction2 = new AttractionResponse(attractionId2, "Музей вооруженнных сил", 1500,"УЛ Берзарина");
        AttractionInfoResponse attractionInfo2=new AttractionInfoResponse(attractionId2,"Музей вооруженнных сил", "УЛ Берзарина","8:00-12:00","Самый большой музей в Москве",1500,"2");
        attractions.put(attractionId2,attractionInfo2);


        station1.attractionResponseList().add(attraction1);
        station1.attractionResponseList().add(attraction2);
        station2.attractionResponseList().add(attraction2);

    }
}