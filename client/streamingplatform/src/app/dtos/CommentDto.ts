import {UserDto} from './UserDto';
import {CommentRatingDto} from "./CommentRatingDto";

export class CommentDto {
  public id: number;
  public parentId: number;
  public author: UserDto;
  public message: string;
  public upVoteCount: number;
  public downVoteCount: number;
  public favouriteVoteCount: number;
  public directRepliesCount: number;
  public allRepliesCount: number;
  public isVideoAuthorFavourite: boolean;
  public isPinned: boolean;
  public wasEdited: boolean;
  public isDeleted: boolean;
  public dateCreated: Date;
  public currentUserCommentRating: CommentRatingDto;
  public directReplies: CommentDto[];
}
