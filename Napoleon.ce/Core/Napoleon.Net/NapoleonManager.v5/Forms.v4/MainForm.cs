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

#if DEBUG
         //Config.UUID = "22";
         //ConnectionHelper.ADDR = "172.24.93.21";
         //ConnectionHelper.PORT = 3000;

         //SimpleDataSet<Order> ods = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
         //DateTime start = DateTime.Now.Date;

         //Config c = Config.GetConfig();
         //serverInfo = ConnectionHelper.GetServerInfo(c.serverCode);
         //ods.Filter = string.Format(COMMON_FILTER_STR, "created", start, start);

         //List<IDataSet> upd = new List<IDataSet>();
         //upd.Add(ods);
         //DBConnection dbc = c.GetConnection(serverInfo);
         //DataModule.RefreshGiveSets(dbc, upd, null).Join();

#endif

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
