import { TestBed } from '@angular/core/testing';

import { VideoGuardService } from './video-guard.service';

describe('VideoGuardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: VideoGuardService = TestBed.get(VideoGuardService);
    expect(service).toBeTruthy();
  });
});
