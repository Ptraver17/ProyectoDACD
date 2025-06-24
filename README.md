# ProyectoDACD

Este proyecto implementa un sistema distribuido de publicación y consumo de eventos relacionados con partidos de fútbol y noticias deportivas. Utiliza almacenamiento por eventos para organizar los datos y permite a una unidad de negocio recuperar información relevante por jornada y liga.

---

## 🎯 Objetivo de la Funcionalidad de Negocio

La funcionalidad principal del sistema es:

- Recolectar información de partidos de fútbol y noticias deportivas mediante "feeder apps".
- Publicar estos datos como eventos en un sistema de almacenamiento de eventos (`eventstore`).
- Permitir a la unidad de negocio consultar todos los partidos y noticias correspondientes a un partido en específico, para análisis o toma de decisiones.

---

## 📚 Justificación de APIs y Persistencia

### 🔗 APIs utilizadas

El sistema está diseñado para conectarse con las siguientes APIs externas:

- **football-data.org**: proporciona información estructurada sobre partidos de fútbol (equipos, ligas, fechas, jornadas).
- **NewsAPI.org**: permite obtener titulares y descripciones de noticias deportivas actualizadas.

Ambas APIs ofrecen respuestas en formato JSON fáciles de transformar en eventos. Además, tienen buena documentación.

### 🗃️ Persistencia en Archivos JSON

Se almacenan los datos en archivos JSON, por las siguientes razones:
- No requiere configuración ni instalación adicional.
- Ideal para entornos distribuidos, demostraciones y evolución rápida.
- Archivos fácilmente legibles, portables y versionables.

Los datos se guardan en estructuras como:

- `jornada32_PD_matches.json`
- `match1048_news.json`

Cada archivo contiene un array de objetos en formato JSON con la información estructurada de partidos o noticias.

---

##  Arquitectura Usada

Este sistema está basado en una arquitectura orientada a eventos con feeders, broker, suscriptores y persistencia de datos:

- Feeders: generan eventos de manera independiente. `FootballFeeder` publica eventos de partidos y `NewsFeeder` publica eventos de noticias.
- Broker (ActiveMQ): intermedia la transmisión de mensajes. 
- Suscriptores: `EventStoreBuilder` actúa como consumidor y persistidor de los eventos, escribiéndolos como archivos `.events`.
- Event Store: almacenamiento intermedio en disco estructurado por carpetas.
- Unidad de Negocio (BusinessUnitApp): lee los eventos persistidos y los transforma en archivos JSON por jornada y liga para análisis o visualización.

---

## 🏗️ Arquitectura Final del Sistema

```
+------------------+         +-----------------------+         +---------------------+
|  FootballFeeder  | ----->  |                       |         |                     |
|  (partidos)      |         |                       |         |                     |
+------------------+         |      ActiveMQ         |         |                     |
                             |      (Broker)         |         |                     |
+------------------+         |   Topic A / Topic B   |         |                     |
|    NewsFeeder    | ----->  |                       | ----->  |  EventStoreBuilder  |
|    (noticias)    |         |                       |         |    (subscriber)     |
+------------------+         +-----------------------+         +---------------------+
                                                                     |
                                                                     v
                                                           +---------------------+
                                                           |     Event Store     |
                                                           |   (archivos .event) |
                                                           +---------------------+
                                                                     |
                                                                     v
                                                           +---------------------+
                                                           |  BusinessUnitApp    |
                                                           |  (lector de eventos)|
                                                           +---------------------+
                                                                     |
                                                                     v
                                                           +---------------------+
                                                           |  Archivos JSON por  |
                                                           |  jornada y liga     |
                                                           +---------------------+
```

---

## ▶️ Cómo Ejecutar los Componentes

> Requisitos:
> - Java 11 o superior
> - Maven instalado (`mvn`)
> - ActiveMQ corriendo localmente (puerto 61616 por defecto)

---

### 🔧 Paso 1: Iniciar ActiveMQ

Antes de ejecutar cualquier componente, asegúrate de tener ActiveMQ ejecutándose en tu máquina local.

---

### 🛠️ Paso 2: Inicializar el Event Store

Ejecuta el inicializador del Event Store que prepara las carpetas necesarias para almacenar eventos.

---

### ⚽ Paso 3: Ejecutar el feeder de partidos

Este componente publica eventos de partidos de fútbol.

---

### 📰 Paso 4: Ejecutar el feeder de noticias

Este componente publica eventos de noticias relacionadas con partidos.

---

### 🧠 Paso 5: Ejecutar la aplicación de unidad de negocio

Este componente lee los eventos publicados por los feeders y los transforma en archivos JSON organizados por jornada y liga para consultas posteriores.

---

### ✅ Paso 6: Verificar resultados

Cuando se ejecuta `BusinessUnitApp`, se despliega un menú interactivo en consola para consultar los archivos generados:

```
1. Ver partidos por liga y jornada
2. Ver noticias por Match ID
3. Salir
Seleccione una opción: 
```

---

### 🏟️ Opción 1: Ver partidos por liga y jornada

Se pedirá:

```
🏆 Ingrese código de liga (PD, PL, SA, BL1, FL1): 
🔢 Ingrese el número de jornada: 
```

Ejemplo:

- Liga: `PD` (La Liga)
- Jornada: `32`

Se leerá el archivo `jornada32_PD_matches.json` y se mostrará la lista de partidos.

---

### 📰 Opción 2: Ver noticias por Match ID

Se pedirá el `Match ID` correspondiente a la noticia que se quiere ver.

```
🆔 Ingrese el Match ID:
```

Ejemplo:

- 🆔 Ingrese el Match ID: 1048

Se leerá el archivo `match1048_news.json` y se mostrarán todas las noticias asociadas.

---

### ❌ Opción 3: Salir

Finaliza la ejecución del programa.

---

## ⏱️ Frecuencia de Actualización de los Feeders

- **📰 Noticias (cada 6 horas):**
  - Generadas cada 6 horas para cubrir pre, post y directo del evento.

- **⚽ Partidos (cada 12 horas):**
  - Actualización cada 12 horas suficiente para reflejar cambios de jornada.

---

## 📁 Estructura del Proyecto

```
src/
 ├── main/java/
 │    ├── Common/               # Modelos y lógica común
 │    ├── football/feeder/      # Generación de partidos
 │    ├── news/feeder/          # Generación de noticias
 │    ├── businessunit/         # Lector/consumidor de eventos
      ├── Historical/feeder/    # Almacenamientos de datos
 │    └── eventstore/builder/   # Configurador del event store
 └── test/java/                 # Código de pruebas
```

---

## Principios y Patrones de diseño

### ✅ Fundamentos de buena arquitectura

- **Abstracción:** Uso de modelos claros (`Match`, `NewsItem`) separados de la lógica de persistencia o de eventos.
- **Modularidad:** Componentes independientes para feeders, event store y unidad de negocio.
- **Cohesión:** Cada clase tiene una única responsabilidad.
- **Acoplamiento:** Bajo acoplamiento gracias a ActiveMQ y separación de responsabilidades.

### ✅ Estilos arquitectónicos aplicados

- **Model-View-Controller (MVC)** implícito.
- **Hexagonal Architecture** para aislar lógica del sistema central.
- **Clean Architecture** clara distinción entre negocio y tecnología.

### ✅ Patrones de diseño aplicados

- **Observer:** mediante suscripción a tópicos en ActiveMQ.
- **Command:** el menú de consola representa acciones del usuario.
- **Event Sourcing:** los eventos son la fuente de verdad.
- **CQRS:** separación entre comandos (generación) y consultas (archivos JSON).
