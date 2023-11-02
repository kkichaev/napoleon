using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmRejectReturn : Form
   {
      private DataSet<string, Org> dsOrg = new DataSet<string,Org>(Org.OBJECT_NAME);
      private DataSet<string, OrgDistrict> dsOrgDistrict;
      private DataSet<string, OrgDistrict> dsChanged = new DataSet<string,OrgDistrict>(OrgDistrict.OBJECT_NAME,false);
      SortableBindingList<RowData> alldata;

      public FmRejectReturn()
      {
         InitializeComponent();
         dsOrgDistrict = (DataSet<string, OrgDistrict>)DataModule.Get(OrgDistrict.OBJECT_NAME) ?? new DataSet<string, OrgDistrict>(OrgDistrict.OBJECT_NAME);
         btnSave.Enabled = false;
      }

      private void FmRejectReturn_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            List<Agent> al = new List<Agent>();
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               al.Add(da.agent);
            }

            al.Sort();
            al.ForEach(x => cbAgents.Items.Add(x));

            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;
         }
      }

      private void bntRefresh_Click(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
         {
            dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), Org.OBJECT_NAME);
            dsOrgDistrict.Filter = String.Format("\"userid\" = {0}", a.id);

            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsOrg);
            upd.Add(dsOrgDistrict);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
         }
      }

      private void DoLoadData()
      {
         btnSave.Enabled = false;
         dsChanged.Clear();

         List<RowData> list = new List<RowData>();

         foreach(Org o in dsOrg.Values)
         {
            bool rr = false;

            if (dsOrgDistrict.ContainsKey(o.id))
               rr = dsOrgDistrict[o.id].rejret != 0;

            RowData rd = new RowData(this);
            rd.org = o;
            rd.rejret = rr;

            list.Add(rd);
         }

         alldata = new SortableBindingList<RowData>(list);
         grid.DataSource = alldata;
      }

      class RowData
      {
         public Org org;
         public bool rejret = false;
         public FmRejectReturn form;

         public RowData(FmRejectReturn form)
         {
            this.form = form;
         }

         public string Name { get { return org.name; } }
         public bool RejRet { get { return rejret; } set { rejret = value; form.ApplyChanges(this); } }
      }


      private void ApplyChanges(RowData rowData)
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if(a != null)
         {
            if (!dsChanged.ContainsKey(rowData.org.id))
            {
               OrgDistrict o = new OrgDistrict();
               o.id = rowData.org.id;
               o.userid = a.id;
               dsChanged[rowData.org.id] = o;
            }

            dsChanged[rowData.org.id].rejret = rowData.rejret ? 1 : 0;
            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<IDataSet> wrSet = new List<IDataSet>();

         if (dsChanged.Count > 0)
            wrSet.Add(dsChanged);

         if (DataModule.WriteDataSet(wrSet, Config.GetConfig().GetConnection()))
            btnSave.Enabled = false;
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string str)
      {
         if (alldata != null)
         {
            str = str.ToUpper();

            List<RowData> src = new List<RowData>();
            foreach (RowData o in alldata)
               if (o.Name.ToUpper().Contains(str))
                  src.Add(o);

            grid.DataSource = new SortableBindingList<RowData>(src);
         }
      }

      bool clearing = false;


      private void ClearFind()
      {
         clearing = true;
         tbFind.Text = "";

         grid.DataSource = alldata;

         clearing = false;
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         ClearFind();
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind();
      }
   }
}
