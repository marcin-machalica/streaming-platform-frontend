import {Component, Inject, OnInit} from '@angular/core';
import {VideoDetailsDto} from '../../../dtos/VideoDetailsDto';
import {VideoService} from '../../../services/api/video.service';
import {ActivatedRoute} from '@angular/router';
import {VideoDto} from '../../../dtos/VideoDto';
import {VideoRatingDto} from '../../../dtos/VideoRatingDto';
import {environment} from '../../../../environments/environment';
import {CommentDto} from '../../../dtos/CommentDto';
import {CommentService} from '../../../services/api/comment.service';
import {DOCUMENT} from '@angular/common';

@Component({
  selector: 'app-video-details',
  templateUrl: './video-details.component.html',
  styleUrls: ['./video-details.component.sass']
})
export class VideoDetailsComponent implements OnInit {

  videoDetails: VideoDetailsDto = { videoDto: new VideoDto(), videoRatingDto: new VideoRatingDto(), directCommentDtos: [] };
  videoId: number;
  videoResourceUrl = (id) => `${environment.serverUrl}api/v1/videos/${id}/download`;

  constructor(private route: ActivatedRoute,
              @Inject(DOCUMENT) document,
              private videoService: VideoService,
              private commentService: CommentService) { }

  ngOnInit() {
    this.videoId = +this.route.snapshot.paramMap.get('id');
    this.loadVideoDetails();
  }

  loadVideoDetails() {
    this.videoService.getVideoDetails(this.videoId).subscribe(response => {
      if (response.body) {
        console.log(response.body);
        this.videoDetails = response.body;
      }
    });
  }

  saveComment(id: number) {
    const comment = new CommentDto();
    comment.parentId = id;
    comment.message = (document.getElementById(`comment_input_${id}`) as HTMLInputElement).value;
    if (comment.message.length < 1 || comment.message.length > 5000) {
      return;
    }

    this.commentService.saveComment(comment, this.videoId).subscribe(response => {
      console.log(response);
      if (response.status === 201) {
        if (!comment.parentId) {
          this.videoDetails.directCommentDtos.push(response.body);
        } else {
          this.loadVideoDetails();
        }
      }
    });
  }

  getCommentDtoWithReplies(commentId: number) {
    this.commentService.getCommentDtoWithReplies(this.videoId, commentId).subscribe(response => {
      const loadedCommentIndex = this.videoDetails.directCommentDtos.findIndex(comment => comment.id === commentId);
      this.videoDetails.directCommentDtos[loadedCommentIndex] = response.body;
      console.log(this.videoDetails.directCommentDtos);
    });
  }
}
