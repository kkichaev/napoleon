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
      TimeColumn minVisitDuration = new TimeColumn();

      ConfigKeyItems minVD = new ConfigKeyItems("MinVisitDuration");
      List<CommonConfig> updated = new List<CommonConfig>();

      private DataSet<int, CommonConfig> dsConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

      public DivisionFormEx() : base()
      {
         childUserList.CurrentCellDirtyStateChanged += new System.EventHandler(CurrentCellDirtyStateChanged);

         minVisitDuration.DataPropertyName = "MinVisitDuration";
         minVisitDuration.HeaderText = "Мин. t визита";
         minVisitDuration.Name = "minvd";
         minVisitDuration.Width = 85;

         childUserList.Columns.Add(minVisitDuration);

         ContextMenuStrip cms = new ContextMenuStrip();
         ToolStripMenuItem tsi = new ToolStripMenuItem();
         tsi.Size = new System.Drawing.Size(173, 22);
         tsi.Text = "Перенести данные агента";
         tsi.Click += MoveAgentData;
         cms.Size = new System.Drawing.Size(174, 26);
         cms.Items.Add(tsi);

         childUserList.ContextMenuStrip = cms;
         childUserList.MouseDown += ChildUserList_MouseDown;
      }

      private void MoveAgentData(object sender, System.EventArgs e)
      {
         if (childUserList.SelectedRows.Count == 0)
            return;

         DataItem di = childUserList.SelectedRows[0].DataBoundItem as DataItem;
         Agent a = di.agent;
         FmMoveAgentData form = new FmMoveAgentData();
         form.SrcAgent = a;
         form.SrcDivision = division;
         if(form.ShowDialog() == DialogResult.OK)
         {
            parent.GetData(true);
            RefreshDataSets();
         }
      }

      private void ChildUserList_MouseDown(object sender, MouseEventArgs e)
      {
         if(e.Button == MouseButtons.Right)
         {
            var ht = childUserList.HitTest(e.X, e.Y);
            if(ht.RowIndex >= 0)
            {
               childUserList.ClearSelection();
               childUserList.Rows[ht.RowIndex].Selected = true;
            }
         }
      }

      void CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
      }

      internal override void BeforeUpdate(List<IDataSet> updSet)
      {
         const string filter = " not userid is null or not userid = ''";
         dsConfig.Filter = filter;

         updSet.Add(dsConfig);
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
         updated.Clear();
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

         public string MinVisitDuration
         {
            get
            {
               return ((DivisionFormEx)owner).GetAgentVisitDuration(agent);
            }

            set
            {
               ((DivisionFormEx)owner).SetAgentVisitDuration(agent, value);
            }
         }
      }

      string ConfigToValue(string cfgVal)
      {
         int val;
         int.TryParse(cfgVal, out val);
         return TimeCell.ToStr(val);
      }

      internal string GetAgentVisitDuration(Agent a)
      {
         foreach (CommonConfig c in updated)
         {
            if (c.userid.Equals(a.id) && c.key.Equals(minVD.Key))
               return ConfigToValue(c.value);
         }

         CommonConfig cc = ConfigUtils.GetConfig(dsConfig, minVD, a);
         if (cc == null)
            return string.Empty;

         return ConfigToValue(cc.value);
      }

      internal void SetAgentVisitDuration(Agent a, string value)
      {
         value = TimeCell.From(value).ToString();
         if (parent != null)
         {
            foreach (CommonConfig c in updated)
            {
               if (c.userid.Equals(a.id) && c.key.Equals(minVD.Key))
               {
                  c.value = value;
                  parent.MarkChanged();
                  return;
               }
            }

            CommonConfig cc = new CommonConfig();
            cc.userid = a.id;
            cc.value = value ?? "";
            cc.key = minVD.Key;
            updated.Add(cc);

            parent.MarkChanged();
         }
      }
   }
}