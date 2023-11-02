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
      private DataSet<int, InvEqu> dsInvEqu;

      public FmDetailEx(FmDetailData detailData)
         : base(detailData)
      {
         dsInvEqu = (DataSet<int, InvEqu>)DataModule.Get(InvEqu.OBJECT_NAME) ?? new DataSet<int, InvEqu>(InvEqu.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsInvEqu, ObjType.TObjType.ControlEquip));

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView("InvEqu", "Контроль оборудования", typeof(InvEquControl)));
         views.Add(new DocView(ObjType.TObjType.ControlEquip.ToString(), "Контроль оборудования", typeof(InvEquControl)));

         docViews = views.ToArray();
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsInvEqu.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

         updSets.Add(dsInvEqu);
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

      protected override void AfterRefreshData()
      {
          foreach (ScriptDoc sd in dsScriptDoc.Values)
          { 
              List<ScriptDocItem> addItems = new List<ScriptDocItem>();

              foreach (ScriptDocItem i in sd.items) 
              {
                  if (i.type.Equals(InvEqu.OBJECT_NAME))
                  {
                      InvEqu invEqu = i.Document as InvEqu;

                      if (invEqu != null)
                      {
                          foreach (Visit v in dsVisit.Values)
                          {
                              if (v.created == invEqu.visitDoc)
                              {
                                  ScriptDocItem item = new ScriptDocItem();
                                  item.date = v.created;
                                  item.type = Visit.OBJECT_NAME;
                                  item.state = ScriptDocItem.DOC_INITED;
                                  item.pos = sd.items.Count+addItems.Count;
                                  item.Document = v;

                                  addItems.Add(item);

                                  break;
                              }
                          }
                      }
                  }
              }

              sd.items.AddRange(addItems);
          }
      }
   }
}