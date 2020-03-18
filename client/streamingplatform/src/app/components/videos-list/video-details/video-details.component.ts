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
import {UserDto} from '../../../dtos/UserDto';
import {KeycloakService} from 'keycloak-angular';
import {CommentRatingService} from '../../../services/api/comment-rating.service';

@Component({
  selector: 'app-video-details',
  templateUrl: './video-details.component.html',
  styleUrls: ['./video-details.component.sass']
})
export class VideoDetailsComponent implements OnInit {

  currentUserId: string;
  videoDetails: VideoDetailsDto = {
    videoDto: { ...(new VideoDto()), author: new UserDto() },
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
      author: node.author,
      message: node.message,
      upVoteCount: node.upVoteCount,
      downVoteCount: node.downVoteCount,
      favouriteCount: node.favouriteCount,
      directRepliesCount: node.directRepliesCount,
      allRepliesCount: node.allRepliesCount,
      isVideoAuthorFavourite: node.isVideoAuthorFavourite,
      isPinned: node.isPinned,
      wasEdited: node.wasEdited,
      isDeleted: node.isDeleted,
      dateCreated: node.dateCreated,
      currentUserCommentRating: node.currentUserCommentRating,
      directReplies: node.directReplies,
      isReplyActive: node.isReplyActive,
      isEditActive: node.isEditActive,
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
              private keycloakService: KeycloakService,
              private videoService: VideoService,
              private commentService: CommentService,
              private commentRatingService: CommentRatingService) {
  }

  ngOnInit() {
    this.videoId = +this.route.snapshot.paramMap.get('id');
    this.loadVideoDetails();
    this.keycloakService.getKeycloakInstance().loadUserInfo().success(user => {
      this.currentUserId = (user as any).sub;
    });
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

  saveComment(node: CommentDtoNode) {
    const id = node ? node.id : 0;
    const comment = new CommentDto();
    comment.parentId = id > 0 ? id : null;
    comment.message = (document.getElementById(`comment_input_${id}`) as HTMLInputElement).value;
    if (comment.message.length < 1 || comment.message.length > 5000) {
      return;
    }

    this.commentService.saveComment(comment, this.videoId).subscribe(response => {
      if (response.status === 201) {
        if (!response.body.parentId) {
          this.videoDetails.directCommentDtos.push(response.body);
          this.reloadComments();
          (document.getElementById(`comment_input_${id}`) as HTMLInputElement).value = '';
          if (node) {
            node.isReplyActive = false;
          }
        } else {
          this.loadVideoDetails();
        }
      }
    });
  }

  updateComment(node: CommentDtoNode) {
    const id = node.id;
    const comment = new CommentDto();
    comment.message = (document.getElementById(`comment_input_${id}`) as HTMLInputElement).value;

    this.commentService.updateComment(comment, this.videoId, id).subscribe(response => {
      if (response.status === 200) {
        (document.getElementById(`comment_input_${id}`) as HTMLInputElement).value = '';
        node.isEditActive = false;
        this.loadVideoDetails();
      }
    });
  }

  deleteComment(comment: CommentDtoNode) {
    const commentId = comment.id;

    this.commentService.deleteComment(this.videoId, commentId).subscribe(response => {
      if (response.status === 204) {
        comment.isDeleted = true;
        this.cancelReplyAndEdit(comment);
      }
    });
  }

  upVoteComment(comment: CommentDtoNode) {
    this.commentRatingService.upVoteComment(this.videoId, comment.id).subscribe(response => {
      if (response.status === 200) {
        const wasUpVote = comment.currentUserCommentRating.isUpVote;
        const wasDownVote = comment.currentUserCommentRating.isDownVote;
        comment.currentUserCommentRating.isUpVote = response.body.isUpVote;
        comment.currentUserCommentRating.isDownVote = response.body.isDownVote;

        comment.upVoteCount += wasUpVote ? -1 : 1;
        if (wasDownVote) {
          comment.downVoteCount -= 1;
        }
      }
    });
  }

  downVoteComment(comment: CommentDtoNode) {
    this.commentRatingService.downVoteComment(this.videoId, comment.id).subscribe(response => {
      if (response.status === 200) {
        const wasUpVote = comment.currentUserCommentRating.isUpVote;
        const wasDownVote = comment.currentUserCommentRating.isDownVote;
        comment.currentUserCommentRating.isUpVote = response.body.isUpVote;
        comment.currentUserCommentRating.isDownVote = response.body.isDownVote;

        comment.downVoteCount += wasDownVote ? -1 : 1;
        if (wasUpVote) {
          comment.upVoteCount -= 1;
        }
      }
    });
  }

  favouriteComment(comment: CommentDtoNode) {
    this.commentRatingService.favouriteComment(this.videoId, comment.id).subscribe(response => {
      if (response.status === 200) {
        const wasFavourite = comment.currentUserCommentRating.isFavourite;
        comment.currentUserCommentRating.isFavourite = response.body.isFavourite;
        comment.favouriteCount += wasFavourite ? -1 : 1;
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

  isOwner(authorId: string) {
    if (authorId === null || this.currentUserId === null) {
      return false;
    }
    return this.currentUserId === authorId;
  }

  setReplyActive(node: CommentDtoNode) {
    node.isReplyActive = true;
    node.isEditActive = false;
  }

  setEditActive(node: CommentDtoNode) {
    node.isReplyActive = false;
    node.isEditActive = true;
  }

  cancelReplyAndEdit(node: CommentDtoNode) {
    node.isReplyActive = false;
    node.isEditActive = false;
  }
}

export class CommentDtoNode extends CommentDto {
  public directReplies: CommentDtoNode[];
  public expandable: boolean;
  public level: number;
  public isReplyActive: boolean;
  public isEditActive: boolean;
}
