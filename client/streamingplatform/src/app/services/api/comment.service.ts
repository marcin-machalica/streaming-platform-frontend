import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CommentRepresentation} from '../../dtos/CommentRepresentation';
import {environment} from '../../../environments/environment';

type CommentRepresentationResponseType = HttpResponse<CommentRepresentation>;

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  resourceUrl = (videoId) => `${environment.serverUrl}api/v1/videos/${videoId}/comments`;

  constructor(private http: HttpClient) { }

  saveComment(dto: CommentRepresentation, videoId: number): Observable<CommentRepresentationResponseType> {
    return this.http.post<CommentRepresentation>(this.resourceUrl(videoId), dto, { observe: 'response' });
  }

  updateComment(dto: CommentRepresentation, videoId: number, commentId: number): Observable<CommentRepresentationResponseType> {
    return this.http.put<CommentRepresentation>(`${this.resourceUrl(videoId)}/${commentId}`, dto, { observe: 'response' });
  }

  deleteComment(videoId: number, commentId: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.resourceUrl(videoId)}/${commentId}`, { observe: 'response' });
  }

  getCommentRepresentationWithReplies(videoId: number, commentId: number): Observable<CommentRepresentationResponseType> {
    return this.http.get<CommentRepresentation>(`${this.resourceUrl(videoId)}/${commentId}`, { observe: 'response' });
  }
}
