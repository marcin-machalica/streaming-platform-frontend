import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CommentDto} from '../../dtos/CommentDto';
import {environment} from '../../../environments/environment';

type EntityArrayResponseType = HttpResponse<CommentDto[]>;
type EntityResponseType = HttpResponse<CommentDto>;

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  resourceUrl = (videoId) => `${environment.serverUrl}api/v1/videos/${videoId}/comments`;

  constructor(private http: HttpClient) { }

  saveComment(commentDto: CommentDto, videoId: number): Observable<EntityResponseType> {
    return this.http.post<CommentDto>(this.resourceUrl(videoId), commentDto, { observe: 'response' });
  }

  getCommentDtoWithReplies(videoId: number, commentId: number): Observable<EntityResponseType> {
    return this.http.get<CommentDto>(`${this.resourceUrl(videoId)}/${commentId}`, { observe: 'response' });
  }
}
