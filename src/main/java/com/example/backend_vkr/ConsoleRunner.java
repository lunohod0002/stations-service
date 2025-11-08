package com.example.backend_vkr;

import com.example.backend_vkr.entities.Attraction;
import com.example.backend_vkr.entities.Media;
import com.example.backend_vkr.entities.Station;
import com.example.backend_vkr.entities.StationAttractions;
import com.example.backend_vkr.entities.enums.MediaType;
import com.example.backend_vkr.repositories.AttractionRepository;
import com.example.backend_vkr.repositories.MediaRepository;
import com.example.backend_vkr.repositories.StationAttractionsRepository;
import com.example.backend_vkr.repositories.StationRepository;
import com.example.backend_vkr.services.StationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.beans.Transient;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class ConsoleRunner implements CommandLineRunner {

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private StationAttractionsRepository stationAttractionsRepository;
    @Transactional
    @Override
    public void run(String... args) throws Exception {
        Media photo1 = new Media(MediaType.PHOTO,"Фото станции 1", "https://example.com/photo1.jpg");

        Media video1 = new Media(MediaType.VIDEO,"Видео 1", "https://example.com/video1.mp4");
        Media audio1 = new Media(MediaType.AUDIO, "Ауидо 1","https://example.com/audio1.mp3");
        mediaRepository.save(photo1);
        mediaRepository.save(video1);
        mediaRepository.save(audio1);

        Station station1 = new Station("Площадь Восстания", "Невский проспект, 1", "Центральная станция", "Невско-Василеостровская");
        station1.setBuiltAt("1955");



        station1.setMedias(Set.of(photo1,video1,audio1));
        photo1.setStations(Set.of(station1));
        video1.setStations(Set.of(station1));
        audio1.setStations(Set.of(station1));

        stationRepository.save(station1);

        Attraction attraction1 = new Attraction(
                "Эрмитаж",
                "Дворцовая набережная, 32",
                "Один из крупнейших музеев мира",
                "10:00–18:00",
                500,
                "https://hermitagemuseum.org"
        );
        Attraction attraction2 = new Attraction(
                "Музей победы",
                "Тверская, 33",
                "Один из крупнейших музеев мира",
                "10:00–18:00",
                111,
                "https://hermitagemus1eum.org"
        );
        Attraction attraction3 = new Attraction(
                "Музей Истории",
                "Тверская, 222",
                "Один из крупнейших музеев мира",
                "10:00–18:00",
                121,
                "https://hermitagemus1eum.org"
        );
        attraction1.setMedias(Set.of(photo1,video1,audio1));
        photo1.setAttractions(Set.of(attraction1));
        video1.setAttractions(Set.of(attraction1));
        audio1.setAttractions(Set.of(attraction1));

        attractionRepository.save(attraction1);
        attractionRepository.save(attraction2);
        attractionRepository.save(attraction3);

        StationAttractions link1 = new StationAttractions(station1, attraction1, "800 м");
        StationAttractions link2 = new StationAttractions(station1, attraction2, "150 м");
        StationAttractions link3 = new StationAttractions(station1, attraction3, "450 м");

        stationAttractionsRepository.save(link1);
        stationAttractionsRepository.save(link2);
        stationAttractionsRepository.save(link3);





        System.out.println("Тестовые данные успешно загружены!");
    }
}