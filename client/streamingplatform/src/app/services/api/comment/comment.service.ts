import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {CommentRepresentation, SaveComment, UpdateComment} from './CommentDto';

type CommentRepresentationResponseType = HttpResponse<CommentRepresentation>;

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  resourceUrl = (videoId) => `${environment.serverUrl}api/v1/videos/${videoId}/comments`;

  constructor(private http: HttpClient) { }

  saveComment(saveComment: SaveComment, videoId: number): Observable<CommentRepresentationResponseType> {
    return this.http.post<CommentRepresentation>(this.resourceUrl(videoId), saveComment, { observe: 'response' });
  }

  updateComment(updateComment: UpdateComment, videoId: number, commentId: number): Observable<CommentRepresentationResponseType> {
    return this.http.put<CommentRepresentation>(`${this.resourceUrl(videoId)}/${commentId}`, updateComment, { observe: 'response' });
  }

  deleteComment(videoId: number, commentId: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.resourceUrl(videoId)}/${commentId}`, { observe: 'response' });
  }

  getCommentRepresentationWithReplies(videoId: number, commentId: number): Observable<CommentRepresentationResponseType> {
    return this.http.get<CommentRepresentation>(`${this.resourceUrl(videoId)}/${commentId}`, { observe: 'response' });
  }
}
