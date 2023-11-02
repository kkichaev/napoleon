using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmFolderEdit : Form
   {
      public FmFolderEdit()
      {
         InitializeComponent();
      }

      public static ManagerFolder EditFolder(ManagerFolder folder)
      {
         ManagerFolder result = null;
         FmFolderEdit form = new FmFolderEdit();

         if (folder != null)
         {
            form.tbName.Text = folder.name;
         }

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = folder == null ? new ManagerFolder() : folder;
            result.id = GRSoft.Network.DataObject.GenId();
            result.name = form.tbName.Text.Trim();
         }

         return result;
      }

      private void FmFolderEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
            foreach (Control c in Controls)
            {
               TextBox tb = c as TextBox;
               if (tb != null && tb.Text.Trim().Length == 0)
               {
                  tb.Focus();
                  e.Cancel = true;
                  MessageBox.Show("Поле не может быть пустым",
                     "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                  break;
               }
            }

      }
   }
}
