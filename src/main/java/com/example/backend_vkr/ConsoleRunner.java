package com.example.backend_vkr;

import com.example.backend_vkr.data.JPAAttractionRepository;
import com.example.backend_vkr.data.JPAMediaRepository;
import com.example.backend_vkr.data.JPAStationAttractionsRepository;
import com.example.backend_vkr.data.JPAStationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

//@Component
public class ConsoleRunner implements CommandLineRunner {
    public void setDataSource(String url) {
        String user = "postgres";
        String password = "postgres";
        DataSource dataSource = new DriverManagerDataSource(url, user, password);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Autowired
    private JPAAttractionRepository JPAAttractionRepository;

    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JPAStationRepository JPAStationRepository;

    @Autowired
    private JPAMediaRepository JPAMediaRepository;

    @Autowired
    private JPAStationAttractionsRepository JPAStationAttractionsRepository;
    @Transactional
    @Override
    public void run(String... args) throws Exception {
//        Media photo1 = new Media(MediaType.PHOTO,"Фото станции 1", "Департамент транспорта Москвы","https://example.com/photo1.jpg","w");
//
//        Media video1 = new Media(MediaType.VIDEO,"Видео 1", "Департамент транспорта Москвы","https://example.com/video1.mp4","w");
//        Media audio1 = new Media(MediaType.AUDIO, "Ауидо 1","Департамент транспорта Москвы","https://example.com/audio1.mp3","w");
//        mediaRepository.save(photo1);
//        mediaRepository.save(video1);
//        mediaRepository.save(audio1);
//
//        Station station1 = new Station("Площадь Восстания", "Невский проспект, 1", "Центральная станция", "Невско-Василеостровская");
//        station1.setBuiltAt("1955");
//
//
//
//        station1.setMedias(Set.of(photo1,video1,audio1));
//        photo1.setStations(Set.of(station1));
//        video1.setStations(Set.of(station1));
//        audio1.setStations(Set.of(station1));
//
//        stationRepository.save(station1);
//
//        Attraction attraction1 = new Attraction(
//                "Эрмитаж",
//                "Дворцовая набережная, 32",
//                "Один из крупнейших музеев мира",
//                "10:00–18:00",
//                500,
//                "https://hermitagemuseum.org"
//        );
//        Attraction attraction2 = new Attraction(
//                "Музей победы",
//                "Тверская, 33",
//                "Один из крупнейших музеев мира",
//                "10:00–18:00",
//                111,
//                "https://hermitagemus1eum.org"
//        );
//        Attraction attraction3 = new Attraction(
//                "Музей Истории",
//                "Тверская, 222",
//                "Один из крупнейших музеев мира",
//                "10:00–18:00",
//                121,
//                "https://hermitagemus1eum.org"
//        );
//        attraction1.setMedias(Set.of(photo1,video1,audio1));
//        photo1.setAttractions(Set.of(attraction1));
//        video1.setAttractions(Set.of(attraction1));
//        audio1.setAttractions(Set.of(attraction1));
//
//        attractionRepository.save(attraction1);
//        attractionRepository.save(attraction2);
//        attractionRepository.save(attraction3);
//
//        StationAttractions link1 = new StationAttractions(station1, attraction1, "800 м");
//        StationAttractions link2 = new StationAttractions(station1, attraction2, "150 м");
//        StationAttractions link3 = new StationAttractions(station1, attraction3, "450 м");
//
//        stationAttractionsRepository.save(link1);
//        stationAttractionsRepository.save(link2);
//        stationAttractionsRepository.save(link3);


        syncStationMediaByMatchingNameAndBranch();


        System.out.println("Тестовые данные успешно загружены!");
    }
    private void syncStationMediaByMatchingNameAndBranch() {
        try {
            String connectionString = "jdbc:postgresql://localhost:5432/postgres";

            setDataSource(connectionString);
            String sql = """
            INSERT INTO attraction_medias (attraction_id, media_id)
            SELECT a.id, m.id
            FROM attractions a
            INNER JOIN medias m ON a.name = m.name
            WHERE NOT EXISTS (
                SELECT 1 FROM attraction_medias am
                WHERE am.attraction_id = a.id AND am.media_id = m.id
            )
            """;
        int updated = jdbcTemplate.update(sql);
        System.out.println("Создано новых связей station-media: " + updated);
        } catch (Exception ex) {
            System.err.println("Ошибка при вставке пользователя: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}