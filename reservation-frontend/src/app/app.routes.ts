import { Routes } from '@angular/router';

import { authGuard } from './guard/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'reservations' },
  {
    path: 'login',
    loadComponent: () => import('./component/login/login').then((m) => m.Login),
  },
  {
    path: 'reservations',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./component/reservation-list/reservation-list').then((m) => m.ReservationList),
  },
];
