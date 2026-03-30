import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Output,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, take } from 'rxjs';

import { CreateReservation } from '../../model/reservation.model';
import { HOTEL_SERVICE_OPTIONS } from '../../model/hotel-services';
import { ReservationService } from '../../service/reservation.service';
import { ToastService } from '../../service/toast.service';
import { httpErrorMessage } from '../../shared/http-error-message';

function normalizeTimeForApi(hora: string): string {
  if (hora.length === 5) {
    return `${hora}:00`;
  }
  return hora;
}

@Component({
  selector: 'app-reservation-create',
  templateUrl: './reservation-create.html',
  styleUrl: './reservation-create.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule],
})
export class ReservationCreate {
  private readonly fb = inject(FormBuilder);
  private readonly reservationService = inject(ReservationService);
  private readonly toast = inject(ToastService);

  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  protected readonly submitting = signal(false);
  protected readonly serviceOptions = HOTEL_SERVICE_OPTIONS;

  readonly form = this.fb.nonNullable.group({
    nombreCliente: ['', [Validators.required, Validators.maxLength(100)]],
    fecha: ['', Validators.required],
    hora: ['', Validators.required],
    servicio: ['', Validators.required],
  });

  protected submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.submitting()) {
      return;
    }

    const v = this.form.getRawValue();
    const payload: CreateReservation = {
      customerName: v.nombreCliente.trim(),
      date: v.fecha,
      time: normalizeTimeForApi(v.hora),
      service: v.servicio,
    };

    this.submitting.set(true);
    this.reservationService
      .create(payload)
      .pipe(
        take(1),
        finalize(() => this.submitting.set(false)),
      )
      .subscribe({
        next: () => this.saved.emit(),
        error: (err) => this.toast.showError(httpErrorMessage(err)),
      });
  }
}
