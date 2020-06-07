package com.mmsm.streamingplatform.channel;

import com.mmsm.streamingplatform.auditor.Auditor;
import com.mmsm.streamingplatform.keycloak.KeycloakController.UserDto;
import com.mmsm.streamingplatform.video.Video;
import com.mmsm.streamingplatform.channel.ChannelController.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "channel")
public class Channel {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="id_sequence")
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Size(max = 5000)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "subscription_count", nullable = false)
    private Long subscriptionCount = 0L;        // todo

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "channel_id")
    private List<Video> videos = new ArrayList<>();

    @Embedded
    private Auditor auditor;

    public static Channel of(String name, String description) {
        return new Channel(null, name, description, 0L, new ArrayList<>(), Auditor.of());
    }

    public ChannelAbout toChannelAbout(String userId) {
        return new ChannelAbout(name, this.getCreatedById().equals(userId), description, subscriptionCount, getCreatedDate());
    }

    public ChannelIdentity toChannelIdentity() {
        return new ChannelIdentity(name);
    }

    public Channel updateChannel(ChannelUpdate channelUpdate) {
        name = channelUpdate.getName();
        description = channelUpdate.getDescription();
        return this;
    }

    public Channel addVideo(Video video) {
        videos.add(video);
        return this;
    }

    public Instant getCreatedDate() {
        return auditor.getCreatedDate();
    }

    public String getCreatedById() {
        return auditor.getCreatedById();
    }
}
