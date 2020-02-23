import {Component, OnInit, ViewChild} from '@angular/core';
import {VideoService} from '../../services/api/video.service';
import {NgForm} from '@angular/forms';
import {VideoSaveDto} from '../../dtos/VideoSaveDto';

@Component({
  selector: 'app-video-upload',
  templateUrl: './video-upload.component.html',
  styleUrls: ['./video-upload.component.sass']
})
export class VideoUploadComponent implements OnInit {

  videoSaveDto: VideoSaveDto = new VideoSaveDto();
  file: File;
  @ViewChild(NgForm, { static: false }) form;

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
    if (this.form.invalid || !this.file) {
      return;
    }
    const formData = new FormData();
    formData.set('file', this.file);
    formData.set('description', this.videoSaveDto.description);
    formData.set('title', this.videoSaveDto.title);
    this.videoService.uploadVideo(formData).subscribe(response => {
    });
  }
}
