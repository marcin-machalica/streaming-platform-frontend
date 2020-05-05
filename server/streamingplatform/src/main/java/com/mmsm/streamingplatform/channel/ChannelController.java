package com.mmsm.streamingplatform.channel;

import com.mmsm.streamingplatform.keycloak.KeycloakController.*;
import com.mmsm.streamingplatform.utils.ControllerUtils;
import com.mmsm.streamingplatform.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@RequestMapping("/channels")
class ChannelController {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChannelAbout {
        private UserDto author;
        private String name;
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

    private final ChannelService channelService;

    @GetMapping("/{channelName}/videos")
    public List<VideoRepresentation> getAllVideos(@PathVariable String channelName) {
        return channelService.getAllVideos(channelName);
    }

    @GetMapping("/{channelName}")
    public ChannelAbout getChannelAbout(@PathVariable String channelName) {
        return channelService.getChannelAbout(channelName);
    }

    @PostMapping
    public ResponseEntity<ChannelAbout> createChannel(@RequestBody ChannelUpdate channelUpdate) throws URISyntaxException {
        ChannelAbout channelAbout = channelService.createChannel(channelUpdate);
        URI uri = new URI("/api/v1/videos/" + channelAbout.getName());
        return ControllerUtils.getCreatedResponse(channelAbout, uri);
    }

    @PutMapping("/{channelName}")
    public ChannelAbout updateChannel(@RequestBody ChannelUpdate channelUpdate, @PathVariable String channelName,
                                              HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        return channelService.updateChannel(channelUpdate, channelName, userId);
    }

    @DeleteMapping("/{channelName}")
    public void deleteChannelByName(@PathVariable String channelName, HttpServletRequest request) {
        String userId = SecurityUtils.getUserIdFromRequest(request);
        channelService.deleteChannelByName(channelName, userId);
    }
}
