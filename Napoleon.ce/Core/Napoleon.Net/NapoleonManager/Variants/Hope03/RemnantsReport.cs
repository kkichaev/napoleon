using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Threading;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class RemnantsReport : Form
   {
      ReportParams rp = new ReportParams();

      public RemnantsReport()
      {
         InitializeComponent();

         dtpBegin.Value = DateTime.Now.Date.AddDays(-7);
         dtpEnd.Value = DateTime.Now.Date;

         Manager mgr = ((Manager)CurrentUser.user);
         mgr.AllDivisions.ForEach(x => cbDivision.Items.Add(x));

         List<Agent> agents = new List<Agent>();
         foreach (Agent a in mgr.GetAgents().Data)
            agents.Add(a);
         agents.Sort();
         agents.ForEach(x=>cbAgents.Items.Add(x));
         cbAgents.Focus();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         rp.end = dtpEnd.Value;
         rp.start = dtpBegin.Value;
         rp.agent = null;
         rp.division = null;

         if (rbAgents.Checked)
            rp.agent = cbAgents.SelectedItem as Agent;
         else if (rbDivision.Checked)
            rp.division = cbDivision.SelectedItem as Division;

         base.OnClosing(e);
      }


      private void cbAgents_Enter(object sender, EventArgs e)
      {
         rbAgents.Checked = true;
      }

      private void cbDivision_Enter(object sender, EventArgs e)
      {
         rbDivision.Checked = true;
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbDivision.Focus();
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Focus();
      }

      ReportParams Params { get { return rp; } }

      public static void Do(Form owner, Agent selected)
      {
         RemnantsReport form = new RemnantsReport();
         form.cbAgents.SelectedItem = selected;

         if (form.ShowDialog() != DialogResult.OK)
            return;

         ReportParams rp = form.Params;
         List<Agent> agents = rp.GetAgents();

         List<IDataSet> upd = new List<IDataSet>();
         string useridIn = DataUtils.MakeFilterFromAgents(null, agents);

         foreach (Agent a in agents)
         {
            DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               upd.Add(orgs);
            }
         }

         rp.dsDocs = new SimpleDataSet<OrgRemnants>(OrgRemnants.OBJECT_NAME, false);
         rp.dsDocs.Filter = String.Format("\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59') and {3}",
            "created", rp.start, rp.end, useridIn);
         upd.Add(rp.dsDocs);

         rp.dsDistribRef = new DataSet<string, DistributionMatrix>(DistributionMatrix.OBJECT_NAME, false);
         upd.Add(rp.dsDistribRef);

         FmWait.StdDataRefresh(owner, upd, () =>
         {
            Thread th = new Thread(DoReport);
            th.Start(rp);
            FmWait.ShowForm(owner, th);
         });
      }

      static void DoReport(object prm)
      {
         ReportBuilder rpb = new ReportBuilder(prm as ReportParams);
         try
         {
            rpb.Do();
         }
         catch (Exception e)
         {
            MessageBox.Show("Ошибка при создании отчета: " + e.Message);
         }
         FmWait.CloseForm();
      }

      internal class ReportParams
      {
         internal DateTime start;
         internal DateTime end;

         internal Agent agent;
         internal Division division;

         internal List<Agent> GetAgents()
         {
            List<Agent> ret = new List<Agent>();
            if (agent != null)
               ret.Add(agent);
            else
            {
               foreach (Division.DivisionAgent a in division.GetAllAgents())
                  if (a.agent != null)
                     ret.Add(a.agent);
            }
            return ret;
         }

         internal SimpleDataSet<OrgRemnants> dsDocs;
         internal DataSet<string, DistributionMatrix> dsDistribRef;
      }

      class EmptyOrg : Org
      {
         public EmptyOrg(string id)
         {
            this.id = id;
            name = "Контрагент с кодом <" + id + ">";
         }
      }

      class RemnantsData : Dictionary<Org, List<OrgRemnants>>
      {
         public RemnantsData() : base(new OrgComparer()) { }

         internal void Add(OrgRemnants doc)
         {
            if (doc.org == null)
               doc.org = new EmptyOrg(doc.id);


            List<OrgRemnants> od;
            if (ContainsKey(doc.org))
               od = this[doc.org];
            else
            {
               od = new List<OrgRemnants>();
               Add(doc.org, od);
            }

            od.Add(doc);
         }

         class OrgComparer : EqualityComparer<Org>
         {
            public override bool Equals(Org x, Org y)
            {
               return x.id == y.id;
            }

            public override int GetHashCode(Org obj)
            {
               return obj.id.GetHashCode();
            }
         }
      }

      class ReportData : Dictionary<Agent, RemnantsData>
      {
         internal void Add(OrgRemnants doc)
         {
            if (doc.agent == null)
               return;

            RemnantsData dd;
            if (ContainsKey(doc.agent))
               dd = this[doc.agent];
            else
            {
               dd = new RemnantsData();
               Add(doc.agent, dd);
            }

            dd.Add(doc);
         }
      }

      class ReportBuilder : Excel
      {
         ReportParams param;
         public ReportBuilder(ReportParams param)
         {
            this.param = param;
         }

         int CmpOrgDistrDocs(OrgRemnants x1, OrgRemnants x2)
         {
            return x1.date.Date.CompareTo(x2.date.Date);
         }

         public void Do()
         {
            ReportData data = new ReportData();
            foreach (OrgRemnants od in param.dsDocs.Data)
               data.Add(od);

            int row = 1;
            foreach (KeyValuePair<Agent, RemnantsData> kv in data)
            {
               foreach (KeyValuePair<Org, List<OrgRemnants>> orgdata in kv.Value)
               {
                  if( !param.dsDistribRef.ContainsKey(orgdata.Key.id) )
                     continue;

                  //int[] contains = new int[orgdata.Value.Count];

                  List<OrgRemnants> docs = orgdata.Value;
                  docs.Sort(CmpOrgDistrDocs);

                  int startRow = row;
                  int col;
                  object cell;
                  cell = GetCell(row++, 1);
                  SetValue(cell, kv.Key.Name + " / " + orgdata.Key.Name);
                  SetCellBoldFont(cell, true);
                  DistributionMatrix matrix = param.dsDistribRef[orgdata.Key.id];
                  foreach(DistributionMatrix.Item dmi in matrix.items)
                  {
                     if( dmi.item == null )
                        continue;
                     SetValue(GetCell(row, 1), dmi.item.Name);

                     const int START_COL = 2;
                     col = START_COL;

                     foreach (OrgRemnants doc in docs)
                     {
                        cell = GetCell(startRow, col);
                        SetValue(cell, doc.date.Date);
                        SetCellBoldFont(cell, true);
                        AutoFit(col);
                        col++;
                     }

                     col = START_COL;

                     foreach (OrgRemnants doc in docs)
                     {
                        object val = "";
                        foreach(OrgRemnantsItem oi in doc.items)
                        if (oi.id == dmi.id)
                        {
                           //contains[col - 2]++;
                           val = oi.qty;
                        }
                        cell = GetCell(row, col);
                        SetValue(cell, val);
                        col++;
                     }

                     row++;
                  }

                  //cell = GetCell(row, 1);
                  //SetValue(cell, "Процент");
                  //SetCellBoldFont(cell, true);
                  //col = 2;
                  //foreach (int val in contains)
                  //{
                  //   double prc = (double)val / matrix.items.Count;
                  //   cell = GetCell(row, col);
                  //   SetValue(cell, prc.ToString("P1"));
                  //   SetCellBoldFont(cell, true);

                  //   col++;
                  //}
                  row++;
               }

               object clmns = GetProperty(ActiveSheet, COLUMNS_STR, new object[] { "A" });
               InvokeMethod(GetProperty(clmns, "EntireColumn"), "AutoFit", (object[])null);
            }

            Visible = true;
         }
      }
   }
}
