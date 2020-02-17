package com.mmsm.streamingplatform;

import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

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
        Video video1 = new Video();
        Video video2 = new Video();
        Video video3 = new Video();
        video1.setFilename("test1.mp4");
        video1.setTitle("test1");
        video1.setVideoRating(new VideoRating());
        video2.setFilename("test2.mp4");
        video2.setTitle("test2");
        video2.setVideoRating(new VideoRating());
        video3.setFilename("test3.mp4");
        video3.setTitle("test3");
        video3.setVideoRating(new VideoRating());
        List<Video> videoList = Arrays.asList(video1, video2, video3);
        videoRepository.saveAll(videoList);
    }

}
