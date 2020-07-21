import {Component, OnInit, ViewChild} from '@angular/core';
import {ChannelAbout, ChannelUpdate} from '../../../services/api/channel/ChannelDto';
import {ChannelService} from '../../../services/api/channel/channel.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForm} from '@angular/forms';
import {ToastService} from '../../../services/toast/toast.service';
import {VideoRepresentation} from '../../../services/api/video/VideoDto';
import {AvatarCropperDialogComponent} from '../../shared/avatar-cropper-dialog/avatar-cropper-dialog.component';
import {MatDialog} from '@angular/material';

@Component({
  selector: 'app-channel',
  templateUrl: './channel.component.html',
  styleUrls: ['./channel.component.sass']
})
export class ChannelComponent implements OnInit {

  channelName: string;
  channelAbout: ChannelAbout = new ChannelAbout();
  channelUpdate: ChannelUpdate = new ChannelUpdate();
  videos: VideoRepresentation[];
  isEditMode = false;

  file: File;
  fileRaw: File;
  avatarSrc;

  @ViewChild(NgForm, { static: false }) form;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private dialog: MatDialog,
              private toastService: ToastService,
              private channelService: ChannelService) { }

  ngOnInit() {
    this.channelName = this.route.snapshot.paramMap.get('channelName');
    this.channelService.getChannelAbout(this.channelName).subscribe(response => {
      this.channelAbout = response.body;
    });

    this.loadAvatar(this.channelService.avatar);
    this.channelService.avatarUpdateEvent.subscribe((blob) => {
      this.loadAvatar(blob);
    });
  }

  startEditMode() {
    this.channelUpdate.name = this.channelAbout.name;
    this.channelUpdate.description = this.channelAbout.description;
    this.isEditMode = true;
  }

  cancelEditMode() {
    this.isEditMode = false;
  }

  updateChannel() {
    if (this.form.invalid) {
      return;
    }

    this.channelService.updateChannel(this.channelName, this.channelUpdate).subscribe(response => {
      this.channelAbout = response.body;
      this.channelService.channelIdentityUpdateEvent.emit();
      this.cancelEditMode();
      this.router.navigateByUrl('/channels/' + this.channelAbout.name);
      this.toastService.showToast('Successfully updated channel!');
    }, error => {
      this.toastService.showToast('An error occurred during channel update', 5000);
    });
  }

  loadTabData(event) {
    if (event.index === TAB.VIDEOS && !this.videos) {
      this.channelService.getChannelVideos(this.channelName).subscribe(response => {
        this.videos = response.body;
      });
    }
  }

  getFormattedDate(date: Date) {
    return new Date (date).toLocaleDateString();
  }

  onDragOverFile(event) {
    event.stopPropagation();
    event.preventDefault();
  }

  onDropFile(event: DragEvent) {
    event.preventDefault();

    const filename = event.dataTransfer.files[0].name;
    if (!filename || !filename.toLowerCase().match(/\.(jpg|png)$/)) {
      return;
    }

    this.file = event.dataTransfer.files[0];
    this.loadAvatar(event.dataTransfer.files[0]);
  }

  openAvatarCropperDialog() {
    const dialogRef = this.dialog.open(AvatarCropperDialogComponent, {
      width: '600px',
      data: {
        file: this.fileRaw,
      }
    });
    dialogRef.afterClosed().subscribe(rawAndCroppedImage => {
      if (!!rawAndCroppedImage) {
        this.file = rawAndCroppedImage.cropped;
        this.fileRaw = rawAndCroppedImage.raw;
        this.loadAvatar(this.file);
      }
    });
  }

  updateAvatar() {
    if (!this.file) {
      return;
    }

    const formData = new FormData();
    formData.set('file', this.file);

    this.channelService.uploadAvatar(this.channelName, formData).subscribe(response => {
      this.toastService.showToast('Successfully updated avatar!');
      this.channelService.getAvatar(this.channelName).subscribe(blob => {
        this.channelService.avatarUpdateEvent.emit(blob);
      });
    });
  }

  private loadAvatar(blob: Blob) {
    if (!blob || blob.size <= 0) {
      this.avatarSrc = 'assets/default_avatar.png';
      return;
    }
    const reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onloadend = () => {
      this.avatarSrc = reader.result;
    };
  }
}

enum TAB {
  ABOUT = 0,
  VIDEOS = 1
}
