# TADAM Project - Currency Exchange

**Nume:** Cosmin BORSAN  
**Grupa:** Financial Computing (SPF)

## Descriere

TADAM Project este o aplicatie Android pentru conversii valutare. Utilizatorul poate alege doua monede, introduce o suma, vedea rezultatul conversiei si consulta istoricul conversiilor facute anterior.

Aplicatia foloseste cursuri valutare reale prin API-ul Frankfurter si salveaza local conversiile in baza de date Room. Include si un ecran de setari pentru schimbarea temei light/dark.

## Functionalitati principale

- conversie valutara intre monedele USD, EUR, GBP, RON, JPY si CHF;
- selectie pentru moneda sursa si moneda destinatie;
- buton pentru inversarea rapida a monedelor;
- afisarea rezultatului si a ratei de schimb folosite;
- istoric local al conversiilor;
- gruparea istoricului pe zile;
- ecran de setari cu mod intunecat;
- validarea inputului pentru suma introdusa;
- teste unitare pentru logica din `ConverterViewModel`.

## Ecrane

Aplicatia are trei ecrane principale:

1. **Currency Exchange** - ecranul principal pentru introducerea sumei si conversie.
2. **History** - lista conversiilor salvate local.
3. **Settings** - setarea temei light/dark.

Navigarea intre ecrane este facuta cu Jetpack Navigation Compose.

### Screenshot-uri

#### Tema light

| Currency Exchange | History | Settings |
| --- | --- | --- |
| <img src="screenshots/01_converter_screen.png" width="220" alt="Currency Exchange screen" /> | <img src="screenshots/02_history_screen.png" width="220" alt="History screen" /> | <img src="screenshots/03_settings_screen.png" width="220" alt="Settings screen" /> |

#### Tema dark

| Currency Exchange | History | Settings |
| --- | --- | --- |
| <img src="screenshots/04_converter_screen_dark.png" width="220" alt="Currency Exchange dark screen" /> | <img src="screenshots/05_history_screen_dark.png" width="220" alt="History dark screen" /> | <img src="screenshots/06_settings_screen_dark.png" width="220" alt="Settings dark screen" /> |

## Tehnologii folosite

- **Kotlin** - limbajul principal al aplicatiei;
- **Jetpack Compose** - interfata grafica declarativa;
- **Material 3** - componente UI;
- **Navigation Compose** - navigare intre ecrane;
- **ViewModel + StateFlow** - administrarea starii UI;
- **Retrofit + Gson** - comunicare cu API-ul extern;
- **Frankfurter API** - sursa pentru ratele de schimb valutar;
- **Room** - baza de date locala pentru istoric;
- **DataStore Preferences** - salvarea preferintei de tema;
- **JUnit + kotlinx-coroutines-test** - teste unitare.

## Arhitectura aplicatiei

Aplicatia urmeaza o arhitectura pe straturi:

```mermaid
flowchart TD
    User[Utilizator] --> UI[Jetpack Compose Screens]
    UI --> VM[ViewModels]
    VM --> Repo[CurrencyRepository]
    Repo --> API[Frankfurter API prin Retrofit]
    Repo --> DB[Room Database]
    UI --> Prefs[DataStore Preferences]
```

### Rolul componentelor

- `ConverterScreen`, `HistoryScreen`, `SettingsScreen` definesc interfata aplicatiei.
- `ConverterViewModel` gestioneaza suma, monedele selectate, rezultatul, erorile si istoricul.
- `SettingsViewModel` gestioneaza preferinta pentru dark mode.
- `CurrencyRepository` centralizeaza accesul la API si baza de date.
- `FrankfurterApiService` descrie endpoint-ul Retrofit pentru rate valutare.
- `ConversionHistoryDao` lucreaza cu tabela locala `conversion_history`.
- `ThemePreferences` foloseste DataStore pentru persistarea temei.

## Fluxul unei conversii

```mermaid
sequenceDiagram
    participant U as Utilizator
    participant S as ConverterScreen
    participant VM as ConverterViewModel
    participant R as CurrencyRepository
    participant API as Frankfurter API
    participant DB as Room

    U->>S: Introduce suma si apasa Convert
    S->>VM: convert()
    VM->>R: convert(from, to, amount)
    R->>API: Cere rata de schimb
    API-->>R: Returneaza rata
    R-->>VM: Returneaza suma convertita
    VM->>R: saveConversion(conversion)
    R->>DB: Salveaza conversia
    VM-->>S: Actualizeaza rezultatul si istoricul
```

## Cerinte acoperite

| Cerinta | Implementare |
| --- | --- |
| Kotlin | Proiectul este scris in Kotlin |
| Jetpack Compose | Ecranele sunt construite cu Compose |
| Cel putin doua ecrane | Exista trei ecrane: conversie, istoric, setari |
| Jetpack Navigation | `AppNavigation` foloseste `NavHost` si rute Compose |
| Arhitectura recomandata | UI + ViewModel + Repository + Data layer |
| Integrare API | Retrofit consuma Frankfurter API |
| Baza de date | Room salveaza istoricul conversiilor |
| Protectie la SQL injection | Room foloseste DAO si query-uri parametrizate/controlate |
| Comunicare securizata | API-ul este accesat prin HTTPS |
| Settings screen | Exista ecran de setari pentru dark mode |
| Unit testing | Exista teste pentru `ConverterViewModel` |
| Cod modular | Codul este impartit in pachete `ui`, `data`, `model`, `theme`, `navigation` |

## Structura proiectului

```text
app/src/main/java/com/example/tadamproject
├── data
│   ├── local          # Room database si DAO
│   ├── preferences    # DataStore pentru tema
│   ├── remote         # Retrofit API service
│   └── CurrencyRepository.kt
├── model              # Entitatea ConversionHistory
├── ui
│   ├── converter      # Conversie si istoric
│   ├── navigation     # Rutele aplicatiei
│   ├── settings       # Setari si dark mode
│   └── theme          # Culori si tema
├── AppContainer.kt
├── MainActivity.kt
└── TadamApplication.kt
```

## Rulare

Pentru rulare este necesar Android Studio cu Android SDK instalat.

1. Deschideti proiectul in Android Studio.
2. Asteptati sincronizarea Gradle.
3. Porniti un emulator sau conectati un telefon Android.
4. Rulati configuratia `app`.

Alternativ, din terminal:

```bash
./gradlew installDebug
```

## Testare

Testele unitare pot fi rulate cu:

```bash
./gradlew test
```

Testele verifica scenarii importante din conversie:

- actualizarea rezultatului dupa conversie;
- salvarea conversiei in istoric;
- tratarea erorilor;
- inversarea monedelor si resetarea rezultatului.

## Concluzie

Aplicatia acopera cerintele proiectului printr-o implementare Android completa: interfata Compose, navigare intre ecrane, API extern, persistenta locala, setari, validare de input si teste unitare. README-ul poate fi folosit si ca suport vizual pentru prezentarea finala.
