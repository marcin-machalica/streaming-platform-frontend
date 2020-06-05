import { BrowserModule } from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import {MatListModule} from '@angular/material/list';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';
import {initializer} from './services/security/keycloak/keycloak-app-init';
import {MatButtonModule, MatIconModule, MatSidenavModule, MatToolbarModule} from '@angular/material';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FlexModule} from '@angular/flex-layout';
import {MatVideoModule} from 'mat-video';
import { VideosListComponent } from './components/videos-list/videos-list.component';
import { VideoDetailsComponent } from './components/videos-list/video-details/video-details.component';
import { VideoUploadComponent } from './components/video-upload/video-upload.component';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTreeModule} from '@angular/material/tree';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {
  ChannelGuardService,
  ReversedChannelGuardService
} from './services/security/channel-guard/channel-guard.service';
import { ChannelCreateComponent } from './components/channel/channel-create/channel-create.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    VideosListComponent,
    VideoDetailsComponent,
    VideoUploadComponent,
    ChannelCreateComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    KeycloakAngularModule,
    MatListModule,
    MatToolbarModule,
    MatSidenavModule,
    MatIconModule,
    MatButtonModule,
    FlexModule,
    MatVideoModule,
    MatFormFieldModule,
    MatInputModule,
    MatTreeModule,
    MatSnackBarModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
      deps: [KeycloakService]
    },
    ChannelGuardService,
    ReversedChannelGuardService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
