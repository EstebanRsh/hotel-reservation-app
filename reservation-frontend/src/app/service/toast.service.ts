import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  readonly message = signal<string | null>(null);

  private hideId: ReturnType<typeof setTimeout> | undefined;

  showError(text: string): void {
    if (this.hideId !== undefined) {
      clearTimeout(this.hideId);
    }
    this.message.set(text);
    this.hideId = setTimeout(() => this.dismiss(), 6000);
  }

  dismiss(): void {
    if (this.hideId !== undefined) {
      clearTimeout(this.hideId);
      this.hideId = undefined;
    }
    this.message.set(null);
  }
}
