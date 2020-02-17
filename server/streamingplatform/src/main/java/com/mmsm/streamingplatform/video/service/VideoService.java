package com.mmsm.streamingplatform.video.service;

import com.mmsm.streamingplatform.utils.SecurityUtils;
import com.mmsm.streamingplatform.video.mapper.VideoDetailsMapper;
import com.mmsm.streamingplatform.video.mapper.VideoMapper;
import com.mmsm.streamingplatform.video.model.Video;
import com.mmsm.streamingplatform.video.model.VideoDetailsDto;
import com.mmsm.streamingplatform.video.model.VideoDto;
import com.mmsm.streamingplatform.video.videorating.model.VideoRating;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;

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

    public boolean deleteVideoById(Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        Optional<String> currentUserOptional = SecurityUtils.getCurrentUser();
        if (videoOptional.isPresent() && currentUserOptional.isPresent() &&
           (SecurityUtils.hasAdminRole() || currentUserOptional.get().equals(videoOptional.get().getCreatedBy()))) {
            videoRepository.delete(videoOptional.get());
            return true;
        }
        return false;
    }
}
