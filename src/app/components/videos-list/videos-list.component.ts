import {Component, OnInit} from '@angular/core';
import {VideoService} from '../../services/api/video/video.service';
import {VideoRepresentation} from '../../services/api/video/VideoDto';
import {ChannelService} from '../../services/api/channel/channel.service';

@Component({
  selector: 'app-videos-list',
  templateUrl: './videos-list.component.html',
  styleUrls: ['./videos-list.component.sass']
})
export class VideosListComponent implements OnInit {

  videos: VideoRepresentation[] = [];

  constructor(private channelService: ChannelService,
              private videoService: VideoService) { }

  ngOnInit() {
    this.loadAllVideos();
  }

  loadAllVideos() {
    this.videoService.findAllVideos().subscribe(response => {
      if (response.body) {
        this.videos = response.body;
        this.loadAvatars();
      }
    });
  }

  getFormattedDate(date: Date) {
    return new Date (date).toLocaleDateString();
  }

  private loadAvatars() {
    const groupedVideos: VideosWithChannelName[] = [];

    this.videos.forEach(videoRepresentation => {
      const existingVideosWithChannelName = groupedVideos.filter(videoWithChannelName => videoWithChannelName.channelName === videoRepresentation.channelIdentity.name)[0] || null;
      if (existingVideosWithChannelName !== null) {
        existingVideosWithChannelName.videos.push(videoRepresentation);
      } else {
        groupedVideos.push({ channelName: videoRepresentation.channelIdentity.name, videos: [videoRepresentation] });
      }
    });

    groupedVideos.forEach(videoWithChannelName => {
      this.channelService.getAvatar(videoWithChannelName.channelName).subscribe(blob => {
        if (!blob) {
          return;
        }
        const reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onloadend = () => {
          videoWithChannelName.videos.forEach(video => {
            video.avatarSrc = reader.result;
          });
        };
      });
    });
  }
}

class VideosWithChannelName {
  channelName: string;
  videos: VideoRepresentation[];
}
