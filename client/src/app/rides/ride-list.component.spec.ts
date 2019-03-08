import {ComponentFixture, TestBed, async} from '@angular/core/testing';
import {Ride} from './ride';
import {RideListComponent} from './ride-list.component';
import {RideListService} from './ride-list.service';
import {Observable} from 'rxjs/Observable';
import {FormsModule} from '@angular/forms';
import {CustomModule} from '../custom.module';
import {MatDialog} from '@angular/material';

import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';

describe('Ride list', () => {

  let rideList: RideListComponent;
  let fixture: ComponentFixture<RideListComponent>;

  let rideListServiceStub: {
    getRides: () => Observable<Ride[]>
  };

  beforeEach(() => {
    // stub RideService for test purposes
    rideListServiceStub = {
      getRides: () => Observable.of([

      ])
    };

    TestBed.configureTestingModule({
      imports: [CustomModule],
      declarations: [RideListComponent],
      // providers:    [ RideListService ]  // NO! Don't provide the real service!
      // Provide a test-double instead
      providers: [{provide: RideListService, useValue: rideListServiceStub}]
    });
  });

  beforeEach(async(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(RideListComponent);
      rideList = fixture.componentInstance;
      fixture.detectChanges();
    });
  }));



});

describe('Misbehaving Ride List', () => {
  let rideList: RideListComponent;
  let fixture: ComponentFixture<RideListComponent>;

  let rideListServiceStub: {
    getRides: () => Observable<Ride[]>
  };

  beforeEach(() => {
    // stub RideService for test purposes
    rideListServiceStub = {
      getRides: () => Observable.create(observer => {
        observer.error('Error-prone observable');
      })
    };

    TestBed.configureTestingModule({
      imports: [FormsModule, CustomModule],
      declarations: [RideListComponent],
      providers: [{provide: RideListService, useValue: rideListServiceStub}]
    });
  });

  beforeEach(async(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(RideListComponent);
      rideList = fixture.componentInstance;
      fixture.detectChanges();
    });
  }));

});


// describe('Adding a ride', () => {
//   let rideList: RideListComponent;
//   let fixture: ComponentFixture<RideListComponent>;
//   const newRide: Ride ;
//   const newId ;
//
//   let calledRide: Ride;
//
//   let rideListServiceStub: {
//     getRides: () => Observable<Ride[]>,
//     addNewRide: (newRide: Ride) => Observable<{ '$oid': string }>
//   };
//   let mockMatDialog: {
//     open: (AddRideComponent, any) => {
//       afterClosed: () => Observable<Ride>
//     };
//   };
//
//   beforeEach(() => {
//     calledRide = null;
//     // stub RideService for test purposes
//     rideListServiceStub = {
//       getRides: () => Observable.of([]),
//       addNewRide: (newRide: Ride) => {
//         calledRide = newRide;
//         return Observable.of({
//           '$oid': newId
//         });
//       }
//     };
//     mockMatDialog = {
//       open: () => {
//         return {
//           afterClosed: () => {
//             return Observable.of(newRide);
//           }
//         };
//       }
//     };
//
//     TestBed.configureTestingModule({
//       imports: [FormsModule, CustomModule],
//       declarations: [RideListComponent],
//       providers: [
//         {provide: RideListService, useValue: rideListServiceStub},
//         {provide: MatDialog, useValue: mockMatDialog},
//       ]
//     });
//   });
//
//   beforeEach(async(() => {
//     TestBed.compileComponents().then(() => {
//       fixture = TestBed.createComponent(RideListComponent);
//       rideList = fixture.componentInstance;
//       fixture.detectChanges();
//     });
//   }));
//
//   it('calls RideListService.addRide', () => {
//     expect(calledRide).toBeNull();
//     //rideList.openDialog();
//     expect(calledRide).toEqual(newRide);
//   });
// });
