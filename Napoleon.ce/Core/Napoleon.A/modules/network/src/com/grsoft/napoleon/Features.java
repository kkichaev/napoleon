package com.grsoft.napoleon;

import com.grsoft.napoleon.modules.CostManager;

public class Features extends FeaturesBase{
	/**
	 * Скрывать логин **
	 */
	public static boolean HIDE_LOGIN = false;

	/**
	 * Максимальное разрешение камеры
	 */
	public static int MAX_FOTO_WIDTH = 1100;
	
	/**
	 * Максимальное разрешение камеры
	 */
	public static int MAX_FOTO_HEIGHT = 1100;

	/**
	 * Максимальное число строк в таблице
	 */
	public static int LINES_LIMIT = 3;
	
	/**
	 * масштабирование веса в заявке по умолчанию округляем до килограмм
	 */
	public static int WEIGHT_SCALE = 0;

	/**
	 * добавляет поле причны визита в форму
	 */
	public static boolean HAVE_VISIT_CAUSE = false;

	/**
	 * загрузка цен отдельно от товара
	 */
	public static CostManager COST_MANAGER = null;

	/**
	 * используются фокусные группы при работе с заявкой
	 */
	public static boolean FOCUSED_GROUP = false;

	/**
	 * используются фокусные товары при работе с заявкой
	 */
	public static boolean FOCUSED_ITEMS = false;

	/**
	 * Торговый вводит не отдельные документы, а работает по сценарию
	 */
	public static boolean SCRIPT_DOC = false;
	
	/**
	 * Нельзя изменять флаг передачи заявки
	 */
	public static boolean CANT_CHANGE_SEND_FLAG = false;
	
	/**
	 * расчет продаж из заявок
	 */
	public static boolean SALES_FROM_ORDERS = true;
	
	/**
	 * если есть связанная накладная, сумма в заявках берется не из заявки, а накладной
	 */
	public static boolean DELIVERY_REPLACE_ORDER_SUM = false;
	
	/**
	 * если есть запрещает отправлять уже отправленный документ (DocumentSender)
	 */
	public static boolean CANT_RESEND_SENDED_DOCUMENT = false;
	
	/**
	 * в прайс-листе есть меню для просмотра прайса без папок
	 */
	public static boolean CAN_EXPAND_PRICE = true;
	
	/**
	 * перемещение по позициям прайса и заявки в карточке товара
	 */
	public static boolean HAVE_PRICE_MOVER = false;
	
	/**
	 * перемещение в карточке только внутри одной папки, работает вместе с HAVE_PRICE_MOVER = true
	 */
	public static boolean PRICE_MOVE_INSIDE_FOLDER = false;

	/**
	 * Адреса доставки у контрагента
	 */
	public static boolean DELIVERY_ADDRESS = false;
		
	/**
	 * Подбор нескольких товаров с одинаковым количеством 
	 */
	public static boolean PACK_INPUT = false;
	
	/**
	 * Прием остатков при отправке документов
	 */
	public static boolean RECEIVE_REMNANTS_WHEN_SENDING = false;
	
	/**
	 * Пункт меню прием остатков в главном меню
	 */
	public static boolean RECIEVE_REMNANTS_IN_MAIN_MENU = false;

	/**
	 * Колонка Код товара в прайсе
	 */
	public static boolean ID_COLUMN_IN_PRICE_LIST = false;
	
	/**
	 * Не давать работать с клиентами в стоплисте
	 */
	public static boolean BLOCK_IN_STOP_LIST = false;
	
	/**
	 * Версия программы для Android-Market
	 */
	public static boolean IS_MARKET_VERSION = false;
	
	
	/**
	 * Можно менять цену в PriceCount
	 */
	public static boolean CAN_CHANGE_COST = false;
	
	/**
	 * галочка упаковками по умолчанию включена
	 */
	public static boolean INPUT_QTY_IN_PACK = false;
	
	/**
	 * нумерация позиций в заявке и в продажах
	 */
	public static boolean SHOW_NUMBER_IN_ORDER = false;
	
	/***
	 * Модуль печати
	 */
	public static boolean PRINT_MODULE = false;
	
	/***
	 * Разрешать пользователю отключать сценарий
	 */
	public static boolean USER_CAN_SCRIPT_OFF = false;
	
	
	/**
	 * Не считается вес в заявке
	 */
	public static boolean NO_WEIGHT_IN_ORDER = false;
	
