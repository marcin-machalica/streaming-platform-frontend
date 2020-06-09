import { TestBed } from '@angular/core/testing';

import { CommentRatingService } from './comment-rating.service';

describe('CommentRatingService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CommentRatingService = TestBed.get(CommentRatingService);
    expect(service).toBeTruthy();
  });
});
