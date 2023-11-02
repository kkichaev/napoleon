using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace UpdateBase
{
   static class Program
   {
      /// <summary>
      /// The main entry point for the application.
      /// </summary>
      [STAThread]
      static void Main()
      {
         foreach (string arg in Environment.GetCommandLineArgs())
         {
            if (arg == "--run")
            {
               Updater upd = new Updater();
               upd.DoExchange();

               return;
            }
         }

         Application.EnableVisualStyles();
         Application.SetCompatibleTextRenderingDefault(false);
         Application.Run(new Form1());
      }
   }

   class Updater
   {
      public void DoExchange()
      {
         Config config = Config.Load();

         Report inRpt = new Report("exportOrders", new Param(), null);
         Report outRpt = new Report("importObjects", new Param(), null);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(inRpt);
         upd.Add(outRpt);

         DataModule.OnDataResponceError += DataModule_OnDataResponceError;
         DataModule.DataProcessed += DataModule_DataProcessed;
         DataModule.RefreshGiveSets(config.Connection, upd, null).Join();
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
      }

   }

}
