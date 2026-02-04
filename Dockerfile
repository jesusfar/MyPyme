# Etapa de Construcción
FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

# Copiar archivos de configuración Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Copiar código fuente
COPY src ./src

# Asegurar permisos de ejecución para mvnw
RUN chmod +x mvnw

# Limpiar y empaquetar la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa de Ejecución
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Crear usuario no privilegiado para seguridad
RUN addgroup --system javauser && adduser --system --shell /bin/false --ingroup javauser javauser

# Copiar el jar construido desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Cambiar propietario al usuario no privilegiado
RUN chown javauser:javauser app.jar

# Usar el usuario no privilegiado
USER javauser

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
