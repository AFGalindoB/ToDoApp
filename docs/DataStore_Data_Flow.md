# Flujo de Preferencias de Usuario (DataStore)

Este documento describe cómo se gestionan las preferencias del usuario dentro de la aplicación utilizando **Jetpack DataStore**, siguiendo un flujo reactivo y desacoplado entre capas.

---

## Jerarquía de Capas

La arquitectura mantiene una separación clara de responsabilidades, donde cada capa transforma los datos según su contexto.

| Capa         | Componente        | Modelo            | Formato de Datos                  |
|:-------------|:------------------|:------------------|:----------------------------------|
| **Form UI**  | Formularios       | `FormState`       | Lógica de entrada de datos        |
| **Domain**   | Internal Data     | `Preferences`     | Lógica de dominio local           |
| **Database** | DataStore         | `Key`             | Protocol Buffers (clave-valor)    |

---

## Flujo de Datos (DataStore Pattern)

A diferencia de entidades complejas como Tareas o Notas, el perfil del usuario utiliza un enfoque **clave-valor** gestionado mediante `DataStore`.

El acceso a estos datos está centralizado a través del **UserPreferencesManager**, asegurando consistencia y encapsulación.

---

## 1. Lectura de Perfil (DataStore → UI)

El flujo de lectura sigue un patrón reactivo donde los datos se transforman progresivamente hasta llegar a la interfaz.

### 1. Persistencia (DataStore)
- Los datos se almacenan en un archivo de preferencias.
- Cada valor está asociado a una clave específica:
   - `NAME_KEY`
   - `BIO_KEY`
   - `IMAGE_KEY`

---

### 2. Manager (UserPreferencesManager)
- Expone un `Flow<UserPreferences>`.
- Se encarga de:
   - Leer las claves crudas.
   - Mapearlas a un modelo de dominio (`UserPreferences`).
- Actúa como punto de abstracción sobre DataStore.

---

### 3. Repository (UserRepository)
- Funciona como único punto de acceso a los datos.
- Oculta completamente la implementación de DataStore.
- Permite mantener independencia entre capas.

---

### 4. ViewModel (SettingsViewModel)
- Transforma el modelo de dominio en datos consumibles por la UI (Strings).
- Utiliza `stateIn` para:
   - Mantener el estado activo (*hot flow*).
   - Garantizar reactividad continua.

---

### 5. UI (AccountScreen)
- Observa los valores expuestos por el ViewModel.
- Reacciona automáticamente a:
   - Cambios internos (ediciones del usuario).
   - Cambios externos (actualizaciones en DataStore).

---

## 2. Escritura de Perfil (UI → DataStore)

El flujo de escritura sigue un proceso controlado para evitar inconsistencias.

### 1. Captura (UI)
- La interfaz almacena cambios en estados temporales:
   - `tempName`
   - `tempBio`
   - `tempImageUri`

---

### 2. Procesamiento (ViewModel)
- Al ejecutar la acción **Guardar**:
   - Se construye un nuevo objeto `UserPreferences`.
   - Se envía al repositorio para su persistencia.

---

### 3. Persistencia (Repository → Manager)
- El `UserRepository` delega la operación al `UserPreferencesManager`.
- El Manager ejecuta una operación de edición asíncrona (dataStore.edit) para actualizar los valores en memoria.