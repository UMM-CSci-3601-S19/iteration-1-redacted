import {User} from "../users/user";

export interface Ride {
  driver: object[];
  riders: User[];
  destination: string;
  origin: string;
  roundTrip: boolean;
  //depatureTime: ;
  driving: boolean;
  notes: string;
}
