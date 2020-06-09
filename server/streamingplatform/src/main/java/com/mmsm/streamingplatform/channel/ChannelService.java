package com.mmsm.streamingplatform.channel;

import com.mmsm.streamingplatform.security.keycloak.KeycloakService;
import com.mmsm.streamingplatform.utils.CommonExceptionsUtils.CanOnlyBePerformedByAuthorException;
import com.mmsm.streamingplatform.video.Video;
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

    private final KeycloakService keycloakService;
    private final ChannelRepository channelRepository;

    public Boolean isChannelCreated(String userId) {
        return channelRepository.existsChannelByAuditorCreatedById(userId);
    }

    public ChannelIdentity getChannelIdentity(String userId) {
        return channelRepository.findByAuditorCreatedById(userId)
            .map(Channel::toChannelIdentity)
            .orElseThrow(() -> new ChannelNotFoundByUserIdException(userId));
    }

    public List<VideoRepresentation> getAllVideos(String channelName) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundByNameException(channelName));
        return channel.getVideos().stream()
            .map(Video::toRepresentation)
            .collect(Collectors.toList());
    }

    public ChannelAbout getChannelAbout(String channelName, String userId) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundByNameException(channelName));
        return channel.toChannelAbout(userId);
    }

    public ChannelAbout createChannel(ChannelUpdate channelUpdate, String userId) {
        boolean channelExists = channelRepository.existsChannelByAuditorCreatedById(userId);
        if (channelExists) {
            throw new ChannelAlreadyCreatedException();
        }

        boolean channelNameExists = channelRepository.existsChannelByName(channelUpdate.getName());
        if (channelNameExists) {
            throw new ChannelNameAlreadyExistsException();
        }

        Channel channel = Channel.of(channelUpdate.getName(), channelUpdate.getDescription());
        channel = channelRepository.save(channel);
        return channel.toChannelAbout(userId);
    }

    public ChannelAbout updateChannel(ChannelUpdate channelUpdate, String channelName, String userId) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundByNameException(channelName));

        if (!userId.equals(channel.getCreatedById())) {
            throw new CanOnlyBePerformedByAuthorException();
        }

        boolean channelNameExists = channelRepository.existsChannelByName(channelUpdate.getName());
        if (!channelUpdate.getName().equals(channelName) && channelNameExists) {
            throw new ChannelNameAlreadyExistsException();
        }

        channel.updateChannel(channelUpdate);
        channel = channelRepository.save(channel);
        return channel.toChannelAbout(userId);
    }

    @Transactional
    public void deleteChannelByName(String channelName, String userId) {
        Channel channel = channelRepository.findByName(channelName).orElseThrow(() -> new ChannelNotFoundByNameException(channelName));

        if (!userId.equals(channel.getCreatedById())) {
            throw new CanOnlyBePerformedByAuthorException();
        }

        channelRepository.delete(channel);
    }
}
