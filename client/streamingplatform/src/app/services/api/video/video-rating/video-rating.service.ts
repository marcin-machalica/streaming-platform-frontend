import { Injectable } from '@angular/core';
import {environment} from '../../../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {VideoRatingDto} from './VideoRatingDto';

type VideoRatingDtoResponseType = HttpResponse<VideoRatingDto>;

@Injectable({
  providedIn: 'root'
})
export class VideoRatingService {

  resourceUrl = (videoId) => `${environment.serverUrl}api/v1/videos/${videoId}`;

  constructor(private http: HttpClient) { }

  upVoteVideo(videoId: number): Observable<VideoRatingDtoResponseType> {
    return this.http.post<VideoRatingDto>(this.resourceUrl(videoId) + '/up-vote', null, { observe: 'response' });
  }

  downVoteVideo(videoId: number): Observable<VideoRatingDtoResponseType> {
    return this.http.post<VideoRatingDto>(this.resourceUrl(videoId) + '/down-vote', null, { observe: 'response' });
  }
}
