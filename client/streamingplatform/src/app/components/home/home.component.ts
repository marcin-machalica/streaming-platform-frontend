import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import {VideoService} from '../../services/api/video.service';
import {VideoDto} from '../../dtos/VideoDto';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.sass']
})

export class HomeComponent implements OnInit {

  videos: VideoDto[] = [];

  constructor(private titleService: Title, private videoService: VideoService) {
  }

  ngOnInit() {
    this.titleService.setTitle('Streaming Platform');
    this.loadAllVideos();
  }

  loadAllVideos() {
    this.videoService.findAllVideoDtos().subscribe(data => {
      if (data) {
        this.videos = data.body;
      }
    });
  }
}
