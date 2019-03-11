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
        {
          _id: '5c81956426f729a694221541',
          driver: 'Janice Gibbs',
          riders: ["Rider1", "Rider2"],
          route: [
            "Martell",
            "Hollymead"
          ],
          roundTrip: true,
          dateTime: 'Tue Oct 22 2002 08:50:21 GMT+0000 (UTC)',
          notes: 'Tempor sunt ipsum nostrud mollit eu ea id mollit elit ad eiusmod id et.'
        },
        {
          _id: '5c8195647df487e29e4d241b',
          driver: 'Vilma Rush',
          riders: [],
          route: [
            "Yettem",
            "Longbranch"
          ],
          roundTrip: false,
          dateTime: 'Mon Jun 19 1989 06:14:16 GMT+0000 (UTC)',
          notes: 'Consectetur aute culpa sint dolor aliquip.'
        },
        {
          _id: '5c819564559e8f42ed7a6555',
          driver: 'Elsa Frazier',
          riders: [],
          route: [
            "Robinette",
            "Chautauqua"
          ],
          roundTrip: false,
          dateTime: 'Fri Aug 27 1982 17:11:44 GMT+0000 (UTC)',
          notes: 'Velit culpa id mollit est Lorem.'
        }
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

  it('contains all the rides', () => {
    expect(rideList.rides.length).toBe(3);
  });

  it('contains a ride with a driver named \'Elsa Frazier\'', () => {
    expect(rideList.rides.some((ride: Ride) => ride.driver === 'Elsa Frazier')).toBe(true);
  });

  it('contain a ride with a driver named \'Vilma Rush\'', () => {
    expect(rideList.rides.some((ride: Ride) => ride.driver === 'Vilma Rush')).toBe(true);
  });

  it('doesn\'t contain a user named \'Santa\'', () => {
    expect(rideList.rides.some((ride: Ride) => ride.driver === 'Santa')).toBe(false);
  });

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


describe('Adding a ride', () => {
  let rideList: RideListComponent;
  let fixture: ComponentFixture<RideListComponent>;
  const newRide: Ride = {
    _id: '',
    driver: 'Arlene Talley',
    riders: [],
    route: [
      "Elfrida",
      "Alden"
    ],
    roundTrip: false,
    dateTime: 'Thu Aug 29 1985 23:08:05 GMT+0000 (UTC)',
    notes: 'Eiusmod ad ipsum laboris amet aute reprehenderit commodo commodo.'
  };
  const newId = 'arlene_id';

  let calledRide: Ride;

  let rideListServiceStub: {
    getRides: () => Observable<Ride[]>,
    addNewRide: (newRide: Ride) => Observable<{ '$oid': string }>
  };
  let mockMatDialog: {
    open: (AddRideComponent, any) => {
      afterClosed: () => Observable<Ride>
    };
  };

  beforeEach(() => {
    calledRide = null;
    // stub RideService for test purposes
    rideListServiceStub = {
      getRides: () => Observable.of([]),
      addNewRide: (newRide: Ride) => {
        calledRide = newRide;
        return Observable.of({
          '$oid': newId
        });
      }
    };
    mockMatDialog = {
      open: () => {
        return {
          afterClosed: () => {
            return Observable.of(newRide);
          }
        };
      }
    };

    TestBed.configureTestingModule({
      imports: [FormsModule, CustomModule],
      declarations: [RideListComponent],
      providers: [
        {provide: RideListService, useValue: rideListServiceStub},
        {provide: MatDialog, useValue: mockMatDialog},
      ]
    });
  });

  beforeEach(async(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(RideListComponent);
      rideList = fixture.componentInstance;
      fixture.detectChanges();
    });
  }));

  it('calls RideListService.addRide', () => {
    expect(calledRide).toBeNull();
    rideList.openDialog();
    expect(calledRide).toEqual(newRide);
  });
});
