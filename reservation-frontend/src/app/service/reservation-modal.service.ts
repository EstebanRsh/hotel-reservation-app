import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReservationModalService {
  readonly open$ = new Subject<void>();

  open(): void {
    this.open$.next();
  }
}
