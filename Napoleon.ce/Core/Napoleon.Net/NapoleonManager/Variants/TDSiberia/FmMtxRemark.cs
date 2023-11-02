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
   public partial class FmMtxRemark : Form
   {
      protected DataSet<int, CommonConfig> dsConfig;
      private static readonly string MTXKEY = "mtxremark";
      public FmMtxRemark()
      {
         InitializeComponent();
         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ?? new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         CommonConfig cc = new CommonConfig();
         cc.userid = string.Empty;
         cc.key = MTXKEY;

         StringBuilder sb = new StringBuilder();

         foreach(Remark r in (BindingList<Remark>)(((BindingSource)grid.DataSource).DataSource))
         {
            if (r.Text.Trim().Length == 0)
               continue;

            if (sb.Length > 0)
               sb.Append(';');

            sb.Append(r.Text);
         }

         cc.value = sb.ToString().Trim();

         if (cc.value.Length > 0)
         {
            DataSet<int, CommonConfig> cfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            cfg.Add(cfg.Count, cc);
            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(cfg);

            if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
               DialogUtil.UpdateErrMsg(this);
            else
               btnSave.Enabled = false;
         }
      }

      private void DoLoadData()
      {
         CommonConfig cc = ConfigUtils.GetCommonConfig(dsConfig, new ConfigKeyItems(MTXKEY)) ?? new CommonConfig();
         string[] vals = cc.value.Split(';');

         BindingList<Remark> list = new BindingList<Remark>();
         foreach (string v in vals)
            list.Add(new Remark(v));

         BindingSource bs = new BindingSource();
         bs.DataSource = list;
         bs.AllowNew = true;
         bs.AddingNew += bs_AddingNew;

         grid.DataSource = bs;

         btnSave.Enabled = false;
      }

      void bs_AddingNew(object sender, AddingNewEventArgs e)
      {
         e.NewObject = new Remark(string.Empty);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         dsConfig.Filter = "(not (\"userid\" is null)) or \"userid\" is null";

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsConfig);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      class Remark
      {
         public string text = string.Empty;

         public Remark(string text)
         {
            this.text = text;
         }

         public string Text { get { return text; } set { text = value; } }
      }

      private void FmMtxRemark_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void grid_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void grid_RowsRemoved(object sender, DataGridViewRowsRemovedEventArgs e)
      {
         btnSave.Enabled = true;
      }
   }
}
