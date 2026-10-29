# ==========================================
# ETAPA 1: El Taller de Construcción (Build)
# ==========================================

# 1. Partimos de una imagen que tiene Java 25 y las herramientas de desarrollo (JDK)
FROM eclipse-temurin:25-jdk AS build

# 2. Nos metemos a una carpeta dentro del contenedor llamada /app
WORKDIR /app

# 3. Copiamos los archivos de configuración de Maven desde tu compu al contenedor
COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./
RUN chmod +x mvnw

# 4. Descargamos las dependencias de Maven para ir adelantando trabajo
RUN ./mvnw dependency:go-offline

# 5. Copiamos el código fuente de tu Java y mandamos a compilar (crea el .jar)
COPY src/ src/
RUN ./mvnw clean package -DskipTests


# ==========================================
# ETAPA 2: El Servidor de Producción (Runtime)
# ==========================================

# 6. Empezamos de cero con una imagen mucho más ligera: solo Java JRE (sin herramientas pesadas)
FROM eclipse-temurin:25-jre

# 7. Nos ponemos en la carpeta /app de esta nueva imagen
WORKDIR /app

# 8. MAGIA: Rescatamos únicamente el archivo .jar compilado de la Etapa 1 y lo copiamos aquí
COPY --from=build /app/target/*.jar app.jar

# 9. Abrimos el puerto 8080 para que Spring Boot reciba peticiones HTTP
EXPOSE 8080

# 10. Indicamos que al encender el contenedor se ejecute nuestra app Java
ENTRYPOINT ["java", "-jar", "app.jar"]
