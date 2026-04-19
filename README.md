# AssistantApp

**AssistantApp** es una herramienta de organización personal diseñada para funcionar como un asistente útil, modular y eficiente. Su enfoque principal es la centralización de tareas y notas bajo una arquitectura sólida y almacenamiento local, permitiendo al usuario gestionar su día a día con rapidez y estructura.

Inspirada en la agilidad de herramientas de productividad modernas, busca ofrecer una experiencia fluida donde la gestión de la información sea inmediata y privada.

## Vista previa

![Pantalla principal](docs/images/home.jpeg)

---

## 🚀 Novedades (v1.0.1)
* **Gestión de Perfil:** Implementación de la pantalla de Cuenta con edición de nombre, biografía y foto de perfil.
* **Persistencia Ligera:** Integración de **Jetpack DataStore** para el almacenamiento de preferencias de usuario.
* **UX Avanzada:** Gestión inteligente del foco del teclado.
* **Documentación:** Corrección de enlaces internos y optimización de guías técnicas.

![Pantalla de cuenta](docs/images/account.jpeg)

---

## Funcionalidades

### Gestión de Tareas

![Crear o editar tarea](docs/images/form.jpeg)

- **Control Total (CRUD):**  
  Creación, edición y eliminación de tareas con persistencia de datos.

- **Seguimiento Eficiente:**  
  Asignación de fechas y estados de finalización para el control de actividades.

- **Organización Temporal:**  
  Visualización de tareas agrupadas por secciones de tiempo en la interfaz principal.

### Gestión de Notas

- **Entidad Independiente:**  
  Soporte para notas con una interfaz dedicada, permitiendo capturar ideas de forma rápida.

- **Reutilización Lógica:**  
  Implementación basada en la misma robustez de persistencia que el sistema de tareas.

### Navegación

- **Navegación Intuitiva:**  
  Interfaz fluida que permite transitar entre los diferentes contextos:
    - Inicio (Notas y Tareas)
    - Papelera
    - Cuenta

### Papelera de Reciclaje (Soft Delete)

![Papelera de reciclaje](docs/images/trash.jpeg)

- **Borrado Lógico:**  
  Las entidades eliminadas se mueven a una papelera en lugar de borrarse definitivamente.

- **Restauración:**  
  Permite recuperar información eliminada accidentalmente, garantizando la integridad de los datos.

---

## Arquitectura y Flujo de Datos

- **Arquitectura Unidireccional:**  
  Separación estricta entre las capas de:
    - Base de Datos (Room)
    - Dominio
    - UI

  Esto facilita el mantenimiento y la escalabilidad del proyecto.

- **Reactividad:**  
  Uso de flujos reactivos (`Flow`) para que la interfaz se actualice automáticamente ante cualquier cambio en la base de datos.

- **Optimización:**  
  Implementación de **Baseline Profiles** para maximizar el rendimiento en la ejecución y reducir los tiempos de arranque.

---

## Tecnologías utilizadas

- **Kotlin** 
  Lenguaje principal para la lógica de negocio.

- **Jetpack Compose**  
  Framework moderno para la construcción de interfaces declarativas y reactivas.

- **Room Persistence**  
  Biblioteca de abstracción sobre SQLite para el almacenamiento local y seguro de datos.

- **Material Design 3**  
  Estándar de diseño para componentes reutilizables y modulares.

- **Jetpack DataStore**
  Persistencia de datos de configuración y perfil. **[Nuevo]**

- **Coil**
  Carga de imágenes asíncrona. **[Nuevo]**

- **Baseline Profiles**  
  Herramienta de optimización de rendimiento en tiempo de ejecución.

---

## Licencia

Este proyecto está bajo la **Licencia MIT**.

Fiel a la filosofía de Software Libre, **AssistantApp** es una herramienta abierta, escalable y adaptable, diseñada para ser un asistente personal eficiente y centrado en el usuario.

---

## Documentación adicional

Para más detalles técnicos sobre la implementación consulte:

### Flujo de Datos
* [Flujo de Datos con Room (Persistencia de Tareas y Notas)](docs/Room_Data_Flow.md)
* [Flujo de Preferencias con DataStore (Perfil de Usuario)](docs/DataStore_Data_Flow.md)