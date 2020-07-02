import {CommentRatingRepresentation} from './comment-rating/CommentRatingDto';
import { ChannelIdentity } from '../channel/ChannelDto';

export class CommentRepresentation {
  public id: number;
  public parentId: number;
  public channelIdentity: ChannelIdentity;
  public authorId: string;
  public message: string;
  public upVoteCount: number;
  public downVoteCount: number;
  public favouriteCount: number;
  public directRepliesCount: number;
  public allRepliesCount: number;
  public isVideoAuthorFavourite: boolean;
  public isPinned: boolean;
  public wasEdited: boolean;
  public isDeleted: boolean;
  public dateCreated: Date;
  public avatarSrc;
  public currentUserCommentRating: CommentRatingRepresentation;
  public directReplies: CommentRepresentation[];
}

export class SaveComment {
  public parentId: number;
  public message: string;
}

export class CommentUpdate {
  public message: string;
}
