# Лабораторная работа 2 — «Лови шарик»

Проект сделан как desktop-приложение на `Java Swing` и подготовлен к запуску из репозитория GitHub. Я вернул `pom.xml`, перевёл код в нормальную Maven-структуру `src/main/java` и добавил локальную Maven-настройку, чтобы IDE и Maven видели главный класс `catchball.GameApplication` корректно.

## Почему раньше была ошибка `Could not find or load main class`

Причина была не в отсутствии класса в коде, а в сборке проекта:

- проект раньше жил в нестандартной структуре `src/...`
- IDE могла не собрать класс в ожидаемую папку перед запуском
- в конфигурациях были старые следы JavaFX и старого JRE
- Maven в этой среде упирается в SSL и в кривое Java-окружение, если не зафиксировать правильные настройки

Из-за этого запуск видел имя `catchball.GameApplication`, но не находил готовый `.class` в classpath.

## Что я исправил

- добавил [pom.xml](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/pom.xml)
- перенёс исходники в `src/main/java`
- оставил ваш изменённый код игры
- добавил [.mvn/maven.config](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/.mvn/maven.config), чтобы Maven использовал проектный `.m2repo`
- положил в проект локальный `.m2repo`, чтобы сборка не зависела от проблемных сертификатов
- обновил общую run-конфигурацию [GameApplication.run.xml](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/.run/GameApplication.run.xml)
- добавил [run.cmd](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/run.cmd) для быстрого запуска на вашей текущей среде

## Что осталось в самой программе

Ваша видоизменённая версия игры сохранена:

- шарик двигается и меняет траекторию
- шарик реагирует на курсор
- одинарный клик даёт очки
- двойной клик ставит игру на паузу
- промах по полю сбрасывает серию
- работает бонусная система по варианту 4
- показываются счёт, серия, статус и скорость шарика

## Структура проекта

- [pom.xml](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/pom.xml)
- [.mvn/maven.config](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/.mvn/maven.config)
- [GameApplication.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/main/java/catchball/GameApplication.java)
- [GameController.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/main/java/catchball/controller/GameController.java)
- [GameModel.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/main/java/catchball/model/GameModel.java)
- [GamePanel.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/main/java/catchball/view/GamePanel.java)
- [GameApplication.run.xml](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/.run/GameApplication.run.xml)
- [run.cmd](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/run.cmd)

## Как запускать в GIGA IDE

1. Клонируйте репозиторий:

```powershell
git clone https://github.com/nutness114/bOis-242-Shcherbinin-lab2-var4.git
```

2. Откройте папку проекта в `GIGA IDE`.
3. Дождитесь индексации.
4. Если IDE увидит Maven-проект, нажмите `Reload`.
5. Откройте [GameApplication.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/main/java/catchball/GameApplication.java).
6. Запустите `main`.

Если IDE подхватит `.run`, можно запускать готовую конфигурацию `GameApplication`.

## Как запустить точно в вашей текущей среде

Самый надёжный способ на этом компьютере:

```powershell
cd "D:\Student\Щербинин\Кроссплатформы\Лабы\Labb 2"
.\run.cmd
```

Этот скрипт:

- использует `D:\GigaIde\jbr`
- собирает проект через Maven офлайн
- запускает `catchball.GameApplication`

## Что я проверил

Я подтвердил локально:

- `mvn compile` проходит при `JAVA_HOME=D:\GigaIde\jbr`
- `target\classes\catchball\GameApplication.class` создаётся
- приложение реально стартует из `target\classes`

## Важное уточнение

В этой среде Maven ломается, если запускается не через JBR от GIGA IDE. Поэтому я зафиксировал рабочий путь и добавил `run.cmd`. Для вашего компьютера это как раз тот вариант, который должен запускаться стабильнее всего прямо из клона репозитория.
