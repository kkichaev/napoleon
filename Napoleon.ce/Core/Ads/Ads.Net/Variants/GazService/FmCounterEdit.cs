using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmCounterEdit : Form
   {
      public FmCounterEdit()
      {
         InitializeComponent();
      }

      public static bool ShowInstance()
      {
         FmCounterEdit instance = new FmCounterEdit();
         bool result = false;

         if (instance.ShowDialog() == DialogResult.OK)
         {
            Counter counter = new Counter();
            counter.name = instance.tbName.Text;

            DsCounter dsCounter = new DsCounter(false);
            dsCounter.Add(counter.name, counter);

            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsCounter);

            result = DataModule.UpdateDataSet(updSet, null, null,  Config.GetConfig().GetConnection());

            if (!result)
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", 
                  MessageBoxButtons.OK, MessageBoxIcon.Error);
         }

         return result;
      }
   }
}
