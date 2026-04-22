# Лабораторная работа 2 — «Лови шарик»

Проект переделан в обычное desktop-приложение на `Java Swing`, чтобы он запускался в `GIGA IDE` на чужом компьютере без `JavaFX`, без Maven-зависимостей и без путей, привязанных к вашей машине.

## Почему раньше не запускалось

Проблемы были сразу в нескольких местах:

- проект зависел от `JavaFX`
- путь к `jfxrt.jar` был жёстко зашит под ваш компьютер
- запуск хранился в `.idea/workspace.xml`, который не переносится через Git
- в репозитории лежали старые файлы из `out`
- `pom.xml` провоцировал лишние ошибки импорта и сборки в чужой среде

Из-за этого после обычного `git clone` проект мог открыться с ошибками даже до запуска.

## Что я исправил

- полностью убрал зависимость от `JavaFX`
- переписал интерфейс и запуск на стандартный `Swing`
- удалил проблемный `pom.xml`, чтобы проект открывался как обычный Java-проект
- убрал привязки к локальному `jfxrt.jar` из [Labb 2.iml](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/Labb%202.iml)
- добавил общую конфигурацию запуска [GameApplication.run.xml](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/.run/GameApplication.run.xml)
- убрал старые артефакты `out/...` из Git

## Что делает программа

- шарик движется по игровому полю
- одинарный клик по шарику засчитывает попадание
- двойной клик по шарику ставит игру на паузу и снимает её
- промах по полю сбрасывает серию
- кнопка `Новая игра` начинает новую партию
- на экране показываются счёт, серия, статус игры и скорость шарика

## Индивидуальный вариант 4

Реализована бонусная система за серию точных попаданий без промахов:

- 1-3 попадания подряд: `1` очко
- 4-6 попаданий подряд: `2` очка
- 7-9 попаданий подряд: `3` очка
- дальше награда увеличивается на `1` за каждые следующие 3 точных попадания подряд
- промах по полю полностью сбрасывает серию

## Файлы проекта

- [GameApplication.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/catchball/GameApplication.java)
- [GameController.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/catchball/controller/GameController.java)
- [GameModel.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/catchball/model/GameModel.java)
- [GamePanel.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/catchball/view/GamePanel.java)
- [GameApplication.run.xml](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/.run/GameApplication.run.xml)

## Как запускать в GIGA IDE на чужом компьютере

1. Клонируйте репозиторий:

```powershell
git clone https://github.com/nutness114/bOis-242-Shcherbinin-lab2-var4.git
```

2. Откройте папку проекта в `GIGA IDE`.
3. Дождитесь, пока IDE считает проект.
4. Откройте [GameApplication.java](/D:/Student/Щербинин/Кроссплатформы/Лабы/Labb%202/src/catchball/GameApplication.java).
5. Нажмите `Run` рядом с `main`.

Если `GIGA IDE` подхватит `.run`, можно запускать готовую конфигурацию `GameApplication` без ручной настройки.

## Ручной запуск через Java

Если нужно быстро проверить проект вручную:

```powershell
cd "D:\Student\Щербинин\Кроссплатформы\Лабы\Labb 2"
```

```powershell
if (Test-Path out-manual) { Remove-Item -LiteralPath out-manual -Recurse -Force }
New-Item -ItemType Directory -Path out-manual | Out-Null
& "D:\GigaIde\jbr\bin\javac.exe" --release 8 -d out-manual (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

```powershell
& "D:\GigaIde\jbr\bin\java.exe" -cp out-manual catchball.GameApplication
```

## Главное

Теперь проект не зависит от `JavaFX`, `jfxrt.jar`, `pom.xml` и ваших локальных путей. Для чужого компьютера это намного надёжнее.
