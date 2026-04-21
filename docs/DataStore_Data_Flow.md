# Flujo de Preferencias de Usuario (DataStore)

Este documento describe cómo se gestionan las preferencias del usuario dentro de la aplicación utilizando **Jetpack DataStore**, siguiendo un flujo reactivo y desacoplado entre capas.

---

## Jerarquía de Capas

La arquitectura mantiene una separación clara de responsabilidades, donde cada capa transforma los datos según su contexto.

| Capa          | Componente       | Responsabilidad                                         | Modelo de Datos            |
|:--------------|:-----------------|:--------------------------------------------------------|:---------------------------|
| **UI**        | AccountScreen    | Gestiona estados temporales (borradores) y renderizado. | `mutableStateOf`           |
| **ViewModel** | Settings / Main  | Exponen flujos calientes (hot flows) específicos.       | `StateFlow<T>`             |
| **Domain**    | UserRepository   | Abstracción de acceso. Separa Perfil de Idioma.         | `UserPreferences`/`String` |
| **Data**      | UserPrefsManager | Implementación física con Jetpack DataStore.            | `Preferences`              |

---

## Flujo de Datos (DataStore Pattern)

A diferencia de entidades estructuradas como tareas o notas —que requieren modelos complejos, relaciones y persistencia basada en base de datos—, el perfil de usuario se gestiona mediante un enfoque ligero y reactivo basado en clave-valor utilizando DataStore.

Este enfoque no es una simplificación, sino una decisión consciente orientada a:

- Minimizar la sobrecarga de almacenamiento
- Permitir lecturas y escrituras granulares
- Facilitar la reactividad inmediata ante cambios

---

## 1. Lectura (DataStore → UI)

El flujo de lectura está diseñado como un pipeline reactivo desacoplado, donde cada capa transforma progresivamente los datos desde su forma persistente hasta un estado consumible por la UI.

A diferencia de un acceso directo a preferencias, este enfoque permite:
- Reacción automática ante cambios
- Separación entre modelo de almacenamiento y modelo de dominio
- Consumo selectivo según contexto (perfil vs idioma)

### 1. Persistencia (DataStore)
Los datos se almacenan en un sistema clave-valor utilizando Jetpack DataStore, donde cada atributo del usuario se persiste de forma independiente.

Esto permite:
- Escrituras granulares
- Lecturas eficientes
- Evolución flexible del modelo sin migraciones complejas

**Claves utilizadas:**
   - `NAME_KEY`
   - `BIO_KEY`
   - `IMAGE_KEY`
   - `CENTER_X_KEY`
   - `CENTER_Y_KEY`
   - `ZOOM_KEY`
   - `LANGUAGE_KEY`

---

### 2. Manager (UserPreferencesManager)
El Manager actúa como puente entre el mundo crudo (key-value) y el modelo de dominio tipado, su responsabilidad no es solo leer datos, sino darles forma semántica.

Expone dos flujos independientes:

- `Flow<UserPreferences>` → estado completo del perfil
- `Flow<String>` → configuración de idioma aislada

Esto introduce una optimización clave: **no todos los consumidores necesitan todo el modelo**
```kotlin
     // Fragmento de lógica de empaquetado de datos para su lectura
     UserPreferences(
       name = prefs[NAME_KEY] ?: "Usuario",
       bio = prefs[BIO_KEY] ?: "Sin descripción",
       imageUri = prefs[IMAGE_KEY],
       centerX = prefs[OFFSET_X_KEY] ?: 0.5f,
       centerY = prefs[OFFSET_Y_KEY] ?: 0.5f,
       zoom = prefs[ZOOM_KEY] ?: 1f,
       language = prefs[LANGUAGE_KEY] ?: LanguageUtils.getSystemLanguageCode()
     )
     
     prefs[LANGUAGE_KEY] ?: LanguageUtils.getSystemLanguageCode()
```
Actúa como punto de abstracción sobre DataStore donde típicamente se aplican operadores como: (`map`, `distinctUntilChanged`). Para evitar emisiones redundantes y mantener eficiencia reactiva.

---

### 3. Repository (UserRepository)
El Repository define el contrato de acceso a datos, eliminando cualquier dependencia directa de DataStore en capas superiores. Más que “redirigir”, su rol es:

- Estabilizar la API de datos
- Permitir cambios futuros sin afectar ViewModels
- Exponer flujos desacoplados por intención de uso

  ```kotlin
    // Fragmento de lógica en UserRepository
    override val userData: Flow<UserPreferences> = preferencesManager.userPreferencesFlow
    override val languageData: Flow<String> = preferencesManager.languageFlow
  ```

