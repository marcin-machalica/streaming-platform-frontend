import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {map} from 'rxjs/operators';
import {ChannelService} from '../../api/channel/channel.service';

@Injectable({
  providedIn: 'root'
})
export class ChannelGuardService implements CanActivate {

  resourceUrl = `${environment.serverUrl}api/v1/is-channel-created`;

  constructor(private http: HttpClient,
              private router: Router,
              private channelService: ChannelService) { }

  isChannelCreated = false;

  checkIsChannelCreated(): Observable<HttpResponse<boolean>> {
    return this.http.get<boolean>(this.resourceUrl, { observe: 'response' });
  }

  canActivate(): Observable<boolean> | boolean {

    if (this.isChannelCreated) {
      return true;
    }

    return this.checkIsChannelCreated().pipe(map(response => {
      this.isChannelCreated = response.body;
      if (!response.body) {
        this.router.navigateByUrl('/create-channel');
      } else {
        this.channelService.channelIdentityUpdateEvent.emit();
        return response.body;
      }
    }));
  }
}

@Injectable({
  providedIn: 'root'
})
export class ReversedChannelGuardService implements CanActivate {

  resourceUrl = `${environment.serverUrl}api/v1/is-channel-created`;

  constructor(private http: HttpClient,
              private router: Router) { }

  isChannelCreated = true;

  checkIsChannelCreated(): Observable<HttpResponse<boolean>> {
    return this.http.get<boolean>(this.resourceUrl, { observe: 'response' });
  }

  canActivate(): Observable<boolean> | boolean {

    if (!this.isChannelCreated) {
      return true;
    }

    return this.checkIsChannelCreated().pipe(map(response => {
      this.isChannelCreated = response.body;
      if (response.body) {
        this.router.navigateByUrl('/404');
      }
      return !response.body;
    }));
  }
}
