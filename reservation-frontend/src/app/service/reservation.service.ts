import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CreateReservation, PageResponse, Reservation } from '../model/reservation.model';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/reservations`;

  getAll(search = '', page = 0, size = 10): Observable<PageResponse<Reservation>> {
    const params: Record<string, string | number> = { page, size };
    if (search.trim()) {
      params['search'] = search.trim();
    }
    return this.http.get<PageResponse<Reservation>>(this.baseUrl, { params });
  }

  create(payload: CreateReservation): Observable<Reservation> {
    return this.http.post<Reservation>(this.baseUrl, payload);
  }

  cancel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
