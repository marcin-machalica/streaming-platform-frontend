package com.mmsm.streamingplatform.video.service;

import com.mmsm.streamingplatform.utils.FileUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import com.mmsm.streamingplatform.video.mapper.VideoDetailsMapper;
import com.mmsm.streamingplatform.video.mapper.VideoMapper;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDetailsDto;
import com.mmsm.streamingplatform.video.model.VideoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.NotSupportedException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;

    @Value("${VIDEOS_STORAGE_PATH}")
    public String VIDEOS_STORAGE_PATH;

    public List<VideoDto> getAllVideoDtos() {
        return videoRepository.findAll()
                .stream()
                .map(VideoMapper::getVideoDtoFromEntity)
                .collect(Collectors.toList());
    }

    public VideoDetailsDto getVideoDetailsDtoByVideoId(Long id) {
        return videoRepository.findById(id)
                .map(VideoDetailsMapper::getVideoDetailsDtoFromEntity)
                .orElse(null);
    }

    public VideoDto saveVideoDto(VideoDto dto) {
        Video video = Optional.ofNullable(dto.getId())
                .map(videoRepository::getOne)
                .orElse(new Video());

        video.setFilename(dto.getFilename());
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        return VideoMapper.getVideoDtoFromEntity(videoRepository.save(video));
    }

    public VideoDto updateVideoDto(VideoDto dto, Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (dto == null || videoOptional.isEmpty()) {
            return null;
        }
        Video video = videoOptional.get();
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        return VideoMapper.getVideoDtoFromEntity(videoRepository.save(video));
    }

    @Transactional
    public boolean deleteVideoById(Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        Optional<String> currentUserOptional = SecurityUtils.getCurrentUser();
        if (videoOptional.isPresent() && currentUserOptional.isPresent() &&
           (SecurityUtils.hasAdminRole() || currentUserOptional.get().equals(videoOptional.get().getCreatedBy()))) {

            Video video = videoOptional.get();
            videoRepository.delete(video);
            File file = new File(VIDEOS_STORAGE_PATH + "/" + video.getFilename());
            return file.delete();
        }
        return false;
    }

    public File downloadFile(Long id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isEmpty()) {
            return null;
        }
        File file = new File(VIDEOS_STORAGE_PATH + "/" + video.get().getFilename());
        return file;
    }

    public void uploadFile(MultipartFile file, Long id) throws IOException, NotSupportedException {
        String fileExtension = FileUtils.getFileExtension(file.getOriginalFilename());
        if (!Arrays.asList(FileUtils.allowedFileFormatsCommaSeparated.split(","))
                .contains(fileExtension)) {
            throw new NotSupportedException("Not supported video extension");
        }
        Video video = videoRepository.findById(id).orElseThrow(() -> new RuntimeException("Corresponding video entity not found"));
        String filename;
        do {
            filename = UUID.randomUUID().toString();
        } while (doFileExists(filename));
        String fullFileName = filename + "." + fileExtension;
        Path targetPath = Path.of(VIDEOS_STORAGE_PATH, "/", fullFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        video.setFilename(fullFileName);
        videoRepository.save(video);
    }

    private boolean doFileExists(String uuid) {
        return videoRepository.getVideoCountByFilename(uuid) != 0;
    }
}
