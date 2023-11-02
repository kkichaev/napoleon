using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      private DataSet<string, Slsnet> dsSlsnet;
      private DataSet<string, City> dsCity;
      private DataSet<int, Contract> dsContract;

      public MainFormEx()
      {
         dsSlsnet = new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsCity = new DataSet<string, City>(City.OBJECT_NAME);
         dsContract = new DataSet<int, Contract>(Contract.OBJECT_NAME);

         ToolStripButton btnPhoto = new System.Windows.Forms.ToolStripButton();
         btnPhoto.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnPhoto.Image = Properties.Resources.accessorieseditor;
         btnPhoto.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnPhoto.Name = "btnPhoto";
         btnPhoto.Size = new System.Drawing.Size(23, 22);
         btnPhoto.Text = "Выгрузка фотографий";
         btnPhoto.Click += new System.EventHandler((s, e) => { new FmExportPhoto().Show(); });

         ToolStripButton btnReport = new System.Windows.Forms.ToolStripButton();
         btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnReport.Image = Properties.Resources.view_calendar_timeline;
         btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnReport.Name = "btnReport";
         btnReport.Size = new System.Drawing.Size(23, 22);
         btnReport.Text = "Отчет по контрактам";
         btnReport.Click += new System.EventHandler((s, e) => { new FmContractReport().Show(); });

         tsbConfig.Items.Add(btnPhoto);
         tsbConfig.Items.Add(btnReport);

         tgvAgentsSummaryCount.Visible = false;
         tgvAgentsSummarySum.Visible = false;
         tsbMakeHtml.Visible = false;
         btnOrderReport.Visible = false;
         btnCensus.Visible = false;
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         updSets.Insert(0, dsCity);
         updSets.Insert(0, dsSlsnet);
         updSets.Add(dsContract);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsContract.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }

      class DivisionSummaryEx : DivisionSummary
      {
         public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig):base(dsConfig) { }
         
         protected override void PostAddData()
         {
            IDataSet cdata = DataModule.Get(Contract.OBJECT_NAME);
            if (cdata != null)
               foreach (Contract order in cdata.Data)
                  this.Add(order);
         }

         private void Add(Contract contract)
         {
            if (contract.agent != null && ContainsKey(contract.userid))
            {
               SummaryData sd = this[contract.userid];
               sd.AddOrg(contract);
            }
         }
      }
   }

   
}
