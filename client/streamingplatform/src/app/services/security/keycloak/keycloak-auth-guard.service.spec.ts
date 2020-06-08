import { TestBed } from '@angular/core/testing';

import { KeycloakAuthGuardService } from './keycloak-auth-guard.service';

describe('KeycloakAuthGuardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: KeycloakAuthGuardService = TestBed.get(KeycloakAuthGuardService);
    expect(service).toBeTruthy();
  });
});
