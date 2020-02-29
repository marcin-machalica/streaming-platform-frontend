export class CommentDto {
  public id: number;
  public parentId: number;
  public message: string;
  public upVoteCount: number;
  public downVoteCount: number;
  public favouriteVoteCount: number;
  public directRepliesCount: number;
  public allRepliesCount: number;
  public isVideoAuthorFavourite: boolean;
  public isPinned: boolean;
  public dateCreated: Date;
  public directReplies: CommentDto[];
  // author todo
}