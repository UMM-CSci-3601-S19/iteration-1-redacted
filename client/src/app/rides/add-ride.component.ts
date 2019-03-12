import {Component, Inject, NgModule, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDatepickerModule, MatNativeDateModule} from '@angular/material';
import {Ride} from './ride';
import {FormControl, Validators, FormGroup, FormBuilder} from "@angular/forms";

@NgModule({
  imports:
    [
      MatDatepickerModule,
      MatNativeDateModule,
    ]
})

@Component({
  selector: 'add-ride.component',
  templateUrl: 'add-ride.component.html',
})
export class AddRideComponent implements OnInit {

  addRideForm: FormGroup;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { ride: Ride }, private fb: FormBuilder) {
  }

  // not sure if this name is magical and making it be found or if I'm missing something,
  // but this is where the red text that shows up (when there is invalid input) comes from
  add_ride_validation_messages = {
    'driver': [
      {type: 'minlength', message: 'Name must be at least 2 characters long'},
      {type: 'maxlength', message: 'Name cannot be more than 25 characters long'},
      {type: 'pattern', message: 'Name must contain only numbers and letters'}
      ]

  };

  createForms() {

    // add ride form validations
    this.addRideForm = this.fb.group({

      driver: new FormControl('driver', Validators.compose([
        Validators.minLength(2),
        Validators.maxLength(25),
        Validators.pattern('^[A-Za-z0-9\\s]+[A-Za-z0-9\\s]+$(\\.0-9+)?')
      ])),
      notes: new FormControl('notes'),
      dateTime: new FormControl('dateTime'),
      pickup: new FormControl('pickup'),
      dropoff: new FormControl('dropoff')

    })

  }

  ngOnInit() {
    this.createForms();
  }

}
