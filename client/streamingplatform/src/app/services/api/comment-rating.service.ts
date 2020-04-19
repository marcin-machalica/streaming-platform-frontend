import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {CommentFavouriteDto, CommentRatingDto} from '../../dtos/CommentRatingDto';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';

type CommentRatingDtoResponseType = HttpResponse<CommentRatingDto>;
type CommentFavouriteDtoResponseType = HttpResponse<CommentFavouriteDto>;

@Injectable({
  providedIn: 'root'
})
export class CommentRatingService {

  resourceUrl = (videoId, commentId) => `${environment.serverUrl}api/v1/videos/${videoId}/comments/${commentId}`;

  constructor(private http: HttpClient) { }

  upVoteComment(videoId: number, commentId: number): Observable<CommentRatingDtoResponseType> {
    return this.http.post<CommentRatingDto>(this.resourceUrl(videoId, commentId) + '/up-vote', null, { observe: 'response' });
  }

  downVoteComment(videoId: number, commentId: number): Observable<CommentRatingDtoResponseType> {
    return this.http.post<CommentRatingDto>(this.resourceUrl(videoId, commentId) + '/down-vote', null, { observe: 'response' });
  }

  favouriteComment(videoId: number, commentId: number): Observable<CommentFavouriteDtoResponseType> {
    return this.http.post<CommentFavouriteDto>(this.resourceUrl(videoId, commentId) + '/favourite', null, { observe: 'response' });
  }

  pinComment(videoId: number, commentId: number): Observable<CommentRatingDtoResponseType> {
    return this.http.post<CommentRatingDto>(this.resourceUrl(videoId, commentId) + '/pin', null, { observe: 'response' });
  }
}
