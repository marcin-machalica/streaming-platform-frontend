import { Injectable } from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {VideoRepresentation} from '../video/VideoDto';
import {ChannelAbout, ChannelUpdate} from './ChannelDto';

type ChannelAboutResponseType = HttpResponse<ChannelAbout>;
type VideoRepresentationArrayResponseType = HttpResponse<VideoRepresentation[]>;

@Injectable({
  providedIn: 'root'
})
export class ChannelService {

  resourceUrl = `${environment.serverUrl}api/v1/channels`;

  constructor(private http: HttpClient) { }

  getChannelAbout(channelName: string): Observable<ChannelAboutResponseType> {
    return this.http.get<ChannelAbout>(this.resourceUrl, { observe: 'response' });
  }

  getChannelVideos(channelName: string): Observable<VideoRepresentationArrayResponseType> {
    return this.http.get<VideoRepresentation[]>(`${this.resourceUrl}/${channelName}/videos`, { observe: 'response' });
  }

  createChannel(channelUpdate: ChannelUpdate): Observable<ChannelAboutResponseType> {
    return this.http.post<ChannelAbout>(this.resourceUrl, channelUpdate, { observe: 'response' });
  }
}
