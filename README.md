# Hotel Colonial — Sistema de Reservas

> Aplicacion full-stack para la gestion interna de reservas hoteleras. El personal de recepcion puede registrar, consultar y cancelar reservas desde una interfaz web protegida por autenticacion JWT.

---

## Tecnologias

**Backend**

![Java](https://img.shields.io/badge/Java_25-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4.0-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_18-4169E1?style=flat&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)

**Frontend**

![Angular](https://img.shields.io/badge/Angular_21-DD0031?style=flat&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript_5.9-3178C6?style=flat&logo=typescript&logoColor=white)
![RxJS](https://img.shields.io/badge/RxJS_7.8-B7178C?style=flat&logo=reactivex&logoColor=white)

---

## Funcionalidades

- Autenticacion con usuario y contrasena — genera un token JWT valido por 8 horas
- Listado de todas las reservas con filtro por estado (activa / cancelada)
- Alta de nuevas reservas con validacion de conflicto de horario
- Cancelacion de reservas activas
- Cierre de sesion automatico al expirar el token
- Documentacion interactiva de la API via Swagger UI

---

## Arquitectura

```
reservation-frontend/        Angular 21 (standalone components)
│   auth interceptor          adjunta el token en cada peticion
│   auth guard                protege las rutas privadas
│   toast service             notificaciones de feedback
│
reservation-backend/         Spring Boot 4 (REST API)
│   AuthController            POST /auth/login
│   ReservationController     GET / POST / DELETE /reservations
│   JwtAuthenticationFilter   valida el token en cada request
│   ReservationService        regla de negocio: sin solapamiento de horarios
│
PostgreSQL                   base de datos relacional
    users                    credenciales y roles
    reservations             registros de reservas
```

---

## Requisitos previos

- Java 25
- Node.js 20 o superior
- PostgreSQL 18 corriendo localmente

---

## Instalacion y ejecucion

### 1. Base de datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE reservation;
```

Las tablas se crean automaticamente cuando el backend arranca por primera vez (Hibernate DDL `update`).

Para cargar datos de prueba:

```bash
psql -U postgres -d reservation -f seed.sql
```

### 2. Backend

```bash
cd reservation-backend
./mvnw spring-boot:run
```

El servidor queda disponible en `http://localhost:8081`.

Al iniciar se crea automaticamente el usuario por defecto:

| Campo    | Valor          |
|----------|----------------|
| Usuario  | `recepcion`    |
| Contrasena | `recepcion123` |
| Rol      | `RECEPTIONIST` |

### 3. Frontend

```bash
cd reservation-frontend
npm install
npm start
```

La aplicacion queda disponible en `http://localhost:4200`.

---

## Variables de entorno

**Backend** — `reservation-backend/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reservation
spring.datasource.username=postgres
spring.datasource.password=1234
jwt.secret=HotelAlamedaRealJwtSecretKey2026!!Seguro
jwt.expiration-ms=28800000
```

> Ajusta `spring.datasource.password` y `jwt.secret` con tus propios valores antes de usar en produccion.

**Frontend** — `reservation-frontend/src/environments/environment.ts`

```typescript
export const environment = {
  apiUrl: 'http://localhost:8081',
} as const;
```

---

## API

La documentacion completa con todos los endpoints, parametros y esquemas esta disponible en Swagger UI una vez que el backend este corriendo:

```
http://localhost:8081/swagger-ui.html
```

Resumen de endpoints:

| Metodo | Ruta                    | Descripcion                  | Auth requerida |
|--------|-------------------------|------------------------------|----------------|
| POST   | `/auth/login`           | Obtener token JWT            | No             |
| GET    | `/reservations`         | Listar todas las reservas    | Si             |
| POST   | `/reservations`         | Crear nueva reserva          | Si             |
| DELETE | `/reservations/{id}`    | Cancelar reserva             | Si             |

---

## Servicios disponibles

Los servicios que se pueden reservar:

- Habitacion Deluxe con balcon
- Suite Junior — sala de estar
- Suite Presidencial
- Desayuno buffet en patio colonial
- Cena maridaje — restaurante gourmet
- Spa, masajes y circuito termal
- Traslado aeropuerto — vehiculo premium
- Conserjeria 24 h y mayordomo de piso

---

## Demo en video

<!-- Sube el video a un Issue del repo y pega aqui el link generado por GitHub -->
<!-- Ejemplo: https://github.com/tu-usuario/tu-repo/assets/12345678/demo.mp4 -->

> Para agregar el video: abre un Issue en el repositorio, arrastra el archivo `.mp4` al campo de texto, copia el link que genera GitHub y reemplaza este bloque.

---

## Licencia
 
 MIT