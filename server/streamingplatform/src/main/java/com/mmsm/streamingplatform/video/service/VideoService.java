package com.mmsm.streamingplatform.video.service;

import com.mmsm.streamingplatform.video.mapper.VideoMapper;
import com.mmsm.streamingplatform.video.model.VideoDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public List<VideoDto> getAllVideoDtos() {
        return videoRepository.findAll()
                .stream()
                .map(VideoMapper::fromVideo)
                .collect(Collectors.toList());
    }
}
