-- ============================================================
--  SEED completo — Hotel Alameda Real
--
--  REQUISITO: el backend debe haber arrancado al menos una vez
--  para que Hibernate cree las tablas (users + reservations).
--
--  Ejecutar desde PowerShell:
--  & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d reservation -f "C:\Users\Esteban\Desktop\FullStackJavaAngular\seed.sql"
-- ============================================================

-- pgcrypto habilita BCrypt nativo en Postgres,
-- idéntico al BCryptPasswordEncoder de Spring Security.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ── Limpiar ────────────────────────────────────────────────
TRUNCATE TABLE reservations RESTART IDENTITY;
TRUNCATE TABLE users        RESTART IDENTITY;

-- ── Usuario ────────────────────────────────────────────────
--   recepcion / recepcion123

INSERT INTO users (username, password_hash, role) VALUES
('recepcion', crypt('recepcion123', gen_salt('bf', 10)), 'RECEPTIONIST');

-- ── Reservas ───────────────────────────────────────────────
-- Servicios válidos (exactamente como en hotel-services.ts):
--   'Habitación Deluxe con balcón'
--   'Suite Junior — sala de estar'
--   'Suite Presidencial'
--   'Desayuno buffet en patio colonial'
--   'Cena maridaje — restaurante gourmet'
--   'Spa, masajes y circuito termal'
--   'Traslado aeropuerto — vehículo premium'
--   'Conserjería 24 h y mayordomo de piso'
--
-- Estado: 'ACTIVE' | 'CANCELLED'
-- Regla: no puede haber dos ACTIVE con la misma fecha+hora.

INSERT INTO reservations (customer_name, date, time, service, status) VALUES

-- Semana del 20 mar
('Valentina Herrera',   '2026-03-20', '09:00:00', 'Desayuno buffet en patio colonial',       'ACTIVE'),
('Martín Rodríguez',    '2026-03-20', '14:00:00', 'Spa, masajes y circuito termal',          'ACTIVE'),
('Lucía Fernández',     '2026-03-21', '11:00:00', 'Suite Junior — sala de estar',            'CANCELLED'),
('Gonzalo Pereyra',     '2026-03-21', '20:00:00', 'Cena maridaje — restaurante gourmet',     'ACTIVE'),
('Sofía Morales',       '2026-03-22', '08:00:00', 'Traslado aeropuerto — vehículo premium',  'ACTIVE'),
('Nicolás Aguirre',     '2026-03-22', '15:00:00', 'Habitación Deluxe con balcón',            'CANCELLED'),
('Camila Ríos',         '2026-03-23', '10:00:00', 'Conserjería 24 h y mayordomo de piso',    'ACTIVE'),
('Diego Salinas',       '2026-03-24', '13:00:00', 'Suite Presidencial',                      'ACTIVE'),

-- Semana del 25 mar
('Florencia Giménez',   '2026-03-25', '09:00:00', 'Desayuno buffet en patio colonial',       'ACTIVE'),
('Sebastián Torres',    '2026-03-25', '19:00:00', 'Cena maridaje — restaurante gourmet',     'ACTIVE'),
('Romina Acosta',       '2026-03-26', '11:00:00', 'Habitación Deluxe con balcón',            'ACTIVE'),
('Andrés Villalba',     '2026-03-26', '16:00:00', 'Spa, masajes y circuito termal',          'CANCELLED'),
('Micaela Benítez',     '2026-03-27', '08:00:00', 'Traslado aeropuerto — vehículo premium',  'ACTIVE'),
('Lucas Montoya',       '2026-03-27', '14:00:00', 'Suite Junior — sala de estar',            'ACTIVE'),
('Paula Vega',          '2026-03-28', '10:00:00', 'Conserjería 24 h y mayordomo de piso',    'ACTIVE'),
('Joaquín Medina',      '2026-03-28', '20:00:00', 'Suite Presidencial',                      'ACTIVE'),

-- Semana del 1 abr
('Antonella Castro',    '2026-04-01', '09:00:00', 'Desayuno buffet en patio colonial',       'ACTIVE'),
('Hernán Soria',        '2026-04-01', '15:00:00', 'Habitación Deluxe con balcón',            'ACTIVE'),
('Valeria Gutiérrez',   '2026-04-02', '11:00:00', 'Spa, masajes y circuito termal',          'ACTIVE'),
('Ezequiel Navarro',    '2026-04-02', '19:00:00', 'Cena maridaje — restaurante gourmet',     'ACTIVE'),
('Natalia Suárez',      '2026-04-03', '08:00:00', 'Traslado aeropuerto — vehículo premium',  'ACTIVE'),
('Ramiro Ibáñez',       '2026-04-03', '14:00:00', 'Suite Junior — sala de estar',            'ACTIVE'),
('Agustina Ruiz',       '2026-04-04', '10:00:00', 'Suite Presidencial',                      'ACTIVE'),
('Federico López',      '2026-04-05', '16:00:00', 'Conserjería 24 h y mayordomo de piso',    'ACTIVE');

-- ── Verificar ──────────────────────────────────────────────
SELECT id, username, role FROM users;

SELECT count(*) AS total_reservas,
       sum(CASE WHEN status = 'ACTIVE'    THEN 1 ELSE 0 END) AS activas,
       sum(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS canceladas
FROM reservations;
