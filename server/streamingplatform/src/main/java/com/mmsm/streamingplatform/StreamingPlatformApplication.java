package com.mmsm.streamingplatform;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class StreamingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamingPlatformApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(VideoRepository videoRepository) {
		return (args) -> {
				loadData(videoRepository);
		};
	}

	@Transactional
	public void loadData(VideoRepository videoRepository) {
		Video movie1 = new Video();
		Video movie2 = new Video();
		Video movie3 = new Video();
		movie1.setPath("aaaa");
		movie2.setPath("bbb");
		movie3.setPath("ccc");
		videoRepository.save(movie2);
		videoRepository.save(movie1);
		videoRepository.save(movie3);
	}

}
