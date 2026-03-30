import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, take } from 'rxjs';

import { AuthService } from '../../service/auth.service';
import { ToastService } from '../../service/toast.service';
import { httpErrorMessage } from '../../shared/http-error-message';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrl: './login.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule],
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  protected readonly submitting = signal(false);

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor() {
    // Si ya está autenticado, redirigir directamente
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/reservations']);
    }
  }

  protected submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.submitting()) {
      return;
    }

    const { username, password } = this.form.getRawValue();

    this.submitting.set(true);
    this.auth
      .login({ username: username.trim(), password })
      .pipe(
        take(1),
        finalize(() => this.submitting.set(false)),
      )
      .subscribe({
        next: () => this.router.navigate(['/reservations']),
        error: (err) => this.toast.showError(httpErrorMessage(err)),
      });
  }
}
