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
      private DataSet<int, Facing> dsFacing;
      private DataSet<int, InvFrg> dsInvFrg;
      private DataSet<int, InvEqu> dsInvEqu;
      private DataSet<int, Barcode> dsBarcode;

      Dictionary<DateTime, Facing> fdocs;
      ToolStripItem itCopy;

#if ClassicMonitor
      string filterBase;
#endif

      public FmDetailEx(FmDetailData detailData)
         : base(detailData)
      {
#if FOCUSED_GROUP
         FocusReport.FocusItemIsFolder = false;
#endif
#if ClassicMonitor
         filterBase = COMMON_FILTER_STR;
         tbnMessage.Visible = false;
         tsReportMenu.Visible = false;
         dgvDetail.ContextMenuStrip = null;
         toolStripSeparator2.Visible = false;
         toolStripSeparator3.Visible = false;
#else
         ContextMenuStrip menu = new ContextMenuStrip();

         menu.Items.Add("Создать задачу", null, new EventHandler(delegate(object sender, EventArgs arg)
            {
               OrgTaskInfo info = new OrgTaskInfo();
               OrderDetailRepresentation o = (OrderDetailRepresentation)dgvDetail.CurrentRow.DataBoundItem;
               info.id = o.NOrg.id;
               info.name = o.NOrg.name;
               Agent a = GetSelectedAgent();

               if (a != null)
                  FmAgentTaskList.ShowForm(info, dtpBegin.Value.Date, dtpEnd.Value.Date, a.id);
            }));

         itCopy = new ToolStripButton("Дублировать в БД", null, miMakeDup_Click);
         menu.Items.Add(itCopy);

         menu.Opening += (s, e) => { itCopy.Visible = (GetDupDataSet() != null); };

         //ToolStripMenuItem ti = new ToolStripMenuItem();
         //ti.Text = "Создать задачу";
         //ti.Size = new System.Drawing.Size(174, 26);
         //ti.Click += new EventHandler(delegate(object sender, EventArgs arg) {
         //   OrgTaskInfo info = new OrgTaskInfo();
         //   OrderDetailRepresentation o = (OrderDetailRepresentation)dgvDetail.CurrentRow.DataBoundItem;
         //   info.id = o.NOrg.id;
         //   info.name = o.NOrg.name;
         //   Agent a = GetSelectedAgent();

         //   if (a != null)
         //      FmAgentTaskList.ShowForm(info, dtpBegin.Value.Date, dtpEnd.Value.Date, a.id);
         //});

         //cmDgvDetail.Items.Add(ti);

         dgvDetail.ContextMenuStrip = menu;
#endif

         dsBarcode = (DataSet<int, Barcode>)DataModule.Get(Barcode.OBJECT_NAME) ?? new DataSet<int, Barcode>(Barcode.OBJECT_NAME);
         dsFacing = (DataSet<int, Facing>)DataModule.Get(Facing.OBJECT_NAME) ?? new DataSet<int, Facing>(Facing.OBJECT_NAME);
         dsInvFrg = (DataSet<int, InvFrg>)DataModule.Get(InvFrg.OBJECT_NAME) ?? new DataSet<int, InvFrg>(InvFrg.OBJECT_NAME);
         dsInvEqu = (DataSet<int, InvEqu>)DataModule.Get(InvEqu.OBJECT_NAME) ?? new DataSet<int, InvEqu>(InvEqu.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsFacing, ObjType.TObjType.Facing));
         documents.Add(new DocumentInfo(dsInvFrg, ObjType.TObjType.InvAudit));
         documents.Add(new DocumentInfo(dsInvEqu, ObjType.TObjType.ControlEquip));
         documents.Add(new DocumentInfo(dsBarcode, ObjType.TObjType.Barcode));

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Facing.OBJECT_NAME, "Фейсинг", typeof(FacingControl)));
         views.Add(new DocView(ObjType.TObjType.InvAudit.ToString(), "Инвентаризация", typeof(InvFrgControl)));
         views.Add(new DocView(ObjType.TObjType.ControlEquip.ToString(), "Контроль оборудования", typeof(InvEquControl)));
         views.Add(new DocView(ObjType.TObjType.Barcode.ToString(), "Штрихкод", typeof(BarcodeControl)));

         docViews = views.ToArray();

