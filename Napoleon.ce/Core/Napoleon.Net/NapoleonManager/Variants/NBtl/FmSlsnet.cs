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
   public partial class FmSlsnet : Form
   {
      public DataSet<string, Slsnet> dsSlsnet;
      protected BindingList<Slsnet> datasource = new BindingList<Slsnet>();
      protected string lastupdateitem = string.Empty;
      private DataSet<string, Slsnet> dsRemSlsnet = new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME, false);

      public FmSlsnet()
      {
         InitializeComponent();
         btnSave.Enabled = false;

         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         grid.AutoGenerateColumns = false;
         grid.DataSource = datasource;
         DoLoadData();
      }

      public delegate void SlsnetRefresh(string id);

      public event SlsnetRefresh OnSlsnetRefresh;

      private void FmSlsnet_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save();
      }

      private bool Save()
      {
         List<IDataSet> wrSet = new List<IDataSet>();

         if(dsSlsnet.Count > 0)
            wrSet.Add(dsSlsnet);

         List<IDataSet> rmSet = new List<IDataSet>();

         if(dsRemSlsnet.Count > 0)
            rmSet.Add(dsRemSlsnet);

         FireSlsnetRefresh();
         return DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
      }

      private void FireSlsnetRefresh()
      {
         if (lastupdateitem.Trim().Length > 0 && OnSlsnetRefresh != null)
            OnSlsnetRefresh(lastupdateitem);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Add();
      }

      protected virtual void Add()
      {
         FmSlsnetEdit dialog = new FmSlsnetEdit();

         if (dialog.ShowDialog() == DialogResult.OK)
         {
            Slsnet sls = new Slsnet();
            sls.id = GRSoft.Network.DataObject.GenId();
            sls.name = dialog.Slsnet;
            sls.plan = dialog.Plan;

            datasource.Add(sls);
            dsSlsnet.Add(sls.id, sls);

            lastupdateitem = sls.id;
            grid.Invalidate();
            btnSave.Enabled = true;
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
          Edit();
      }

      protected virtual void Edit()
      {
          if (grid.CurrentRow != null)
          {
              FmSlsnetEdit dialog = new FmSlsnetEdit();
              Slsnet sls = grid.CurrentRow.DataBoundItem as Slsnet;

              if (sls != null)
              {
                  dialog.Slsnet = sls.Name;
                  dialog.Plan = sls.Plan;

                  if (dialog.ShowDialog() == DialogResult.OK)
                  {
                      sls.name = dialog.Slsnet;
                      sls.plan = dialog.Plan;  
                      btnSave.Enabled = true;
                      lastupdateitem = sls.id;
                      grid.Invalidate();
                  }
              }
          }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            Slsnet sls = grid.CurrentRow.DataBoundItem as Slsnet;

            if (sls != null && DialogUtil.AskToDel(this))
            {
               datasource.Remove(sls);
               dsSlsnet.Remove(sls.id);

               btnSave.Enabled = true;
               dsRemSlsnet.Add(sls.id, sls);
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
         {
            dsRemSlsnet.Clear();
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsSlsnet);

         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      private void DoLoadData()
      {
         datasource.Clear();

         List<Slsnet> list = new List<Slsnet>();
         list.AddRange(dsSlsnet.Values);
         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });

         foreach (Slsnet s in list)
            datasource.Add(s);
      }

      private void FmSlsnet_Load(object sender, EventArgs e)
      {
         if (dsSlsnet.Count == 0)
            btnRefresh.PerformClick();
      }
   }
}
