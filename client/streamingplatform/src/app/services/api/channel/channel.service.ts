import {EventEmitter, Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {VideoRepresentation} from '../video/VideoDto';
import {ChannelAbout, ChannelIdentity, ChannelUpdate} from './ChannelDto';

type ChannelAboutResponseType = HttpResponse<ChannelAbout>;
type VideoRepresentationArrayResponseType = HttpResponse<VideoRepresentation[]>;
type ChannelIdentityResponseType = HttpResponse<ChannelIdentity>;

@Injectable({
  providedIn: 'root'
})
export class ChannelService {

  resourceUrl = `${environment.serverUrl}api/v1/channels`;
  currentChannelResourceUrl = `${environment.serverUrl}api/v1/current-channel`;

  public channelIdentityUpdateEvent = new EventEmitter();

  constructor(private http: HttpClient) { }

  getChannelIdentity(): Observable<ChannelIdentityResponseType> {
    return this.http.get<ChannelIdentity>(this.currentChannelResourceUrl, { observe: 'response' });
  }

  getChannelAbout(channelName: string): Observable<ChannelAboutResponseType> {
    return this.http.get<ChannelAbout>(`${this.resourceUrl}/${channelName}`, { observe: 'response' });
  }

  getChannelVideos(channelName: string): Observable<VideoRepresentationArrayResponseType> {
    return this.http.get<VideoRepresentation[]>(`${this.resourceUrl}/${channelName}/videos`, { observe: 'response' });
  }

  createChannel(channelUpdate: ChannelUpdate): Observable<ChannelAboutResponseType> {
    return this.http.post<ChannelAbout>(this.resourceUrl, channelUpdate, { observe: 'response' });
  }

  updateChannel(channelName: string, channelUpdate: ChannelUpdate): Observable<ChannelAboutResponseType> {
    return this.http.put<ChannelAbout>(`${this.resourceUrl}/${channelName}`, channelUpdate, { observe: 'response' });
  }
}
