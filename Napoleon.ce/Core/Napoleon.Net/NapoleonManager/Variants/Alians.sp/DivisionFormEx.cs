using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      readonly static string RANGE = "Range";
      readonly static string ORDRNG = "OrdRng";
      readonly static string SHOWPRICE = "ShowPrice";
      readonly static string SHOWASSORTMTX = "ShowAssortMtx";

      DataGridViewComboBoxColumn range = new DataGridViewComboBoxColumn();
      DataGridViewComboBoxColumn ordrng = new DataGridViewComboBoxColumn();
      DataGridViewCheckBoxColumn showprice = new DataGridViewCheckBoxColumn();
      DataGridViewCheckBoxColumn showassortmtx = new DataGridViewCheckBoxColumn();

      public DivisionFormEx()
      {
         string[] rangeVal = new string[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };

         range.HeaderText = "История (мес.)";
         range.DataPropertyName = RANGE;
         range.Items.AddRange(rangeVal);
         range.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         range.FillWeight = 30;

         ordrng.HeaderText = "Без заказа (нед.)";
         ordrng.DataPropertyName = ORDRNG;
         ordrng.Items.AddRange(rangeVal);
         ordrng.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         ordrng.FillWeight = 30;

         showprice.HeaderText = "Весь прайс";
         showprice.DataPropertyName = SHOWPRICE;
         showprice.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         showprice.FillWeight = 30;

         showassortmtx.HeaderText = "Активный ассортимент";
         showassortmtx.DataPropertyName = SHOWASSORTMTX;
         showassortmtx.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         showassortmtx.FillWeight = 30;

         childUserList.Columns.Add(range);
         childUserList.Columns.Add(ordrng);
         childUserList.Columns.Add(showprice);
         childUserList.Columns.Add(showassortmtx);
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      private void MarkChanged()
      {
         parent.MarkChanged();
      }

      class DataItemEx : DataItem
      {
         private bool inited = false;
         public string range = "2";
         public string ordrng = "2";
         public bool showprice = false;
         public bool showassortmtx = false;

         public string Range
         {
            get 
            {
               if (!inited)
                  Init();

               return range; 
            }

            set
            {
               range = value; 
               ((DivisionFormEx)owner).MarkChanged();
            }
         }

         public bool ShowPrice
         {
            get
            {
               if (!inited)
                  Init();

               return showprice;
            }

            set
            {
               showprice = value;
               ((DivisionFormEx)owner).MarkChanged();
            }
         }

         public bool ShowAssortMtx
         {
            get
            {
               if (!inited)
                  Init();

               return showassortmtx;
            }

            set
            {
               showassortmtx = value;
               ((DivisionFormEx)owner).MarkChanged();
            }
         }

         public string OrdRng
         {
            get
            {
               if (!inited)
                  Init();

               return ordrng;
            }

            set
            {
               ordrng = value;
               ((DivisionFormEx)owner).MarkChanged();
            }
         }

         private void Init()
         {
            inited = true;
            bool br = false;
            bool bu = false;
            bool bp = false;
            bool ba = false;

            try
            {
               foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
                  if (serverConfig.userid.Equals(agent.id))
                  {
                     if (!br && serverConfig.key.Equals(RANGE))
                     {
                        br = true;
                        range = serverConfig.value;
                     }
                     else if (!bu && serverConfig.key.Equals(ORDRNG))
                     {
                        bu = true;
                        ordrng = serverConfig.value;
                     }
                     else if (!bp && serverConfig.key.Equals(SHOWPRICE))
                     {
                        bu = true;
                        showprice = Boolean.Parse(serverConfig.value);
                     }
                     else if (!ba && serverConfig.key.Equals(SHOWASSORTMTX))
                     {
                        bu = true;
                        showassortmtx = Boolean.Parse(serverConfig.value);
                     }
                  }
            }
            catch (Exception) { }
         }

         public DataItemEx(Agent a, DivisionForm o)
            : base(a, o)
         {
         }
      }

      internal override bool BeforeWriteChanges(List<GRSoft.Network.IDataSet> wrObj, List<GRSoft.Network.IDataSet> rmvObj, List<GRSoft.Network.ReplacedSet> replaced, GRSoft.Network.DBConnection conn)
      {
         int ctr = 0;
         DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

         foreach (DataItemEx item in (List<object>)((RefreshableSource) childUserList.DataSource).DataSource)
         {
            CommonConfig cfg = new CommonConfig();
            cfg.key = RANGE;
            cfg.userid = item.agent.id;
            cfg.value = item.range;
            addCfg[ctr++] = cfg;

            cfg = new CommonConfig();
            cfg.key = ORDRNG;
            cfg.userid = item.agent.id;
            cfg.value = item.ordrng;
            addCfg[ctr++] = cfg;

            cfg = new CommonConfig();
            cfg.key = SHOWPRICE;
            cfg.userid = item.agent.id;
            cfg.value = item.showprice.ToString();
            addCfg[ctr++] = cfg;

            cfg = new CommonConfig();
            cfg.key = SHOWASSORTMTX;
            cfg.userid = item.agent.id;
            cfg.value = item.showassortmtx.ToString();
            addCfg[ctr++] = cfg;
         }

         wrObj.Add(addCfg);

         return true;
      }

      protected override bool IsAllowCommit(DataGridViewCell cell)
      {
         bool result = base.IsAllowCommit(cell);

         if (!result)
            result = cell != null && 
               (childUserList.Columns[cell.ColumnIndex].DataPropertyName == ORDRNG ||
                  childUserList.Columns[cell.ColumnIndex].DataPropertyName == RANGE ||
                  childUserList.Columns[cell.ColumnIndex].DataPropertyName == SHOWPRICE ||
                  childUserList.Columns[cell.ColumnIndex].DataPropertyName == SHOWASSORTMTX);

         return result;
      }
   }

   class ConfigCfg : CommonConfig
   {
      public static readonly new string OBJECT_NAME = "Config";
   }
}
