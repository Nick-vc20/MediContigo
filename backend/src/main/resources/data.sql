-- 1. Insertar Administrador
INSERT INTO administrador (correo, contrasena) 
VALUES ('admin@medicontigo.com', 'admin123');

-- 2. Insertar Centro Médico (ID = 1)
INSERT INTO centros_medicos (nombre, tipo, direccion, latitud, longitud, costo_consulta, tiempo_espera, estado) 
VALUES ('Hospital Lima', 'Hospital', 'Lima Centro', -12.046374, -77.042793, 50.00, '30 minutos', 'ACTIVO');

-- 3. Insertar Especialidad asociada directamente al centro_id = 1 (ID asignado = 1)
INSERT INTO especialidad (centro_id, nombre, descripcion, estado) 
VALUES (1, 'Medicina General', 'Atención médica primaria y preventiva', TRUE);

-- 4. Insertar Horario para la Especialidad (ID especialidad = 1)
INSERT INTO horario_especialidad (especialidad_id, dia_semana, hora_inicio, hora_fin) 
VALUES (1, 1, '08:00:00', '18:00:00');