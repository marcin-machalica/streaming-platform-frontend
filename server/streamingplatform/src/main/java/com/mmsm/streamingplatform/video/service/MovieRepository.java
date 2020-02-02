package com.mmsm.streamingplatform.video.service;

import com.mmsm.streamingplatform.video.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

}
