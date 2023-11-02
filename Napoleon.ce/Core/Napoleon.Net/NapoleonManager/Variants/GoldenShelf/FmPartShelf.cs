using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPartShelf : Form
   {
      private DataSet<string, ContractDef> dsContract;
      private DataSet<string, Slsnet> dsSlsnet;
      private DataSet<int, PartShelf> dsPartShelf;
      private Dictionary<string, List<PartShelfData>> shelfdata = new Dictionary<string, List<PartShelfData>>();

      public FmPartShelf()
      {
         InitializeComponent();

         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;

         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsPartShelf = (DataSet<int, PartShelf>) DataModule.Get(PartShelf.OBJECT_NAME) ?? new DataSet<int, PartShelf>(PartShelf.OBJECT_NAME);

         dgvContract.AutoGenerateColumns = false;
         dgvPartShelf.AutoGenerateColumns = false;

         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsContract);
         upd.Add(dsSlsnet);

         const string PERIOD_FILTER_STR = "\"start\" < ToDate('{1:dd/MM/yyyy}') and \"finish\" >= ToDate('{0:dd/MM/yyyy}')";

         dsContract.Filter = string.Format(PERIOD_FILTER_STR, dpv.Start, dpv.Finish.AddDays(1));

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         List<ContractDef> list = new List<ContractDef>();
         list.AddRange(dsContract.Values);
         list.Sort((lhs, rhs) => { return lhs.start.CompareTo(rhs.start); });
         dgvContract.DataSource = list;
      }

      private void dgvContract_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         List<PartShelfData> list = new List<PartShelfData>();

         ContractDef cd = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as ContractDef;

         if(cd != null)
            UpdatePartData(list, cd);

         dgvPartShelf.DataSource = list;
      }

      private void UpdatePartData(List<PartShelfData> list, ContractDef cd)
      {
         if (!shelfdata.ContainsKey(cd.id))
         {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsPartShelf);
            dsPartShelf.Filter = string.Format("\"cid\"='{0}'", cd.id);
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, null).Join();
            Dictionary<string, double> data = new Dictionary<string, double>();

            foreach (PartShelf ps in dsPartShelf.Values)
               data[ps.sid] = ps.part;

            foreach (Slsnet s in dsSlsnet.Values)
               list.Add(new PartShelfData(cd.id, s, data.ContainsKey(s.id) ? data[s.id] : 0));

            shelfdata[cd.id] = list;
         }
         else
            list.AddRange(shelfdata[cd.id]);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
            btnSave.Enabled = false;
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private bool Save()
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         IDataSet upd = CollectUpdates();

         if (upd.Count > 0)
            wrSet.Add(upd);

         return DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection());
      }

      private IDataSet CollectUpdates()
      {
         DataSet<int, PartShelf> ds = new DataSet<int, PartShelf>(PartShelf.OBJECT_NAME, false);

         foreach (List<PartShelfData> list in shelfdata.Values)
            foreach(PartShelfData d in list)
            { 
               PartShelf ps = new PartShelf();
               ps.cid = d.CID;
               ps.sid = d.SID;
               ps.part = d.Part;
               ds.Add(ds.Count, ps);
            }

         return ds;
      }

      private void dgvPartShelf_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void FmPartShelf_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void FmPartShelf_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save();
      }
   }

   class PartShelfData
   {
      private string cid = string.Empty;
      private Slsnet sls;
      private double part = 0.0;

      public PartShelfData(string cid, Slsnet sls, double part)
      {
         this.cid = cid;
         this.sls = sls;
         this.part = part;
      }

      public string Name { get { return sls.Name; } }
      public double Part { get { return part; } set { part = value; } }
      public string CID { get { return cid; } }
      public string SID { get { return sls.id; } }
   }
}
