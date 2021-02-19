# Содержание <a id="plan"></a>

* **Домашние задния**
    1. [Обход файлов](#HW1)
    2. [Множество на массиве](#HW2)
    3. [Студенты](#HW3)
    4. [Implementor](#HW4)
    5. [Jar Implementor](#HW5)
    6. [Javadoc](#HW6)
    7. [Итеративный параллелизм](#HW7)
    8. [Параллельный запуск](#HW8)
    9. [Web Crawler](#HW9)
    10. [HelloUDP](#HW10)
    11. [Физические лица](#HW11)
    12. [HelloNonblockingUDP](#HW12)
    13. [Статистика текста](#HW13)
* [**Артефакты для тестов**](./artifacts)
* [**Информация о тестах**](TESTREAD.md)

---
# Домашние задания
## Домашнее задание 1. Обход файлов <a id="HW1"></a> [↑](#plan)

#### 1. Разработайте класс Walk, осуществляющий подсчет хеш-сумм файлов.

* Формат запуска:

```commandline
java Walk <входной файл> <выходной файл>
```

* Входной файл содержит список файлов, которые требуется обойти.
* Выходной файл должен содержать по одной строке для каждого файла. Формат строки:
  ```<шестнадцатеричная хеш-сумма> <путь к файлу>```
* Для подсчета хеш-суммы используйте алгоритм [FNV](https://ru.wikipedia.org/wiki/FNV).
* Если при чтении файла возникают ошибки, укажите в качестве его хеш-суммы 00000000.
* Кодировка входного и выходного файлов — UTF-8.
* Если родительская директория выходного файла не существует, то соответствующий путь надо создать.
* Размеры файлов могут превышать размер оперативной памяти.

#### Пример

Входной файл

                        java/info/kgeorgiy/java/advanced/walk/samples/1
                        java/info/kgeorgiy/java/advanced/walk/samples/12
                        java/info/kgeorgiy/java/advanced/walk/samples/123
                        java/info/kgeorgiy/java/advanced/walk/samples/1234
                        java/info/kgeorgiy/java/advanced/walk/samples/1
                        java/info/kgeorgiy/java/advanced/walk/samples/binary
                        java/info/kgeorgiy/java/advanced/walk/samples/no-such-file

Выходной файл

                        050c5d2e java/info/kgeorgiy/java/advanced/walk/samples/1
                        2076af58 java/info/kgeorgiy/java/advanced/walk/samples/12
                        72d607bb java/info/kgeorgiy/java/advanced/walk/samples/123
                        81ee2b55 java/info/kgeorgiy/java/advanced/walk/samples/1234
                        050c5d2e java/info/kgeorgiy/java/advanced/walk/samples/1
                        8e8881c5 java/info/kgeorgiy/java/advanced/walk/samples/binary
                        00000000 java/info/kgeorgiy/java/advanced/walk/samples/no-such-file

---

### Усложненная версия:

* Разработайте класс ```RecursiveWalk```, осуществляющий подсчет хеш-сумм файлов в директориях
* Входной файл содержит список файлов и директорий, которые требуется обойти. Обход директорий осуществляется
  рекурсивно.

#### Пример

Входной файл

                        java/info/kgeorgiy/java/advanced/walk/samples/binary
                        java/info/kgeorgiy/java/advanced/walk/samples

Выходной файл

                        8e8881c5 java/info/kgeorgiy/java/advanced/walk/samples/binary
                        050c5d2e java/info/kgeorgiy/java/advanced/walk/samples/1
                        2076af58 java/info/kgeorgiy/java/advanced/walk/samples/12
                        72d607bb java/info/kgeorgiy/java/advanced/walk/samples/123
                        81ee2b55 java/info/kgeorgiy/java/advanced/walk/samples/1234
                        8e8881c5 java/info/kgeorgiy/java/advanced/walk/samples/binary

#### При выполнении задания следует обратить внимание на:

* Дизайн и обработку исключений, диагностику ошибок.
* Программа должна корректно завершаться даже в случае ошибки.
* Корректная работа с вводом-выводом.
* Отсутствие утечки ресурсов.

---

## Домашнее задание 2. Множество на массиве <a id="HW2"></a> [↑](#plan)

#### Разработайте класс ```ArraySet```, реализующие неизменяемое упорядоченное множество.

* Класс ```ArraySet``` должен реализовывать интерфейс ```SortedSet``` (упрощенная версия) или ```NavigableSet``` (
  усложненная версия).
* Все операции над множествами должны производиться с максимально возможной асимптотической эффективностью.

##### При выполнении задания следует обратить внимание на:

* Применение стандартных коллекций.
* Избавление от повторяющегося кода.

---

## Домашнее задание 3. Студенты <a id="HW3"></a> [↑](#plan)

#### Разработайте класс ```StudentDB```, осуществляющий поиск по базе данных студентов.

* Класс StudentDB должен реализовывать интерфейс StudentQuery (простая версия) или StudentGroupQuery (сложная версия).
* Каждый метод должен состоять из ровно одного оператора. При этом длинные операторы надо разбивать на несколько строк.

###### При выполнении задания следует обратить внимание на:

* Применение лямбда-выражений и потоков.
* Избавление от повторяющегося кода.

---

## Домашнее задание 4. Implementor <a id="HW4"></a> [↑](#plan)

#### Реализуйте класс ```Implementor```, который будет генерировать реализации классов и интерфейсов.

* Аргумент командной строки: полное имя класса/интерфейса, для которого требуется сгенерировать реализацию.
* В результате работы должен быть сгенерирован java-код класса с суффиксом Impl, расширяющий (реализующий) указанный
  класс (интерфейс).
* Сгенерированный класс должен компилироваться без ошибок.
* Сгенерированный класс не должен быть абстрактным.
* Методы сгенерированного класса должны игнорировать свои аргументы и возвращать значения по умолчанию.

###### В задании выделяются три уровня сложности:

* ***Простой*** — ```Implementor``` должен уметь реализовывать только интерфейсы (но не классы). Поддержка generics не
  требуется.
* ***Сложный*** — ```Implementor``` должен уметь реализовывать и классы и интерфейсы. Поддержка generics не требуется.
* ***Бонусный*** — ```Implementor``` должен уметь реализовывать generic-классы и интерфейсы. Сгенерированный код должен
  иметь корректные параметры типов и не порождать UncheckedWarning.

---

## Домашнее задание 5. Jar Implementor <a id="HW5"></a> [↑](#plan)

* Создайте .jar-файл, содержащий скомпилированный ```Implementor``` и сопутствующие классы.
    * Созданный .jar-файл должен запускаться командой java -jar.
    * Запускаемый .jar-файл должен принимать те же аргументы командной строки, что и класс Implementor.
* Модифицируйте Implemetor так, что бы при запуске с аргументами -jar имя-класса файл.jar он генерировал .jar-файл с
  реализацией соответствующего класса (интерфейса).
* Для проверки, кроме исходного кода так же должны быть предъявлены:
    * скрипт для создания запускаемого .jar-файла, в том числе, исходный код манифеста;
    * запускаемый .jar-файл.
* Данное домашнее задание сдается только вместе с предыдущим. Предыдущее домашнее задание отдельно сдать будет нельзя.
* ***Сложная версия***. Решение должно быть модуляризовано.

---

## Домашнее задание 6. Javadoc <a id="HW6"></a> [↑](#plan)

* Документируйте класс ```Implementor``` и сопутствующие классы с применением Javadoc.
    * Должны быть документированы все классы и все члены классов, в том числе ```private```.
    * Документация должна генерироваться без предупреждений.
    * Сгенерированная документация должна содержать корректные ссылки на классы стандартной библиотеки.
* Для проверки, кроме исходного кода так же должны быть предъявлены:
    * скрипт для генерации документации;
    * сгенерированная документация.
* Данное домашнее задание сдается только вместе с предыдущим. Предыдущее домашнее задание отдельно сдать будет нельзя.

---

## Домашнее задание 7. Итеративный параллелизм <a id="HW7"></a> [↑](#plan)

#### Реализуйте класс ```IterativeParallelism```, который будет обрабатывать списки в несколько потоков.

* В *простом* варианте должны быть реализованы следующие методы:
    * ```minimum(threads, list, comparator)``` — первый минимум;
    * ```maximum(threads, list, comparator)``` — первый максимум;
    * ```all(threads, list, predicate)``` — проверка, что все элементы списка
      удовлетворяют [предикату](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html);
    * ```any(threads, list, predicate)``` — проверка, что существует элемент списка,
      удовлетворяющий [предикату](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html).
* В *сложном* варианте должны быть дополнительно реализованы следующие методы:
    * ```filter(threads, list, predicate)``` — вернуть список, содержащий элементы
      удовлетворяющие [предикату](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html);
    * ```map(threads, list, function)``` — вернуть список, содержащий результаты
      применения [функции](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html);
    * ```join(threads, list)``` — конкатенация строковых представлений элементов списка.
* Во все функции передается параметр ```threads``` — сколько потоков надо использовать при вычислении. Вы можете
  рассчитывать, что число потоков не велико.
* Не следует рассчитывать на то, что переданные компараторы, предикаты и функции работают быстро.
* При выполнении задания нельзя использовать *Concurrency Utilities*.
* Рекомендуется подумать, какое отношение к заданию имеют [моноиды](https://en.wikipedia.org/wiki/Monoid).

---

## Домашнее задание 8. Параллельный запуск <a id="HW8"></a> [↑](#plan)

* Напишите класс ```ParallelMapperImpl```, реализующий интерфейс ```ParallelMapper```.

```java
public interface ParallelMapper extends AutoCloseable {
    <T, R> List<R> run(
            Function<? super T, ? extends R> f,
            List<? extends T> args
    ) throws InterruptedException;

    @Override
    void close() throws InterruptedException;
}
```

*
    * Метод ```run``` должен параллельно вычислять функцию ```f``` на каждом из указанных аргументов (```args```).
    * Метод ```close``` должен останавливать все рабочие потоки.
    * Конструктор ```ParallelMapperImpl(int threads)``` создает ```threads``` рабочих потоков, которые могут быть
      использованы для распараллеливания.
    * К одному ```ParallelMapperImpl``` могут одновременно обращаться несколько клиентов.
    * Задания на исполнение должны накапливаться в очереди и обрабатываться в порядке поступления.
    * В реализации не должно быть активных ожиданий.
* Добавьте класс ```IterativeParallelism``` так, чтобы он мог использовать ```ParallelMapper```.
    * Добавьте конструктор ```IterativeParallelism(ParallelMapper)```
    * Методы класса должны делить работу на ```threads``` фрагментов и исполнять их при помощи ```ParallelMapper```.
    * Должна быть возможность одновременного запуска и работы нескольких клиентов, использующих
      один ```ParallelMapper```.
    * При наличии ```ParallelMapper``` сам ```IterativeParallelism``` новые потоки создавать не должен.

---

## Домашнее задание 9. Web Crawler <a id="HW9"></a> [↑](#plan)

#### Напишите потокобезопасный класс WebCrawler, который будет рекурсивно обходить сайты.

* Класс ```WebCrawler``` должен иметь конструктор

```java
 public WebCrawler(Downloader downloader,int downloaders,int extractors,int perHost)
```

*
    * ```downloader``` позволяет скачивать страницы и извлекать из них ссылки;
    * ```downloaders``` — максимальное число одновременно загружаемых страниц;
    * ```extractors``` — максимальное число страниц, из которых извлекаются ссылки;
    * ```perHost``` — максимальное число страниц, одновременно загружаемых c одного хоста. Для опредения хоста следует
      использовать метод ```getHost``` класса ```URLUtils``` из тестов.
* Класс ```WebCrawler``` должен реализовывать интерфейс ```Crawler```

```java
public interface Crawler extends AutoCloseable {
    Result download(String url, int depth);

    void close();
}
```

*
    * Метод ```download``` должен рекурсивно обходить страницы, начиная с указанного URL на указанную глубину и
      возвращать список загруженных страниц и файлов. Например, если глубина равна 1, то должна быть загружена только
      указанная страница. Если глубина равна 2, то указанная страница и те страницы и файлы, на которые она ссылается и
      так далее. Этот метод может вызываться параллельно в нескольких потоках.
    * Загрузка и обработка страниц (извлечение ссылок) должна выполняться максимально параллельно, с учетом ограничений
      на число одновременно загружаемых страниц (в том числе с одного хоста) и страниц, с которых загружаются ссылки.
    * Для распараллеливания разрешается создать до ```downloaders``` + ```extractors``` вспомогательных потоков.
    * Загружать и/или извлекать ссылки из одной и той же страницы в рамках одного обхода (```download```) запрещается.
    * Метод ```close``` должен завершать все вспомогательные потоки.
* Для загрузки страниц должен применяться ```Downloader```, передаваемый первым аргументом конструктора.

```java
public interface Downloader {
    public Document download(final String url) throws IOException;
}
```

*
    * Метод download загружает документ по его адресу ([URL](http://tools.ietf.org/html/rfc3986)).
    * Документ позволяет получить ссылки по загруженной странице:

```java
public interface Document {
    List<String> extractLinks() throws IOException;
}
```

Ссылки, возвращаемые документом являются абсолютными и имеют схему ```http``` или ```https```.

* Должен быть реализован метод main, позволяющий запустить обход из командной строки
    * Командная строка ```commandline WebCrawler url [depth [downloads [extractors [perHost]]]]```
    * Для загрузки страниц требуется использовать реализацию ```CachingDownloader``` из тестов.

* Версии задания
    * *Простая* — можно не учитывать ограничения на число одновременных закачек с одного
      хоста (```perHost >= downloaders```).
    * *Полная* — требуется учитывать все ограничения.
    * *Бонусная* — сделать параллельный обод в ширину.

---

## Домашнее задание 10. HelloUDP <a id="HW10"></a> [↑](#plan)

* Реализуйте клиент и сервер, взаимодействующие по UDP.
* Класс ```HelloUDPClient``` должен отправлять запросы на сервер, принимать результаты и выводить их на консоль.
    * Аргументы командной строки:
        1. имя или ip-адрес компьютера, на котором запущен сервер;
        2. номер порта, на который отсылать запросы;
        3. префикс запросов (строка);
        4. число параллельных потоков запросов;
        5. число запросов в каждом потоке.
    * Запросы должны одновременно отсылаться в указанном числе потоков. Каждый поток должен ожидать обработки своего
      запроса и выводить сам запрос и результат его обработки на консоль. Если запрос не был обработан, требуется
      послать его заново.
    * Запросы должны формироваться по схеме ```<префикс запросов><номер потока>_<номер запроса в потоке>```.
* Класс ```HelloUDPServer``` должен принимать задания, отсылаемые классом ```HelloUDPClient``` и отвечать на них.
    * Аргументы командной строки:
        1. номер порта, по которому будут приниматься запросы;
        2. число рабочих потоков, которые будут обрабатывать запросы.
    * Ответом на запрос должно быть Hello, <текст запроса>.
    * Если сервер не успевает обрабатывать запросы, прием запросов может быть временно приостановлен.

---

## Домашнее задание 11. Физические лица <a id="HW11"></a> [↑](#plan)

* Добавьте к банковскому приложению возможность работы с физическими лицами.
    * У физического лица (```Person```) можно запросить имя, фамилию и номер паспорта.
    * Локальные физические лица (```LocalPerson```) должны передаваться при помощи механизма сериализации.
    * Удалённые физические лица (```RemotePerson```) должны передаваться при помощи удалённых объектов.
    * Должна быть возможность поиска физического лица по номеру паспорта, с выбором типа возвращаемого лица.
    * Должна быть возможность создания записи о физическом лице по его данным.
    * У физического лица может быть несколько счетов, к которым должен предоставляться доступ.
    * Счету физического лица с идентификатором ```subId``` должен соответствовать банковский счет с ```id```
      вида ```passport:subId```.
    * Изменения, производимые со счетом в банке (создание и изменение баланса), должны быть видны всем
      соответствующим ```RemotePerson```, и только тем ```LocalPerson```, которые были созданы после этого изменения.
    * Изменения в счетах, производимые через ```RemotePerson```, должны сразу применяться глобально, а производимые
      через ```LocalPerson``` – только локально для этого конкретного ```LocalPerson```.
* Реализуйте приложение, демонстрирующее работу с физическим лицами.
    * Аргументы командной строки: имя, фамилия, номер паспорта физического лица, номер счета, изменение суммы счета.
    * Если информация об указанном физическом лице отсутствует, то оно должно быть добавлено. В противном случае –
      должны быть проверены его данные.
    * Если у физического лица отсутствует счет с указанным номером, то он создается с нулевым балансом.
    * После обновления суммы счета новый баланс должен выводиться на консоль.
* Напишите тесты, проверяющее вышеуказанное поведение как банка, так и приложения.
    * Для реализации тестов рекомендуется
      использовать [JUnit](https://junit.org/junit5/) ([Tutorial](https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-our-first-test-class/))
      . Множество примеров использования можно найти в тестах.
    * Если вы знакомы с другим тестовым фреймворком (например, [TestNG](https://testng.org/)), то можете использовать
      его.
    * Использовать самописные фреймворки и тесты запускаемые через main нельзя.
* Весь код должен находиться в пакете ```ru.ifmo.rain.фамилия.bank``` и его подпакетах
* ***Сложный вариант***
    * Тесты не должны рассчитывать на наличие запущенного RMI Registry.
    * Создайте класс ```BankTests```, запускающий тесты.
    * Создайте скрипт, запускающий ```BankTests``` и возвращающий код (статус) 0 в случае успеха и 1 в случае неудачи.
    * Создайте скрипт, запускающий тесты с использованием стандартного подхода для вашего тестового фреймворка. Код
      возврата должен быть как в предыдущем пункте.

---

## Домашнее задание 12. HelloNonblockingUDP <a id="HW12"></a> [↑](#plan)

* Реализуйте клиент и сервер, взаимодействующие по UDP, используя только неблокирующий ввод-вывод.
* Класс ```HelloUDPNonblockingClient``` должен иметь функциональность аналогичную ```HelloUDPClient```, но без создания
  новых потоков.
* Класс ```HelloUDPNonblockingServer``` должен иметь функциональность аналогичную ```HelloUDPServer```, но все операции
  с сокетом должны производиться в одном потоке.
* В реализации не должно быть активных ожиданий, в том числе через ```Selector```.
* Обратите внимание на выделение общего кода старой и новой реализации.
* *Бонусный вариант*. Клиент и сервер могут перед началом работы выделить O(число потоков) памяти. Выделять
  дополнительную память во время работы запрещено.

---

## Домашнее задание 13. Статистика текста <a id="HW13"></a> [↑](#plan)

* Создайте приложение TextStatistics, анализирующее тексты на различных языках.
    * Аргументы командной
      строки: ```commandline TextStatistics <локаль текста> <локаль вывода> <файл с текстом> <файл отчета>```
    * Поддерживаемые локали текста: все локали, имеющиеся в системе.
    * Поддерживаемые локали вывода: русская и английская.
    * Файлы имеют кодировку UTF-8.
    * Подсчет статистики должен вестись по следующим категориям:
        * предложения
        * строки
        * слова
        * числа
        * деньги
        * даты
    * Для каждой категории должна собираться следующая статистика:
        * число вхождений
        * число различных значений
        * минимальное значение
        * максимальное значение
        * минимальная длина
        * максимальная длина
        * среднее значение/длина
    * Отчет должен выводиться в формате HTML.
    * **Пример отчета**:

---    

### Анализируемый файл: input.txt

#### Сводная статистика

Число предложений: 38

Число строк: 41

…

#### Статистика по словам

Число слов: 153 (95 уникальных)

Минимальное слово: HTML

Максимальное слово: языках

Минимальная длина слова: 1 (и)

Максимальная длина слова: 14 (Поддерживаемые)

Средняя длина слова: 10

#### Статистика по …

---

* Вы можете рассчитывать на то, что весь текст помещается в память.
* При выполнении задания следует обратить внимание на:
    * Декомпозицию сообщений для локализации
    * Согласование сообщений по роду и числу
* Напишите тесты, проверяющее вышеуказанное поведение приложения.
    * Для реализации тестов рекомендуется
      использовать [JUnit](https://junit.org/junit5/) ([Tutorial](https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-our-first-test-class/))
      . Множество примеров использования можно найти в тестах.
    * Если вы знакомы с другим тестовым фреймворком (например, [TestNG](https://testng.org/)), то можете использовать
      его.
    * Использовать самописные фреймворки и тесты запускаемые через ```main``` нельзя.
* Весь код должен находиться в пакете ```ru.ifmo.rain.фамилия.i18n``` и его подпакетах