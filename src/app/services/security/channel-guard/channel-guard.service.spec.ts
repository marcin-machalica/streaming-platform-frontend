import { TestBed } from '@angular/core/testing';

import { ChannelGuardService } from './channel-guard.service';

describe('ChannelGuardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ChannelGuardService = TestBed.get(ChannelGuardService);
    expect(service).toBeTruthy();
  });
});
