
CREATE TABLE IF NOT EXISTS administrador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(255) NOT NULL,
    contrasena VARCHAR(255) NOT NULL
);
CREATE TABLE centros_medicos (
    id SERIAL PRIMARY KEY, -- o INT AUTO_INCREMENT si usas H2/MySQL
    nombre VARCHAR(150) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    latitud DECIMAL(10, 8) NOT NULL,
    longitud DECIMAL(11, 8) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS especialidad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    centro_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_esp_centro FOREIGN KEY (centro_id) REFERENCES centros_medicos(id),
    CONSTRAINT uq_esp_centro_nombre UNIQUE (centro_id, nombre)
);

CREATE TABLE IF NOT EXISTS horario_especialidad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    especialidad_id INT NOT NULL,
    dia_semana INT NOT NULL,          -- 1 = lunes ... 7 = domingo
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    CONSTRAINT fk_hor_esp FOREIGN KEY (especialidad_id) REFERENCES especialidad(id),
    CONSTRAINT chk_dia CHECK (dia_semana BETWEEN 1 AND 7),
    CONSTRAINT chk_horas CHECK (hora_fin > hora_inicio)
);

CREATE TABLE IF NOT EXISTS medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    cmp VARCHAR(30) NOT NULL UNIQUE,
    especialidad_id INT NOT NULL,
    centro_id INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_ingreso DATE NOT NULL,

    FOREIGN KEY (especialidad_id) REFERENCES especialidad(id),
    FOREIGN KEY (centro_id) REFERENCES centros_medicos(id)
    );