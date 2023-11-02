using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class FmConfigEx : FmConfig
   {
      TabPage tpData = new TabPage();
      AddConfigData acd = new AddConfigData();
      public FmConfigEx()
      {
         tpData.Controls.Add(acd);
         tpData.Location = new System.Drawing.Point(4, 22);
         tpData.Name = "tpData";
         tpData.Padding = new System.Windows.Forms.Padding(3);
         tpData.Size = new System.Drawing.Size(364, 263);
         tpData.Text = "Данные";
         tpData.UseVisualStyleBackColor = true;
         tbConfig.TabPages.Add(tpData);

         tbConfig.SelectedIndexChanged += tbConfig_SelectedIndexChanged;
      }

      void tbConfig_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (tbConfig.SelectedTab == tpData) 
         {
            List<IDataSet> sel = new List<IDataSet>();

            if (dsConfig.Count == 0)
            {
               sel.Add(dsConfig);
               FmWait.StdDataRefresh(this, sel, DoLoad);
            }
            else
               DoLoad();
         }
      }

      protected override void FillConfigFromControls()
      {
         base.FillConfigFromControls();
         string orgCode = acd.tbOrgCode.Text.Trim();
         if(orgCode.Length > 0 || dsConfig.Count > 0 )
         {
            CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG, "");
            if (cc == null || cc.value != orgCode)
            {
               if (cc == null)
               {
                  cc = ConfigUtils.CreateConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG, "");
                  dsConfig.Add(dsConfig.Count, cc);
               }

               cc.value = acd.tbOrgCode.Text;
               List<IDataSet> wr = new List<IDataSet>();
               wr.Add(dsConfig);
               DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
            }
         }
      }

      void DoLoad()
      {
         CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG, "");
         acd.tbOrgCode.Text = cc == null ? "" : cc.value;
      }
   }
}
