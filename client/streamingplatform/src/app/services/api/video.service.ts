import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {VideoDto} from '../../dtos/VideoDto';
import {VideoDetailsDto} from '../../dtos/VideoDetailsDto';

type EntityDetailsResponseType = HttpResponse<VideoDetailsDto>;
type EntityArrayResponseType = HttpResponse<VideoDto[]>;
type EntityResponseType = HttpResponse<VideoDto>;

@Injectable({ providedIn: 'root' })
export class VideoService {

  resourceUrl: string = environment.serverUrl + 'api/v1/videos';

  constructor(private http: HttpClient) { }

  findAllVideoDtos(): Observable<EntityArrayResponseType> {
    return this.http.get<VideoDto[]>(this.resourceUrl, { observe: 'response' });
  }

  getVideoDetails(id: number): Observable<EntityDetailsResponseType> {
    return this.http.get<VideoDetailsDto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  uploadVideo(fileFormData: FormData): Observable<EntityResponseType> {
    return this.http.post<VideoDto>(this.resourceUrl, fileFormData, { observe: 'response' });
  }
}
