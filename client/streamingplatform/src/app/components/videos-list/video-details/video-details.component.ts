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
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';

@Component({
  selector: 'app-video-details',
  templateUrl: './video-details.component.html',
  styleUrls: ['./video-details.component.sass']
})
export class VideoDetailsComponent implements OnInit {

  videoDetails: VideoDetailsDto = {
    videoDto: new VideoDto(),
    videoRatingDto: new VideoRatingDto(),
    directCommentDtos: []
  };
  videoId: number;
  dataSource;
  videoResourceUrl = (id) => `${environment.serverUrl}api/v1/videos/${id}/download`;

  private _transformer = (node: CommentDtoNode, level: number) => {
    return {
      id: node.id,
      parentId: node.parentId,
      message: node.message,
      upVoteCount: node.upVoteCount,
      downVoteCount: node.downVoteCount,
      favouriteVoteCount: node.favouriteVoteCount,
      directRepliesCount: node.directRepliesCount,
      allRepliesCount: node.allRepliesCount,
      isVideoAuthorFavourite: node.isVideoAuthorFavourite,
      isPinned: node.isPinned,
      dateCreated: node.dateCreated,
      directReplies: node.directReplies,
      // author todo
      expandable: !!node.directReplies && node.directReplies.length > 0,
      level: level,
    };
  }

  treeControl = new FlatTreeControl<CommentDtoNode>(
    node => node.level, node => node.expandable);

  treeFlattener = new MatTreeFlattener(
    this._transformer, node => node.level, node => node.expandable, node => node.directReplies);

  hasChild = (_: number, node: CommentDtoNode) => node.expandable;

  constructor(private route: ActivatedRoute,
              @Inject(DOCUMENT) document,
              private videoService: VideoService,
              private commentService: CommentService) {
  }

  ngOnInit() {
    this.videoId = +this.route.snapshot.paramMap.get('id');
    this.loadVideoDetails();
  }

  loadVideoDetails() {
    this.videoService.getVideoDetails(this.videoId).subscribe(response => {
      if (response.body) {
        this.videoDetails = response.body;
        this.reloadComments();
      }
    });
  }

  reloadComments() {
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.dataSource.data = this.videoDetails.directCommentDtos;
  }

  saveComment(id: number) {
    const comment = new CommentDto();
    comment.parentId = id > 0 ? id : null;
    comment.message = (document.getElementById(`comment_input_${id}`) as HTMLInputElement).value;
    if (comment.message.length < 1 || comment.message.length > 5000) {
      return;
    }

    this.commentService.saveComment(comment, this.videoId).subscribe(response => {
      if (response.status === 201) {
        if (!comment.parentId) {
          this.videoDetails.directCommentDtos.push(response.body);
          this.reloadComments();
          comment.message = (document.getElementById(`comment_input_${id}`) as HTMLInputElement).value = '';
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
      this.reloadComments();
    });
  }
}

export class CommentDtoNode extends CommentDto {
  public directReplies: CommentDtoNode[];
  public expandable: boolean;
  public level: number;
}
