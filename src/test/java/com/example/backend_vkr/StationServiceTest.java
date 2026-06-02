package com.example.backend_vkr;

import com.example.backend_vkr.application.dto.*;
import com.example.backend_vkr.application.services.AttractionService;
import com.example.backend_vkr.data.JPAAttractionRepository;
import com.example.backend_vkr.data.JPAMediaRepository;
import com.example.backend_vkr.data.JPAStationAttractionsRepository;
import com.example.backend_vkr.data.JPAStationRepository;
import com.example.backend_vkr.domain.Attraction;
import com.example.backend_vkr.domain.Media;
import com.example.backend_vkr.domain.Station;
import com.example.backend_vkr.domain.StationAttractions;
import com.example.backend_vkr.domain.enums.MediaType;
import com.example.backend_vkr.domain.repositories.AttractionRepository;
import com.example.backend_vkr.domain.repositories.MediaRepository;
import com.example.backend_vkr.domain.repositories.StationAttractionsRepository;
import com.example.backend_vkr.domain.repositories.StationRepository;
import com.example.backend_vkr.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

	@Mock private MediaRepository mediaRepository;
	@Mock private AttractionRepository attractionRepository;
	@Mock private StationAttractionsRepository stationAttractionRepository;
	@Mock private StationRepository stationRepository;

	@InjectMocks private AttractionService attractionService;

	private Attraction attraction;
	private Station station;

	@BeforeEach
	void setUp() {
		station = new Station("ул. Ленина, 1", List.of(), "описание", "Кольцевая", "Парк культуры");
		attraction = new Attraction("Музей", "+71234567890", "test@mail.ru",
				"ул. Тверская, 5", "описание музея", "10-18", 500, "http://museum.ru");

		Media photo = new Media(MediaType.PHOTO, "main", "http://photo.jpg");
		attraction.setMedias(Set.of(photo));
	}

	@Test
	void getStationAttractions_returnsPagedResponse() {
		Long stationId = 1L;
		StationAttractions link = new StationAttractions(station, attraction, 300);
		Page<StationAttractions> page = new PageImpl<>(List.of(link), PageRequest.of(0, 10), 1);

		when(stationRepository.existsById(stationId)).thenReturn(true);
		when(stationAttractionRepository.findAllStationAttractionsPage(eq(stationId), any(Pageable.class)))
				.thenReturn(page);

		PagedResponse<AttractionResponse> result =
				attractionService.getStationAttractions(stationId, 0, 10);

		assertThat(result.content()).hasSize(1);
		assertThat(result.content().getFirst().name()).isEqualTo("Музей");
		assertThat(result.content().getFirst().distance()).isEqualTo(300);
		assertThat(result.totalElements()).isEqualTo(1);
	}

	@Test
	void findAttractionById_returnsAttractionInfo() {
		Long id = 42L;
		when(attractionRepository.findById(id)).thenReturn(Optional.of(attraction));
		when(mediaRepository.findAllAttractionMediasByType(MediaType.PHOTO, id))
				.thenReturn(List.of("photo.jpg"));
		when(mediaRepository.findAllAttractionMediasByType(MediaType.VIDEO, id))
				.thenReturn(List.of());
		when(mediaRepository.findAllAttractionMediasByType(MediaType.AUDIO, id))
				.thenReturn(List.of("audio.mp3"));

		AttractionInfoResponse result = attractionService.findAttractionById(id);

		assertThat(result.name()).isEqualTo("Музей");
		assertThat(result.email()).isEqualTo("test@mail.ru");
		assertThat(result.images()).containsExactly("photo.jpg");
		assertThat(result.audios()).containsExactly("audio.mp3");
		assertThat(result.videos()).isEmpty();
	}

	@Test
	void findAttractionById_whenNotFound_throwsException() {
		when(attractionRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> attractionService.findAttractionById(99L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void addAttraction_savesAndReturnsId() {
		var stationAttrReq = new StationAttractionRequest("Парк культуры", "Кольцевая", 300);
		var mediaReq = new MediaRequest(MediaType.PHOTO, "http://photo.jpg");
		var request = new AttractionRequest(
				"описание", "ул. Тверская, 5",
				100, "10:00-11:00","+71234567890","wwww",
				"описание", "jjj",
				 List.of(mediaReq),List.of(stationAttrReq));


		when(stationRepository.findByNameAndBranch("Парк культуры", "Кольцевая"))
				.thenReturn(Optional.of(station));
		when(attractionRepository.save(any(Attraction.class)))
				.thenAnswer(inv -> inv.getArgument(0));

		AttractionCreatedResponse result = attractionService.addAttraction(request);

		assertThat(result).isNotNull();
		verify(attractionRepository).save(any(Attraction.class));
		verify(mediaRepository).saveAll(any());
		verify(stationAttractionRepository).saveAll(any());
	}
}