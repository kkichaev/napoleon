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
   public partial class FmProfessionEdit : Form
   {
      private bool addMode = false;

      public FmProfessionEdit()
      {
         InitializeComponent();
      }

      public static bool ShowInstance()
      {
         return ShowInstance(null);
      }

      public static bool ShowInstance(Profession profession)
      {
         FmProfessionEdit instance = new FmProfessionEdit();
         instance.addMode = profession == null;

         if (profession != null)
         {
            instance.Text = "Изменить";
            instance.tbCode.Text = profession.Id;
            instance.tbCode.Enabled = false;
            instance.tbName.Text = profession.Name;
         }
         else
         {
            instance.Text = "Создать";
            profession = new Profession();
         }

         if (instance.ShowDialog() == DialogResult.OK)
         {
            DsProfession dsProfession = new DsProfession(false);

            if (instance.addMode)
            {
               profession.id = instance.tbCode.Text.Trim().Length == 0 ? 
                  ((DsProfession)DataModule.Get(Profession.OBJECT_NAME)).GetNextKey()
                  : instance.tbCode.Text;
            }

            profession.name = instance.tbName.Text;
            dsProfession[profession.id] = profession;

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsProfession);

            if (DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection()))
            {
               return true;
            }
            else MessageBox.Show("Ошибка при добавлении");
         }

         return false;
      }

      private void FmProfessionEdit_FormClosing(object sender, FormClosingEventArgs e)
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
               ((IDictionary)DataModule.Get(Profession.OBJECT_NAME)).Contains(tbCode.Text))
            {
               tbCode.Focus();
               e.Cancel = true;
               MessageBox.Show("Запись с кодом " + tbCode.Text + " существует.");
            }
         }
      }
   }
}
