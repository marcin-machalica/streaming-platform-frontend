import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DefaultComponent} from './default.component';
import {HomeComponent} from '../../modules/home/home.component';
import {RouterModule} from '@angular/router';
import {FlexLayoutModule} from '@angular/flex-layout';
import {MatListModule, MatSidenavModule} from '@angular/material';
import {SharedModule} from '../../shared/shared.module';



@NgModule({
  declarations: [
    DefaultComponent,
    HomeComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    FlexLayoutModule,
    MatListModule,
    SharedModule,
    MatSidenavModule
  ]
})
export class DefaultModule { }
