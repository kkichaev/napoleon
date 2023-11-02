using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      readonly static String COST_PROPERTY_NAME = "Costype";
      public DataSet<int, ConfigCfg> dsConfig = new DataSet<int, ConfigCfg>(ConfigCfg.OBJECT_NAME, false);
      List<String> costList = new List<string>();
      DataGridViewComboBoxColumn costype = new DataGridViewComboBoxColumn();

      public DivisionFormEx()
      {
         costype.HeaderText = "Тип цены";
         costype.DataPropertyName = COST_PROPERTY_NAME;
         
         childUserList.Columns.Add(costype);
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      private void MarkChanged()
      {
         parent.MarkChanged();
      }

      internal override void BeforeUpdate(List<IDataSet> updSet)
      {
         updSet.Add(dsConfig);
      }

      internal override void DataLoaded()
      {
         string ccs = string.Empty;

         foreach (ConfigCfg cc in dsConfig.Data)
            if (cc.key.Equals("ВидЦены"))
               ccs = cc.value;

         costList.AddRange(ccs.Split(';'));
         costype.DataSource = costList;
      }

      class CCVal
      {
         public int val = 0;
      }

      class DataItemEx : DataItem
      {
         public CCVal costype;

         public string Costype
         {
            get 
            {
               if (costype == null)
               {
                  costype = new CCVal();
                  costype.val = GetCostType();
               }

               if (costype.val >= 0 && costype.val < ((DivisionFormEx)owner).costList.Count)
                  return ((DivisionFormEx)owner).costList[costype.val];
               else
                  return string.Empty;
            } 

            set 
            {
               int idx = ((DivisionFormEx)owner).costList.IndexOf(value);

               if (idx >= 0)
                  costype.val = idx;

               ((DivisionFormEx)owner).MarkChanged();
            } 
         }

         public DataItemEx(Agent a, DivisionForm o)
            : base(a, o)
         {
         }

         private int GetCostType()
         {
            int result = 0;
            try
            {
               foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
                  if (serverConfig.userid.Equals(agent.id) &&
                        serverConfig.key.Equals(COST_PROPERTY_NAME))
                  {
                     result = Int32.Parse(serverConfig.value);
                     break;
                  }
            }
            catch (Exception) { }

            return result;
         }
      }

      internal override bool BeforeWriteChanges(List<GRSoft.Network.IDataSet> wrObj, List<GRSoft.Network.IDataSet> rmvObj, List<GRSoft.Network.ReplacedSet> replaced, GRSoft.Network.DBConnection conn)
      {
         int ctr = 0;
         DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

         foreach (DataItemEx item in (List<object>)((RefreshableSource) childUserList.DataSource).DataSource)
         {
            CommonConfig cfg = new CommonConfig();
            cfg.key = COST_PROPERTY_NAME;
            cfg.userid = item.agent.id;
            cfg.value = item.costype == null ? "0" : item.costype.val.ToString();
            addCfg[ctr++] = cfg;
         }

         wrObj.Add(addCfg);

         return true;
      }

      protected override bool IsAllowCommit(DataGridViewCell cell)
      {
         bool result = base.IsAllowCommit(cell);

         if (!result)
            result = cell != null && childUserList.Columns[cell.ColumnIndex].DataPropertyName == COST_PROPERTY_NAME;

         return result;
      }
   }

   class ConfigCfg : CommonConfig
   {
      public static readonly new string OBJECT_NAME = "Config";
   }
}
