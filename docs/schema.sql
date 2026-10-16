-- Crear la base de datos
CREATE DATABASE Cotizador_3DPrinter;
GO

-- Crear login y usuario de la aplicación
-- Reemplaza <TU_PASSWORD> por la contraseña que uses en la variable DB_PASSWORD
CREATE LOGIN cotizador3dApp WITH PASSWORD = '<TU_PASSWORD>', CHECK_POLICY = OFF;
GO

USE Cotizador_3DPrinter;
GO

CREATE USER cotizador3dApp FOR LOGIN cotizador3dApp;
ALTER ROLE db_owner ADD MEMBER cotizador3dApp;
GO

CREATE TABLE Filament (
    id BIGINT IDENTITY(1,1) NOT NULL,
    material_type VARCHAR(100) NOT NULL,
    color VARCHAR(50) NOT NULL,
    price_per_kg DECIMAL(10,4) NOT NULL,
    filament_settings_id VARCHAR(20) NOT NULL,
    available BIT NOT NULL,
    CONSTRAINT Filament_pk PRIMARY KEY (id)
);
GO

CREATE TABLE Quote (
    id BIGINT IDENTITY(1,1) NOT NULL,
    created_at DATETIME2 NOT NULL,
    stl_file VARBINARY(MAX) NOT NULL,
    filament_id BIGINT NOT NULL,
    layer_profile VARCHAR(30) NOT NULL,
    infill_percentage INT NOT NULL,
    piece_quantity INT NOT NULL,
    supports_needed BIT NOT NULL,
    [status] VARCHAR(20) NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    client_email VARCHAR(100) NOT NULL,
    weight_grams FLOAT NULL,
    time_seconds FLOAT NULL,
    total_price DECIMAL(10,2) NULL,
    CONSTRAINT Quote_pk PRIMARY KEY (id),
    CONSTRAINT FK_Quote_Filament FOREIGN KEY (filament_id) REFERENCES Filament(id)
);
GO