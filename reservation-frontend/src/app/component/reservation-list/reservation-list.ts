import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { finalize, take } from 'rxjs';

import { PageResponse, Reservation, ReservationStatus } from '../../model/reservation.model';
import { ReservationModalService } from '../../service/reservation-modal.service';
import { ReservationService } from '../../service/reservation.service';
import { httpErrorMessage } from '../../shared/http-error-message';
import { ReservationCreate } from '../reservation-create/reservation-create';

@Component({
  selector: 'app-reservation-list',
  templateUrl: './reservation-list.html',
  styleUrl: './reservation-list.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReservationCreate, FormsModule],
})
export class ReservationList {
  private readonly reservationService = inject(ReservationService);
  private readonly reservationModal = inject(ReservationModalService);

  protected readonly loading = signal(false);
  protected readonly loadError = signal<string | null>(null);
  protected readonly cancellingId = signal<number | null>(null);
  protected readonly actionError = signal<string | null>(null);
  protected readonly showModal = signal(false);

  protected search = '';
  protected readonly page = signal<PageResponse<Reservation>>({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  });

  protected readonly status = ReservationStatus;

  constructor() {
    this.loadReservations();
    this.reservationModal.open$
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.openModal());
  }

  protected loadReservations(pageIndex = 0): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.reservationService
      .getAll(this.search, pageIndex, this.page().size)
      .pipe(
        take(1),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (res) => this.page.set(res),
        error: (err) => this.loadError.set(httpErrorMessage(err)),
      });
  }

  protected onSearch(): void {
    this.loadReservations(0);
  }

  protected goToPage(index: number): void {
    this.loadReservations(index);
  }

  protected cancelReservation(id: number): void {
    this.actionError.set(null);
    this.cancellingId.set(id);
    this.reservationService
      .cancel(id)
      .pipe(
        take(1),
        finalize(() => this.cancellingId.set(null)),
      )
      .subscribe({
        next: () => this.loadReservations(this.page().page),
        error: (err) => this.actionError.set(httpErrorMessage(err)),
      });
  }

  protected openModal(): void {
    this.showModal.set(true);
  }
  protected closeModal(): void {
    this.showModal.set(false);
  }
  protected onReservationSaved(): void {
    this.showModal.set(false);
    this.loadReservations(0);
  }

  protected statusLabel(value: ReservationStatus): string {
    return value === ReservationStatus.ACTIVE ? 'Activa' : 'Cancelada';
  }

  protected get pages(): number[] {
    return Array.from({ length: this.page().totalPages }, (_, i) => i);
  }
}
