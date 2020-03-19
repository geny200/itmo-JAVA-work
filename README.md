Этот репозиторий будет склонирован для каждого студента и доступен по адресу
`http://ctddev.ifmo.ru:25231/git/<user>/java-advanced-2020-solutions`, где `<user>` —
имя пользователя и пароль, которые вам пришлют на `@niuitmo.ru`

Для сдачи домашних заданий
 * Клонируйте ваш личный репозиторий
    * `git clone http://ctddev.ifmo.ru:25231/git/<user>/java-advanced-2020-solutions`
    * У личных репозиториев __нет__ web-интерфейса, используйте инструменты командной строки.
 * Добавьте ссылку на исходный репозиторий
    * `git remote add source https://www.kgeorgiy.info/git/geo/java-advanced-2020-solutions`
    * По мере появления новых домашних заданий в исходном репозитории будут появляться заготовки решений
      забирайте из через `git pull source`.
 * Переименуйте пакет `ru.ifmo.rain.konovalov`, заменив
   `__last_name__` на вашу фамилию.
 * В остальном сохраняйте текущую структуру каталогов и имена файлов.
    * Если структура репозитория не соответсвует исходной, преподаватель не будет проверять решение.
 * Добавляйте только исходные файлы решений
 * Вы можете редактировать `.gitignore` как вам удобно
 * Делайте
 * Отправьте решение на проверку
    * Проверьте, что все исходники компилируеются
    * Проверьте, что тесты сдаваемого ДЗ проходят
    * Закоммитьте все изменения в `master`
    * Запушите все изменения
    * Запросите проверку решения, скинув в чат,
      указанный преподавателем ссылку на ваш личный
      репозиторий и номер домашнего задания
 * После проверки преподаватель либо укажет найденные недостатки в `NOTES.md`,
   либо укажет их в виде комментариев в исходном коде, пометив их как `:NOTE:`
