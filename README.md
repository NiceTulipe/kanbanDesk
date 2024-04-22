# java-kanbanDesk


## Типы задач
Простейшим кирпичиком такой системы является задача (англ. task). У задачи есть следующие свойства:

1.    Название, кратко описывающее суть задачи (например, «Переезд»).
2.    Описание, в котором раскрываются детали.
3.    Уникальный идентификационный номер задачи, по которому её можно будет найти.
4.   Статус, отображающий её прогресс. Мы будем выделять следующие этапы жизни задачи:
     a. NEW — задача только создана, но к её выполнению ещё не приступили.
     b. IN_PROGRESS — над задачей ведётся работа.
     c. DONE — задача выполнена.

Иногда для выполнения какой-нибудь масштабной задачи её лучше разбить на подзадачи (англ. subtask). Большую задачу, которая делится на подзадачи, мы будем называть эпиком (англ. epic). 
Таким образом, в нашей системе задачи могут быть трёх типов: обычные задачи, эпики и подзадачи. Для них должны выполняться следующие условия:

    Для каждой подзадачи известно, в рамках какого эпика она выполняется.
    Каждый эпик знает, какие подзадачи в него входят.
    Завершение всех подзадач эпика считается завершением эпика.


## Менеджер 

### В нём должны быть реализованы следующие функции:


1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
   - 2.1 Получение списка всех задач.
   - 2.2 Удаление всех задач.
   - 2.3 Получение по идентификатору.
   - 2.4 Создание. Сам объект должен передаваться в качестве параметра.
   - 2.5 Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
   - 2.6 Удаление по идентификатору.
3. Дополнительные методы:
   - 3.1 Получение списка всех подзадач определённого эпика.
4. Управление статусами осуществляется по следующему правилу:
   - 4.1 Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
   - 4.2 Для эпиков:
     * если у эпика нет подзадач или все они имеют статус **NEW**, то статус должен быть **NEW**.
     * если все подзадачи имеют статус **DONE**, то и эпик считается завершённым — со статусом **DONE**.
     * во всех остальных случаях статус должен быть **IN_PROGRESS**.
5. Запись и получение истории обращения ко всем видам задач:
   - 5.1 Добавление истории просмотров.
   - 5.2 Возвращает список последних 10 запросов.
6. Программа записывает состояние задач в файл после перезапуска программы состояние востанавливается. 
