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
      this.router.navigateByUrl('');
      this.toastService.showToast('Successfully created channel!');
    }, error => {
      if (error.status === 409 && error.error.message) {
        this.toastService.showToast(error.error.message, 5000);
      } else {
        this.toastService.showToast('An error occurred during channel creation', 5000);
      }
    });
  }
}
