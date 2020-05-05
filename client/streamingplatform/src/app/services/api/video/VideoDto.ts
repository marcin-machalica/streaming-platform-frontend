import {UserDto} from '../keycloak-admin-api/UserDto';
import {VideoRatingDto} from './video-rating/VideoRatingDto';
import {CommentRepresentation} from '../comment/CommentDto';

export class VideoRepresentation {
  public id: number;
  public author: UserDto;
  public title: string;
  public description: string;
  public createdDate: Date;
}

export class VideoDetails {
  id: number;
  author: UserDto;
  title: string;
  description: string;
  upVoteCount: number;
  downVoteCount: number;
  viewCount: number;
  shareCount: number;
  createdDate: Date;
  currentUserVideoRating: VideoRatingDto;
  directCommentDtos: CommentRepresentation[];

  constructor() {
    this.author = new UserDto();
    this.currentUserVideoRating = new VideoRatingDto();
    this.directCommentDtos = [];
  }
}

export class VideoUpdate {
  public title: string;
  public description: string;
}
