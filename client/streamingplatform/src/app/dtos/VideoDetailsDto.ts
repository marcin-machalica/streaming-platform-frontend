import {VideoDto} from './VideoDto';
import {VideoRatingDto} from './VideoRatingDto';
import {CommentDto} from './CommentDto';

export class VideoDetailsDto {
  videoDto: VideoDto;
  videoRatingDto: VideoRatingDto;
  directCommentDtos: CommentDto[];
}
