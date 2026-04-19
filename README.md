# AssistantApp

**AssistantApp** es una herramienta de organización personal diseñada para funcionar como un asistente útil, modular y eficiente. Su enfoque principal es la centralización de tareas y notas bajo una arquitectura sólida y almacenamiento local, permitiendo al usuario gestionar su día a día con rapidez y estructura.

Inspirada en la agilidad de herramientas de productividad modernas, busca ofrecer una experiencia fluida donde la gestión de la información sea inmediata y privada.

---

## 🚀 Funcionalidades actuales

### 📌 Gestión de Tareas

- **Control Total (CRUD):**  
  Creación, edición y eliminación de tareas con persistencia de datos.

- **Seguimiento Eficiente:**  
  Asignación de fechas y estados de finalización para el control de actividades.

- **Organización Temporal:**  
  Visualización de tareas agrupadas por secciones de tiempo en la interfaz principal.

---

### 📝 Gestión de Notas

- **Entidad Independiente:**  
  Soporte para notas con una interfaz dedicada, permitiendo capturar ideas de forma rápida.

- **Reutilización Lógica:**  
  Implementación basada en la misma robustez de persistencia que el sistema de tareas.

---

### 🔄 Navegación

- **Navegación Intuitiva:**  
  Interfaz fluida que permite transitar entre los diferentes contextos:
    - Tareas
    - Notas
    - Papelera

---

### 🗑️ Papelera de Reciclaje (Soft Delete)

- **Borrado Lógico:**  
  Las entidades eliminadas se mueven a una papelera en lugar de borrarse definitivamente.

- **Restauración:**  
  Permite recuperar información eliminada accidentalmente, garantizando la integridad de los datos.

---

## 🏗️ Arquitectura y Flujo de Datos

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

## 📄 Licencia

Este proyecto está bajo la **Licencia MIT**.

Fiel a la filosofía de Software Libre, **AssistantApp** es una herramienta abierta, escalable y adaptable, diseñada para ser un asistente personal eficiente y centrado en el usuario.

---

## 📚 Documentación adicional

Para más detalles técnicos sobre la implementación consulte:

👉 [Arquitectura y Flujo de Datos](data/Arch_Data_Flow.md)