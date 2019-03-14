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

  add_ride_validation_messages = {
    'driver': [
      {type: 'required', message: 'Required'},
      {type: 'minlength', message: 'Name must be at least 2 characters long'},
      {type: 'maxlength', message: 'Name cannot be more than 25 characters long'},
      {type: 'pattern', message: 'Name must contain only numbers and letters'}
    ],
    'pickup': [
      {type: 'required', message: 'Required'},
      {type: 'minlength', message: 'Meetup location must be at least 2 characters long'},
      {type: 'maxlength', message: 'Meetup location cannot be more than 25 characters long'},
      {type: 'pattern', message: 'Meetup location must contain only numbers and letters'}
    ],
    'dropoff': [
      {type: 'required', message: 'Required'},
      {type: 'minlength', message: 'Destination must be at least 2 characters long'},
      {type: 'maxlength', message: 'Destination cannot be more than 25 characters long'},
      {type: 'pattern', message: 'Destination must contain only numbers and letters'}
    ],
    'notes': [
      {type: 'maxlength', message: 'Notes cannot be more than 120 characters long'}
    ]

  };

  createForms() {

    // add ride form validations
    this.addRideForm = this.fb.group({

      driver: new FormControl('driver', Validators.compose([
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(25),
        Validators.pattern('^[A-Za-z0-9\\s]+[A-Za-z0-9\\s]+$(\\.0-9+)?')
      ])),
      notes: new FormControl('notes', Validators.compose([
        Validators.maxLength(120)
      ])),
      dateTime: new FormControl('dateTime'),
      pickup: new FormControl('pickup', Validators.compose([
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(25),
        Validators.pattern('^[A-Za-z0-9\\s]+[A-Za-z0-9\\s]+$(\\.0-9+)?')
      ])),
      dropoff: new FormControl('dropoff', Validators.compose([
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(25),
        Validators.pattern('^[A-Za-z0-9\\s]+[A-Za-z0-9\\s]+$(\\.0-9+)?')
      ]))

    })

  }

  ngOnInit() {
    this.createForms();
  }

}
