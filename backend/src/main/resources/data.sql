-- 1. Administrador
INSERT INTO administrador (correo, contrasena)
VALUES ('admin@medicontigo.com', 'admin123');

-- 2. Centros medicos (datos de ejemplo para la zona de Lima). Hospital Lima es el ID = 1
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Hospital Lima', 'Hospital', 'Lima Centro', '(01) 555-0101', -12.046374, -77.042793, 50.00, '30 minutos', 'BAJO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Clínica San Isidro Salud', 'Clínica', 'Av. Camino Real 480, San Isidro', '(01) 555-0102', -12.0977, -77.0365, 120.00, '20 minutos', 'MEDIO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Clínica Miraflores Vida', 'Clínica', 'Av. Larco 1150, Miraflores', '(01) 555-0103', -12.1211, -77.0297, 150.00, '15 minutos', 'BAJO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Hospital Regional del Rímac', 'Hospital', 'Jr. Trujillo 350, Rímac', '(01) 555-0104', -12.029, -77.043, 35.00, '90 minutos', 'ALTO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Hospital Jesús María Norte', 'Hospital', 'Av. Brasil 2100, Jesús María', '(01) 555-0105', -12.0707, -77.0517, 45.00, '60 minutos', 'MEDIO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Posta Médica Lince', 'Posta', 'Av. Arenales 1620, Lince', '(01) 555-0106', -12.084, -77.0345, 15.00, '25 minutos', 'BAJO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Clínica La Victoria Familiar', 'Clínica', 'Av. Grau 870, La Victoria', '(01) 555-0107', -12.0654, -77.0158, 80.00, '30 minutos', 'MEDIO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Posta de Salud Breña', 'Posta', 'Av. Venezuela 1480, Breña', '(01) 555-0108', -12.056, -77.051, 10.00, '40 minutos', 'BAJO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Clínica Surquillo Integral', 'Clínica', 'Av. Angamos Este 1900, Surquillo', '(01) 555-0109', -12.111, -77.0105, 110.00, '20 minutos', 'BAJO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Hospital San Borja Sur', 'Hospital', 'Av. Javier Prado Este 2400, San Borja', '(01) 555-0110', -12.0905, -76.999, 60.00, '75 minutos', 'ALTO', 'ACTIVO');
INSERT INTO centros_medicos (nombre, tipo, direccion, telefono, latitud, longitud, costo_consulta, tiempo_espera, nivel_ocupacion, estado)
VALUES ('Posta Médica Surco Viejo', 'Posta', 'Jr. Tomás Marsano 3800, Surco', '(01) 555-0111', -12.145, -76.993, 20.00, '35 minutos', 'BAJO', 'ACTIVO');

-- Hospital Lima
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Hospital Lima';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Rosa Quispe Huamán', 'CMP-40101', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Ginecología', 'Salud de la mujer y control prenatal', TRUE FROM centros_medicos WHERE nombre = 'Hospital Lima';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Ginecología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Carmen Salas Ortiz', 'CMP-40102', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Ginecología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Traumatología', 'Lesiones de huesos, músculos y articulaciones', TRUE FROM centros_medicos WHERE nombre = 'Hospital Lima';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '09:00:00', '15:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '09:00:00', '15:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '09:00:00', '15:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Traumatología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Jorge Paredes Lino', 'CMP-40103', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Lima' AND e.nombre = 'Traumatología';

-- Clínica San Isidro Salud
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Clínica San Isidro Salud';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Andrea Vega Salazar', 'CMP-40104', e.id, c.id, TRUE, '2023-01-09' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Luis Montoya Rey', 'CMP-40105', e.id, c.id, TRUE, '2018-11-12' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Cardiología', 'Diagnóstico y tratamiento de enfermedades del corazón', TRUE FROM centros_medicos WHERE nombre = 'Clínica San Isidro Salud';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Ricardo Benavides Cruz', 'CMP-40106', e.id, c.id, TRUE, '2019-03-04' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Cardiología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Dermatología', 'Cuidado y tratamiento de la piel', TRUE FROM centros_medicos WHERE nombre = 'Clínica San Isidro Salud';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Dermatología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Valeria Núñez Pinto', 'CMP-40107', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Dermatología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Nutrición', 'Evaluación nutricional y planes de alimentación', TRUE FROM centros_medicos WHERE nombre = 'Clínica San Isidro Salud';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Nutrición';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Nutrición';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Nutrición';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Nutrición';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Nutrición';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Daniela Ferrer Lazo', 'CMP-40108', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica San Isidro Salud' AND e.nombre = 'Nutrición';

