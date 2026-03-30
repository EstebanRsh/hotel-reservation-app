export enum ReservationStatus {
  ACTIVE = 'ACTIVE',
  CANCELLED = 'CANCELLED',
}

/** Request body aligned with the backend (LocalDate / LocalTime serialized as strings). */
export interface CreateReservation {
  customerName: string;
  date: string;
  time: string;
  service: string;
}

export interface Reservation {
  id: number;
  customerName: string;
  /** yyyy-MM-dd */
  date: string;
  /** HH:mm:ss */
  time: string;
  service: string;
  status: ReservationStatus;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
