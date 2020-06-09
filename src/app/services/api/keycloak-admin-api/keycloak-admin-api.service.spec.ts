import { TestBed } from '@angular/core/testing';

import { KeycloakAdminApiService } from './keycloak-admin-api.service';

describe('KeycloakAdminApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: KeycloakAdminApiService = TestBed.get(KeycloakAdminApiService);
    expect(service).toBeTruthy();
  });
});
