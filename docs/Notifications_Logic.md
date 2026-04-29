# Arquitectura de Notificaciones y Resiliencia

Este documento describe la arquitectura técnica del sistema de recordatorios de Noir Assistant, diseñado para ofrecer notificaciones confiables, resilientes y compatibles con las restricciones modernas de Android, incluyendo Doze Mode, optimizaciones agresivas de batería y reinicios del dispositivo.

La implementación combina `AlarmManager`, `BroadcastReceiver` y `WorkManager` para equilibrar:

- Precisión temporal
- Persistencia tras reinicios
- Compatibilidad con ahorro energético
- Seguridad en segundo plano
- Escalabilidad de procesamiento

---

## Flujo de Inicialización y Permisos

Durante el arranque de la aplicación `MainActivity`, el sistema sincroniza automáticamente la programación de recordatorios utilizando las preferencias persistidas del usuario.

| Permiso                                | Proposito                                               |
|:---------------------------------------|:--------------------------------------------------------|
| `POST_NOTIFICATIONS`                   | Permite mostrar notificaciones en Android 13+           |
| `SCHEDULE_EXACT_ALARM`                 | Habilita alarmas exactas                                |
| `RECEIVE_BOOT_COMPLETED`               | Permite restaurar alarmas tras reiniciar el dispositivo |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Solicita exclusión de optimización energética           |

### Solicitud de Notificaciones
En Android 13 (API 33+) las notificaciones requieren autorización explícita del usuario:

```kotlin
     ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        101
     )
```

---

## Estrategia de Precisión y Gestión Energética

Android moderno restringe fuertemente los procesos en segundo plano para optimizar batería. Por esta razón, Noir Assistant implementa una estrategia híbrida y adaptable.

### Modo Exacto (Alta Precisión)

Cuando la aplicación detecta que el usuario deshabilitó las optimizaciones de batería el sistema utiliza: `AlarmManager.setAlarmClock()`

#### Ventajas
- Máxima prioridad dentro del sistema Android
- Menor probabilidad de retrasos
- Mayor precisión temporal
- Mejor comportamiento durante Doze Mode

### Modo Compatible (Fallback Seguro)

Si el sistema aún optimiza batería o el permiso exacto fue revocado se utiliza: `AlarmManager.setAndAllowWhileIdle()`

#### Objetivo
Garantizar compatibilidad incluso bajo restricciones agresivas del sistema operativo.

Aunque este método puede presentar ligeros retrasos en algunos fabricantes, asegura que la notificación siga ejecutándose.

### Consideraciones OEM (Fabricantes)
Algunas capas de personalización como:

- Xiaomi (MIUI / HyperOS)
- Huawei (EMUI)
- Oppo / Realme
- Vivo

pueden finalizar procesos en segundo plano de forma agresiva.

**Para maximizar confiabilidad, se recomienda:**

1. Desactivar optimización de batería: Evita que Android suspenda procesos críticos.
2. Habilitar Auto-start / Inicio Automático: Permite que los `BroadcastReceiver` funcionen correctamente tras reinicios o limpieza de memoria.

---

## Componentes Principales del Sistema

### AlarmScheduler — El Programador
Responsable de calcular y registrar la siguiente alarma exacta.

**Responsabilidades:**

- Parsear la hora configurada por el usuario
- Calcular el siguiente disparo válido
- Evitar programaciones duplicadas mediante un PendingIntent fijo
- Seleccionar dinámicamente el tipo de alarma más adecuado

**Características Técnicas:**

El sistema revisa las siguientes configuraciones: `PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE`. Las cuales permiten:

- **FLAG_UPDATE_CURRENT:** evitar múltiples alarmas duplicadas
- **FLAG_IMMUTABLE:** mejora seguridad en Android moderno

**Reprogramación Inteligente:** Si la hora configurada ya pasó en el día actual, el sistema automáticamente agenda la siguiente ejecución para el día siguiente.

### AlarmReceiver — El Despertador
`BroadcastReceiver` encargado de reaccionar cuando Android dispara la alarma.

**Diseño de Responsabilidad Única:**
El receiver no realiza operaciones pesadas. Únicamente:

1. Recibe el evento del sistema
2. Lanza un `WorkManager`

**Motivo de Diseño:**
Android limita severamente el tiempo de ejecución de un `BroadcastReceiver`. Por esta razón realizar:

