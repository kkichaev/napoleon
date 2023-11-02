/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * Базовый класс для отчетов экспортируемых в Microsoft Excel
 * 
 * kki   30/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;

namespace GRSoft.Ads.Report
{
   /// <summary>
   /// Базовый класс для работы с объектом Microsoft Excel
   /// </summary>
   class Excel
   {
      #region Public Methods
      //Показать - скрыть приложение Excel
      public bool Visible
      {
         get { return (bool)GetProperty(VISIBLE_STR); }
         set { SetProperty(VISIBLE_STR, value); }
      }
      #endregion

      #region Protected Field
      protected const int xlCenter = -4108;
      protected const int xlContinuous = 1;
      protected const int xlLeft = -4131;
      protected const int xlThin = 2;
      protected const int xlTop = -4160;
      #endregion

      #region Protected Methods
      /// <summary>
      /// Excel
      /// </summary>
      protected Excel()
      {
         const string EXCEL_UID = "Excel.Application";
         const string SHEETSINNEWWORKBOOK = "SheetsInNewWorkbook";

         application = Activator.CreateInstance(Type.GetTypeFromProgID(EXCEL_UID));
         SetProperty(SHEETSINNEWWORKBOOK, 1);
         AddWorkBook();
      }

      /// <summary>
      /// Коллекция WorkBooks
      /// </summary>
      protected object WorkBooks
      {
         get { return GetProperty(WORKBOOKS_STR); }
      }

      /// <summary>
      /// Возвращает активный экземпляр WorkBook
      /// </summary>
      protected object ActiveWorkBook
      {
         get { return GetProperty(ACTIVEWORKBOOK_STR); }
      }

      /// <summary>
      /// Коллекция WorkSheets
      /// </summary>
      protected object WorkSheets
      {
         get { return GetProperty(ActiveWorkBook, WORKSHEETS_STR); }
      }

      /// <summary>
      /// Возвращает WorkSheet по индексу, индексация идет с 1!
      /// </summary>
      /// <param name="index">номер страницы</param>
      /// <returns>WorkSheet</returns>
      protected object GetWorkSheet(int index)
      {
         return GetProperty(WorkSheets, ITEM_STR, index);
      }

      /// <summary>
      /// Возвращает WorkSheet по имени закладки("Лист1", "Лист2" и т.п.)
      /// </summary>
      /// <param name="caption">имя закладки</param>
      /// <returns>WorkSheet</returns>
      protected object GetWorkSheet(string caption)
      {
         return GetProperty(WorkSheets, ITEM_STR, caption);
      }

      /// <summary>
      /// Текущий активные WorkSheet
      /// </summary>
      protected object ActiveSheet
      {
         get { return GetProperty(ACTIVESHEET_STR); }
      }

      /// <summary>
      /// Текущий Window
      /// </summary>
      protected object ActiveWindow
      {
         get { return GetProperty("ActiveWindow"); }
      }

      /// <summary>
      /// Объект Range, представляет как текущую ячейку так и набора ячеек
      /// </summary>
      /// <param name="range">позиция ячейки или диапзон ("A1" или "A1:B4")</param>
      /// <returns>Range</returns>
      protected object GetRange(string range)
      {
         return GetProperty(ActiveSheet, RANGE_STR, range);
      }

      /// <summary>
      /// Объект Range, представляет как диапазон ячеек
      /// </summary>
      /// <param name="r1">строка 1 ячейки</param>
      /// <param name="c1">колонка 1 ячейки</param>
      /// <param name="r2">строка 2 ячейки</param>
      /// <param name="c2">колонка 2 ячеки</param>
      /// <returns>Range</returns>
      protected object GetRange(int r1, int c1, int r2, int c2)
      {
         return GetProperty(ActiveSheet, "Range", GetCell(r1, c1), GetCell(r2, c2));
      }

      /// <summary>
      /// Установить значение для ячейки или набора ячеек
      /// </summary>
      /// <param name="range">позиция ячейки или диапзон ("A1" или "A1:B4")</param>
      /// <param name="values">значения</param>
      protected void SetValue(string range, params object[] values)
      {
         SetProperty(GetRange(range), VALUE_STR, values);
      }

      /// <summary>
      /// Установить значение ячейки по индексам строки и клонки
      /// </summary>
      /// <param name="row">строка</param>
      /// <param name="col">колнка</param>
      /// <param name="value">значение</param>
      protected void SetValue(int row, int col, object value)
      {
         SetProperty(GetCell(row, col), VALUE_STR, value);
      }