	/**
	 * Проведение заявки OnLine
	 */
	public static boolean ORDER_ONLINE = false;
	
	/**
	 * Анкеты, ставится внути QuestionDoc
	 */
	public static boolean QUESTION = false;
	
	/**
	 * нельзя копировать документы
	 */
	public static boolean DISABLE_DOC_COPY = false;
	
	/***
	 * показывать сообщение о выходе из программы
	 */
	public static boolean SHOW_EXIT_WARNING = true;
	
	/***
	 * при печати докумен формируется через pdf
	 */
	public static boolean PRINT_THROW_PDF = true;
	
	/**
	 * Размещать проданные за месяц позиции в верху развернутого списка товаров
	 */
	public static boolean PUT_SALED_ITEMS_BEFORE = false;
	
	/***
	 * Добавить организацию
	 */
	public static boolean POTENZIAL_ORG = true;
	
	/**
	 * Есть список цен и можно выбрать цену в документе Продажа 
	 */
	public static boolean CAN_CHANGE_COST_IN_SALES = false;
	
	
	/**
	 * Фильтр нулевых позиций в прайсе сразу же включен
	 */
	public static boolean SHOW_ZERO_FILTER = false;
	
	/**
	 * Фильтр документов по товару
	 */
	public static boolean FILTER_DOCUMENTS_BY_ITEM = false;
	
	/**
	 * Ввод остака перед количеством. Вводить кол-во без остатка нельзя 
	 */
	public static boolean PUT_REST_BEFORE_QTY = false;
	
	/***
	 * Ассортиментная матрица(матрица с товарами из заказов в течении последнего месяца)
	 */
	public static boolean ASSORTMENT_MATRIX = true;
	
	/***
	 * Сложная история продаж - использовать пустые данные(остаток = 0, офтаке = 0, заказ = 0)
	 * иначе ищем ближайшие по дате данные.
	 */
	public static boolean NOT_ZERO_DATA_FOR_COPLEX_HISTORY = false;
	
	/**
	 * В сложной истории продаж считать остаток упаковками
	 */
	public static boolean REST_IN_PACK = false;
	
	/**
	 * Галочка вкл/выкл в натсройках
	 * Не проверять кол-во остатка товара
	 */
	public static boolean CONFIG_DONT_CHECK_PRICE_QTY = false;
	
	/**
	 * ввод кол-ва только целыми числами
	 */
	public static boolean INTEGER_INPUTS_QTY = false;
	
	/**
	 * интервал для вывода последних проданных товаров в месяцах, 0 - берется из последней заявки
	 */
	public static int LAST_SALED_ITEMS_PERIOD = 0;
	
	
	/**
	 * Использовать в возвратах сумму, иначе будет 0 
	 */
	public static boolean USE_COST_IN_RETURNS = false;
	
	
	/**
	 * показявать пароль звездочками  
	 */
	public static boolean HIDE_PASSWORD = false;
	
	/**
	 * При вводе остатков вместо , будет +1
	 */
	public static boolean REPLACE_COMMA_TO_PLUS = false;
	
	
	/**
	 * нельзя послать отдельные документы в сценарии (только полный сценарий)
	 */
	public static boolean CANT_SEND_SCRIPT_PART = true;
	
	/**
	 * Показывать вес вместо количества в истории продаж (вем показываем в целых кг)
	 */
	public static boolean SHOW_WEIGHT_IN_HISTORY = false;
	
	/**
	 * Стоп-лист приходить отдельно от контрагента
	 */
	public static boolean ORG_STOP_TABLE = false;
	
	/**
	 * Показывать адрес в окне документы и в заявке
	 */
	public static boolean SHOW_ORG_ADDRESS = false;
	
	/**
	 * Удалять посещения в сценарии если нет фотографий
	 */
	public static boolean DEL_VISIT_WITHOUT_PHOTO = false; 
	
	/***
	 * Отображать документы в окнет "Список документов" при включенном сценарии,
	 * иначе будет один документ "Визит"  
	 */
	public static boolean SCRIPT_OFF_IN_DOC_LIST = true;
	
	/***
	 * Где храниться таблица маршрутов в SQL или DBF, при хранении в SQL,
	 * мы должны сами отсортировать
	 */
	@Deprecated
	public static boolean SQL_ORG_ROUTE = false;
	
