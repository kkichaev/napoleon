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
   public partial class FmWorkTypeEdit : Form
   {
      public FmWorkTypeEdit()
      {
         InitializeComponent();
      }

      public static bool ShowInstance(WorkType workType)
      {
         bool result = false;
         bool addMode = workType == null;
         FmWorkTypeEdit instance = new FmWorkTypeEdit();
         instance.Text = addMode ? "Добавить" : "Изменить";

         if (workType != null)
         {
            instance.tbId.Text = workType.id;
            instance.tbName.Text = workType.name;
         }
         else
         {
            DsWorkType dsWorkType = (DsWorkType)DataModule.
               Get(WorkType.OBJECT_NAME) ?? new DsWorkType(true);
            instance.tbId.Text = dsWorkType.GetNextKey();
         }

         if (instance.ShowDialog() == DialogResult.OK)
         {
            WorkType wt = new WorkType();
            wt.id = instance.tbId.Text;
            wt.name = instance.tbName.Text;

            DsWorkType dswt = new DsWorkType(false);
            dswt.Add(wt.id, wt);
            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dswt);

           result = DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection());

            if (!result)
               MessageBox.Show("Ошибка!");
         }

         return result;
      }

      private void FmWorkTypeEdit_Load(object sender, EventArgs e)
      {
         
      }
   }
}
