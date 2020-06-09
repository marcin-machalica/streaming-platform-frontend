import {Component, OnInit, ViewChild} from '@angular/core';
import {ChannelAbout, ChannelUpdate} from '../../../services/api/channel/ChannelDto';
import {ChannelService} from '../../../services/api/channel/channel.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NgForm} from '@angular/forms';
import {ToastService} from '../../../services/toast/toast.service';
import {VideoRepresentation} from "../../../services/api/video/VideoDto";

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

  @ViewChild(NgForm, { static: false }) form;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private toastService: ToastService,
              private channelService: ChannelService) { }

  ngOnInit() {
    this.channelName = this.route.snapshot.paramMap.get('channelName');
    this.channelService.getChannelAbout(this.channelName).subscribe(response => {
      this.channelAbout = response.body;
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
}

enum TAB {
  ABOUT = 0,
  VIDEOS = 1
}
