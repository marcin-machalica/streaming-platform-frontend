import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {VideosListComponent} from './components/videos-list/videos-list.component';
import {VideoDetailsComponent} from './components/videos-list/video-details/video-details.component';
import {VideoUploadComponent} from './components/video-upload/video-upload.component';
import {
  ChannelGuardService,
  ReversedChannelGuardService
} from './services/security/channel-guard/channel-guard.service';
import {ChannelCreateComponent} from './components/channel/channel-create/channel-create.component';


const routes: Routes = [
  { path: 'create-channel', component: ChannelCreateComponent, canActivate: [ReversedChannelGuardService] },
  { path: '', component: HomeComponent, canActivate: [ChannelGuardService] },
  { path: 'videos', component: VideosListComponent, canActivate: [ChannelGuardService] },
  { path: 'videos/upload', component: VideoUploadComponent, canActivate: [ChannelGuardService] },
  { path: 'videos/:id', component: VideoDetailsComponent, canActivate: [ChannelGuardService] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
