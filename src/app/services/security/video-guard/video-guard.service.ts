import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class VideoGuardService implements CanActivate {

  resourceUrl = videoId => `${environment.serverUrl}api/v1/videos/${videoId}/can-access`;

  constructor(private http: HttpClient,
              private router: Router) { }

  canAccessVideo(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<HttpResponse<boolean>> {
    const videoId = +route.paramMap.get('id');
    return this.http.get<boolean>(this.resourceUrl(videoId), { observe: 'response' });
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {

    return this.canAccessVideo(route, state).pipe(map(response => {
      if (!response.body) {
        this.router.navigateByUrl('/404');
      } else {
        return response.body;
      }
    }));
  }
}
