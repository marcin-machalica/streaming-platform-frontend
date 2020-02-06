import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MatListModule} from '@angular/material/list';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {LayoutModule} from '@angular/cdk/layout';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';
import {initializer} from './services/keycloak/keycloak-app-init';
import {DefaultModule} from './layouts/default/default.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    DefaultModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    MatListModule,
    NoopAnimationsModule,
    LayoutModule,
    MatToolbarModule,
    MatSidenavModule,
    KeycloakAngularModule,
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
export class AppModule {
}
