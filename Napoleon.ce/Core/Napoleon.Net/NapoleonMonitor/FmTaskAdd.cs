using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmTaskAdd : Form
   {
      private DataSet<string, Org> dsOrg = null;
      private DataSet<string, PotenzialOrg> dsPtnzOrg = null;

      private Org selectedOrg = null;

      public FmTaskAdd()
      {
         InitializeComponent();
      }

      internal void Init(DataSet<string, Org> dsOrg, DataSet<string, PotenzialOrg> dsPtnzOrg)
      {
         this.dsOrg = dsOrg;
         this.dsPtnzOrg = dsPtnzOrg;
      }

      public Task Task
      { 
         get 
         {
            Task t = null;
            if (selectedOrg != null && task.Text.Length > 0)
            {
               t = new Task();
               t.org = selectedOrg;
               t.id = selectedOrg.id;
               t.date = date.Value;
               t.task = task.Text;
            }
            return t;
         } 
      }

      private void selectOrg_Click(object sender, EventArgs e)
      {
         Org o = FmSelectContrAgent.SelectOrg(dsOrg, dsPtnzOrg);
         if (o != null)
         {
            selectedOrg = o;
            orgTitle.Text = o.name;
         }
      }
   }
}
