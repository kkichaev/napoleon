using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Globalization;
using System.Collections;
using System.IO;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
#if MerchUnionMonitor
      string filterBase;
#endif

      public FmDetailEx(FmDetailData detailData)
         : base(detailData)
      {
#if MerchUnionMonitor
         filterBase = COMMON_FILTER_STR;
         tbnMessage.Visible = false;
         tsReportMenu.Visible = false;
         dgvDetail.ContextMenuStrip = null;
         toolStripSeparator2.Visible = false;
         toolStripSeparator3.Visible = false;
         dgvDetailScriptName.Visible = false;
         tslFilter.Margin = new Padding(tslFilter.Margin.Left, tslFilter.Margin.Top, tslFilter.Margin.Right + 50, tslFilter.Margin.Bottom);
#else

         ContextMenuStrip menu = new ContextMenuStrip();
         menu.Items.Add("Создать на основании", null, new EventHandler(delegate(object sender, EventArgs arg)
         {
            OrgTaskInfo info = new OrgTaskInfo();
            OrderDetailRepresentation o = (OrderDetailRepresentation)dgvDetail.CurrentRow.DataBoundItem;
            ScriptDoc script = o.StoreObject as ScriptDoc;

            if (script != null)
            {
               FmCreateScript form = new FmCreateScript(script);
               form.Show();
            }
         }));

         menu.Items.Add("Удалить документ", null, new EventHandler(delegate(object sender, EventArgs arg)
         {
            if (MessageBox.Show(this, "Вы уверены?", "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
            {
               OrderDetailRepresentation o = (OrderDetailRepresentation)dgvDetail.CurrentRow.DataBoundItem;
               ScriptDoc script = o.StoreObject as ScriptDoc;

               if (script != null)
               {
                  DataSet<int, ScriptDoc> src = new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME, false);
                  {
                     src.Add(src.Count, script);

                     List<IDataSet> update = new List<IDataSet>();
                     update.Add(src);

                     foreach (ScriptDocItem si in script.items)
                     {
                        if (si.Document != null)
                        {
                           DataSet<int, BaseDocument> ds = new DataSet<int, BaseDocument>(si.type, false);
                           ds.Add(ds.Count, si.Document);

                           update.Add(ds);
                        }
                     }

                     Config cfg = Config.GetConfig();

                     if (DataModule.UpdateDataSet(null, update, null, cfg.GetConnection(), GetSelectedIdAgent()))
                     {
                        btnRefresh.PerformClick();
                        MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
                           MessageBoxIcon.Information);
                     }
                     else
                        MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                           MessageBoxIcon.Error);
                  }
               }
            }
         }));

         dgvDetail.ContextMenuStrip = menu;

#endif

#if MerchUnionMonitor
         //sbMode.Visible = false;
         btnRoute.Visible = false;
         cbFilter.Width = 150;
#endif
      }

#if MerchUnionMonitor
      //public override void SetScriptMode(bool scriptMode)
      //{
      //   base.SetScriptMode(true);
      //}

      //public override bool IsScriptMode
      //{
      //   get { return true; }
      //}
#endif

      protected override void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
#if MerchUnionMonitor
         COMMON_FILTER_STR = "\"userid\"='{3}' and " + ((MainFormEx)MainForm.Instance).GetMonitorFilter(agentID);
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd.AddDays(1));
#else
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd);
#endif
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

#if MerchUnionMonitor 
         dsScriptDoc.Filter = String.Format(filterBase + " and " + ((MainFormEx)MainForm.Instance).ScriptFilter(), "created", dateBegin, dateEnd, agentID);
#endif
      }


#if MerchUnionMonitor

      internal override OrdersDetail CreateOrderDetail()
      {
         return new OrderDetailEx(documents);
      }
#endif
   }

#if MerchUnionMonitor
   class OrderDetailEx : ScriptDetail
   {
      public OrderDetailEx(List<DocumentInfo> documents)
         : base(documents)
      {
      }

      protected override bool NeedAddNotVisited(FmDetailData cond, bool checkRoute, List<Org> routes)
      {
         return false;
      }

      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
         ((FmDetail)cond.fmDetail).dgvDetailScriptName.Visible = false;
      }
   }
#endif
}