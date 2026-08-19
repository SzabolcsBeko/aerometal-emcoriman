-- Optional sample data. Enable with spring.sql.init.mode=always if desired.
-- =============================================================
-- Sample data for Employee / Component / Right Manager
-- =============================================================
-- INSERT IGNORE makes repeated application starts safe when the
-- unique constraints already contain these values.

INSERT IGNORE INTO employee (id, first_name, last_name, hire_date, email) VALUES
    (1, 'John','Smith', '2025-01-10', 'john.smith@example.com'),
    (2, 'Anna','Kovacs', '2025-02-10', 'anna.kovacs@example.com'),
    (3, 'Peter','Nagy', '2025-03-10', 'peter.nagy@example.com'),
    (4, 'Maria','Horvath', '2025-04-10', 'maria.horvath@example.com');

INSERT IGNORE INTO component (id, name, description) VALUES
    (1, 'AA-KK-101', 'Four cylinder propeller engine'),
    (2, 'BB-PP-305', 'Twelve cylinder airplane ignition'),
    (3, 'CC-HH-442', 'Water cooler four cylinder engine'),
    (4, 'EE-NN-642', 'Eight cylinder airplane engine');

INSERT IGNORE INTO access_right (id, name, description) VALUES
    (1, 'INS', 'Folyamközi ellenőr'),
    (2, 'CCS-E2', 'Végellenőr és tanusító'),
    (3, 'ECM', 'Motorszerelő-komponens javító'),
    (4, 'AEM', 'Segédberendezés javító'),
    (5, 'PM', 'Gyakornok');

INSERT IGNORE INTO access_assignment (employee_id, component_id, access_right_id) VALUES
    (1, 1, 1),
    (1, 1, 2),
    (2, 2, 1),
    (2, 2, 3),
    (3, 3, 1),
    (4, 4, 5);
