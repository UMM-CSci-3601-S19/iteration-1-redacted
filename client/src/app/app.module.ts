import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {UserComponent} from './users/user.component';
import {UserListComponent} from './users/user-list.component';
import {UserListService} from './users/user-list.service';
import {RideComponent} from './rides/ride.component';
import {RideListComponent} from './rides/ride-list.component';
import {RideListService} from './rides/ride-list.service';
import {Routing} from './app.routes';
import {APP_BASE_HREF} from '@angular/common';

import {CustomModule} from './custom.module';
import {AddUserComponent} from './users/add-user.component';
import {AddRideComponent} from "./rides/add-ride.component";
import {MatDatepickerModule, MatNativeDateModule} from "@angular/material";


@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    Routing,
    CustomModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  declarations: [
    AppComponent,
    HomeComponent,
    UserListComponent,
    UserComponent,
    RideListComponent,
    RideComponent,
    AddUserComponent,
    AddRideComponent
  ],
  providers: [
    UserListService,
    RideListService,
    {provide: APP_BASE_HREF, useValue: '/'},
  ],
  entryComponents: [
    AddUserComponent,
    AddRideComponent
  ],
  bootstrap: [AppComponent]
})

export class AppModule {
}
