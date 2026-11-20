#  RutaSegura

**RutaSegura** es una aplicación móvil desarrollada para Android orientada al registro, monitoreo comunitario y gestión de incidencias de seguridad y accesibilidad urbana. Permite a los ciudadanos reportar problemas en la vía pública (luminarias apagadas, baches, obstrucciones, zonas inseguras), visualizar reportes en un listado detallado y consultarlos geográficamente en un mapa interactivo.


##  Funcionalidades Principales

1. **Pantalla Principal (`MainActivity`)**:
   - Acceso rápido a las tres secciones principales de la aplicación: Registro de Reportes, Listado y Mapa de Incidencias.
   
2. **Registro de Reportes (`RegistroActivity`)**:
   - Formulario completo para ingresar el título de la incidencia, categoría, descripción detallada, fecha/hora automática y estado inicial (*Pendiente*).
   - **Captura GPS Automática**: Obtiene las coordenadas geográficas de ubicación en tiempo real.
   - **Sensor de Luminosidad**: Lee el sensor de luz ambiental del dispositivo en luxes (`lx`).

3. **Listado y Detalle de Reportes (`ListadoActivity` & `DetalleActivity`)**:
   - Visualización en formato de lista (RecyclerView con `ReporteAdapter`) de todas las incidencias registradas.
   - Pantalla de detalles para consultar la información completa de cada reporte.

4. **Mapa Interactivo (`MapaActivity`)**:
   - Visualización geográfica de los reportes y ubicación actual mediante mapas.

---

## 🛠️ Instrucciones de Compilación y Configuración

### Prerrequisitos
- **Android Studio** (Ladybug, Koala o versión más reciente recomendada).
- **JDK 17** o superior.
- **Android SDK** con API nivel 34 o superior.
- Dispositivo físico o emulador con **Google Play Services** (necesario para Google Maps y Firebase).

### Pasos para compilar
1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/eduardoo47/RutaSegura.git
   cd RutaSegura
   ```

2. **Configurar Firebase (`google-services.json`)**:
   - La aplicación utiliza Firebase Firestore. Asegúrate de colocar tu archivo `google-services.json` válido dentro del directorio `app/`. (Se incluye un archivo de ejemplo `google-services.example.json`).

3. **Abrir en Android Studio**:
   - Selecciona **File > Open** y elige la carpeta raíz del proyecto `RutaSegura`.
   - Espera a que Gradle sincronice las dependencias.

4. **Ejecutar la aplicación**:
   - Conecta un dispositivo Android físico o inicia un emulador con soporte de Google APIs.
   - Haz clic en el botón **Run 'app'** en Android Studio o ejecuta mediante terminal:
     ```bash
     ./gradlew assembleDebug
     ```

---

##  Limitaciones

- **Servicios de Google Play**: Requiere un entorno con Google Play Services instalado para el funcionamiento del mapa y servicios de ubicación.
- **Sensor de Luz Ambiental**: La lectura del sensor de luminosidad depende estrictamente del hardware del dispositivo físico. En emuladores estándar puede retornar `0.0 lx` a menos que se simulen sensores mediante herramientas de emulación.
- **Conectividad**: La sincronización de reportes con Firestore requiere conexión a internet activa.

---

##  Evidencias de Pruebas

- **Pruebas Unitarias (`ExampleUnitTest`)**: Verificación de lógica básica de negocio.
- **Pruebas Instrumentadas (`ExampleInstrumentedTest`)**: Verificación del entorno de ejecución en Android y contexto de la aplicación.
- **Compilación Exitosa**: El proyecto compila sin errores ni advertencias críticas de Gradle (`BUILD SUCCESSFUL`).
- **Pruebas de Interfaz (UI)**: Navegación probada exitosamente entre `MainActivity`, `RegistroActivity`, `ListadoActivity`, `DetalleActivity` y `MapaActivity`.
