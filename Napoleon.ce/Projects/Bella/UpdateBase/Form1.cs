using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace UpdateBase
{
   public partial class Form1 : Form
   {
      Config config;
      public Form1()
      {
         InitializeComponent();

         config = Config.Load();

         IP.Text = config.IP;
         port.Text = config.port.ToString();
         login.Text = config.login;
         password.Text = config.password;
      }

      void DoExchange()
      {
         config.IP = IP.Text;
         int.TryParse(port.Text, out config.port);
         config.login = login.Text;
         config.password = password.Text;

         config.Save();

         Report inRpt = new Report("exportOrders", new Param(), null);
         Report outRpt = new Report("importObjects", new Param(), null);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(inRpt);
         upd.Add(outRpt);

         DataModule.OnDataResponceError += DataModule_OnDataResponceError;
         DataModule.DataProcessed += DataModule_DataProcessed;
         DataModule.RefreshGiveSets(config.Connection, upd, null);
      }

      private void button1_Click(object sender, EventArgs e)
      {
         DoExchange();
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         //MessageBox.Show("Выполнено!");
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         MessageBox.Show(e.Msg);
      }
   }

   class Param : GRSoft.Network.DataObject
   {

   }
}
