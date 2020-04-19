import {VideoRatingDto} from './VideoRatingDto';
import {CommentDto} from './CommentDto';
import {UserDto} from './UserDto';

export class VideoDetailsDto {
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
  directCommentDtos: CommentDto[];

  constructor() {
    this.author = new UserDto();
    this.currentUserVideoRating = new VideoRatingDto();
  }
}
