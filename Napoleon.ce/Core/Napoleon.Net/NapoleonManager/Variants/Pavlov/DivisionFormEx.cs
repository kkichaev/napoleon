/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Подразделения для Закромов - выбор склада
 * 
 * ert   28/03/2011   creating
 */
using GRSoft.Network;
using System.Windows.Forms;
using System.Collections;
using System.Collections.Generic;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      DataGridViewCheckBoxColumn whColumn = new DataGridViewCheckBoxColumn();
      DataGridViewTextBoxColumn whMinCost = new DataGridViewTextBoxColumn();

      List<CommonConfig> updated = new List<CommonConfig>();

      private DataSet<int, CommonConfig> dsConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

      public DivisionFormEx() : base()
      {
         childUserList.CurrentCellDirtyStateChanged += new System.EventHandler(CurrentCellDirtyStateChanged);

         whColumn.DataPropertyName = "CostChangeAllowed";
         whColumn.HeaderText = "Можно менять цену";
         whColumn.Name = "costchg";
         whColumn.Width = 65;

         childUserList.Columns.Add(whColumn);

         whMinCost.DataPropertyName = "AgentMinCost";
         whMinCost.HeaderText = "Мин. заказ";
         whMinCost.Name = "mincost";
         whMinCost.Width = 65;

         childUserList.Columns.Add(whMinCost);
      }

      void CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         DataGridViewCell cell = childUserList.CurrentCell;
         if (cell != null &&
               (childUserList.Columns[cell.ColumnIndex].HeaderText == whColumn.HeaderText)
            )
         {
            childUserList.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      internal override void BeforeUpdate(List<IDataSet> updSet)
      {
         const string filter = " not userid is null or not userid = ''";
         dsConfig.Filter = filter;

         updSet.Add(dsConfig);
      }


      internal bool IsAgentCanChangeCost(Agent a)
      {
         foreach (CommonConfig c in updated)
         {
            if (c.userid.Equals(a.id) && c.key.Equals(ConfigKeyItems.ALLOW_CHANGE_COST.Key))
               return (int.Parse(c.value) == 1);
         }

         CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.ALLOW_CHANGE_COST, a);
         if( cc == null )
            return false;

         return (int.Parse(cc.value) == 1);
      }

      internal override bool BeforeWriteChanges(List<IDataSet> wrObj, List<IDataSet> rmvObj, List<ReplacedSet> replaced, DBConnection conn)
      {
         bool ret = true;

         if (updated.Count > 0)
         {
            int index = 0;
            DataSet<int, CommonConfig> ins = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            foreach (CommonConfig c in updated)
               ins.Add(index++, c);

            wrObj.Add(ins);
         }

         return ret;
      }

      internal override void AfterWrited()
      {
         foreach (CommonConfig c in updated)
         {
            ConfigUtils.AddConfig(dsCommonConfig, c);
            ConfigUtils.AddConfig(dsConfig, c);
         }
         // foreach (CommonConfig c in updated)
            // ConfigUtils.AddConfig(dsCommonConfig, c);
         updated.Clear();
      }

      internal void SetAgentCanChangeCost(Agent a, bool canChange)
      {
         if (parent != null)
         {
            string value = (canChange) ? "1" : "0";
            foreach (CommonConfig c in updated)
            {
               if (c.userid.Equals(a.id) && c.key.Equals(ConfigKeyItems.ALLOW_CHANGE_COST.Key))
               {
                  c.value = value;
                  return;
               }
            }

            CommonConfig cc = new CommonConfig();
            cc.userid = a.id;
            cc.value = value;
            cc.key = ConfigKeyItems.ALLOW_CHANGE_COST.Key;
            updated.Add(cc);

            parent.MarkChanged();
         }
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      class DataItemEx : DivisionForm.DataItem
      {
         public DataItemEx(Agent a, DivisionForm o) : base(a, o)
         {
         }

         public bool CostChangeAllowed
         {
            get
            {
               return ((DivisionFormEx)owner).IsAgentCanChangeCost(agent);
            }

            set
            {
               ((DivisionFormEx)owner).SetAgentCanChangeCost(agent, value);
            }
         }

         public string AgentMinCost
         {
            get
            {
               return ((DivisionFormEx)owner).GetAgentMinCost(agent);
            }

            set
            {
               ((DivisionFormEx)owner).SetAgentMinCost(agent, value);
            }
         }
      }

      private void InitializeComponent()
      {
         this.SuspendLayout();
         // 
         // DivisionFormEx
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.Name = "DivisionFormEx";
         this.Size = new System.Drawing.Size(1310, 530);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      internal string GetAgentMinCost(Agent a)
      {
         foreach (CommonConfig c in updated)
         {
            if (c.userid.Equals(a.id) && c.key.Equals(ConfigKeyItems.MIN_COST.Key))
               return c.value;
         }

         CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.MIN_COST, a);
         if (cc == null)
            return string.Empty;

         return cc.value;
      }

      internal void SetAgentMinCost(Agent a, string value)
      {
         if (parent != null)
         {
            foreach (CommonConfig c in updated)
            {
               if (c.userid.Equals(a.id) && c.key.Equals(ConfigKeyItems.MIN_COST.Key))
               {
                  c.value = value;
                  return;
               }
            }

            CommonConfig cc = new CommonConfig();
            cc.userid = a.id;
            cc.value = value ?? "";
            cc.key = ConfigKeyItems.MIN_COST.Key;
            updated.Add(cc);

            parent.MarkChanged();
         }
      }
   }
}