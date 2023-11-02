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
   public partial class VisitQualityReport : Form
   {
      DataSet<string, Price> dsPrice;
      DataSet<string, ManagerFolder> dsFolders;
      VisitQualityReportSetting config;

      public VisitQualityReport()
      {
         InitializeComponent();

         for (int i = 0; i <= 7; i++)
            cbDay.Items.Add(new DayWeekItem(i));

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ?? new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         if( dsPrice.Count == 0 )
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsPrice);
         }

         if(dsFolders.Count == 0)
         {
            dsFolders.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsFolders);
         }

         if (upd.Count > 0)
            FmWait.StdDataRefresh(this, upd, LoadData);
         else
            LoadData();
      }

      private void LoadData()
      {
         config = VisitQualityReportSetting.Load();

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgent.Items.Contains(a.agent) == false)
                  cbAgent.Items.Add(a.agent);

            cbDivision.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivision.Items.Add(d);
         }

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;

         cbDay.SelectedIndex = config.weekday;
         
         dtpBegin.Value = config.start;
         dtpEnd.Value = config.end;

         tbItem1.Text = config.item1.name;
         tbItem2.Text = config.item2.name;
         tbItem3.Text = config.item3.name;
      }

      void SaveData()
      {
         config.start = dtpBegin.Value;
         config.end = dtpEnd.Value;
         config.weekday = cbDay.SelectedIndex;

         config.Save();
      }

      private void rbAgent_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbAgent.Enabled = (sender as RadioButton).Checked;
      }

      private void rbDivision_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbDivision.Enabled = (sender as RadioButton).Checked;
      }

      class DayWeekItem
      {
         int index;
         WeekDay day = null;

         public DayWeekItem(int index)
         {
            this.index = index;
            if (index > 0)
               day = new WeekDay(index);
         }

         public override string ToString()
         {
            return day == null ? "Все" : day.FullName;
         }

         public int Index { get { return index; } }
      }

      int GetWeekDay(DayOfWeek dw) { return dw == DayOfWeek.Sunday ? 7 : (int)dw; }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         SaveData();
         
         VisitQualReportParam param = new VisitQualReportParam();
         param.start = config.start;
         param.end = config.end; 
         param.weekday = config.weekday;
         if (param.start == param.end)
            param.weekday = GetWeekDay(param.start.DayOfWeek);

         param.items.Add(new VisitQualReportParam.Item(config.item1));
         param.items.Add(new VisitQualReportParam.Item(config.item2));
         param.items.Add(new VisitQualReportParam.Item(config.item3));

         if (rbAgent.Checked)
            param.users.Add(cbAgent.SelectedItem as Agent);
         else
         {
            Division d = cbDivision.SelectedItem as Division;
            foreach (Division.DivisionAgent a in d.GetAllAgents())
               if (a.agent != null)
                  param.users.Add(a.agent);
         }

         Config.GetConfig().GetConnection().ReceiveTimeout = 5 * 30 * 1000;
         ReportResult.DoReport("visit_quality", param, this);
      }

      private void btnSetItem1_Click(object sender, EventArgs e)
      {
         VisitQualityReportSetting.Item item = SelectItem();
         if (item != null)
         {
            config.item1 = item;
            tbItem1.Text = item.name;
         }
      }

      private void btnSetItem2_Click(object sender, EventArgs e)
      {
         VisitQualityReportSetting.Item item = SelectItem();
         if (item != null)
         {
            config.item2 = item;
            tbItem2.Text = item.name;
         }
      }

      private void btnSetItem3_Click(object sender, EventArgs e)
      {
         VisitQualityReportSetting.Item item = SelectItem();
         if (item != null)
         {
            config.item3 = item;
            tbItem3.Text = item.name;
         }
      }

      VisitQualityReportSetting.Item SelectItem()
      { 
         Price prc = null;
         ManagerFolder fld = null;
         VisitQualityReportSetting.Item item = null;

         if (FmSelectSKU.SkuDialogQuery(this, out prc, out fld) == System.Windows.Forms.DialogResult.OK)
         {
            item = new VisitQualityReportSetting.Item();
            if (fld != null)
            {
               item.id = fld.id;
               item.name = fld.name;
               item.isFolder = true;
            }
            else if (prc != null)
            {
               item.id = prc.id;
               item.name = prc.name;
               item.isFolder = false;
            }
         }
         return item;
      }
   }

   public class VisitQualReportParam : GRSoft.Network.DataObject
   {
      public DateTime start = DateTime.Now.Date;
      public DateTime end = DateTime.Now.Date; // конечная датв включиетельно
      public int weekday = 0;  // 0 все

      public class Item : GRSoft.Network.DataObject
      {
         public string id = "";
         public int isFolder;

         public Item(VisitQualityReportSetting.Item src)
         {
            id = src.id;
            isFolder = src.isFolder ? 1 : 0;
         }
      }

      public List<Item> items = new List<Item>();
      public List<Agent> users = new List<Agent>();
   }

   [Serializable]
   public class VisitQualityReportSetting : BaseFormSetting<VisitQualityReportSetting>
   {
        public DateTime start = DateTime.Now.Date;
        public DateTime end = DateTime.Now.Date;
        public int weekday = 0;  // 0 все

        [Serializable]
        public class Item
        {
           public string name = "";
           public string id = "";
           public bool isFolder;
        }

        public Item item1;
        public Item item2;
        public Item item3;
   }
}
