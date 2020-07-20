import {
  AfterViewInit,
  Component, EventEmitter,
  Inject,
  OnDestroy,
  OnInit,
} from '@angular/core';
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
import {VideoDetails} from '../../../services/api/video/VideoDto';
import {CommentRepresentation, SaveComment, CommentUpdate} from '../../../services/api/comment/CommentDto';
import {ChannelService} from '../../../services/api/channel/channel.service';

@Component({
  selector: 'app-video-details',
  templateUrl: './video-details.component.html',
  styleUrls: ['./video-details.component.sass']
})
export class VideoDetailsComponent implements OnInit, AfterViewInit, OnDestroy {

  isVideoAuthor = false;
  currentUserId: string;
  videoDetails: VideoDetails = new VideoDetails();
  videoId: number;
  dataSource;
  video;
  duration;
  viewCountdown;
  intervalId;
  watchedVideosIds;

  currentUserAvatarSrc;

  private avatarsLoadedEvent = new EventEmitter();

  videoResourceUrl = (id) => `${environment.serverUrl}api/v1/videos/${id}/download`;

  private _transformer = (node: CommentNode, level: number) => {
    return {
      id: node.id,
      parentId: node.parentId,
      channelIdentity: node.channelIdentity,
      authorId: node.authorId,
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
      dateCreated: new Date(node.dateCreated).toLocaleDateString(),
      avatarSrc: node.avatarSrc,
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
              private channelService: ChannelService,
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
      this.isVideoAuthor = this.videoDetails.authorId && this.videoDetails.authorId === this.currentUserId;
    });
    const watchedVideos = localStorage.getItem('watchedVideosIds');
    this.watchedVideosIds = watchedVideos !== null ? JSON.parse(watchedVideos) : [];

    this.loadCurrentUserAvatar(this.channelService.avatar);
    this.channelService.avatarUpdateEvent.subscribe((blob) => {
      this.loadCurrentUserAvatar(blob);
    });

    this.avatarsLoadedEvent.subscribe(() => {
      this.reloadComments();
    });
  }

  ngAfterViewInit() {
    setTimeout(() => this.loadCommentAuthorsAvatars(), 500);
    this.video = document.getElementsByTagName('video')[0];

    if (this.watchedVideosIds.includes(this.videoId)) {
      return;
    }

    this.video.addEventListener('loadeddata', () => {
      this.duration = parseInt(this.video.duration);
      this.viewCountdown = this.duration >= 30 ? 30 : this.duration;
      this.intervalId = setInterval(() => {
        if (this.viewCountdown <= 0) {
          this.videoService.upViewCount(this.videoId).subscribe(response => {
            this.watchedVideosIds.push(this.videoId);
            localStorage.setItem('watchedVideosIds', JSON.stringify(this.watchedVideosIds));
          });
          clearInterval(this.intervalId);
          return;
        }
        if (!this.video.paused) {
          this.viewCountdown--;
        }
      }, 1000);
    });
  }

  ngOnDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  loadVideoDetails() {
    this.videoService.getVideoDetails(this.videoId).subscribe(response => {
      if (response.body) {
        this.videoDetails = response.body;
        this.isVideoAuthor = this.currentUserId && this.videoDetails.authorId === this.currentUserId;
        this.reloadComments();
        this.loadCommentAuthorsAvatars();
        this.loadVideoAuthorAvatar();
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
          const comment = response.body;
          comment.avatarSrc = this.currentUserAvatarSrc;
          this.videoDetails.directCommentDtos.push(comment);
          this.reloadComments();
          (document.getElementById(`comment_input_${commentId}`) as HTMLInputElement).value = '';
          if (node) {
            node.isReplyActive = false;
          }
        } else {
          this.loadVideoDetails();
          setTimeout(() => this.loadCommentAuthorsAvatars(), 500);
        }
      }
    });
  }

  updateComment(node: CommentNode) {
    const commentId = node.id;
    const commentUpdate = new CommentUpdate();
    commentUpdate.message = (document.getElementById(`comment_input_${commentId}`) as HTMLInputElement).value;

    if (commentUpdate.message.length < 1 || commentUpdate.message.length > 5000) {
      return;
    }

    this.commentService.updateComment(commentUpdate, this.videoId, commentId).subscribe(response => {
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
      comment.isPinned = !comment.isPinned;
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

  getFormattedDate(date: Date) {
    return new Date (date).toLocaleDateString();
  }

  private loadCurrentUserAvatar(blob: Blob) {
    if (!blob) {
      return;
    }
    const reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onloadend = () => {
      this.currentUserAvatarSrc = reader.result;
    };
  }

  private loadCommentAuthorsAvatars() {
    const groupedComments: CommentsWithChannelName[] = [];
    this.updateCommentsGroupedByChannelName(groupedComments, this.videoDetails.directCommentDtos);
    let countDown = groupedComments.length;
    groupedComments.forEach(groupedComment => {
      this.channelService.getAvatar(groupedComment.channelName).subscribe(blob => {
        if (!blob) {
          return;
        }
        const reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onloadend = () => {
          groupedComment.comments.forEach(comment => {
            comment.avatarSrc = reader.result;
          });
        };

        if (--countDown <= 0) {
          this.avatarsLoadedEvent.emit();
        }
      });
    });
  }

  private updateCommentsGroupedByChannelName(groupedComments: CommentsWithChannelName[], directComments: CommentRepresentation[]) {
    directComments.forEach(comment => {
      const existingGroupedComment = groupedComments.filter(comm => comm.channelName === comment.channelIdentity.name)[0] || null;
      if (existingGroupedComment !== null) {
        existingGroupedComment.comments.push(comment);
      } else {
        groupedComments.push({ channelName: comment.channelIdentity.name, comments: [comment] });
      }

      if (!!comment.directReplies && comment.directReplies.length > 0) {
        this.updateCommentsGroupedByChannelName(groupedComments, comment.directReplies);
      }
    });
  }

  private loadVideoAuthorAvatar() {
    this.channelService.getAvatar(this.videoDetails.channelIdentity.name).subscribe(blob => {
      if (!blob) {
        return;
      }
      const reader = new FileReader();
      reader.readAsDataURL(blob);
      reader.onloadend = () => {
        this.videoDetails.avatarSrc = reader.result;
      };
    });
  }
}

class CommentsWithChannelName {
  channelName: string;
  comments: CommentRepresentation[];
}

export class CommentNode extends CommentRepresentation {
  public directReplies: CommentNode[];
  public expandable: boolean;
  public level: number;
  public isReplyActive: boolean;
  public isEditActive: boolean;
  public dateCreated: any;
}
