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

  constructor(private keycloakService: KeycloakService,
              private channelService: ChannelService) { }

  ngOnInit() {
    this.username = this.keycloakService.getUsername();
    this.channelService.channelIdentityUpdateEvent.subscribe(() => {
      this.channelService.getChannelIdentity().subscribe(response => {
        this.channelIdentity = response.body;
      });
    });
  }

  logout() {
    this.keycloakService.logout();
  }
}
