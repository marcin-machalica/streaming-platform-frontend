import { Component, OnInit } from '@angular/core';
import {VideoDto} from '../../dtos/VideoDto';
import {VideoService} from '../../services/api/video.service';

@Component({
  selector: 'app-videos-list',
  templateUrl: './videos-list.component.html',
  styleUrls: ['./videos-list.component.sass']
})
export class VideosListComponent implements OnInit {

  videos: VideoDto[] = [];

  constructor(private videoService: VideoService) { }

  ngOnInit() {
    this.loadAllVideos();
  }

  loadAllVideos() {
    this.videoService.findAllVideoDtos().subscribe(response => {
      if (response.body) {
        this.videos = response.body;
      }
    });
  }
}
