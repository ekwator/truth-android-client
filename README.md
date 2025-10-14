# Truth Training — Android Client
Truth Android Client (v0.1.0-pre)
=================================

Требования:
- Android Studio (Giraffe+), JDK 17
- Android SDK 24+

Сборка:
```bash
./gradlew assembleLocalDebug
```

Базовая конфигурация:
- BASE_URL задаётся через BuildConfig и productFlavors:
  - local: `http://10.0.2.2:8080`
  - remote: замените `https://truth-core.example.com`

Интеграция с Truth Core (v0.3.0):
- Эндпоинты: POST `/api/v1/auth`, GET `/api/v1/info`, `/api/v1/stats`, `/graph/json`, POST `/api/v1/refresh` (опц.)
- JWT хранится в SharedPreferences; авторизация через заголовок `Authorization: Bearer <token>`

Тесты:
```bash
./gradlew test
```

Примечания по интеграции:
- Доп. материалы см. в `truthcore_api/api_reference_link.md` и в репозитории Truth Core.

Mock-сборка:
- Запуск: `./gradlew assembleMockDebug`
- Источники: `app/src/mock/assets/api/*.json`
- Реализация: `MockTruthApi`, включается при flavor `mock`.

Взаимодействие с Truth Core из Android:
- Экран `MainDashboardActivity` предоставляет кнопки для действий:
  - Sync Peers, Submit Claim, Get Claims, Analyze Text, Get Stats
- Ответы отображаются как JSON на экране
- Пример запроса: `{"action":"get_stats"}`
