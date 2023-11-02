/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Главная форма
 * 
 * ert   21/04/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using GRSoft.Network;
using System.Collections;
using System.Reflection;
using System.Threading;
using System.Globalization;
using GRSoft.UILib;
using System.Runtime.InteropServices;
using System.IO;
using GRSoft.NapoleonManager.Utils;
using System.Security.Cryptography.X509Certificates;

namespace GRSoft.NapoleonManager
{
   public partial class 
      MainForm : Form
   {
      public MainForm()
      {
         InitializeComponent();
         __Initing();

         dtpEndDate.Value = DateTime.Now;
         dtpEndDate.Enabled = false;

         this.tsbSelectRange.ButtonClick += new System.EventHandler(this.tsbSelectRange_Click);
         this.tsmiToday.Click += new System.EventHandler(this.tsmiToday_Click);
         this.tsmiRange.Click += new System.EventHandler(this.tsmiRange_Click);
      }

      public DateTime GetStartDate()
      {
         return dtpBeginDate.Value.Date;
      }

      public DateTime GetFinishDate()
      {
         return tsmiToday.Checked ? GetStartDate() : dtpEndDate.Value.Date;
      }

      //Условие выборки "за сегодня"
      protected void tsmiToday_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(true, "За сегодня");
      }

      //Условие выборки "за период"
      protected void tsmiRange_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(false, "За период");
      }

      //Настройка кнопок для выбора периода 
      protected void AdjustRangeButton(bool isToday, string toolTipText)
      {
         tsbSelectRange.Image = isToday ? tsmiToday.Image : tsmiRange.Image;
         tsmiToday.Checked = isToday;
         tsmiRange.Checked = !isToday;
         tsbSelectRange.ToolTipText = toolTipText;
         dtpEndDate.Enabled = !isToday;
      }

      public DateTime GetRangeEndDate()
      {
         DateTime endDate = tsmiRange.Checked ? GetFinishDate().AddDays(1) : GetStartDate().AddDays(1);
         return endDate.Date;
      }

      //Переключение условий выборки по щелчку на кнопку
      protected void tsbSelectRange_Click(object sender, EventArgs e)
      {
         if (tsmiToday.Checked)
         {
            tsmiRange_Click(sender, e);
         }
         else
         {
            tsmiToday_Click(sender, e);
         }
      }
   }
}