	/***
	 * Вид презентации по папкам или общим списком
	 */
	public static boolean FOLDER_PRESENTATION = false;
	
	/***
	 * Задачи на торговую точку
	 */
	public static boolean ORG_TASK = false;
	
	/***
	 * Показывать миниатюры из презентаций в кароточке товара
	 */
	public static boolean SHOW_PRESENT_IMG = true;
	
	/***
	 * Сумма документа сценарий считать только 
	 * от сум документов порожденных от OrderImpl
	 */
	public static boolean SCRIPT_SUM_ONLY_FOR_SALES = false;
	
	/**
	 * По умолчанию галка > 0 в синхронизации снята 
	 */
	public static boolean LOAD_FULL_PRICE = false;
	
	/**
	 * кнопка OK в инкассации
	 */
	public static boolean OK_BTN_INCASS = false;
	
	/**
	 * Отправка пустых документов запрещена
	 */
	public static boolean CAN_SEND_EMPTY_DOCS = false;
	
	/***
	 * Презентация через карту памяти
	 */
	public static boolean PRESENTATION_ON_SDCARD = false;
	
	/**
	 * можно задать папку для презентации
	 */
	public static boolean CAN_CHANGE_PRESENT_FOLDER = false;
	
	/**
	 * устанавливать фокус (открывать клавиатуру) при поиске
	 */
	public static boolean REQUSET_FOCUS_IN_SEARCH = false;
	
	/**
	 * Разрешаем создавать несколько ПКО на одну продажу
	 */
	public static boolean ALLOW_MULTY_PKO_ON_SALES = false;
	
	/**
	 * Запрещаем редактировать документы после печати
	 */
	public static boolean DISABLE_EDIT_AFTER_PRINT = false;
	
	/***
	 * Не считать сумму документа возврат в сумме документа
	 * сценария
	 */
	public static boolean EXCLUDE_RETURN_DOC_SUM_FROM_SCRIPT = false;
	
	/**
	 * Нельзя удалять напечатанные документы.
	 */
	public static boolean CANT_DEL_PRINTED_DOCS = false;
	
	/**
	 * Показывать вес в заявках в списке документов
	 */
	public static boolean SHOW_WEIGHT_IN_DOC_LIST = false;
	
	
	/**
	 * Удаляет пустые заявки перед синхронизацией
	 * 22/08/2014 Владимир сказал что пустые заявки должны удаляться
	 */
	public static boolean REMOVE_EMPTY_ORDERS = true;
	
	/**
	 * Галка посещения в окне синхронизации выключена/включена по умолчанию 
	 */
	public static boolean UPDATE_DB_CHECK_VISITS = false;
	
	/**
	 * использовать порядок элементов в матрице 
	 */
	public static boolean USE_MATRIX_ORDER = true;
	
	/**
	 * Пишет кол-во документов в списке документов
	 */
	public static boolean COUNT_DOCS_IN_DOCSLIST = false;
	
	/***
	 * Выбор суммы документов в главном окне за период
	 */
	public static boolean DOC_SUM_BY_PERIOD = true;
	
	/**
	 * Цена с названием товара в презентации
	 */
	public static boolean COST_IN_PRESENTATION = true;
	
	/**
	 * Код товара в презентации	
	 */
	public static boolean ID_IN_PRESENTATION = false;
	
	/***
	 * Не требовать снятие координат в момент создания документа
	 */
	public static boolean ALLOW_CREATE_DOC_WHITHOUT_GPS_POS = false;
	
	/**
	 * показывать в упаковках в документах Itemsable (зависит от настойки CfgNpl.isPackView)
	 */
	public static boolean QTY_IN_PACK_IN_DOCS = false;
	
	/**
	 * Не давать выйти из заявки без фокусных товаров 
	 */
	public static boolean BLOCK_ORDER_WITHOUT_FOCUS = false;
	
	/**
	 * Сохранять данные по синхронизам 
	 */
	public static boolean SYNC_INFO = false;
	
	/**
	 * Универсальная Передаточная Накладная (УПД)
	 */
	public static boolean UPD = false;
	
	/**
	 * Показывать текстовый статус документа в списке документов
	 */
	public static boolean DOC_STATUS_IN_DOC_LIST = false;
	
	/**
	 * В фоновом режиме обновлят остатки
	 */
	public static boolean UPDATE_PRICE_BACKGROUND = false;
	
