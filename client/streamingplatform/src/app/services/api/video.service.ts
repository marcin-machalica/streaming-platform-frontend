import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {VideoDto} from '../../dtos/VideoDto';

type EntityResponseType = HttpResponse<VideoDto>;
type EntityArrayResponseType = HttpResponse<VideoDto[]>;

@Injectable({ providedIn: 'root' })
export class VideoService {

  resourceUrl: string = environment.serverUrl + 'api/v1/videos';

  constructor(private http: HttpClient) { }

  findAllVideoDtos(): Observable<EntityArrayResponseType> {
    return this.http.get<VideoDto[]>(this.resourceUrl, { observe: 'response' });
  }
}