-- Clínica Miraflores Vida
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Clínica Miraflores Vida';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Patricia Lozano Cueva', 'CMP-40109', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Pediatría', 'Atención integral de niños y adolescentes', TRUE FROM centros_medicos WHERE nombre = 'Clínica Miraflores Vida';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Marcos Gutiérrez Ames', 'CMP-40110', e.id, c.id, TRUE, '2023-01-09' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Elena Rosales Díaz', 'CMP-40111', e.id, c.id, TRUE, '2018-11-12' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Pediatría';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Odontología', 'Salud bucal y tratamiento dental', TRUE FROM centros_medicos WHERE nombre = 'Clínica Miraflores Vida';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Odontología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Hugo Cárdenas Bravo', 'CMP-40112', e.id, c.id, TRUE, '2019-03-04' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Odontología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Psicología', 'Atención en salud mental y bienestar emocional', TRUE FROM centros_medicos WHERE nombre = 'Clínica Miraflores Vida';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Psicología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Sofía Mendoza Rivas', 'CMP-40113', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Psicología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Oftalmología', 'Salud visual y enfermedades de los ojos', TRUE FROM centros_medicos WHERE nombre = 'Clínica Miraflores Vida';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Oftalmología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Tomás Alarcón Vidal', 'CMP-40114', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Miraflores Vida' AND e.nombre = 'Oftalmología';

-- Hospital Regional del Rímac
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Hospital Regional del Rímac';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Víctor Huertas Palma', 'CMP-40115', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Julia Chávez Ruiz', 'CMP-40116', e.id, c.id, TRUE, '2023-01-09' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Emergencias', 'Atención inmediata de urgencias médicas', TRUE FROM centros_medicos WHERE nombre = 'Hospital Regional del Rímac';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 7, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Mario Ticona Flores', 'CMP-40117', e.id, c.id, TRUE, '2018-11-12' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Gladys Arce Mori', 'CMP-40118', e.id, c.id, TRUE, '2019-03-04' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Emergencias';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Pediatría', 'Atención integral de niños y adolescentes', TRUE FROM centros_medicos WHERE nombre = 'Hospital Regional del Rímac';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Pediatría';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Lourdes Camacho Vera', 'CMP-40119', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Pediatría';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina Interna', 'Atención de enfermedades en adultos', TRUE FROM centros_medicos WHERE nombre = 'Hospital Regional del Rímac';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Óscar Dueñas Peña', 'CMP-40120', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Medicina Interna';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Traumatología', 'Lesiones de huesos, músculos y articulaciones', TRUE FROM centros_medicos WHERE nombre = 'Hospital Regional del Rímac';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Traumatología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Raúl Sandoval Ibáñez', 'CMP-40121', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Regional del Rímac' AND e.nombre = 'Traumatología';

-- Hospital Jesús María Norte
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Hospital Jesús María Norte';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Natalia Espinoza Gil', 'CMP-40122', e.id, c.id, TRUE, '2023-01-09' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Emergencias', 'Atención inmediata de urgencias médicas', TRUE FROM centros_medicos WHERE nombre = 'Hospital Jesús María Norte';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 7, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Fernando Olivera Tapia', 'CMP-40123', e.id, c.id, TRUE, '2018-11-12' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Emergencias';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Ginecología', 'Salud de la mujer y control prenatal', TRUE FROM centros_medicos WHERE nombre = 'Hospital Jesús María Norte';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Ginecología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Ivonne Barreto Ñaupa', 'CMP-40124', e.id, c.id, TRUE, '2019-03-04' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Ginecología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Cardiología', 'Diagnóstico y tratamiento de enfermedades del corazón', TRUE FROM centros_medicos WHERE nombre = 'Hospital Jesús María Norte';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Alberto Zevallos Cano', 'CMP-40125', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital Jesús María Norte' AND e.nombre = 'Cardiología';

-- Posta Médica Lince
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Posta Médica Lince';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Karen Villanueva Soto', 'CMP-40126', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Odontología', 'Salud bucal y tratamiento dental', TRUE FROM centros_medicos WHERE nombre = 'Posta Médica Lince';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Odontología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Odontología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Eduardo Rivera Cóndor', 'CMP-40127', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Lince' AND e.nombre = 'Odontología';

-- Clínica La Victoria Familiar
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Clínica La Victoria Familiar';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Pilar Aguirre Neyra', 'CMP-40128', e.id, c.id, TRUE, '2023-01-09' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Pediatría', 'Atención integral de niños y adolescentes', TRUE FROM centros_medicos WHERE nombre = 'Clínica La Victoria Familiar';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Cristian Meza Lara', 'CMP-40129', e.id, c.id, TRUE, '2018-11-12' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Pediatría';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Ginecología', 'Salud de la mujer y control prenatal', TRUE FROM centros_medicos WHERE nombre = 'Clínica La Victoria Familiar';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Ginecología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Ginecología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Rocío Valdivia Cori', 'CMP-40130', e.id, c.id, TRUE, '2019-03-04' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Ginecología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Dermatología', 'Cuidado y tratamiento de la piel', TRUE FROM centros_medicos WHERE nombre = 'Clínica La Victoria Familiar';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Dermatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '09:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Dermatología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Miguel Arana Poma', 'CMP-40131', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica La Victoria Familiar' AND e.nombre = 'Dermatología';

