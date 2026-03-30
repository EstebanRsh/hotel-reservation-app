import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { Toast } from './component/toast/toast';
import { AuthService } from './service/auth.service';
import { ReservationModalService } from './service/reservation-modal.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  protected readonly auth = inject(AuthService);
  private readonly reservationModal = inject(ReservationModalService);

  protected logout(): void {
    this.auth.logout();
  }

  protected openNewReservation(): void {
    this.reservationModal.open();
  }
}
