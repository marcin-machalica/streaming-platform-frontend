import { Injectable } from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  constructor(private snackBar: MatSnackBar) { }

  showToast(message: string, durationInMillis = 2000) {
    this.snackBar.open(message, null, {
      duration: durationInMillis,
    });
  }
}
