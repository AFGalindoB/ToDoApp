# Documentación de Flujo de Datos

Este documento sirve como guía para entender cómo fluyen los datos en la aplicación y evitar inconsistencias al agregar nuevas funcionalidades.

---

## Jerarquía de Capas
La aplicación sigue una arquitectura de capas unidireccional. Las dependencias fluyen hacia abajo, pero los datos fluyen en ambos sentidos mediante mappers.

| Capa         | Componente    | Modelo      | Formato de Datos             |
|:-------------|:--------------|:------------|:-----------------------------|
| **Form**     | Formularios   | `FormState` | Logica de entrada de datos   |
| **Domain**   | Internal Data | `Domain`    | Logica de dominio local      |
| **Database** | Room          | `Entity`    | Tipos primitivos para SQLite |

---
## Flujo de Datos Unificado (Data Flow Pattern)

Este patrón describe cómo la aplicación procesa la información desde la persistencia hasta la pantalla, garantizando la separación de capas y la reactividad.


### 1. Lectura (DB → UI)
Se sigue una estructura de tubería (pipeline) donde los datos se transforman progresivamente.

1. **Persistencia (Room Entity)**
    * **Componente:** `TaskEntity` / `NoteEntity`.
    * **Función:** Representa la estructura física en SQLite (tipos primitivos).

2. **Acceso a Datos (DAO)**
    * **Componente:** `TaskDao` / `NoteDao`.
    * **Función:** Expone flujos reactivos (`Flow<List<Entity>>`). Emiten una nueva lista cada vez que la tabla cambia.

3. **Abstracción (Repository)**
    * **Componente:** `OfflineTaskRepository` / `OfflineNoteRepository`.
    * **Función:** Actúa como mediador. Es el encargado de proveer los datos a la lógica de negocio sin revelar el origen (DB o API).

4. **Lógica de Negocio y Estado (ViewModel)**
    * **Transformación de Dominio:** Se mapea la entidad a un modelo de dominio (`toDomain()`). Esto protege a la UI de cambios en la base de datos.
    * **Preparación de UI (Opcional):** * En **Tareas**: Se aplica lógica extra como `groupBy` para crear secciones de tiempo.
        * En **Notas**: Se mantiene como una lista plana. 
    * **Exposición:** Se usa `stateIn` para convertir el flujo en un estado observable por Compose.

5. **Visualización (Compose UI)**
    * **Componente:** `TaskListScreen` / `NotesListScreen`.
    * **Función:**
      * **Para Notas:** Consume directamente el `StateFlow<List<NoteDomain>>`. Se renderiza como una lista o cuadrícula simple de modelos de dominio.
      * **Para Tareas:** Consume un `StateFlow<Map<Int, List<TaskDomain>>>`. El ViewModel realiza una agrupación adicional por **secciones de tiempo**, por lo que la UI recibe tanto el dominio como su jerarquía de sección para el renderizado.


### Escritura (UI → DB)

#### Creacion de Entidades
Este flujo describe el proceso de añadir un nuevo registro utilizando el componente de formulario compartido.

1. **Origen (UI - LazyColumn/FAB)**
    * El usuario inicia la acción de crear desde un botón flotante.
    * Se invoca el componente de interfaz **Upsert** (diálogo flotante).

2. **Interfaz de Entrada (Diálogo Upsert)**
    * **Estado Inicial:** Recibe un valor `null` como entidad de dominio.
    * **Comportamiento:** Al recibir `null`, el diálogo se inicializa con campos vacíos.
    * **Salida:** Al confirmar, el diálogo devuelve un objeto `FormState` con la información recolectada.

3. **Procesamiento (ViewModel)**
    * **Evento:** Se ejecuta una función `create(form: FormState)`.
    * **Lógica Interna:** * Se abre un `viewModelScope.launch` para ejecutar la operación de forma asíncrona.
      
      * Se utiliza `DateUtils.now()` para generar las marcas de tiempo.
      * Se instancia una **TaskEntity** directamente, mapeando los datos del formulario y asignando `createdAt`, `updatedAt` y `deleteAt = 0L`.
      * Ejemplo: 
          ```kotlin
           // Fragmento de lógica en ViewModel de Tasks
           val task = TaskEntity(
               title = form.title,
               content = form.content,
               date = form.date ?: 0L,
               completed = form.completed,
               createdAt = now,
               updatedAt = now,
               deleteAt = 0L
           )
  
           // Fragmento de lógica en ViewModel de Notes
           val note = NoteEntity(
              title = form.title,
              content = form.content,
              createdAt = now,
              updatedAt = now,
              deleteAt = 0L
          )
          ```
    
