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
   public partial class FmFolderEdit : Form
   {
      public FmFolderEdit()
      {
         InitializeComponent();
      }

      public static Folder ShowInstance(Folder folder)
      {
         FmFolderEdit instance = new FmFolderEdit();
         Folder result = folder ?? new Folder();

         if (folder == null)
         {
            instance.Text = "Создать";
            result.id = DateTime.Now.Ticks.ToString();
         }
         else
         {
            instance.tbName.Text = folder.name;
            instance.Text = "Изменить"; 
         }

         if (instance.ShowDialog() == DialogResult.OK)
         {
            result.name = instance.tbName.Text;
            return result;
         }

         return null;
      }

      private void FmFolderEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && tbName.Text.Trim().Length == 0)
         {
            tbName.Focus();
            e.Cancel = true;
            MessageBox.Show("Поле не может быть пустым");
         }
      }
   }
}
