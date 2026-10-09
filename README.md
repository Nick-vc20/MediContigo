# MediContigo - Base sencilla (Sprint 1)

Proyecto **Java puro (POO)**, sin Spring Boot, React ni base de datos por ahora. Es un punto de partida para que el equipo trabaje en GitHub sin configurar frameworks.

## Abrir y ejecutar
1. Descomprime el ZIP.
2. En **IntelliJ IDEA**, selecciona **Open** y abre la carpeta `MediContigo_Sencillo`.
3. Configura un **JDK 17 o superior** si lo solicita.
4. Marca `src` como **Sources Root** si IntelliJ no lo reconoce.
5. Abre `src/medicontigo/Main.java` y ejecuta `Main.main()`.

También se puede compilar en terminal desde la carpeta del proyecto:

```bash
javac -encoding UTF-8 -d out src/medicontigo/modelo/*.java src/medicontigo/servicio/*.java src/medicontigo/Main.java
java -cp out medicontigo.Main
```

## Distribución de historias (acuerdo del equipo)
- **HU-01:** `modelo/CentroMedico.java` y `servicio/CentroMedicoService.java` — registro de centros médicos.
- **HU-02:** `modelo/Administrador.java` y `servicio/AdministradorService.java` — autenticación.
- **HU-03:** `modelo/Especialidad.java` y `servicio/EspecialidadService.java` — especialidades y horarios.
- **HU-04:** `modelo/Medico.java` y `servicio/MedicoService.java` — personal médico.

Cada responsable completa la lógica, validaciones y pruebas de su historia. **HU-01 es la única con una pequeña demostración en memoria**; ninguna HU está terminada. No hay interfaz gráfica, API ni base de datos todavía. El documento PDF prevé una aplicación web con estas capacidades, que podrán incorporarse en una siguiente etapa.

**Seguridad:** `Administrador` es solo un esqueleto de clase. No almacenen contraseñas reales en texto plano ni usen esta base para producción. Implementen hash seguro, sesiones y autorización en HU-02.

## GitHub: una rama por historia
El encargado del repositorio publica primero la base en `main`:

```bash
git init
git add .
git commit -m "Base inicial de MediContigo"
git branch -M main
git remote add origin URL_DEL_REPOSITORIO
git push -u origin main
```

Cada compañero clona el repositorio y crea su rama:

```bash
git clone URL_DEL_REPOSITORIO
cd NOMBRE_DEL_REPOSITORIO
git checkout -b feature/hu-01-centros
```

Cambiar `hu-01-centros` por `hu-02-auth`, `hu-03-especialidades` o `hu-04-medicos`. Al terminar: `git add .`, `git commit -m "Implementar HU-01"`, `git push -u origin feature/hu-01-centros` y crear un **Pull Request** hacia `main` en GitHub.

**Regla:** no trabajar directamente en `main`; antes de unir cambios, compilar y revisar el Pull Request.