#if ClassicMonitor
         sbMode.Visible = false;
#endif
      }

#if ClassicMonitor
      public override bool IsScriptMode
      {
         get { return false; }
      }
#endif

      protected override void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
#if ClassicMonitor
         COMMON_FILTER_STR = "\"userid\"='{3}' and " + ((MainFormEx)MainForm.Instance).GetMonitorFilter(agentID);
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd.AddDays(1));
#else
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd);
#endif
      }

      protected override IDataSet GetDuplicate(Network.DataObject dataObject)
      {
         Facing fo = dataObject as Facing;
         if (fo != null)
         {
            SimpleDataSet<Facing> fs = new SimpleDataSet<Facing>(Facing.OBJECT_NAME, false);
            fs.Add(fo);
            return fs;
         }
         return base.GetDuplicate(dataObject);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

#if ClassicMonitor
         dsScriptDoc.Filter = String.Format(filterBase + " and " + ((MainFormEx)MainForm.Instance).ScriptFilter(), "created", dateBegin, dateEnd, agentID);
#endif
         dsFacing.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsInvFrg.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsBarcode.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

         updSets.Add(dsFacing);
         updSets.Add(dsInvFrg);
         updSets.Add(dsInvEqu);
         updSets.Add(dsBarcode);
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();

         fdocs = new Dictionary<DateTime, Facing>();
         foreach(Facing f in dsFacing.Data)
         {
            fdocs[f.created] = f;
         }
      }

      private Control FindDetailControl(DocView dv)
      {
         Control result = null;

         foreach (Control cc in scBottom.Panel1.Controls)
            if (cc.Name.Equals(dv.viewer.Name))
            {
               result = cc;
               break;
            }

         return result;
      }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         if (curRow == null)
            return;

         OrderDetailRepresentation odr = curRow.DataBoundItem as OrderDetailRepresentation;
         ScriptDoc sd = odr.StoreObject as ScriptDoc;
         if(sd != null && !sd.HaveFacing())
         {
            foreach(ScriptDocItem sdi in sd.items)
            {
               if(sdi.type == Visit.OBJECT_NAME)
               {
                  Visit v = sdi.Document as Visit;
                  if(v != null)
                  {
                     Facing f;
                     if(fdocs.TryGetValue(v.created, out f))
                     {
                        ScriptDocItem newItem = new ScriptDocItem();
                        newItem.date = f.created;
                        newItem.type = Facing.OBJECT_NAME;
                        newItem.state = ScriptDocItem.DOC_INITED;
                        newItem.pos = sd.items.Count;
                        newItem.Document = f;

                        sd.items.Add(newItem);
                        break;
                     }
                  }
               }
            }
         }

         base.UpdateDetailTable(curRow);
      }

      protected override void UpdateDetail(OrderDetailRepresentation odr)
      {
         Control c = RefreshDetail(odr);

         if (c != null)
            c.BringToFront();
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = null;

         DocView dv = GetDocView(Enum.GetName(typeof(ObjType.TObjType), odr.Doctype.Val));

         if (dv != null)
         {
            result = FindDetailControl(dv); ;

            if (result == null)
            {
               result = dv.MakeControl();
               detailPanel.Controls.Add(result);
               result.Dock = DockStyle.Fill;
            }

            if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }

         return result;
      }


#if ClassicMonitor

      internal override OrdersDetail CreateOrderDetail()
      {
         return new OrderDetailEx(documents);
      }
#endif
   }

#if ClassicMonitor
   class OrderDetailEx : OrdersDetail
   {
      public OrderDetailEx(List<DocumentInfo> documents)
         : base(documents)
      {
      }

      protected override bool NeedAddNotVisited(FmDetailData cond, bool checkRoute, List<Org> routes)
      {
         return false;
      }
   }
#endif
}