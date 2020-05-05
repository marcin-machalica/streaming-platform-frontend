package com.mmsm.streamingplatform.channel;

import com.mmsm.streamingplatform.keycloak.KeycloakController.UserDto;
import com.mmsm.streamingplatform.keycloak.KeycloakService;
import com.mmsm.streamingplatform.video.VideoController.*;
import com.mmsm.streamingplatform.channel.ChannelController.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChannelService {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class ChannelNotFoundException extends RuntimeException {
        ChannelNotFoundException(String name) {
            super("Channel not found with name: " + name);
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    static class CanOnlyBePerformedByAuthorException extends RuntimeException {
        CanOnlyBePerformedByAuthorException() {
            super("This action can only be performed by the author");
        }
    }

    private final KeycloakService keycloakService;
    private final ChannelRepository channelRepository;

    public List<VideoRepresentation> getAllVideos(String channelName) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundException(channelName));
        return channel.getVideos().stream()
            .map(video -> video.toRepresentation(keycloakService.getUserDtoById(video.getCreatedById())))
            .collect(Collectors.toList());
    }

    public ChannelAbout getChannelAbout(String channelName) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundException(channelName));
        return channel.toChannelAbout(keycloakService.getUserDtoById(channel.getCreatedById()));
    }

    public ChannelAbout createChannel(ChannelUpdate channelUpdate) {
        Channel channel = Channel.of(channelUpdate.getName(), channelUpdate.getDescription());
        channel = channelRepository.save(channel);
        UserDto author = keycloakService.getUserDtoById(channel.getCreatedById());
        return channel.toChannelAbout(author);
    }

    public ChannelAbout updateChannel(ChannelUpdate channelUpdate, String channelName, String userId) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundException(channelName));

        if (!userId.equals(channel.getCreatedById())) {
            throw new CanOnlyBePerformedByAuthorException();
        }

        channel.updateChannel(channelUpdate);
        channel = channelRepository.save(channel);
        UserDto author = keycloakService.getUserDtoById(channel.getCreatedById());
        return channel.toChannelAbout(author);
    }

    @Transactional
    public void deleteChannelByName(String channelName, String userId) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundException(channelName));

        if (!userId.equals(channel.getCreatedById())) {
            throw new CanOnlyBePerformedByAuthorException();
        }

        channelRepository.delete(channel);
    }
}
