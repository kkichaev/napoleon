using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSCActionEdit : Form
   {
      StorcheckActions doc;

      public FmSCActionEdit()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;
      }

      public void SetDoc(StorcheckActions doc)
      {
         this.doc = doc;
         dtpStart.Value = doc.date.Date;

         List<StorcheckActions.Item> src = new List<StorcheckActions.Item>();
         foreach(StorcheckActions.Item item in doc.items)
         {
            StorcheckActions.Item i = new StorcheckActions.Item();
            i.name = item.name;
            src.Add(i);
         }

         src.Sort();
         dgvItems.DataSource =  new BindingList<StorcheckActions.Item>(src);
      }

      private void btnCancel_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.Cancel;
         Close();
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.OK;
         
         doc.date = dtpStart.Value.Date;
         doc.items.Clear();

         BindingList<StorcheckActions.Item> src = (BindingList<StorcheckActions.Item>)dgvItems.DataSource;
         foreach (StorcheckActions.Item i in src)
         {
            if (i.name.Length > 0)
               doc.items.Add(i);
         }

         Close();
      }

      private void toolStripButton2_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         BindingList<StorcheckActions.Item> src = (BindingList<StorcheckActions.Item>)dgvItems.DataSource;
         src.RemoveAt(dgvItems.CurrentRow.Index);
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         BindingList<StorcheckActions.Item> src = (BindingList<StorcheckActions.Item>)dgvItems.DataSource;
         src.AddNew();
      }
   }
}
