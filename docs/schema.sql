IF DB_ID('Cotizador_3DPrinter') IS NULL
    CREATE DATABASE Cotizador_3DPrinter;
GO

IF NOT EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = 'cotizador3dApp')
    CREATE LOGIN cotizador3dApp WITH PASSWORD = '$(APP_PASSWORD)', CHECK_POLICY = OFF;
ELSE
    ALTER LOGIN cotizador3dApp WITH PASSWORD = '$(APP_PASSWORD)', CHECK_POLICY = OFF;
GO

USE Cotizador_3DPrinter;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'cotizador3dApp')
    CREATE USER cotizador3dApp FOR LOGIN cotizador3dApp;
ALTER ROLE db_owner ADD MEMBER cotizador3dApp;
GO

IF OBJECT_ID('dbo.Filament', 'U') IS NULL
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

IF OBJECT_ID('dbo.Quote', 'U') IS NULL
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