      /// <summary>
      /// Возвращает объект Cell(ячейка) идексу
      /// </summary>
      /// <param name="row">строка</param>
      /// <param name="col">колонка</param>
      /// <returns>Cells</returns>
      protected object GetCell(int row, int col)
      {
         return GetProperty(ActiveSheet, CELLS_STR, row, col);
      }

      /// <summary>
      /// Возвращает объект содержание ячейки
      /// </summary>
      /// <param name="row">строка</param>
      /// <param name="col">колонка</param>
      /// <returns>объект что содержит ячека</returns>
      protected object GetValue(int row, int col)
      {
         return GetProperty(GetCell(row, col), VALUE_STR);
      }

      /// <summary>
      /// Добавить новую книгу к приложению
      /// </summary>
      /// <returns></returns>
      protected object AddWorkBook()
      {
         return InvokeMethod(WorkBooks, ADD_STR);
      }

      /// <summary>
      /// Добавить новую вкладну Sheet
      /// </summary>
      /// <returns>Sheet</returns>
      protected object AddSheet()
      {
         return InvokeMethod(WorkSheets, ADD_STR);
      }

      /// <summary>
      /// Установить надпись на ярлычке Sheet
      /// </summary>
      /// <param name="Sheet">Sheet</param>
      /// <param name="Name">Текст</param>
      protected void SetSheetName(object Sheet, string Name)
      {
         string nn = Name.Replace('*', '_').Replace('/', '_').Replace('?', '_').Replace('[', '(').Replace(']',')');
         if (nn.Length > 31)
            nn = nn.Substring(0, 31);
         SetProperty(Sheet, NAME_STR, nn);
      }

      /// <summary>
      /// Возвращает Sheet по индексу, индексация идет с 1
      /// </summary>
      /// <param name="index">индекс Sheet</param>
      /// <returns>Sheet</returns>
      protected object GetSheetByIndex(int index)
      {
         return GetProperty(ActiveWorkBook, SHEETS_STR, index);
      }

      /// <summary>
      /// Возвращает Column по индексу, индекс с 1
      /// </summary>
      /// <param name="index">индекс</param>
      /// <returns>Column</returns>
      protected object GetColumnByIndex(int index)
      {
         return GetProperty(ActiveSheet, COLUMNS_STR, index);
      }

      /// <summary>
      /// Возвращает объект строку по индексу индекс с 1
      /// </summary>
      /// <param name="index">индекс</param>
      /// <returns>Row</returns>
      protected object GetRowByIndex(int index)
      {
         return GetProperty(ActiveSheet, ROWS_STR, index);
      }

      /// <summary>
      /// Установить ширину колонки
      /// </summary>
      /// <param name="index">индекс колонки</param>
      /// <param name="width">ширина</param>
      protected void SetColumnWidth(int index, double width)
      {
         SetProperty(GetColumnByIndex(index), COLUMN_WIDTH_STR, width);
      }

      /// <summary>
      /// Установить высоту строки
      /// </summary>
      /// <param name="index">индекс строки</param>
      /// <param name="height">высота</param>
      protected void SetRowHeight(int index, double height)
      {
         SetProperty(GetRowByIndex(index), ROW_HIGTH_STR, height);
      }

      /// <summary>
      /// Объеденить ячейки
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      protected void MergeCells(int r1, int c1, int r2, int c2)
      {
         SetProperty(GetRange(r1, c1, r2, c2), MERGE_CELLS_STR, true);
      }

      /// <summary>
      /// Установить бордюры на диапазоне ячеек
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      /// <param name="style">стиль линии</param>
      protected void SetBordersOnRange(int r1, int c1, int r2, int c2, int style)
      {
         SetProperty(GetBordersRange(GetRange(r1, c1, r2, c2)), LINE_STYLE_STR, style);
      }

      /// <summary>
      /// Установить горизонтальное выравнивание в ячейке
      /// </summary>
      /// <param name="row">строка</param>
      /// <param name="col">колонка</param>
      /// <param name="order">тип выравнивания</param>
      protected void SetCellHorizontalAlign(int row, int col, int order)
      {
         SetCellHorizontalAlign(row, col, row, col, order);
      }

      /// <summary>
      /// Установить горизонтальное выравнивание для диапазона ячеек
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      /// <param name="order">тип выравнивания</param>
      protected void SetCellHorizontalAlign(int r1, int c1, int r2, int c2, int order)
      {
         SetProperty(GetRange(r1, c1, r2, c2), HORIZONTAL_ALIGMENT_STR, order);
      }

