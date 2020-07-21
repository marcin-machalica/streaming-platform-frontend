import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {CropperSettings, ImageCropperComponent} from 'ng2-img-cropper';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
  selector: 'app-avatar-cropper-dialog',
  templateUrl: './avatar-cropper-dialog.component.html',
  styleUrls: ['./avatar-cropper-dialog.component.sass']
})
export class AvatarCropperDialogComponent implements OnInit {

  file: File;
  image = new Image();

  imageWrapper: any = {};
  cropperSettings: CropperSettings;

  @ViewChild('cropper', undefined)
  cropper: ImageCropperComponent;

  constructor(private dialogRef: MatDialogRef<AvatarCropperDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.cropperSettings = new CropperSettings();
    this.cropperSettings.noFileInput = true;
    this.cropperSettings.canvasWidth = 550;
    this.cropperSettings.canvasHeight = 400;
    this.cropperSettings.croppedWidth = 100;
    this.cropperSettings.croppedHeight = 100;

    this.file = this.data.file;
    if (!!this.file) {
      this.updateImage();
    }
  }

  selectFile(event) {
    this.file = event.target.files[0];
    this.updateImage();
  }

  updateImage() {
    const reader = new FileReader();
    const that = this;
    reader.onloadend = function (loadEvent: any) {
      that.image.src = loadEvent.target.result;
      that.cropper.setImage(that.image);
    };
    reader.readAsDataURL(this.file);
  }

  confirmAvatar() {
    const base64 = Object.values(this.cropper.image)[1];
    const blob = this.base64toBlob(base64);
    const rawAndCroppedImage = new RawAndCroppedImage(this.file, blob);
    this.dialogRef.close(rawAndCroppedImage);
  }

  base64toBlob(base64) {
    const binary = atob(base64.split(',')[1]);
    const array = [];
    for (let i = 0; i < binary.length; i++) {
      array.push(binary.charCodeAt(i));
    }
    return new Blob([new Uint8Array(array)], {
      type: 'image/png'
    });
  }
}

export class RawAndCroppedImage {
  constructor(public raw?: File, public cropped?: Blob) {
  }
}