Aquí se consolida una decisión arquitectónica importante: **separar lectura por contexto, no por estructura interna**

---

### 4. ViewModel
El ViewModel transforma flujos fríos `Flow` en estados observables y persistentes `StateFlow`, alineados con el ciclo de vida de la UI.

* **SettingsViewModel**

  - Consume userData 
  - Lo convierte en StateFlow usando stateIn 
  - Mantiene el último valor en memoria

  **Esto permite:**
  - Evitar recomputaciones innecesarias
  - Tener siempre un estado consistente disponible para la UI
  - Integración directa con Compose

* **MainViewModel (Configuración Global)**
  - Consume exclusivamente languageData
  - Se suscribe al flujo de idioma

  **Esto permite:**   
  - Que reaccione inmediatamente ante cambios lo que genera que se refleje instantáneamente en la interfaz sin necesidad de reiniciar la actividad manualmente.
  - Sea eficiente en el manejo de recursos haciendo que el `MainViewModel` no se entera de si se cambio la foto de perfil o la bio. Solo se despierta cuando el string de idioma en el DataStore cambia.

---

### 5. UI (AccountScreen)
La UI actúa como consumidor final del estado, sin conocimiento de la fuente de datos.

- Observa mediante `collectAsStateWithLifecycle()`
- Recibe un estado inmutable `UserPreferences`
- Lo transforma en estados temporales editables
  - `tempName`
  - `tempBio`
  - `tempImageUri`
  - `tempX`
  - `tempY`
  - `tempZoom`

**Con esto se asegura que:**

- La UI no modifica el estado global directamente
- Trabaja sobre una copia desacoplada (borradores)

Lo que permite una edición segura, cancelar de cambios y tener un control total sobre cuándo persistir.

---

## 2. Escritura de Perfil (UI → DataStore)

El flujo de escritura está diseñado como un proceso controlado y explícito, donde la UI nunca modifica directamente el estado persistente.

En su lugar, trabaja sobre estados temporales desacoplados, y solo persiste cambios cuando se confirma una acción.

Esto permite:

- Evitar estados inconsistentes
- Controlar cuándo ocurren las escrituras
- Diferenciar entre cambios locales y globales

### 1. Captura (UI)
La UI actúa como un entorno de edición aislado.

En AccountScreen, los datos del usuario no se modifican directamente, sino que se transforman en borradores editables:
- `tempName`
- `tempBio`
- `tempImageUri`
- `tempX`
- `tempY`
- `tempZoom`

Esto permite que los estados representen una copia desacoplada del estado real

**Actualización de Perfil:**

Cuando el usuario confirma cambios:

Los estados temporales se agrupan en un objeto de dominio:
```kotlin
  UserPreferences(
      name = tempName,
      bio = tempBio,
      imageUri = tempImageUri,
      centerX = tempX,
      centerY = tempY,
      zoom = tempZoom,
      language = currentLanguage // se preserva o se maneja aparte
  )
```
Con esto la UI no conoce DataStore solo conoce el modelo de dominio

**Actualización de Idioma**

El idioma sigue un flujo distinto, para este se envía únicamente el código:
- `"es"`
- `"en"`

No se reconstruye el objeto completo. A esta configuración se le trata como una operación independiente

---

### 2. Procesamiento (ViewModel)
El ViewModel actúa como punto de validación y orquestación. Este recibe dos tipos de intención:

- Actualización de perfil → `UserPreferences`
- Cambio del idioma → `String`

Su responsabilidad no es solo “pasar datos”, sino:

Interpretar la intención de la UI
Delegar al caso correcto en el repositorio
Mantener la separación entre tipos de actualización 

---

### 3. Persistencia (Repository)
El Repository define operaciones explícitas, no genéricas. Por ejemplo:

- `updateProfile(userPreferences)`
- `updateLanguage(code)`

Esto evita ambigüedad y hace visible la intención del sistema

Además:

Desacopla completamente la UI de DataStore y permite cambiar la implementación sin afectar capas superiores

---

### 4. Manager (UserPreferencesManager)
El Manager ejecuta la persistencia real en DataStore. Dependiendo de la operación:

- **Escritura de Perfil:** Actualiza múltiples claves en una sola transacción
- **Escritura de Idioma:** Actualiza únicamente LANGUAGE_KEY

Lo que introduce una propiedad importante:
- Las escrituras son atómicas y específicas
- No se reescribe todo el estado innecesariamente.