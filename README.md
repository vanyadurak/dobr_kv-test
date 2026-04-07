## Быстрый старт

### Предварительные требования

- Kotlin
- Gradle
- Docker & Docker Compose

### Запуск на локальной машине

#### Подготовка

Клонируйте репозиторий на вашу локальную машину:

```bash
git clone https://github.com/vanyadurak/dobr_kv-test.git
cd dobr_kv-test
```

#### Конфигурация окружения

Приложение использует переменные окружения. Создайте файл .env на основе примера:

```bash
cp .env.example .env
```

**Важно**: Файл .env.example содержит пример значений по умолчанию; замените их на ваши реальные параметры.

#### Запуск приложения на локальной машине

**Важно**: Перед запуском приложения установите переменное окружение в приложение.

Как это сделать в Intellij IDEA:

1. Выберите Edit \
   ![Изменение конфигурациия](/images/example-1.png)
2. Начните добавлять новые переменные в Environment variables.\
   ![Включение переменных окружений](/images/example-2.png)
3. В предложенном окне выберите опцию добавления файла.\
   ![Выбор файла .env](/images/example-3.png)
4. Выберети файл .env.\
   ![Запуск приложения](/images/example-4.png)

#### Запуск приложения через Docker Compose

Чтобы запустить приложение через Docker Compose, выполните следующие шаги в корне проекта:

```
1. ./gradlew clean build
2. ./gradlew bootBuildImage --imageName=dobr_kv-test
3. docker compose up -d
```

#### Проверка работоспособности

После запуска сервисы будут доступны по следующим адресам:

- API Endpoint: http://localhost:8080/api/tasks
- Swagger UI: http://localhost:8080/swagger-ui/index.html (Документация)
- Health Check: http://localhost:8080/actuator/health