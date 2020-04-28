package com.mmsm.streamingplatform;

import com.mmsm.streamingplatform.comment.Comment;
import com.mmsm.streamingplatform.comment.CommentRepository;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.service.VideoRepository;
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
    public CommandLineRunner demo(VideoRepository videoRepository, CommentRepository commentRepository) {
        return (args) -> {
            loadData(videoRepository, commentRepository);
        };
    }

    @Transactional
    public void loadData(VideoRepository videoRepository, CommentRepository commentRepository) {
        Video video1 = new Video();
        Video video2 = new Video();
        Video video3 = new Video();
        video1.setFilename("test1.mp4");
        video1.setTitle("test1");
        video1.setCreatedById("00436daf-3fe5-4fa5-92f1-58db4ea4744a");
        video1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras tristique venenatis condimentum. Nam varius orci sed erat porttitor mollis. Etiam quam nibh, feugiat nec lectus mattis, porttitor blandit lectus. Nam vitae euismod mi, consectetur laoreet justo. In facilisis at metus sit amet posuere. Integer laoreet fermentum lorem, nec iaculis odio scelerisque ac. Donec feugiat et lectus vitae euismod. Sed ultricies non sem vel aliquet. Morbi ac sagittis elit, quis scelerisque ligula. Etiam diam magna, mattis a consequat semper, pretium ut risus. Nam vitae orci auctor, interdum libero eu, lobortis nisl. Curabitur ac nulla vel nunc vestibulum fringilla eget ut nulla. Ut sit amet tristique libero. Fusce dapibus erat at facilisis aliquam.");

        Comment comment1 = new Comment();
        comment1.setMessage("test comment 1");
        comment1.setCreatedById("00436daf-3fe5-4fa5-92f1-58db4ea4744a");
        video1.setComments(Arrays.asList(comment1));
        video1 = videoRepository.save(video1);
        comment1 = video1.getComments().get(0);

        Comment comment2 = new Comment();
        comment2.setMessage("test comment 2");
        comment2.setCreatedById("00436daf-3fe5-4fa5-92f1-58db4ea4744a");
        comment1.addChildrenComment(comment2);

        video2.setFilename("test2.mp4");
        video2.setTitle("test2");
        video2.setCreatedById("00436daf-3fe5-4fa5-92f1-58db4ea4744a");
        video2.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras tristique venenatis condimentum. Nam varius orci sed erat porttitor mollis. Etiam quam nibh, feugiat nec lectus mattis, porttitor blandit lectus. Nam vitae euismod mi, consectetur laoreet justo. In facilisis at metus sit amet posuere. Integer laoreet fermentum lorem, nec iaculis odio scelerisque ac. Donec feugiat et lectus vitae euismod. Sed ultricies non sem vel aliquet. Morbi ac sagittis elit, quis scelerisque ligula. Etiam diam magna, mattis a consequat semper, pretium ut risus. Nam vitae orci auctor, interdum libero eu, lobortis nisl. Curabitur ac nulla vel nunc vestibulum fringilla eget ut nulla. Ut sit amet tristique libero. Fusce dapibus erat at facilisis aliquam.");
        video3.setFilename("test3.mp4");
        video3.setTitle("test3");
        video3.setCreatedById("00436daf-3fe5-4fa5-92f1-58db4ea4744a");
        video3.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras tristique venenatis condimentum. Nam varius orci sed erat porttitor mollis. Etiam quam nibh, feugiat nec lectus mattis, porttitor blandit lectus. Nam vitae euismod mi, consectetur laoreet justo. In facilisis at metus sit amet posuere. Integer laoreet fermentum lorem, nec iaculis odio scelerisque ac. Donec feugiat et lectus vitae euismod. Sed ultricies non sem vel aliquet. Morbi ac sagittis elit, quis scelerisque ligula. Etiam diam magna, mattis a consequat semper, pretium ut risus. Nam vitae orci auctor, interdum libero eu, lobortis nisl. Curabitur ac nulla vel nunc vestibulum fringilla eget ut nulla. Ut sit amet tristique libero. Fusce dapibus erat at facilisis aliquam.");
        List<Video> videoList = Arrays.asList(video1, video2, video3);
        videoRepository.saveAll(videoList);
    }
}
