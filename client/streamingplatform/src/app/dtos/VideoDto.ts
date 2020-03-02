import {UserDto} from './UserDto';

export class VideoDto {
  public id: number;
  public author: UserDto;
  public title: string;
  public description: string;
  public createdDate: Date;
}
