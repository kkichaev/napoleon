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
      DataGridViewCheckBoxColumn cbByRoute = new DataGridViewCheckBoxColumn();

      List<CommonConfig> updated = new List<CommonConfig>();
      ConfigKeyItems byRouteCfg = new ConfigKeyItems("ByRoute");

      //private DataSet<int, CommonConfig> dsConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

      public DivisionFormEx() : base()
      {
         childUserList.CurrentCellDirtyStateChanged += new System.EventHandler(CurrentCellDirtyStateChanged);

         cbByRoute.DataPropertyName = "ByRoute";
         cbByRoute.HeaderText = "Выполн. маршрута";
         cbByRoute.Name = "costchg";
         cbByRoute.Width = 65;

         childUserList.Columns.Add(cbByRoute);
      }

      void CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         DataGridViewCell cell = childUserList.CurrentCell;
         if (cell != null && cell.ColumnIndex == cbByRoute.Index)
         {
            childUserList.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      //internal override void BeforeUpdate(List<IDataSet> updSet)
      //{
      //   const string filter = " not userid is null or not userid = ''";
      //   dsConfig.Filter = filter;

      //   updSet.Add(dsConfig);
      //}


      internal bool CheckByRoute(Agent a)
      {
         foreach (CommonConfig c in updated)
         {
            if (c.userid.Equals(a.id) && c.key.Equals(byRouteCfg.Key))
               return (int.Parse(c.value) == 1);
         }

         CommonConfig cc = ConfigUtils.GetConfig(dsCommonConfig, byRouteCfg, a);
         //CommonConfig cc = ConfigUtils.GetConfig(dsConfig, byRouteCfg, a);
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
            ConfigUtils.AddConfig(dsCommonConfig, c);
         updated.Clear();
      }

      internal void SetByRoute(Agent a, bool canChange)
      {
         if (parent != null)
         {
            string value = (canChange) ? "1" : "0";
            foreach (CommonConfig c in updated)
            {
               if (c.userid.Equals(a.id) && c.key.Equals(byRouteCfg.Key))
               {
                  c.value = value;
                  return;
               }
            }

            CommonConfig cc = new CommonConfig();
            cc.userid = a.id;
            cc.value = value;
            cc.key = byRouteCfg.Key;
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

         public bool ByRoute
         {
            get
            {
               return ((DivisionFormEx)owner).CheckByRoute(agent);
            }

            set
            {
               ((DivisionFormEx)owner).SetByRoute(agent, value);
            }
         }
      }
   }
}