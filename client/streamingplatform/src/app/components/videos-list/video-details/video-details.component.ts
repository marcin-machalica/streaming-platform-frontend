import {Component, OnInit} from '@angular/core';
import {VideoDetailsDto} from '../../../dtos/VideoDetailsDto';
import {VideoService} from '../../../services/api/video.service';
import {ActivatedRoute} from '@angular/router';
import {VideoDto} from '../../../dtos/VideoDto';
import {VideoRatingDto} from '../../../dtos/VideoRatingDto';
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'app-video-details',
  templateUrl: './video-details.component.html',
  styleUrls: ['./video-details.component.sass']
})
export class VideoDetailsComponent implements OnInit {

  videoResourceUrl = environment.serverUrl + 'api/v1/videos/download/';
  videoDetails: VideoDetailsDto = { videoDto: new VideoDto(), videoRatingDto: new VideoRatingDto()};
  videoId: number;

  constructor(private route: ActivatedRoute,
              private videoService: VideoService) { }

  ngOnInit() {
    this.videoId = +this.route.snapshot.paramMap.get('id');
    this.loadVideoDetails();
  }

  loadVideoDetails() {
    this.videoService.getVideoDetails(this.videoId).subscribe(response => {
      if (response.body) {
        this.videoDetails = response.body;
      }
    });
  }
}
