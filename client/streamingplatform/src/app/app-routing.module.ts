import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {VideoPlayerComponent} from './video-player/video-player.component';
import {VideosListComponent} from './components/videos-list/videos-list.component';
import {VideoDetailsComponent} from './components/videos-list/video-details/video-details.component';


const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'videos', component: VideosListComponent },
  { path: 'videos/:id', component: VideoDetailsComponent },
  { path: 'player', component: VideoPlayerComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
