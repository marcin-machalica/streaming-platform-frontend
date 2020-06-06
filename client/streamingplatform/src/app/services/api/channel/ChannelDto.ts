import {UserDto} from '../keycloak-admin-api/UserDto';

export class ChannelAbout {
  public author: UserDto;
  public name: string;
  public description: string;
  public subscriptionCount: number;
  public createdDate: Date;
}

export class ChannelUpdate {
  public name: string;
  public description: string;
}

export class ChannelIdentity {
  public name: string;
}
