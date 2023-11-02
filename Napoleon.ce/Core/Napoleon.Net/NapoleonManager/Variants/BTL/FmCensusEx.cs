using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmCensusEx : FmCensus
   {
      public DataSet<string, Region> dsRegion;
      public DataSet<string, Region1> dsRegion1;
      public DataSet<string, Region2> dsRegion2;

      public FmCensusEx()
      {
         dsRegion = (DataSet<string, Region>)DataModule.Get(GRSoft.NapoleonManager.Region.OBJECT_NAME) ??
            new DataSet<string, Region>(GRSoft.NapoleonManager.Region.OBJECT_NAME);
         dsRegion1 = (DataSet<string, Region1>)DataModule.Get(GRSoft.NapoleonManager.Region1.OBJECT_NAME) ??
            new DataSet<string, Region1>(GRSoft.NapoleonManager.Region1.OBJECT_NAME);
         dsRegion2 = (DataSet<string, Region2>)DataModule.Get(GRSoft.NapoleonManager.Region2.OBJECT_NAME) ??
            new DataSet<string, Region2>(GRSoft.NapoleonManager.Region2.OBJECT_NAME);

         splitContainer4.Panel2Collapsed = true;

         // 
         // clmnRegion
         // 
         DataGridViewTextBoxColumn clmnRegion = new DataGridViewTextBoxColumn();
         clmnRegion.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnRegion.DataPropertyName = "Region";
         clmnRegion.HeaderText = "Н/П";
         clmnRegion.Name = "clmnRegion";

         dgvOrgs.Columns.Insert(1, clmnRegion);

         clmnTaskCount.Visible = false;
         clmnDoneCount.Visible = false;
         clmnOrgTask.Visible = false;
         clmnOrgDone.Visible = false;
         dtpBeginDate.Visible = false;
         dtpEndDate.Visible = false;
         toolStripLabel1.Visible = false;
         toolStripLabel2.Visible = false;
         btnRefresh.Margin = new Padding();
         tsbSelectRange.Visible = false;
      }

      protected override FmPtnzlOrgEdit CreateFmPotenzlOrgEdit()
      {
         FmPtnzlOrgEditEx result = new FmPtnzlOrgEditEx(this);
         return result;
      }

      protected override List<IDataSet> CreateUpdateList()
      {
         List <IDataSet> result =  base.CreateUpdateList();

         if (result == null)
            result = new List<IDataSet>();

         result.Insert(0, dsRegion);
         result.Insert(0, dsRegion1);
         result.Insert(0, dsRegion2);

         return result;
      }
   }
}
