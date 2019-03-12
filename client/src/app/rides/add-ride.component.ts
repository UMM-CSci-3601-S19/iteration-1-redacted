import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material';
import {Ride} from './ride';
import {FormControl, Validators, FormGroup, FormBuilder} from "@angular/forms";

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



  };

  createForms() {

    // add ride form validations
    this.addRideForm = this.fb.group({

      driver: new FormControl('driver'),
      notes: new FormControl('notes'),
      dateTime: new FormControl('dateTime'),
      roundTrip: new FormControl('roundTrip')

    })

  }

  ngOnInit() {
    this.createForms();
  }

}
