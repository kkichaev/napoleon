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

            dtpBeginDate.Value = DateTime.Now;
            btnReport.Click += btnReport_Click;
            rttReport.Visible = false;
      }

      private void btnReport_Click(object sender, EventArgs e)
        {
            Type type = FormEntries.GetFormType(typeof(FmReports));
            ConstructorInfo ci = type.GetConstructor(Type.EmptyTypes);
            Form fm = (Form)ci.Invoke(new object[] { });
            fm.Show();
        }

        private void tsbCoverArea_Click(object sender, EventArgs e)
        {
           if (CurrentUser.user != null)
           {
              Type prcType = FormEntries.GetFormType(typeof(FmCoverArea));
              ConstructorInfo ci = prcType.GetConstructor(new Type[] { typeof(string), typeof(DateTime) });
              Form fm = (Form)ci.Invoke(new object[] { "", GetStartDate() });
              fm.Show();
           }
        }

        public DateTime GetStartDate()
        {
            return dtpBeginDate.Value.Date;
        }

        public DateTime GetFinishDate()
        {
            return dtpBeginDate.Value.Date;
        }

        public DateTime GetRangeEndDate()
        {
            return GetFinishDate();
        }
    }
}
