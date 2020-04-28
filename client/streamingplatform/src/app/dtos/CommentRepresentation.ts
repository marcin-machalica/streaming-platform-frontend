import {UserDto} from './UserDto';
import {CommentRatingRepresentation} from './CommentRatingRepresentation';

export class CommentRepresentation {
  public id: number;
  public parentId: number;
  public author: UserDto;
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
  public currentUserCommentRating: CommentRatingRepresentation;
  public directReplies: CommentRepresentation[];
}
