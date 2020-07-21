import {Component, OnInit} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';
import {ChannelService} from './services/api/channel/channel.service';
import {ChannelIdentity} from './services/api/channel/ChannelDto';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent implements OnInit {

  title = 'streamingplatform';
  username: string;
  channelIdentity: ChannelIdentity = new ChannelIdentity();
  avatarSrc;

  constructor(private keycloakService: KeycloakService,
              private channelService: ChannelService) { }

  ngOnInit() {
    this.username = this.keycloakService.getUsername();
    this.channelService.channelIdentityUpdateEvent.subscribe(() => {
      this.channelService.getChannelIdentity().subscribe(response => {
        this.channelIdentity = response.body;
        this.channelService.getAvatar(this.channelIdentity.name).subscribe(blob => {
          this.channelService.avatarUpdateEvent.emit(blob);
        });
      });
    });

    this.channelService.avatarUpdateEvent.subscribe((blob) => {
      this.channelService.avatar = blob;
      this.loadAvatar(blob);
    });
  }

  logout() {
    this.keycloakService.logout();
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
