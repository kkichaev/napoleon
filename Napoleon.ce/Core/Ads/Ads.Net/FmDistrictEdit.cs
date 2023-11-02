using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;

namespace GRSoft.Ads
{
   public partial class FmDistrictEdit : Form
   {
      private bool addMode = false;

      public FmDistrictEdit()
      {
         InitializeComponent();
      }

      public static bool ShowInstance()
      {
         return ShowInstance(null);
      }

      public static bool ShowInstance(District district)
      {
         FmDistrictEdit instance = new FmDistrictEdit();
         instance.addMode = district == null;

         if (district != null)
         {
            instance.tbCode.Text = district.Id;
            instance.tbCode.Enabled = false;
            instance.tbName.Text = district.Name;
         }
         else
            district = new District();

         if (instance.addMode)
            instance.Text = "Создать";
         else
            instance.Text = "Изменить";

         if (instance.ShowDialog() == DialogResult.OK)
         {
            DsDistrict dsDistrict = new DsDistrict(false);
            
            if (instance.addMode)
               district.id = instance.tbCode.Text.Trim().Length == 0 ? 
                  ((DsDistrict)DataModule.Get(District.OBJECT_NAME)).GetNextKey()
                  : instance.tbCode.Text;

            district.name = instance.tbName.Text;
            dsDistrict[district.id] = district;

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsDistrict);

            if (DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection()))
            {
               return true;
            }
            else MessageBox.Show("Ошибка при добавлении");
         }

         return false;
      }

      private void FmDistrictEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (tbName.Text.Trim().Length == 0)
            {
               tbName.Focus();
               e.Cancel = true;
               MessageBox.Show("Введите наименование.");
            }

            if (addMode &&
               tbCode.Text.Trim().Length > 0 &&
               ((IDictionary)DataModule.Get(District.OBJECT_NAME)).Contains(tbCode.Text))
            {
               tbCode.Focus();
               e.Cancel = true;
               MessageBox.Show("Запись с кодом " + tbCode.Text + " существует.");
            }
         }
      }
   }
}
