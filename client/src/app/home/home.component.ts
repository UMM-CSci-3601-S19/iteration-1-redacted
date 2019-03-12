import {Component} from '@angular/core';

@Component({
  templateUrl: 'home.component.html'

})
export class HomeComponent {
  public header: string;
  public body: string;

  constructor() {
    this.header = 'MoRide';
    this.body = 'Welcome to MoRide!  Helping people carpool since 2019!'
  }
}
