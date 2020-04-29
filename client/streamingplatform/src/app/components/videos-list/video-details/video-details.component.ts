import {Component, Inject, OnInit} from '@angular/core';
import {VideoService} from '../../../services/api/video/video.service';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {CommentService} from '../../../services/api/comment/comment.service';
import {DOCUMENT} from '@angular/common';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {KeycloakService} from 'keycloak-angular';
import {CommentRatingService} from '../../../services/api/comment/comment-rating/comment-rating.service';
import {VideoRatingService} from '../../../services/api/video/video-rating/video-rating.service';
import {VideoDetailsDto} from '../../../services/api/video/VideoDto';
import {CommentRepresentation, SaveComment, UpdateComment} from '../../../services/api/comment/CommentDto';

@Component({
  selector: 'app-video-details',
  templateUrl: './video-details.component.html',
  styleUrls: ['./video-details.component.sass']
})
export class VideoDetailsComponent implements OnInit {

  isVideoAuthor = false;
  currentUserId: string;
  videoDetails: VideoDetailsDto = new VideoDetailsDto();
  videoId: number;
  dataSource;
  videoResourceUrl = (id) => `${environment.serverUrl}api/v1/videos/${id}/download`;

  private _transformer = (node: CommentNode, level: number) => {
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

  treeControl = new FlatTreeControl<CommentNode>(
    node => node.level, node => node.expandable);

  treeFlattener = new MatTreeFlattener(
    this._transformer, node => node.level, node => node.expandable, node => node.directReplies);

  hasChild = (_: number, node: CommentNode) => node.expandable;

  constructor(private route: ActivatedRoute,
              @Inject(DOCUMENT) document,
              private keycloakService: KeycloakService,
              private videoService: VideoService,
              private commentService: CommentService,
              private videoRatingService: VideoRatingService,
              private commentRatingService: CommentRatingService) {
  }

  ngOnInit() {
    this.videoId = +this.route.snapshot.paramMap.get('id');
    this.loadVideoDetails();
    this.keycloakService.getKeycloakInstance().loadUserInfo().success(user => {
      this.currentUserId = (user as any).sub;
      this.isVideoAuthor = this.currentUserId !== null && this.currentUserId === this.videoDetails.author.id;
    });
  }

  loadVideoDetails() {
    this.videoService.getVideoDetails(this.videoId).subscribe(response => {
      if (response.body) {
        this.videoDetails = response.body;
        this.isVideoAuthor = this.currentUserId !== null && this.currentUserId === this.videoDetails.author.id;
        this.reloadComments();
      }
    });
  }

  reloadComments() {
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    this.dataSource.data = this.videoDetails.directCommentDtos;
  }

  upVoteVideo() {
    this.videoRatingService.upVoteVideo(this.videoId).subscribe(response => {
      if (response.status === 200) {
        const wasUpVote = this.videoDetails.currentUserVideoRating.isUpVote;
        const wasDownVote = this.videoDetails.currentUserVideoRating.isDownVote;
        this.videoDetails.currentUserVideoRating.isUpVote = response.body.isUpVote;
        this.videoDetails.currentUserVideoRating.isDownVote = response.body.isDownVote;

        this.videoDetails.upVoteCount += wasUpVote ? -1 : 1;
        if (wasDownVote) {
          this.videoDetails.downVoteCount -= 1;
        }
      }
    });
  }

  downVoteVideo() {
    this.videoRatingService.downVoteVideo(this.videoId).subscribe(response => {
      if (response.status === 200) {
        const wasUpVote = this.videoDetails.currentUserVideoRating.isUpVote;
        const wasDownVote = this.videoDetails.currentUserVideoRating.isDownVote;
        this.videoDetails.currentUserVideoRating.isUpVote = response.body.isUpVote;
        this.videoDetails.currentUserVideoRating.isDownVote = response.body.isDownVote;

        this.videoDetails.downVoteCount += wasDownVote ? -1 : 1;
        if (wasUpVote) {
          this.videoDetails.upVoteCount -= 1;
        }
      }
    });
  }

  saveComment(node: CommentNode) {
    const commentId = node ? node.id : 0;
    const saveComment = new SaveComment();
    saveComment.parentId = commentId > 0 ? commentId : null;
    saveComment.message = (document.getElementById(`comment_input_${commentId}`) as HTMLInputElement).value;
    if (saveComment.message.length < 1 || saveComment.message.length > 5000) {
      return;
    }

    this.commentService.saveComment(saveComment, this.videoId).subscribe(response => {
      if (response.status === 201) {
        if (!response.body.parentId) {
          this.videoDetails.directCommentDtos.push(response.body);
          this.reloadComments();
          (document.getElementById(`comment_input_${commentId}`) as HTMLInputElement).value = '';
          if (node) {
            node.isReplyActive = false;
          }
        } else {
          this.loadVideoDetails();
        }
      }
    });
  }

  updateComment(node: CommentNode) {
    const commentId = node.id;
    const updateComment = new UpdateComment();
    updateComment.message = (document.getElementById(`comment_input_${commentId}`) as HTMLInputElement).value;

    if (updateComment.message.length < 1 || updateComment.message.length > 5000) {
      return;
    }

    this.commentService.updateComment(updateComment, this.videoId, commentId).subscribe(response => {
      if (response.status === 200) {
        (document.getElementById(`comment_input_${commentId}`) as HTMLInputElement).value = '';
        node.isEditActive = false;
        this.loadVideoDetails();
      }
    });
  }

  deleteComment(comment: CommentNode) {
    const commentId = comment.id;

    this.commentService.deleteComment(this.videoId, commentId).subscribe(response => {
      comment.isDeleted = true;
      this.cancelReplyAndEdit(comment);
    });
  }

  upVoteComment(comment: CommentNode) {
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

  downVoteComment(comment: CommentNode) {
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

  favouriteComment(comment: CommentNode) {
    this.commentRatingService.favouriteComment(this.videoId, comment.id).subscribe(response => {
      if (response.status === 200) {
        comment.favouriteCount = response.body.favouriteCount;
        comment.currentUserCommentRating.isFavourite = response.body.isFavourite;
        comment.isVideoAuthorFavourite = response.body.isVideoAuthorFavourite;
      }
    });
  }

  pinComment(comment: CommentNode) {
    this.commentRatingService.pinComment(this.videoId, comment.id).subscribe(response => {
      if (response.status === 204) {
        comment.isPinned = !comment.isPinned;
      }
    });
  }

  getCommentRepresentationWithReplies(commentId: number) {
    this.commentService.getCommentRepresentationWithReplies(this.videoId, commentId).subscribe(response => {
      const loadedCommentIndex = this.videoDetails.directCommentDtos.findIndex(comment => comment.id === commentId);
      this.videoDetails.directCommentDtos[loadedCommentIndex] = response.body;
      this.reloadComments();
    });
  }

  isOwner(authorId: string) {
    if (!authorId || this.currentUserId === null) {
      return false;
    }
    return this.currentUserId === authorId;
  }

  isFirstLevelComment(node: CommentNode) {
    return this.videoDetails.directCommentDtos.find(comment => comment.id === node.id);
  }

  setReplyActive(node: CommentNode) {
    node.isReplyActive = true;
    node.isEditActive = false;
  }

  setEditActive(node: CommentNode) {
    node.isReplyActive = false;
    node.isEditActive = true;
  }

  cancelReplyAndEdit(node: CommentNode) {
    node.isReplyActive = false;
    node.isEditActive = false;
  }
}

export class CommentNode extends CommentRepresentation {
  public directReplies: CommentNode[];
  public expandable: boolean;
  public level: number;
  public isReplyActive: boolean;
  public isEditActive: boolean;
}
