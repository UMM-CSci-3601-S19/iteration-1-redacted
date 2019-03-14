
export interface Ride {
  _id: string;
  driver: string;
  riders: string[];
  route: string[];
  roundTrip: boolean;
  dateTime: string;
  notes: string;
}
