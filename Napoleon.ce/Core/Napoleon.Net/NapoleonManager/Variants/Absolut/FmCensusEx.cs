using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Properties;
using System.Data;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class FmCensusEx : FmCensus
   {
      public FmCensusEx()
      { 
         ToolStripButton btnMap = new ToolStripButton();
         btnMap.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnMap.Image = Resources.maporg;
         btnMap.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnMap.Margin = new System.Windows.Forms.Padding(0, 1, 0, 2);
         btnMap.Name = "btnMap";
         btnMap.Size = new System.Drawing.Size(23, 22);
         btnMap.Text = "Карта";
         btnMap.ToolTipText = "Карта";
         btnMap.Click += new System.EventHandler(btnMap_Click);
         tsbConfig.Items.Add(btnMap);

         ToolStripButton btnImport = new ToolStripButton();
         btnImport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnImport.Image = Resources.importpotorgl;
         btnImport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnImport.Margin = new System.Windows.Forms.Padding(0, 1, 0, 2);
         btnImport.Name = "btnImport";
         btnImport.Size = new System.Drawing.Size(23, 22);
         btnImport.Text = "Импорт";
         btnImport.ToolTipText = "Импорт";
         btnImport.Click += new System.EventHandler(btnImport_Click);
         tsbConfig.Items.Add(btnImport);
      }

      private void btnMap_Click(object sender, EventArgs e)
      {
         new FmPtnzMap().Show();
      }

      private void btnImport_Click(object sender, EventArgs e)
      {
         DataGridViewRow agentRow = dgvAgents.CurrentRow;

         if (agentRow == null)
            return;

         AgentData ad = agentRow.DataBoundItem as AgentData;

         if (ad == null)
            return;

         OpenFileDialog dlg = new OpenFileDialog();
         dlg.Filter = "Excel | *.xls*;";

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Invoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, false); }));

            var fileName = dlg.FileName;
            var connectionString = string.Format("Provider=Microsoft.ACE.OLEDB.12.0;Data Source={0}; Extended Properties=Excel 12.0", fileName);

            var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [Лист1$]", connectionString);
            var ds = new DataSet();

            adapter.Fill(ds, "name1");

            DataTable data = ds.Tables["name1"];
            string fid = string.Empty;

            GRSoft.Network.DataSet<string, PotenzialOrgEx> wrSet = new GRSoft.Network.DataSet<string, PotenzialOrgEx>(PotenzialOrg.OBJECT_NAME, false);

            bool skipFirstRow = true;

            foreach (DataRow row in data.Rows)
            {
               if (skipFirstRow)
               {
                  skipFirstRow = false;
                  continue;
               }

               object[] r = row.ItemArray;
               if (r[3].ToString().Trim().Length > 0)
               {
                  PotenzialOrgEx pe = new PotenzialOrgEx();
                  pe.id = GRSoft.Network.DataObject.GenId();
                  pe.userid = ad.Agent.id;
                  pe.name = r[2].ToString();
                  pe.address = r[3].ToString();
                  pe.outer = 1;
                  wrSet.Add(pe.id, pe);
               }
            }

            List<IDataSet> add = new List<IDataSet>(new IDataSet[] { wrSet });
            DataModule.UpdateDataSet(add, null, null, Config.GetConfig().GetConnection(), ad.Agent.id);

            Invoke(new EmptyParamHandler(delegate() { FmWait.CloseForm(); }));
            btnRefresh_Click(btnRefresh, EventArgs.Empty);
         }
      }
   }
}
