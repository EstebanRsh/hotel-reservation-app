import { HttpErrorResponse } from '@angular/common/http';

const KNOWN_BACKEND_MESSAGES: Record<string, string> = {
  'Another active reservation already exists for the same date and time':
    'Ya existe otra reserva activa para la misma fecha y hora.',
  'Reservation is already cancelled': 'La reserva ya está cancelada.',
  'Validation failed': 'Los datos enviados no son válidos. Revisa el formulario.',
  'Credenciales incorrectas': 'Usuario o contraseña incorrectos.',
  'No autenticado. Inicie sesión.': 'Sesión expirada. Por favor, vuelva a iniciar sesión.',
};

function translateBackendMessage(message: string): string {
  const direct = KNOWN_BACKEND_MESSAGES[message];
  if (direct) {
    return direct;
  }
  const notFoundPrefix = 'Reservation not found with id: ';
  if (message.startsWith(notFoundPrefix)) {
    const id = message.slice(notFoundPrefix.length).trim();
    return `No se encontró la reserva con id ${id}.`;
  }
  return message;
}

export function httpErrorMessage(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    const body = err.error as { message?: string } | null;
    if (body && typeof body.message === 'string') {
      return translateBackendMessage(body.message);
    }
    return (
      err.message || `No se pudo completar la solicitud (código ${err.status}).`
    );
  }
  return 'Ha ocurrido un error inesperado.';
}