-- Posta de Salud Breña
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Posta de Salud Breña';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Silvia Tello Mamani', 'CMP-40132', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Nutrición', 'Evaluación nutricional y planes de alimentación', TRUE FROM centros_medicos WHERE nombre = 'Posta de Salud Breña';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Nutrición';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Nutrición';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Paola Cornejo Reyes', 'CMP-40133', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta de Salud Breña' AND e.nombre = 'Nutrición';

-- Clínica Surquillo Integral
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Clínica Surquillo Integral';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Gonzalo Ríos Maldonado', 'CMP-40134', e.id, c.id, TRUE, '2023-01-09' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Cardiología', 'Diagnóstico y tratamiento de enfermedades del corazón', TRUE FROM centros_medicos WHERE nombre = 'Clínica Surquillo Integral';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Cardiología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Cardiología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Beatriz Llanos Vargas', 'CMP-40135', e.id, c.id, TRUE, '2018-11-12' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Cardiología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Oftalmología', 'Salud visual y enfermedades de los ojos', TRUE FROM centros_medicos WHERE nombre = 'Clínica Surquillo Integral';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Alonso Quiroz Ledesma', 'CMP-40136', e.id, c.id, TRUE, '2019-03-04' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Oftalmología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Psicología', 'Atención en salud mental y bienestar emocional', TRUE FROM centros_medicos WHERE nombre = 'Clínica Surquillo Integral';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '09:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Psicología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '09:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Psicología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Mariana Ugarte Bellido', 'CMP-40137', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Clínica Surquillo Integral' AND e.nombre = 'Psicología';

-- Hospital San Borja Sur
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Hospital San Borja Sur';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '20:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Teresa Cabrera Ayala', 'CMP-40138', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Emergencias', 'Atención inmediata de urgencias médicas', TRUE FROM centros_medicos WHERE nombre = 'Hospital San Borja Sur';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 7, '00:00:00', '23:59:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Walter Condori Huanca', 'CMP-40139', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Diana Palomino Rey', 'CMP-40140', e.id, c.id, TRUE, '2023-01-09' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Emergencias';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina Interna', 'Atención de enfermedades en adultos', TRUE FROM centros_medicos WHERE nombre = 'Hospital San Borja Sur';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '14:00:00', '18:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Javier Orrego Salinas', 'CMP-40141', e.id, c.id, TRUE, '2018-11-12' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Medicina Interna';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Traumatología', 'Lesiones de huesos, músculos y articulaciones', TRUE FROM centros_medicos WHERE nombre = 'Hospital San Borja Sur';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Traumatología';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Traumatología';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Lucía Fonseca Delgado', 'CMP-40142', e.id, c.id, TRUE, '2019-03-04' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Traumatología';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Pediatría', 'Atención integral de niños y adolescentes', TRUE FROM centros_medicos WHERE nombre = 'Hospital San Borja Sur';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '08:00:00', '14:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Pediatría';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Armando Peralta Cuba', 'CMP-40143', e.id, c.id, TRUE, '2020-08-17' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Hospital San Borja Sur' AND e.nombre = 'Pediatría';

-- Posta Médica Surco Viejo
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Medicina General', 'Atención médica primaria y preventiva', TRUE FROM centros_medicos WHERE nombre = 'Posta Médica Surco Viejo';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 1, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 3, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 5, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Medicina General';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '07:00:00', '13:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Medicina General';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Ximena Bustamante Roca', 'CMP-40144', e.id, c.id, TRUE, '2021-02-01' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Medicina General';
INSERT INTO especialidad (centro_id, nombre, descripcion, estado)
SELECT id, 'Pediatría', 'Atención integral de niños y adolescentes', TRUE FROM centros_medicos WHERE nombre = 'Posta Médica Surco Viejo';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 2, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 4, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Pediatría';
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin)
SELECT e.id, 6, '08:00:00', '12:00:00' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Pediatría';
INSERT INTO medicos (nombre_completo, cmp, especialidad_id, centro_id, activo, fecha_ingreso)
SELECT 'Daniel Aliaga Torres', 'CMP-40145', e.id, c.id, TRUE, '2022-05-23' FROM especialidad e JOIN centros_medicos c ON c.id = e.centro_id
WHERE c.nombre = 'Posta Médica Surco Viejo' AND e.nombre = 'Pediatría';
