import { Injectable } from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {VideoDetails, VideoRepresentation} from './VideoDto';

type VideoDetailsResponseType = HttpResponse<VideoDetails>;
type VideoDetailsArrayResponseType = HttpResponse<VideoRepresentation[]>;
type VideoRepresentationResponseType = HttpResponse<VideoRepresentation>;

@Injectable({ providedIn: 'root' })
export class VideoService {

  resourceUrl: string = environment.serverUrl + 'api/v1/videos';

  constructor(private http: HttpClient) { }

  findAllVideos(): Observable<VideoDetailsArrayResponseType> {
    return this.http.get<VideoRepresentation[]>(this.resourceUrl, { observe: 'response' });
  }

  getVideoDetails(id: number): Observable<VideoDetailsResponseType> {
    return this.http.get<VideoDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  uploadVideo(fileFormData: FormData): Observable<VideoRepresentationResponseType> {
    return this.http.post<VideoRepresentation>(this.resourceUrl, fileFormData, { observe: 'response' });
  }
}
