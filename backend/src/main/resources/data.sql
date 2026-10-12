
INSERT INTO administrador (correo, contrasena) VALUES ('admin@medicontigo.com', 'admin123');

INSERT INTO centros_medicos
(nombre, tipo, direccion, latitud, longitud)
VALUES
    ('Hospital Lima', 'Hospital', 'Lima', -12.046374, -77.042793);

INSERT INTO especialidad (nombre, estado, centro_id) VALUES ('Medicina General', TRUE, 1);