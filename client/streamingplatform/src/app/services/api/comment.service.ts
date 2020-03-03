import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CommentDto} from '../../dtos/CommentDto';
import {environment} from '../../../environments/environment';

type CommentDtoArrayResponseType = HttpResponse<CommentDto[]>;
type CommentDtoResponseType = HttpResponse<CommentDto>;

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  resourceUrl = (videoId) => `${environment.serverUrl}api/v1/videos/${videoId}/comments`;

  constructor(private http: HttpClient) { }

  saveComment(commentDto: CommentDto, videoId: number): Observable<CommentDtoResponseType> {
    return this.http.post<CommentDto>(this.resourceUrl(videoId), commentDto, { observe: 'response' });
  }

  updateComment(commentDto: CommentDto, videoId: number, commentId: number): Observable<CommentDtoResponseType> {
    return this.http.put<CommentDto>(`${this.resourceUrl(videoId)}/${commentId}`, commentDto, { observe: 'response' });
  }

  deleteComment(videoId: number, commentId: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.resourceUrl(videoId)}/${commentId}`, { observe: 'response' });
  }

  getCommentDtoWithReplies(videoId: number, commentId: number): Observable<CommentDtoResponseType> {
    return this.http.get<CommentDto>(`${this.resourceUrl(videoId)}/${commentId}`, { observe: 'response' });
  }
}
