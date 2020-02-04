import { BrowserModule } from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import {MatListModule} from '@angular/material/list';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';
import {initializer} from './services/keycloak/keycloak-app-init';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    KeycloakAngularModule,
    MatListModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
      deps: [KeycloakService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
