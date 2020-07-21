import {Component, OnInit, ViewChild} from '@angular/core';
import {ChannelUpdate} from '../../../services/api/channel/ChannelDto';
import {ChannelService} from '../../../services/api/channel/channel.service';
import {NgForm} from '@angular/forms';
import {ToastService} from '../../../services/toast/toast.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-channel-create',
  templateUrl: './channel-create.component.html',
  styleUrls: ['./channel-create.component.sass']
})
export class ChannelCreateComponent implements OnInit {

  channelUpdate: ChannelUpdate = new ChannelUpdate();
  file: File;
  avatarSrc;

  @ViewChild(NgForm, { static: false }) form;

  constructor(private router: Router,
              private toastService: ToastService,
              private channelService: ChannelService) { }

  ngOnInit() {
  }

  createChannel() {
    if (this.form.invalid) {
      return;
    }

    this.channelService.createChannel(this.channelUpdate).subscribe(response => {
      this.updateAvatar(this.channelUpdate.name);
      this.toastService.showToast('Successfully created channel!');
      location.href = '/';
    }, error => {
      if (error.status === 409 && error.error.message) {
        this.toastService.showToast(error.error.message, 5000);
      } else {
        this.toastService.showToast('An error occurred during channel creation', 5000);
      }
    });
  }

  selectFile(event) {
    this.file = event.target.files[0];
    this.loadAvatar(event.target.files[0]);
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

  updateAvatar(channelName: string) {
    if (this.file) {
      const formData = new FormData();
      formData.set('file', this.file);

      this.channelService.uploadAvatar(channelName, formData).subscribe(response => {
        this.channelService.getAvatar(channelName).subscribe(blob => {
          this.channelService.avatarUpdateEvent.emit(blob);
        });
      });
    }
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
