## Java Spring Homework

<img src="images/synergy_title_image.png" width="500" height="300">

### Description

Project with application on Spring. Goal of this
project is to improve my Spring knowledge.
All tasks completed according to the `Synergy University`
tasks.

### Task 2:

Создать приложение с помощью `Spring IoC`, чтобы 
познакомиться с основной функциональностью `IoC`, 
на которой строится весь `Spring`.

1) Нужно создать простое приложение (мини чат бот) на Spring,
которое будет выводить в консоль список контактов.

2) При помощи блокнота создать файл contacts.csv и
добавить туда 5 человек. Фамилия, имя, отчество, номер
телефона. Сохранить в виде CSV файла («Сохранить как…»
пишем «contacts.csv» и нажимаем «сохранить»)

Содержимое файла должно получиться примерно следующее:

```text
LastName,FirstName,MiddleName,Phone
Leonov,Alexey,Vladimirovich,79990190299
Ivanov,Oleg,Petrovich,79530191296
Stepanov,Petr,Ivanovich,79881233245
Petrakov,Alexandr,Alexeyevich,79532111296
Ivanova,Oksana,Vladimirocna,79522191213
```

3) Для чтения файла необходимо применить аннотацию @Value,
   предварительно ознакомившись с документацией на официальном
   сайте Spring (см. вспомогательные материалы к домашнему заданию)

4) Создать новый интерфейс ContactService и его имплементацию
   ContactServiceCsvImpl. Названия метода для поиска контактов
   назвать findAll(). В имплементации должна находиться вся логика
   по чтению и обработке контактов в CSV файле

5) Все зависимости должны быть настроены в IoC контейнере

6) В консоли нужно вывести список контактов в следующем
   формате: «LastName FirstName MiddleName, Phone»

7) Опциональное задание со звездочкой*: приложение
   должно корректно запускаться с помощью "java -jar

#### Рекомендации :

- Все классы в приложении должны решать строго
определённую задачу (см. "Правила оформления
кода", прикреплённые в доп. материале)

- Имя ресурса с контактами (CSV-файла)
  можно захардкодить строчкой

- CSV с вопросами читается именно как ресурс, а не как файл

- Весь ввод-вывод осуществляется на английском языке.
  Это нужно во избежание ошибок с кодировками

- Крайне желательно написать юнит-тест какого-нибудь
  сервиса. Оцениваться будет только попытка написать тест

- Помним - "без фанатизма"

#### Результат :
Простое приложение (мини чат-бот) на Spring, которое выводит
в консоль список контактов из файла contacts.csv при запуске

Структура :

```text
src
├──/main/java/io/mkalugin/synergy/
│    ├── config
│    │     └── AppConfig.java
│    ├── model
│    │     └── Contact.java
│    ├── service
│    │     ├── ContactService.java
│    │     └── ContactServiceImpl.java     
│    └── SynergyApplication.java
└──/test/java/io/mkalugin/synergy/
     └── ContactServiceImplTest.java
```

Для проверки необходимо запустить файл 
`SynergyApplication.java`

### Запуск приложения с помошью jar файла:

```bash
mvn clean package
java -jar target/synergy-0.0.1-SNAPSHOT.jar --contacts.file.path=C:\Users\mkalugin\IdeaProjects\synergy\src\main\resources\contacts.csv
```

#### Вывод в консоли :
<img src='images/13.07_screen_jar_file.png' width=500 height=200>

### Полезные ссылки :

[Ресурсы Spring](https://docs.spring.io/spring-framework/reference/core/resources.html)

### Автор : Калугин Максим