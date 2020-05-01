export class CommentRatingRepresentation {
  public commentId: number;
  public isUpVote: boolean;
  public isDownVote: boolean;
  public isFavourite: boolean;
}

export class CommentFavouriteDto {
  public favouriteCount: number;
  public isFavourite: boolean;
  public isVideoAuthorFavourite: boolean;
}
