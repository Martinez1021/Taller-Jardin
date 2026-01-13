-- Script de inicialización de la base de datos
-- Se ejecuta automáticamente cuando se crea el contenedor por primera vez

USE control_presencia;

-- Crear usuario administrador de prueba
INSERT INTO usuario (username, password, rol, activo) VALUES
('admin', '$2a$10$8K1p/H8lKOKWMmXZlVKdlO3v2eFc2EJHwYzgJxNrj3Q4WX9XmY3Hm', 'ADMIN', true);
-- Password: admin123

-- Crear departamentos de ejemplo
INSERT INTO departamento (nombre, descripcion) VALUES
('Desarrollo', 'Departamento de desarrollo de software'),
('Recursos Humanos', 'Gestión de personal'),
('Ventas', 'Departamento comercial'),
('Administración', 'Gestión administrativa');

-- Crear horarios de ejemplo
INSERT INTO horario (nombre, hora_entrada, hora_salida, dias_semana) VALUES
('Turno Mañana', '08:00:00', '16:00:00', 'L,M,X,J,V'),
('Turno Tarde', '14:00:00', '22:00:00', 'L,M,X,J,V'),
('Turno Completo', '09:00:00', '18:00:00', 'L,M,X,J,V');

-- Crear trabajadores de ejemplo
INSERT INTO trabajador (nombre, apellidos, email, numero_tarjeta, pin, departamento_id, horario_id, usuario_id, activo) VALUES
('Juan', 'García Martínez', 'juan.garcia@empresa.com', '1001', '1234', 1, 1, 1, true),
('María', 'López Sánchez', 'maria.lopez@empresa.com', '1002', '2345', 2, 1, NULL, true),
('Pedro', 'Rodríguez Pérez', 'pedro.rodriguez@empresa.com', '1003', '3456', 1, 3, NULL, true),
('Ana', 'Fernández Gómez', 'ana.fernandez@empresa.com', '1004', '4567', 3, 1, NULL, true),
('Luis', 'Martín Ruiz', 'luis.martin@empresa.com', '1005', '5678', 1, 1, NULL, true);

-- Crear fichajes de ejemplo (últimos 7 días)
INSERT INTO fichaje (trabajador_id, fecha, tipo_fichaje, hora_entrada, hora_salida, clima, temperatura) VALUES
-- Hoy
(1, CURDATE(), 'ENTRADA', CONCAT(CURDATE(), ' 08:15:00'), NULL, 'Soleado', 18.5),
(3, CURDATE(), 'ENTRADA', CONCAT(CURDATE(), ' 09:05:00'), NULL, 'Nublado', 16.2),
-- Ayer
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'ENTRADA', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 08:10:00'), NULL, 'Parcialmente nublado', 17.8),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'SALIDA', NULL, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 17:05:00'), NULL, NULL),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'ENTRADA', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 08:20:00'), NULL, 'Soleado', 19.5),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'SALIDA', NULL, CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 16:55:00'), NULL, NULL);

-- Crear incidencias de ejemplo
INSERT INTO incidencia (trabajador_id, fecha, tipo, descripcion, estado, fecha_resolucion, comentario_resolucion) VALUES
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'RETRASO', 'Retraso de 30 minutos por tráfico', 'RESUELTA', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Justificado'),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'FALTA', 'Ausencia no justificada', 'PENDIENTE', NULL, NULL);

-- Confirmar datos insertados
SELECT 'Base de datos inicializada correctamente' AS status;
SELECT COUNT(*) AS total_usuarios FROM usuario;
SELECT COUNT(*) AS total_trabajadores FROM trabajador;
SELECT COUNT(*) AS total_departamentos FROM departamento;
SELECT COUNT(*) AS total_horarios FROM horario;
SELECT COUNT(*) AS total_fichajes FROM fichaje;
SELECT COUNT(*) AS total_incidencias FROM incidencia;

