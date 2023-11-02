using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class SelectAgents : Form
   {
      bool multiSelect = true;

      public SelectAgents()
      {
         InitializeComponent();

         Agents a = DataModule.Get("Agents") as Agents;
         if (a != null)
         {
            ArrayList agents = new ArrayList(a.Data);
            agents.Sort(new CmpAgentsByName());
            agentsList.DataSource = agents;
         }
      }

      public bool MultiSelect
      {
         get { return multiSelect; }
         set
         {
            multiSelect = value;
            agentsList.Columns[alChecked.DisplayIndex].Visible = multiSelect;
         }
      }

      public Agent[] SelectedAgents
      {
         get
         {
            List<Agent> a = new List<Agent>();
            if (!multiSelect)
            {
               DataGridViewSelectedRowCollection sr = agentsList.SelectedRows;
               if (sr.Count > 0)
                  a.Add(sr[0].DataBoundItem as Agent);
            }
            else
            {
               foreach (DataGridViewRow row in agentsList.Rows)
               {
                  object vo = row.Cells[alChecked.DisplayIndex].Value;
                  bool val = (vo == null) ? false : (bool)vo;
                  if (val)
                     a.Add(row.DataBoundItem as Agent);
               }
            }

            Agent[] res = new Agent[a.Count];
            a.CopyTo(res);

            return res;
         }
      }

      private void agentsList_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         DialogResult = DialogResult.OK;
      }
   }

   internal class CmpAgentsByName : IComparer
   {
      public int Compare(object x, object y)
      {
         return (x as Agent).name.CompareTo((y as Agent).name);
      }
   }

   internal class CmpOrgsByName : IComparer
   {
      #region IComparer Members

      public int Compare(object x, object y)
      {
         return (x as Org).Name.CompareTo((y as Org).Name);
      }

      #endregion
   }
}
