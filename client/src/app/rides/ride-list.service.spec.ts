import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';
import {HttpClient} from '@angular/common/http';

import {Ride} from './ride';
import {RideListService} from './ride-list.service';

describe('Ride list service: ', () => {
  // A small collection of test rides
  const testRides: Ride[] = [
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
  ];
  const mRides: Ride[] = testRides.filter(ride =>
    ride.driver.toLowerCase().indexOf('m') !== -1
  );


  // We will need some url information from the rideListService to meaningfully test company filtering;
  // https://stackoverflow.com/questions/35987055/how-to-write-unit-testing-for-angular-2-typescript-for-private-methods-with-ja
  let rideListService: RideListService;
  let currentlyImpossibleToGenerateSearchRideUrl: string;

  // These are used to mock the HTTP requests so that we (a) don't have to
  // have the server running and (b) we can check exactly which HTTP
  // requests were made to ensure that we're making the correct requests.
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    // Set up the mock handling of the HTTP requests
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    httpClient = TestBed.get(HttpClient);
    httpTestingController = TestBed.get(HttpTestingController);
    // Construct an instance of the service with the mock
    // HTTP client.
    rideListService = new RideListService(httpClient);
  });

  afterEach(() => {
    // After every test, assert that there are no more pending requests.
    httpTestingController.verify();
  });

  it('getRides() calls api/rides', () => {
    rideListService.getRides().subscribe(
      rides => expect(rides).toBe(testRides)
    );
    const req = httpTestingController.expectOne(rideListService.baseUrl);
    expect(req.request.method).toEqual('GET');
    req.flush(testRides);
  });

  it('getRideById() calls api/rides/id', () => {
    const targetRide: Ride = testRides[1];
    const targetId: string = targetRide._id;
    rideListService.getRideById(targetId).subscribe(
      ride => expect(ride).toBe(targetRide)
    );

    const expectedUrl: string = rideListService.baseUrl + '/' + targetId;
    const req = httpTestingController.expectOne(expectedUrl);
    expect(req.request.method).toEqual('GET');
    req.flush(targetRide);
  });

  it('adding a ride calls api/rides/new', () => {
    const arlene_id = 'arlene_id';
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

    rideListService.addNewRide(newRide).subscribe(
      id => {
        expect(id).toBe(arlene_id);
      }
    );

    const expectedUrl: string = rideListService.baseUrl + '/new';
    const req = httpTestingController.expectOne(expectedUrl);
    expect(req.request.method).toEqual('POST');
    req.flush(arlene_id);
  });

});
