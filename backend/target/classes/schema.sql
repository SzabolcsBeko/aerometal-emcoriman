-- =============================================================
-- Employee / Component / Access Right Manager - MySQL schema
-- =============================================================
-- Spring Boot connects to the access_manager database configured
-- in application.yml. The JDBC URL creates the database if needed.

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    hire_date DATE,
    PRIMARY KEY (id),
    CONSTRAINT uk_employee_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS component (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_component_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- RIGHT is an SQL keyword, therefore the entity uses app_right.
CREATE TABLE IF NOT EXISTS access_right (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_app_right_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS access_assignment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    component_id BIGINT NOT NULL,
    access_right_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_assignment_employee_component_right
        UNIQUE (employee_id, component_id, access_right_id),
    CONSTRAINT fk_assignment_employee
        FOREIGN KEY (employee_id) REFERENCES employee (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_assignment_component
        FOREIGN KEY (component_id) REFERENCES component (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_assignment_right
        FOREIGN KEY (access_right_id) REFERENCES access_right (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

