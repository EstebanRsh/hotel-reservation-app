import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ToastService } from '../../service/toast.service';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.html',
  styleUrl: './toast.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Toast {
  protected readonly toast = inject(ToastService);

  protected dismiss(): void {
    this.toast.dismiss();
  }
}