4. **Acceso a Datos**
   * **Repository:** Recibe la Entity ya construida y la pasa al DAO sin necesidad de transformaciones adicionales.
   * **DAO:** Inserta la Entity en la base de datos y en caso de conflicto usa la estrategia de ignorar.
   * **Cierre de ciclo:** Al actualizarse la base de datos, el flujo de Lectura reacciona automáticamente y la LazyColumn muestra la nueva tarea sin refrescar manualmente.

#### Actualizacion de Entidades
Este flujo describe la modificación de un registro existente, reutilizando el componente de formulario pero con una carga de datos previa.

1. **Origen (UI - LazyColumn)**
    * El usuario selecciona una tarea específica de la lista para editarla.
    * Se invoca el componente **Upsert**.

2. **Interfaz de Entrada (Diálogo Upsert)**
    * **Estado Inicial:** Recibe la entidad de **TaskDomain** correspondiente a la tarea seleccionada.
    * **Comportamiento:** Al detectar que el objeto no es nulo, el diálogo se pre-carga con la información de la tarea (título, contenido, etc.).
    * **Salida:** Al confirmar, el diálogo devuelve el `TaskFormState` modificado.

3. **Procesamiento (ViewModel)**
    * **Evento:** Se ejecuta la función `update(entityDomain: Domain, form: FormState)`.
    * **Lógica de Combinación:** El ViewModel crea una nueva **TaskEntity** realizando un "merge" de datos:
        * **Del Dominio:** Mantiene el `id` original y la fecha de creación `createdAt`.
        * **Del Formulario:** Toma los datos del formulario y los actualiza.
        * **Metadatos:** Actualiza `updatedAt` con el tiempo actual mediante `DateUtils.now()`.
    * Ejemplo:
   ```kotlin
   // Fragmento de lógica en ViewModel de Tasks
   val updated = TaskEntity(
       id = task.id, 
       title = form.title,
       content = form.content,
       date = form.date ?: 0L,
       completed = form.completed,
       createdAt = task.createdAt, // Mantiene origen
       updatedAt = DateUtils.now(), // Marca edición
       deleteAt = 0L
   )
   
   // Fragmento de lógica en ViewModel de Notes
   val updated = NoteEntity(
                id = note.id,
                title = form.title,
                content = form.content,
                createdAt = note.createdAt,
                updatedAt = DateUtils.now(),
                deleteAt = 0L
            )
   ```
4. **Acceso a Datos**
    * **Repository:** Recibe la Entity ya construida y la pasa al DAO sin necesidad de transformaciones adicionales.
    * **DAO:** Actualiza la Entity en la base de datos usando la sentencia `@Update`.
    * **Cierre de ciclo:** Al actualizarse la base de datos, el flujo de Lectura reacciona automáticamente y la LazyColumn muestra la tarea actualizada sin refrescar manualmente.

#### Eliminacion de Entidades de Manera Permanente
Este flujo describe la eliminación física y definitiva de un registro en la base de datos a través de su identificador único.

1. **Origen (UI - LazyColumn/Acción)**
   * El usuario activa la eliminación mediante un gesto de swipe
   * La UI identifica la entidad y extrae su objeto de **Domain**.

2. **Procesamiento (ViewModel)**
   * **Evento:** Se ejecuta la función de eliminación `permanentlyDelete(entityDomain: Domain)`).
   * **Acción:** El ViewModel abre un `viewModelScope.launch` y extrae el **ID** de la entidad de dominio.
   * **Comunicación:** Invoca al repositorio pasando únicamente el **ID** numérico, simplificando la carga de datos en la llamada.

3. **Intermediario (Repository)**
   * **Función:** Recibe el ID y lo redirige directamente al DAO. Al no requerir mapeos complejos a Entity en este punto, el flujo es altamente eficiente.

4. **Persistencia (DAO)**
   * **Acción:** Ejecuta la sentencia SQL: `DELETE FROM tasks WHERE id = :id`.
   * **Resultado Físico:** El registro desaparece permanentemente de la tabla en SQLite.
   * **Actualización Reactiva:** El flujo de **Lectura** detecta la ausencia del registro y actualiza la `LazyColumn`, eliminando la tarjeta de la interfaz de forma inmediata.