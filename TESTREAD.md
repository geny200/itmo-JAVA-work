# Содержание <a id="plan"></a>

* [**Домашние задния**](README.md)
* [**Артефакты для тестов**](./artifacts)
* **Информация о тестах**
    1. [Обход файлов](#HW1)
    2. [Множество на массиве](#HW2)
    3. [Студенты](#HW3)
    4. [Implementor](#HW4)
    5. [Jar Implementor](#HW5)
    6.    
    7. [Итеративный параллелизм](#HW7)
    8. [Параллельный запуск](#HW8)
    9. [Web Crawler](#HW9)
    10. [HelloUDP](#HW10)
    11. [Физические лица](https://github.com/geny200/itmo-bank-test)
    12. 
    13. [Статистика текста](./java-solutions/ru/ifmo/test/konovalov/i18n)

---
# Тесты к курсу «Технологии Java»

## Домашнее задание 10. HelloUDP <a id="HW10"></a> [↑](#plan)

Интерфейсы

 * `HelloUDPClient` должен реализовывать интерфейс
    [HelloClient](./java-solutions/info/kgeorgiy/java/advanced/hello/HelloClient.java)
 * `HelloUDPServer` должен реализовывать интерфейс
    [HelloServer](./java-solutions/info/kgeorgiy/java/advanced/hello/HelloServer.java)

Тестирование

 * простой вариант:
    * клиент:
        ```info.kgeorgiy.java.advanced.hello client <полное имя класса>```
    * сервер:
        ```info.kgeorgiy.java.advanced.hello server <полное имя класса>```
 * сложный вариант:
    * на противоположной стороне находится система, дающая ответы на различных языках
    * клиент:
        ```info.kgeorgiy.java.advanced.hello client-i18n <полное имя класса>```
    * сервер:
        ```info.kgeorgiy.java.advanced.hello server-i18n <полное имя класса>```
 * продвинутый вариант:
    * на противоположной стороне находится старая система,
      не полностью соответствующая последней спецификации
    * клиент:
        ```info.kgeorgiy.java.advanced.hello client-evil <полное имя класса>```
    * сервер:
        ```info.kgeorgiy.java.advanced.hello server-evil <полное имя класса>```

Исходный код тестов:

* [Клиент](./java-solutions/info/kgeorgiy/java/advanced/hello/HelloClientTest.java)
* [Сервер](./java-solutions/info/kgeorgiy/java/advanced/hello/HelloServerTest.java)


## Домашнее задание 9. Web Crawler <a id="HW9"></a> [↑](#plan)

Тестирование

 * простой вариант:
    ```info.kgeorgiy.java.advanced.crawler easy <полное имя класса>```
 * сложный вариант:
    ```info.kgeorgiy.java.advanced.crawler hard <полное имя класса>```

Исходный код тестов:

* [интерфейсы и вспомогательные классы](./java-solutions/info/kgeorgiy/java/advanced/crawler/)
* [простой вариант](./java-solutions/info/kgeorgiy/java/advanced/crawler/CrawlerEasyTest.java)
* [сложный вариант](./java-solutions/info/kgeorgiy/java/advanced/crawler/CrawlerHardTest.java)


## Домашнее задание 8. Параллельный запуск <a id="HW8"></a> [↑](#plan)

Тестирование

 * простой вариант:
    ```info.kgeorgiy.java.advanced.mapper scalar <полное имя класса>```
 * сложный вариант:
    ```info.kgeorgiy.java.advanced.mapper list <полное имя класса>```
 * продвинутый вариант:
    ```info.kgeorgiy.java.advanced.mapper advanced <полное имя класса>```

Исходный код тестов:

* [простой вариант](./java-solutions/info/kgeorgiy/java/advanced/mapper/ScalarMapperTest.java)
* [сложный вариант](./java-solutions/info/kgeorgiy/java/advanced/mapper/ListMapperTest.java)
* [продвинутый вариант](./java-solutions/info/kgeorgiy/java/advanced/mapper/AdvancedMapperTest.java)


## Домашнее задание 7. Итеративный параллелизм <a id="HW7"></a> [↑](#plan)

Тестирование

 * простой вариант:
   ```info.kgeorgiy.java.advanced.concurrent scalar <полное имя класса>```

   Класс должен реализовывать интерфейс
   [ScalarIP](./java-solutions/info/kgeorgiy/java/advanced/concurrent/ScalarIP.java).

 * сложный вариант:
   ```info.kgeorgiy.java.advanced.concurrent list <полное имя класса>```

   Класс должен реализовывать интерфейс
   [ListIP](./java-solutions/info/kgeorgiy/java/advanced/concurrent/ListIP.java).

 * продвинутый вариант:
   ```info.kgeorgiy.java.advanced.concurrent advanced <полное имя класса>```

   Класс должен реализовывать интерфейс
   [AdvancedIP](./java-solutions/info/kgeorgiy/java/advanced/concurrent/AdvancedIP.java).

Исходный код тестов:

* [простой вариант](./java-solutions/info/kgeorgiy/java/advanced/concurrent/ScalarIPTest.java)
* [сложный вариант](./java-solutions/info/kgeorgiy/java/advanced/concurrent/ListIPTest.java)
* [продвинутый вариант](./java-solutions/info/kgeorgiy/java/advanced/concurrent/AdvancedIPTest.java)


## Домашнее задание 5. JarImplementor <a id="HW5"></a> [↑](#plan)

Класс должен реализовывать интерфейс
[JarImpler](./java-solutions/info/kgeorgiy/java/advanced/implementor/JarImpler.java).

Тестирование

 * простой вариант:
    ```info.kgeorgiy.java.advanced.implementor jar-interface <полное имя класса>```
 * сложный вариант:
    ```info.kgeorgiy.java.advanced.implementor jar-class <полное имя класса>```
 * продвинутый вариант:
    ```info.kgeorgiy.java.advanced.implementor jar-advanced <полное имя класса>```

Исходный код тестов:

* [простой вариант](./java-solutions/info/kgeorgiy/java/advanced/implementor/InterfaceJarImplementorTest.java)
* [сложный вариант](./java-solutions/info/kgeorgiy/java/advanced/implementor/ClassJarImplementorTest.java)
* [продвинутый вариант](./java-solutions/info/kgeorgiy/java/advanced/implementor/AdvancedJarImplementorTest.java)


## Домашнее задание 4. Implementor <a id="HW4"></a> [↑](#plan)

Класс должен реализовывать интерфейс
[Impler](./java-solutions/info/kgeorgiy/java/advanced/implementor/Impler.java).

Тестирование

 * простой вариант:
    ```info.kgeorgiy.java.advanced.implementor interface <полное имя класса>```
 * сложный вариант:
    ```info.kgeorgiy.java.advanced.implementor class <полное имя класса>```
 * продвинутый вариант:
    ```info.kgeorgiy.java.advanced.implementor advanced <полное имя класса>```

Исходный код тестов:

* [простой вариант](./java-solutions/info/kgeorgiy/java/advanced/implementor/InterfaceImplementorTest.java)
* [сложный вариант](./java-solutions/info/kgeorgiy/java/advanced/implementor/ClassImplementorTest.java)
* [продвинутый вариант](./java-solutions/info/kgeorgiy/java/advanced/implementor/AdvancedImplementorTest.java)


## Домашнее задание 3. Студенты <a id="HW3"></a> [↑](#plan)

Тестирование

 * простой вариант:
    ```info.kgeorgiy.java.advanced.student StudentQuery <полное имя класса>```
 * сложный вариант:
    ```info.kgeorgiy.java.advanced.student StudentGroupQuery <полное имя класса>```

Исходный код

 * простой вариант:
    [интерфейс](./java-solutions/info/kgeorgiy/java/advanced/student/StudentQuery.java),
    [тесты](./java-solutions/info/kgeorgiy/java/advanced/student/StudentQueryTest.java)
 * сложный вариант:
    [интерфейс](./java-solutions/info/kgeorgiy/java/advanced/student/StudentGroupQuery.java),
    [тесты](./java-solutions/info/kgeorgiy/java/advanced/student/StudentGroupQueryTest.java)
 * продвинутый вариант:
    [интерфейс](./java-solutions/info/kgeorgiy/java/advanced/student/AdvancedStudentGroupQuery.java),
    [тесты](./java-solutions/info/kgeorgiy/java/advanced/student/AdvancedStudentGroupQueryTest.java)


## Домашнее задание 2. ArraySortedSet <a id="HW2"></a> [↑](#plan)

Тестирование

 * простой вариант:
    ```info.kgeorgiy.java.advanced.arrayset SortedSet <полное имя класса>```
 * сложный вариант:
    ```info.kgeorgiy.java.advanced.arrayset NavigableSet <полное имя класса>```

Исходный код тестов:

 * [простой вариант](./java-solutions/info/kgeorgiy/java/advanced/arrayset/SortedSetTest.java)
 * [сложный вариант](./java-solutions/info/kgeorgiy/java/advanced/arrayset/NavigableSetTest.java)


## Домашнее задание 1. Обход файлов <a id="HW1"></a> [↑](#plan)

Для того, чтобы протестировать программу:

 * Скачайте
    * тесты
        * [info.kgeorgiy.java.advanced.base.jar](artifacts/info.kgeorgiy.java.advanced.base.jar)
        * [info.kgeorgiy.java.advanced.walk.jar](artifacts/info.kgeorgiy.java.advanced.walk.jar)
    * и библиотеки к ним:
        * [junit-4.11.jar](lib/junit-4.11.jar)
        * [hamcrest-core-1.3.jar](lib/hamcrest-core-1.3.jar)
 * Откомпилируйте решение домашнего задания
 * Протестируйте домашнее задание
    * Текущая директория должна:
       * содержать все скачанные `.jar` файлы;
       * содержать скомпилированное решение;
       * __не__ содержать скомпилированные самостоятельно тесты.
    * простой вариант:
        ```java -cp . -p . -m info.kgeorgiy.java.advanced.walk Walk <полное имя класса>```
    * сложный вариант:
        ```java -cp . -p . -m info.kgeorgiy.java.advanced.walk RecursiveWalk <полное имя класса>```

Исходный код тестов:

 * [простой вариант](./java-solutions/info/kgeorgiy/java/advanced/walk/WalkTest.java)
 * [сложный вариант](./java-solutions/info/kgeorgiy/java/advanced/walk/RecursiveWalkTest.java)