	/**
	 * Показывать сумму продаж по папкам в прайсе
	 */
	public static boolean SHOW_DAILY_SALES_IN_WAREHOUSE = false; 
	
	/**
	 * показывает вес вместе с суммой 
	 */
	public static boolean SHOW_DAILY_WEIGHT_IN_WAREHOUSE = false; 
	
	/**
	 * Если продали в упаковках, то в формах цена будет за упаковку
	 */
	public static boolean USE_PACK_QTY_IN_FORMS = false;

	/**
	 * Можно устанавливать статус документа отправлен
	 */
	public static boolean CAN_SET_SEND_FLAG = false;
	
	/**
	 * Функция старт - стоп в точке
	 */
	public static boolean START_STOP = false;
	
	/**
	 * Сложная инкассация. Распределение сумм по накладным
	 */
	public static boolean INCASS_DEBET_DISTRIB = false;
	
	/**
	 * Накладные за день
	 */
	public static boolean DDLV = false;
	
	/**
	 * Делать невидимой кнопку создать документ, для документов, которые не создает пользователь
	 */
	public static boolean BTN_NEW_DOC_INVISIBLE = false;
	
	/**
	 * Помнить последнюю выбранную матрицу
	 */
	public static boolean OPEN_LAST_MATRIX = false;
	
	/**
	 * Если есть незавершенные сценарии вместо окна синхронизаций, открывыется окно со списком этих документов
	 */
	public static boolean CHECK_UNCOMPLETE_SCRIPTS = false;
	
	/**
	 * Не отправляем незавершенные сценарии
	 */
	public static boolean DONT_SEND_UNCOMPLETE_SCRIPTS = false;

	
	/**
	 * Настройка фоновой передачи 
	 */
	public static boolean SEND_IN_BACKGROUND = false;
	
	
	/***
	 * фильтр нулевых цен вместе м фильтром нулевых остатков 
	 */
	public static boolean COST_FILTER_IN_PRICE = false;
	
	/**
	 * Запрос отчетов из КИС
	 */
	public static boolean REPORT_REQUEST = false;
	
	/**
	 * Открывать камеру в VisitEdit
	 */
	public static boolean START_VISIT_OPEN_CAMERA = false;
	
	/**
	 * Не открывать первый документ при создании сценария (открывать окно сценария)
	 */
	public static boolean DONT_SHOW_FIRST_SCRIPT_DOC = false;
	
	
	/**
	 * Сохранять заметки при очистке базы
	 */
	public static boolean KEEP_NOTES_ON_CLEAR_DB = false;
	
	/**
	 * Показывать вес в главном окне
	 */
	public static boolean SHOW_WEIGHT_IN_MAIN_FORM = false;

	/**
	 * Сохраняем истрию маршрута
	 */
	public static boolean ROUTE_HISTORY = false;

	/**
	 * При поиске пробел заменяем на % 
	 */
	public static boolean MULTI_WORD_SEARCH = true;
	
	/**
	 * FLog - пишет лог в файл Path.SHARED_FOLDER / log.txt
	 */
	public static boolean FILE_LOG_DEBUG = false;

	/**
	 * разршеаем сценарии без флага в конфигурации
	 */
	public static boolean NO_SCRIPT_CONFIG = false;
	
	/*
	 * архвировать пакет перед отправкой
	 */
	public static boolean ZIP_PACKET = true;
	
	/*
	 * 3.62
	 */
	public static boolean _362 = false;
	
	
	/**
	 * фото передаются по одному Нет ограничения на кол-во фото в посещении 
	 */
	public static boolean UNLIMIT_VISIT_ITEMS = false;
	
	/**
	 * Отпарвка настроек программы на сервер
	 */
	public static boolean SEND_PROGRAM_SETTINGS = false;
	
	/**
	 * Не закрывть диалог синхронизации после окончания 
	 */
	public static boolean KEEP_DIALOG_AFTER_SYNC = false;
	
	/**
	 * Контроль создания документа в точке 
	 */
	public static boolean ORG_DISPOSITION = false;
	
	
	/**
	 * Красим красным просроченные долги, и контрагентов с просроченными долгами
	 */
	public static boolean MARK_OVERDUE_DEBTS = false;
	
	/**
	 * Строка расчета номера недели 
	 */
	public static boolean TRACE_WEEK_INDEX = false;
}
