package com.mmsm.streamingplatform.channel;

import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mmsm.streamingplatform.video.VideoController.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@RestController
public class ChannelController {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    static class ChannelNotFoundByNameException extends RuntimeException {
        ChannelNotFoundByNameException(String name) {
            super("Channel not found with name: " + name);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class ChannelNotFoundByUserIdException extends RuntimeException {
        public ChannelNotFoundByUserIdException(String userId) {
            super("Channel not found with userId: " + userId);
        }
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    static class ChannelAlreadyCreatedException extends RuntimeException {
        ChannelAlreadyCreatedException() {
            super("Channel was already created by the user");
        }
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    static class ChannelNameAlreadyExistsException extends RuntimeException {
        ChannelNameAlreadyExistsException() {
            super("Channel's name has to be unique");
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChannelAbout {
        private String name;
        private Boolean isAuthor;
        private String description;
        private Long subscriptionCount;
        private Instant createdDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChannelUpdate {
        private String name;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelIdentity {
        private String name;
    }

    private final ChannelService channelService;

    @GetMapping("/current-channel")
    public ChannelIdentity getChannelIdentity(HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return channelService.getChannelIdentity(userId);
    }

    @GetMapping("/channels/{channelName}/videos")
    public List<VideoRepresentation> getAllVideos(@PathVariable String channelName) {
        return channelService.getAllVideos(channelName);
    }

    @GetMapping("/channels/{channelName}")
    public ChannelAbout getChannelAbout(@PathVariable String channelName, HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return channelService.getChannelAbout(channelName, userId);
    }

    @PostMapping("/channels")
    public ResponseEntity<ChannelAbout> createChannel(@RequestBody ChannelUpdate channelUpdate,
                                                      HttpServletRequest request) throws URISyntaxException {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        ChannelAbout channelAbout = channelService.createChannel(channelUpdate, userId);
        URI uri = new URI("/api/v1/videos/" + channelAbout.getName());
        return ControllerUtils.getCreatedResponse(channelAbout, uri);
    }

    @PutMapping("/channels/{channelName}")
    public ChannelAbout updateChannel(@RequestBody ChannelUpdate channelUpdate, @PathVariable String channelName,
                                      HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return channelService.updateChannel(channelUpdate, channelName, userId);
    }

    @DeleteMapping("/channels/{channelName}")
    public void deleteChannelByName(@PathVariable String channelName, HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        channelService.deleteChannelByName(channelName, userId);
    }
}
