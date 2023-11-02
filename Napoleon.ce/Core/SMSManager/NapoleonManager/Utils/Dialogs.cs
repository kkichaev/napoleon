/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Различные утилиты для диалоговых окон
 * 
 * kki   01/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.Network;

namespace GRSoft.NapoleonManager.Utils
{
   class Dialogs
   {
      public static bool AllowedDelCurRow()
      { 
         const String TITLE = "Вопрос";
         const String MSG = "Запись будет удалена";

         return MessageBox.Show(MSG, TITLE, MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK;
      }

      public static void UpdateLocalityComboBox(ToolStripComboBox cbLocality,
         DsLocality dsLocality)
      {
         object prevSelItem = cbLocality.SelectedItem;

         if (prevSelItem == null)
         {
            int localityID = PermanentData.Data.LocalityID;

            if (localityID != PermanentData.NOT_USED &&
                  dsLocality.ContainsKey(localityID))
               prevSelItem = new LocalityItem(dsLocality[localityID]);
         }

         cbLocality.Items.Clear();

         foreach (Locality locality in dsLocality.Data)
            cbLocality.Items.Add(new LocalityItem(locality));

         cbLocality.Sorted = true;
         SelectItem(cbLocality, prevSelItem);
      }

      public static bool SelectItem(ToolStripComboBox comboBox, object item)
      {
         if (item == null)
         {
            comboBox.Text = string.Empty;
            return false;
         }

         foreach (object i in comboBox.Items)
            if (i.Equals(item))
            {
               comboBox.SelectedItem = i;
               return true;
            }

         comboBox.Text = String.Empty;
         
         return false;
      }

      public static void UpdateSchoolComboBox(ToolStripComboBox cbLocality,
         ToolStripComboBox cbSchool, DsSchoolEntity dsSchoolEntity)
      { 
         if (cbLocality.SelectedItem == null)
            return;

         object prevSelItem = cbSchool.SelectedItem;

         if (prevSelItem == null)
         {
            int schoolID = PermanentData.Data.SchoolID;

            if (schoolID != PermanentData.NOT_USED &&
                  dsSchoolEntity.ContainsKey(schoolID))
               prevSelItem = new SchoolItem(dsSchoolEntity[schoolID]);
         }

         cbSchool.Items.Clear();

         int locality = ((LocalityItem)cbLocality.SelectedItem).locality.id;

         foreach (SchoolEntity school in dsSchoolEntity.Data)
         {
            if (school.locality == locality && 
               school.parent == SchoolEntity.SHOOL_PARENT)
               cbSchool.Items.Add(new SchoolItem(school));
         }

         cbSchool.Sorted = true;
         SelectItem(cbSchool, prevSelItem);
      }

      /// <summary>
      /// Обновление компонента ToolStripComboBox для 
      /// данных DsSchoolEntity
      /// </summary>
      /// <param name="cbSchool">ComboBox школы</param>
      /// <param name="cbClass">ComboBox классы</param>
      /// <param name="dsSchoolEntity">Набор даннх школы-классы</param>
      public static void UpdateClassComboBox(ToolStripComboBox cbSchool,
         ToolStripComboBox cbClass, DsSchoolEntity dsSchoolEntity)
      {
         if (cbSchool.SelectedItem == null)
            return;

         object prevSelItem = cbClass.SelectedItem;

         if (prevSelItem == null)
         {
            int classID = PermanentData.Data.ClassID;

            if (classID != PermanentData.NOT_USED &&
                  dsSchoolEntity.ContainsKey(classID))
               prevSelItem = new SchoolItem(dsSchoolEntity[classID]);
         }

         cbClass.Items.Clear();

         int parent = ((SchoolItem)cbSchool.SelectedItem).entity.id;

         foreach (SchoolEntity group in dsSchoolEntity.Data)
         {
            if (group.parent == parent)
               cbClass.Items.Add(new SchoolItem(group));
         }

         cbClass.Sorted = true;

         SelectItem(cbClass, prevSelItem);
      }

      /// <summary>
      /// Установить текущей ту строку объекта DataGridView
      /// по которой был сделан клик мышкой
      /// </summary>
      /// <param name="dgv">DataGridView</param>
      /// <param name="e">MouseEventArgs</param>
      public static void SetRowCurrent(DataGridView dgv, MouseEventArgs e)
      {
         if (dgv.CurrentRow == null)
            return;

         DataGridView.HitTestInfo hitTestInfo = dgv.HitTest(e.X, e.Y);

         if (hitTestInfo.RowIndex == -1)
            return;

         dgv.CurrentCell = dgv[0, hitTestInfo.RowIndex];
      }

      public static void PleaseFillFieldsDlg()
      {
         const string TITLE = "Внимание";
         const string MSG = "Необходимо заполнить поля для данных.";

         MessageBox.Show(MSG, TITLE, MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      /// <summary>
      /// Общее для всех дейсвтия по окончанию выборки из базы данных
      /// </summary>
      public static void EndOfResponce()
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
      }

      /// <summary>
      ///  Произошла ошибка в соединении
      /// </summary>
      /// <param name="e"></param>
      public static void ResponceError(EDataResponse e)
      {
         Dialogs.EndOfResponce();
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      /// <summary>
      /// Сообщение что какой то функционал еще не реализован
      /// </summary>
      public static void NotImplementedYetMsg()
      {
         MessageBox.Show("Эта функция пока не реализована");
      }
   }
}
