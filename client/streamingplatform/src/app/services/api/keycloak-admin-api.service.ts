import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {UserDto} from '../../dtos/UserDto';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';

type UserDtoArrayResponseType = HttpResponse<UserDto[]>;
type UserDtoResponseType = HttpResponse<UserDto>;

@Injectable({
  providedIn: 'root'
})
export class KeycloakAdminApiService {

  resourceUrl = environment.serverUrl + 'api/v1/keycloak';
  usersResourceUrl = this.resourceUrl + '/users';

  constructor(private http: HttpClient) { }

  getAllUserDtos(): Observable<UserDtoArrayResponseType> {
    return this.http.get<UserDto[]>(this.usersResourceUrl, { observe: 'response' });
  }
}
