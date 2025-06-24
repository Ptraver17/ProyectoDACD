# Proyecto Final - Fútbol y Noticias
Este proyecto recoge y analiza información de partidas y noticias de fútbol de las 5 grandes ligas europeas. Utiliza arquitectura orientada a eventos, almacenamiento loca y análisis de impacto mediático.

## Estructura 
- **FootballFeederApp**: consulta partidos desde Football Data API.
- **NewsFeederApp**: consulta noticias relacionadas desde NewsAPI.
- **EventStoreBuilder**: escucha y guarda todos los eventos en archivos locales.
- **HistoricalFeederApp**: carga todos los partidos y noticias de las 30 primeras jornadas.
- **BusinessUnitApp**: permite consultar partidos, noticias y ranking de impacto desde consola.

## Instrucciones de ejecución
1. Inicia el broker de mensajes de ActiveMQ
2. (Paso opcional) Ejecuta HistoricalFeederApp
3. Ejecuta los feeders (FootballFeederApp y NewsFeederApp)
4. Ejecuta EventStoreBuilder
5. Ejecuta BusinessUnitApp y consulta los datos por consola

## Propuesta de valor
Este sistema recopila datos sobre partidos de fútbol en las 5 grandes ligas europeas y noticias relacionadas con cada partido en base a una ID que nos permite identificar cada partido en las APIs usadas. Adicionalmente, analizamos el impacto mediático de cada partido en los medios, dando así un ranking para la jornada que el usuario elija. Este impacto se analiza de la siguiente manera:
- Analiza cuántas noticias genera cada partdo
- Detecta palabras clave relevantes (lesión, derbi, baja, gol, etc.)
- Con estos datos, genera un índice que sirve para ordenar los partidos
- Así, permite identificar qué encuentros fueron más comentados en los medios, algo que las APIs originales no nos ofrecen directamente.

## Almacenamiento
Los datos se guardan en 3 archivos distintos:
- matches.events: almacena partidos en formatos JSONL, uno por línea.
- news.events: almacena noticias relacionadas con cada partido en formato JSONL.
- news.csv: contiene un resumen tabular de las noticias, indexado por Match ID, útil para evitar duplicados y facilitar consultas.

# Diagrama de clases

![Diagrama](https://github.com/user-attachments/assets/a9faad5b-1cab-42aa-8665-295dae2a1a2a)

# Tecnologías usadas
- **Java**: Lenguaje principal del proyecto.
- **Apache ActiveMQ**: Sistema de mensajería usado para comunicar los componentes.
- **Jackson**: Para leer y escribir archivos JSON de manera sencilla.
- **JSoup**: para hacer llamadas HTTP a las APIs utilizadas.
- **StarUML**: Para crear el diagrama de clase del proyecto.
