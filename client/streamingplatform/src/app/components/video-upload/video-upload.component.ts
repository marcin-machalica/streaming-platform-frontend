import {Component, OnInit, ViewChild} from '@angular/core';
import {VideoService} from '../../services/api/video/video.service';
import {NgForm} from '@angular/forms';
import {VideoSaveDto} from '../../services/api/video/VideoDto';
import {ToastService} from '../../services/toast/toast.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-video-upload',
  templateUrl: './video-upload.component.html',
  styleUrls: ['./video-upload.component.sass']
})
export class VideoUploadComponent implements OnInit {

  videoSaveDto: VideoSaveDto = new VideoSaveDto();
  file: File;
  @ViewChild(NgForm, { static: false }) form;

  constructor(private router: Router,
              private videoService: VideoService,
              private toastService: ToastService) { }

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
      this.router.navigateByUrl('videos/' + response.body.id);
      this.toastService.showToast('Successfully created video!');
    });
  }
}