- Consultas Room
- Acceso a disco
- Lógica compleja
- Operaciones suspendidas

Directamente, dentro del receiver puede provocar:

- ANR
- Cancelaciones
- Comportamiento inconsistente

Por ello, toda la carga real se delega al ReminderWorker.

### ReminderWorker — El Procesador
Implementado mediante `CoroutineWorker`. Es el núcleo lógico del sistema de notificaciones.

**Responsabilidades:**
Consulta a la base de datos obteniendo tareas que estén pendientes para el día de hoy y que estén atrasadas y las divide en:

- Tareas vencidas
- Tareas del día actual

Con esto hace una generación dinámica de mensajes. Por ejemplo:
- Hoy: Estudiar IA.
- Tienes 3 tareas para completar hoy.
- Hoy: 2 tareas (+ 1 atrasada).

**Estrategia de Recuperación:**
Ante fallos realiza el WorkManager intentará volver a ejecutar automáticamente el procesamiento, mejorando considerablemente la resiliencia del sistema.

### NotificationHelper — La Capa Visual
Centraliza toda la construcción de notificaciones.

**NotificationChannel:**
Desde Android 8+ las notificaciones requieren canales obligatorios.
```kotlin
NotificationChannel(
    CHANNEL_ID,
    "Recordatorios Diarios",
    NotificationManager.IMPORTANCE_HIGH
)
```

**Configuración de la notificación:**
```kotlin
.setPriority(NotificationCompat.PRIORITY_MAX)
.setCategory(NotificationCompat.CATEGORY_ALARM)
```
Lo que permite:

- Alta visibilidad
- Sonido/vibración
- Comportamiento prioritario
- Mejor compatibilidad con recordatorios críticos

Sumado a esta el helper también aplica:

- Icono monocromático con el logo de Noir Assistant
- Colores de acento del sistema

### Persistencia Tras Reinicios — BootReceiver

Uno de los problemas clásicos de Android es que las alarmas desaparecen tras reiniciar el dispositivo por lo cual para resolver esto, Noir Assistant implementa un `BootReceiver`.

Para esto los eventos escuchados son `Intent.ACTION_BOOT_COMPLETED` y `android.intent.action.QUICKBOOT_POWERON` para compatibilidad extendida con algunos fabricantes.

**Flujo de Recuperación:**
Tras el reinicio:

1. Android envía el broadcast
2. BootReceiver se activa
3. Se recuperan preferencias persistidas
4. Se vuelve a llamar: `AlarmScheduler.schedule(context, time)`

Obteniendo como resultado que las alarmas sobreviven a:

- Reinicios
- Apagados
- Actualizaciones menores del sistema
- Limpieza de memoria

Sin la necesidad de mantener servicios permanentes en segundo plano.

---

## Decisiones de Ingeniería

### ¿Por qué no usar únicamente WorkManager?

WorkManager es excelente para tareas diferibles y persistentes, pero:

- No garantiza ejecución exacta al segundo
- Puede retrasar tareas bajo Doze Mode

Por sí solo no es adecuado para recordatorios precisos.

### ¿Por qué no usar únicamente AlarmManager?

AlarmManager ofrece precisión temporal, pero:

- No debe ejecutar operaciones largas
- No maneja bien procesamiento complejo
- Los BroadcastReceiver tienen ventanas de ejecución limitadas

### Arquitectura Híbrida Elegida
| Componente          | Funcion                           |
|:--------------------|:----------------------------------|
| `AlarmManager`      | Precisión temporal                |
| `BroadcastReceiver` | Activación inmediata              |
| `WorkManager`       | Procesamiento seguro y resiliente |
| `Room`              | Persistencia de tareas            |
| `BootReceiver`      | Recuperación tras reinicios       |

Por lo cual esta arquitectura permite:
- Alta precisión temporal
- Compatibilidad con Doze Mode
- Persistencia tras reinicios
- Bajo consumo energético
- Separación clara de responsabilidades
- Escalable y mantenible
- Compatible con Android moderno
- Recuperación automática ante errores

---

## Consideraciones Futuras

La arquitectura actual permite evolucionar fácilmente hacía:

- Múltiples recordatorios simultáneos
- Recordatorios recurrentes complejos
- Notificaciones enriquecidas
- Acciones rápidas desde notificación

La separación entre programación, procesamiento y presentación facilita futuras expansiones sin comprometer estabilidad.