      /// <summary>
      /// Установить вертикальное выравнивание в ячейке
      /// </summary>
      /// <param name="row">строка</param>
      /// <param name="col">колонка</param>
      /// <param name="order">тип выравнивания</param>
      protected void SetCellVerticalAlign(int row, int col, int order)
      {
         SetCellVerticalAlign(row, col, row, col, order);
      }

      /// <summary>
      /// Установить вертикальное выравнивание для диапазона ячеек
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      /// <param name="order">тип выравнивания</param>
      protected void SetCellVerticalAlign(int r1, int c1, int r2, int c2, int order)
      {
         SetProperty(GetRange(r1, c1, r2, c2), VERTICAL_ALIGMENT_STR, order);
      }

      /// <summary>
      /// Установить "жирный" шрифт в ячейке
      /// </summary>
      /// <param name="row">строка</param>
      /// <param name="col">колонка</param>
      /// <param name="bold">true - жирные, false - нормальный</param>
      protected void SetCellBoldFont(int row, int col, bool bold)
      {
         SetProperty(GetProperty(GetRange(row, col, row, col), FONT_STR),BOLD_STR,bold);
      }

      /// <summary>
      /// Установить "жирный" шрифт для региона ячеек
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      /// <param name="bold">true - жирные, false - нормальный</param>
      protected void SetCellBoldFont(int r1, int c1, int r2, int c2, bool bold)
      {
         SetProperty(GetProperty(GetRange(r1, c1, r2, c2), FONT_STR), BOLD_STR, bold);
      }

      /// <summary>
      /// Возвращает объект Borders
      /// </summary>
      /// <param name="instance">Объект у которого берем Borders</param>
      /// <returns>Borders</returns>
      protected object GetBordersRange(object instance)
      {
         return GetProperty(instance, BORDERS_STR);
      }

      /// <summary>
      /// Установить "Italic" шрифт для региона ячеек
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      /// <param name="bold">true - Italic, false - нормальный</param>
      protected void SetCellItalicFont(int r1, int c1, int r2, int c2, bool italic)
      {
         SetProperty(GetProperty(GetRange(r1, c1, r2, c2), FONT_STR), ITALIC_STR, italic);
      }

      /// <summary>
      /// Установить "переносить строки" для диапазона ячеек
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      /// <param name="wrap">true - включить перенос, false - без переноса</param>
      protected void SetWrapeText(int r1, int c1, int r2, int c2, bool wrap)
      {
         SetProperty(GetRange(r1, c1, r2, c2), WRAP_TEXT_STR, wrap);
      }

      /// <summary>
      /// Установить "переносить строки" для одной ячейки
      /// </summary>
      /// <param name="r1">строка</param>
      /// <param name="c1">колонка</param>
      /// <param name="wrap">true - включить перенос, false - без переноса</param>
      protected void SetWrapeText(int r1, int c1, bool wrap)
      {
         SetProperty(GetRange(r1, c1, r1, c1), WRAP_TEXT_STR, wrap);
      }

      /// <summary>
      /// Автоподбор ячейки по содержимому
      /// </summary>
      /// <param name="r1">строка</param>
      /// <param name="c1">колонка</param>
      /// <param name="shrink">true - включить, false - выключить</param>
      protected void SetShrinkToFit(int r1, int c1, bool shrink)
      {
         SetProperty(GetRange(r1, c1, r1, c1), SHRINK_TO_FIR_STR, shrink);
      }

      /// <summary>
      /// Установить значение "ориентация строки в ячейки в градусах
      /// </summary>
      /// <param name="row">строк</param>
      /// <param name="col">колонка</param>
      /// <param name="val">значение градус угла наклона</param>
      protected void SetOrientation(int row, int col, int val)
      {
         SetOrientation(row, col, row, col, val);
      }

      /// <summary>
      /// Установить значение "ориентация строки в ячейки в градусах
      /// </summary>
      /// <param name="r1">строка первой ячейки</param>
      /// <param name="c1">колонка первой ячейки</param>
      /// <param name="r2">строка последней ячейки</param>
      /// <param name="c2">колонка последней ячейки</param>
      /// <param name="val">значение градус угла наклона</param>
      protected void SetOrientation(int r1, int c1, int r2, int c2, int val)
      {
         SetProperty(GetRange(r1, c1, r2, c2), ORIENTATION_STR, val);
      }

      /// <summary>
      /// Закрепление областей на листе
      /// </summary>
      /// <param name="range"> объект по которому будет сделано закрепление областей</param>
      protected void FreezePanes(string range)
      {
         SetSelectedCell(range);
         SetProperty(ActiveWindow, FREEZE_PANES_STR, true);
      }

