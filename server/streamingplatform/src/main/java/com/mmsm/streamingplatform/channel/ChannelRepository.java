package com.mmsm.streamingplatform.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findByName(String name);

    Optional<Channel> findByAuditorCreatedById(String createdById);

    boolean existsChannelByAuditorCreatedById(String createdById);

    boolean existsChannelByName(String name);
}
