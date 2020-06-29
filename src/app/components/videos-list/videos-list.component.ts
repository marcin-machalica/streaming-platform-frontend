import { Component, OnInit } from '@angular/core';
import {VideoService} from '../../services/api/video/video.service';
import {VideoRepresentation} from '../../services/api/video/VideoDto';

@Component({
  selector: 'app-videos-list',
  templateUrl: './videos-list.component.html',
  styleUrls: ['./videos-list.component.sass']
})
export class VideosListComponent implements OnInit {

  videos: VideoRepresentation[] = [];

  constructor(private videoService: VideoService) { }

  ngOnInit() {
    this.loadAllVideos();
  }

  loadAllVideos() {
    this.videoService.findAllVideos().subscribe(response => {
      if (response.body) {
        this.videos = response.body;
      }
    });
  }

  getFormattedDate(date: Date) {
    return new Date (date).toLocaleDateString();
  }
}
