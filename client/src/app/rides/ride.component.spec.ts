import {ComponentFixture, TestBed, async} from '@angular/core/testing';
import {Ride} from './ride';
import {RideComponent} from './ride.component';
import {RideListService} from './ride-list.service';
import {Observable} from 'rxjs/Observable';
import {CustomModule} from "../custom.module";

describe('Ride component', () => {

  let rideComponent: RideComponent;
  let fixture: ComponentFixture<RideComponent>;

  let rideListServiceStub: {
    getRideById: (rideId: string) => Observable<Ride>
  };

  beforeEach(() => {
    // stub RideService for test purposes
    rideListServiceStub = {
      getRideById: (rideId: string) => Observable.of([
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
      ].find(ride => ride._id === rideId))
    };

    TestBed.configureTestingModule({
      imports: [CustomModule],
      declarations: [RideComponent],
      providers: [{provide: RideListService, useValue: rideListServiceStub}]
    });
  });

  beforeEach(async(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(RideComponent);
      rideComponent = fixture.componentInstance;
    });
  }));

  it('can retrieve Vilma\'s ride by ID', () => {
    rideComponent.setId('5c8195647df487e29e4d241b');
    expect(rideComponent.ride).toBeDefined();
    expect(rideComponent.ride.driver).toBe('Vilma Rush');
    expect(rideComponent.ride.notes).toBe('Consectetur aute culpa sint dolor aliquip.');
  });

  it('can retrieve Elsa\'s ride by ID', () => {
    rideComponent.setId('5c819564559e8f42ed7a6555');
    expect(rideComponent.ride).toBeDefined();
    expect(rideComponent.ride.driver).toBe('Elsa Frazier');
    expect(rideComponent.ride.notes).toBe('Velit culpa id mollit est Lorem.');
  });


  it('returns undefined for Santa', () => {
    rideComponent.setId('Santa');
    expect(rideComponent.ride).not.toBeDefined();
  });

});
