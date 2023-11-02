using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmVisitRpt : Form
   {
      private delegate void TAction();

      public FmVisitRpt()
      {
         InitializeComponent();
      }

      public string ReportName { get; set; }

      private void FmVisitRpt_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            List<Division> list = mc.AllDivisions;
            list.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
            list.ForEach((d) => { lbDiv.Items.Add(d); });
         }
      }

      private void lbDiv_ItemCheck(object sender, ItemCheckEventArgs e)
      {
         ManageItemCheck((CheckedListBox)sender, lbDiv_ItemCheck, ManageDivision,
            e.Index, e.NewValue);
      }

      private void ManageItemCheck(CheckedListBox listBox, ItemCheckEventHandler handler, TAction action, int index, System.Windows.Forms.CheckState checkState)
      {
         listBox.ItemCheck -= handler;
         listBox.SetItemCheckState(index, checkState);
         listBox.ItemCheck += handler;
         action();
      }

      private void ManageDivision()
      {
         lbAgent.Items.Clear();
         lbOrg.Items.Clear();

         List<Agent> list = new List<Agent>();

         foreach (object i in lbDiv.CheckedItems)
         {
            Division d = i as Division;

            if (d != null) 
            {
               d.GetAllAgents().ForEach((a) => { list.Add(a.agent); });
            }
         }

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         list.ForEach((a)=>{
            if (!lbAgent.Items.Contains(a))
               lbAgent.Items.Add(a);
         });
      }

      public class RequestData : GRSoft.Network.DataObject
      {
         public string userid = string.Empty;
      }

      public class ResponceData : GRSoft.Network.DataObject
      {
         [ItemType(typeof(Org))]
         public List<Org> orgs = new List<Org>();
      }

      private void btnRerfresh_Click(object sender, EventArgs e)
      {
         lbOrg.Items.Clear();
         RequestData data = new RequestData();
         data.userid = CollectUserID();
         SimpleDataSet<Org> res = new SimpleDataSet<Org>("Org", false);
         Report r = new Report("agent_orgs", data, res);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         List<Org> list = new List<Org>();
         foreach (Org o in res.Values)
            list.Add(o);

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         list.ForEach((a) => { lbOrg.Items.Add(a); });
      }

      private string CollectUserID()
      {
         StringBuilder sb = new StringBuilder();

         foreach (object i in lbAgent.CheckedItems)
         {
            Agent a = i as Agent;

            if (a != null)
            {
               if (sb.Length > 0)
                  sb.Append(",");

               sb.Append(a.id);
            }
         }

         return sb.ToString();
      }

      private void btnDivOn_Click(object sender, EventArgs e)
      {
         SetListItemChecked(lbDiv, true);
      }

      private void SetListItemChecked(CheckedListBox list, bool isCheck)
      {
         list.BeginUpdate();
         for (int i = 0; i < list.Items.Count; i++)
            list.SetItemChecked(i, isCheck);
         list.EndUpdate();
      }

      private void btnDivOff_Click(object sender, EventArgs e)
      {
         SetListItemChecked(lbDiv, false);
      }

      private void btnAgentOn_Click(object sender, EventArgs e)
      {
         SetListItemChecked(lbAgent, true);
      }

      private void btnAgentOff_Click(object sender, EventArgs e)
      {
         SetListItemChecked(lbAgent, false);
      }

      private void btnOrgOn_Click(object sender, EventArgs e)
      {
         SetListItemChecked(lbOrg, true);
      }

      private void btnOrgOff_Click(object sender, EventArgs e)
      {
         SetListItemChecked(lbOrg, false);
      }

      private class Data : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
         public string userid = string.Empty;
         public string orgid = string.Empty;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Data data = new Data();
         data.start = dtpStart.Value.Date;
         data.finish = dtpFinish.Value.Date;
         data.userid = CollectChechedUserID();
         data.orgid = CollectCkechedOrgID();
         
         ReportResult.DoReport(ReportName ?? "visit_report", data, this);
      }

      private string CollectCkechedOrgID()
      {
         StringBuilder sb = new StringBuilder();
         foreach (object i in lbOrg.CheckedItems)
         {
            Org o = i as Org;

            if (o != null)
            {
               if (sb.Length > 0)
                  sb.Append(",");

               sb.Append(o.id);
            }
         }

         return sb.ToString();
      }

      private string CollectChechedUserID()
      {
         StringBuilder sb = new StringBuilder();
         foreach (object i in lbAgent.CheckedItems)
         {
            Agent a = i as Agent;

            if (a != null)
            {
               if (sb.Length > 0)
                  sb.Append(",");

               sb.Append(a.id);
            }
         }

         return sb.ToString();
      }
   }
}
