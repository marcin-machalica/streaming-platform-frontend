import { Component, OnInit } from '@angular/core';
import {VideoService} from '../../services/api/video.service';

@Component({
  selector: 'app-video-upload',
  templateUrl: './video-upload.component.html',
  styleUrls: ['./video-upload.component.sass']
})
export class VideoUploadComponent implements OnInit {

  file: any;

  constructor(private videoService: VideoService) { }

  ngOnInit() {
  }

  onDropFile(event: DragEvent) {
    event.preventDefault();
    this.file = event.dataTransfer.files[0];
  }

  onDragOverFile(event) {
    event.stopPropagation();
    event.preventDefault();
  }

  selectFile(event) {
    this.file = event.target.files[0];
  }

  uploadFile() {
    if (!this.file) {
      return;
    }
    const formData = new FormData();
    formData.set('file', this.file);
    this.videoService.uploadVideo(2, formData).subscribe(response => {
    });
  }

}
