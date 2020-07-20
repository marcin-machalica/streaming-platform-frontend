import {VideoRatingDto} from './video-rating/VideoRatingDto';
import {CommentRepresentation} from '../comment/CommentDto';
import {ChannelIdentity} from '../channel/ChannelDto';

export class VideoRepresentation {
  public id: number;
  public channelIdentity: ChannelIdentity;
  public title: string;
  public description: string;
  public createdDate: Date;
  public avatarSrc;
}

export class VideoDetails {
  id: number;
  channelIdentity: ChannelIdentity;
  authorId: string;
  title: string;
  description: string;
  upVoteCount: number;
  downVoteCount: number;
  viewCount: number;
  shareCount: number;
  createdDate: Date;
  avatarSrc;
  currentUserVideoRating: VideoRatingDto;
  directCommentDtos: CommentRepresentation[];

  constructor() {
    this.channelIdentity = new ChannelIdentity();
    this.currentUserVideoRating = new VideoRatingDto();
    this.directCommentDtos = [];
  }
}

export class VideoUpdate {

  public title: string;
  public description: string;
  public visibility: VideoVisibility;

  constructor(title?: string, description?: string, visibility?: VideoVisibility) {
    this.visibility = visibility || VideoVisibility.PUBLIC;
  }
}

export enum VideoVisibility {
  PUBLIC = 'PUBLIC',
  LINK_ONLY = 'LINK_ONLY',
  PRIVATE = 'PRIVATE',
}
