import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AvatarCropperDialogComponent } from './avatar-cropper-dialog.component';

describe('AvatarCropperDialogComponent', () => {
  let component: AvatarCropperDialogComponent;
  let fixture: ComponentFixture<AvatarCropperDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AvatarCropperDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AvatarCropperDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