      /// <summary>
      /// Установить выделенным диапазон
      /// </summary>
      /// <param name="range">диапазон ячеек("A1 or A1:A2")</param>
      protected void SetSelectedCell(string range)
      {
         object selected = GetRange(range);
         InvokeMethod(selected, "Select");
      }
      #endregion

      #region Private Fields
      private object application = null;

      //Строковые константы
      private const string ADD_STR = "Add";
      private const string FONT_STR = "Font";
      private const string ITEM_STR = "Item";
      private const string BOLD_STR = "Bold";
      private const string NAME_STR = "Name";
      private const string ROWS_STR = "Rows";
      private const string CELLS_STR = "Cells";
      private const string RANGE_STR = "Range";
      private const string VALUE_STR = "Value";
      private const string SHEETS_STR = "Sheets";
      private const string ITALIC_STR = "Italic";
      private const string COLUMNS_STR = "Columns";
      private const string VISIBLE_STR = "Visible";
      private const string BORDERS_STR = "Borders";
      private const string WORKBOOKS_STR = "WorkBooks";
      private const string WRAP_TEXT_STR = "WrapText";
      private const string ROW_HIGTH_STR = "RowHeight";
      private const string WORKSHEETS_STR = "Worksheets";
      private const string LINE_STYLE_STR = "LineStyle";
      private const string ACTIVESHEET_STR = "ActiveSheet";
      private const string MERGE_CELLS_STR = "MergeCells";
      private const string ORIENTATION_STR = "Orientation";
      private const string COLUMN_WIDTH_STR = "ColumnWidth";
      private const string FREEZE_PANES_STR = "FreezePanes";
      private const string SHRINK_TO_FIR_STR = "ShrinkToFit";
      private const string ACTIVEWORKBOOK_STR = "ActiveWorkbook";
      private const string VERTICAL_ALIGMENT_STR = "VerticalAlignment";
      private const string HORIZONTAL_ALIGMENT_STR = "HorizontalAlignment";
      #endregion 

      #region Private Methods
      /// <summary>
      /// Задать значение свойствау базового объекта приложения
      /// </summary>
      /// <param name="propName">имя свойства</param>
      /// <param name="values">значение</param>
      private void SetProperty(string propName, params object[] values)
      {
         SetProperty(application, propName, values);
      }

      /// <summary>
      /// Задать значение свойства 
      /// </summary>
      /// <param name="instance">объект у которого будет изменено свойство</param>
      /// <param name="propName">имя свойсва</param>
      /// <param name="values">значение</param>
      private void SetProperty(object instance, string propName, params object[] values)
      {
         instance.GetType().InvokeMember(propName, BindingFlags.SetProperty, null, instance, values);
      }

      /// <summary>
      /// Получить значение свойства у базового объекта приложения
      /// </summary>
      /// <param name="propName">имя свойства</param>
      /// <returns>значение</returns>
      private object GetProperty(string propName)
      {
         return GetProperty(application, propName);
      }

      /// <summary>
      /// Получить значение свойства
      /// </summary>
      /// <param name="instance">объект свойства которо запрашиваем</param>
      /// <param name="propName">имя свойства</param>
      /// <returns>значение</returns>
      private object GetProperty(object instance, string propName)
      {
         return GetProperty(instance, propName, null);
      }

      /// <summary>
      /// Получить значение свойства по дополнительным параметрам
      /// </summary>
      /// <param name="instance">объект свойства которо запрашиваем</param>
      /// <param name="propName">имя свойства</param>
      /// <param name="param">дополнительные параметры</param>
      /// <returns>значение</returns>
      private object GetProperty(object instance, string propName, params object[] param)
      {
         return instance.GetType().InvokeMember(propName, BindingFlags.GetProperty, null, instance, param); 
      }

      /// <summary>
      /// Вызвать метод у объекта
      /// </summary>
      /// <param name="methodName">имя метода</param>
      /// <param name="target">объект</param>
      /// <returns>возвращаемое значение</returns>
      private object InvokeMethod(object target, string methodName)
      {
         return InvokeMethod(target, methodName, null);
      }

      /// <summary>
      /// Вызвать метод у объекта
      /// </summary>
      /// <param name="methodName">имя метода</param>
      /// <param name="target">объект</param>
      /// <param name="param">параметры метода</param>
      /// <returns>возвращаемое значение</returns>
      private object InvokeMethod(object target, string methodName, params object[] param)
      {
         return target.GetType().InvokeMember(methodName, BindingFlags.InvokeMethod, null, target, param);
      }
      #endregion
   }
}
