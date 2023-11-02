using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.Collections;
using System.Drawing;
using System.IO;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      SimpleDataSet<Distrib> dsDistrib = null;
      SimpleDataSet<DMP> dsDMP = null;
      DataSet<string, DMPType> dsDMPType;

      Dictionary<DateTime, Visit> visits = new Dictionary<DateTime, Visit>();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();

         dsDistrib = (SimpleDataSet<Distrib>)DataModule.Get(Distrib.OBJECT_NAME) ?? new SimpleDataSet<Distrib>(Distrib.OBJECT_NAME);
         dsDMP = (SimpleDataSet<DMP>)DataModule.Get(DMP.OBJECT_NAME) ?? new SimpleDataSet<DMP>(DMP.OBJECT_NAME);
         dsDMPType = (DataSet<string, DMPType>)DataModule.Get(DMPType.OBJECT_NAME) ?? new DataSet<string, DMPType>(DMPType.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsDistrib, ObjType.TObjType.OrgDistrib));
         documents.Add(new DocumentInfo(dsDMP, ObjType.TObjType.DMP));

         List<DocView> docs = new List<DocView>(docViews);
         ObjType ot = new ObjType(ObjType.TObjType.OrgDistrib);
         docs.Add(new DocView(Distrib.OBJECT_NAME, ot.ToString(), typeof(FmDistribView)));
         ot = new ObjType(ObjType.TObjType.DMP);
         docs.Add(new DocView(DMP.OBJECT_NAME, ot.ToString(), typeof(FmDMPView)));

         docViews = docs.ToArray();
      }

      void Init()
      {
///         detailPanel.Controls.Add(dgvDetailItems);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsDistrib.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsDMP.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

         updSets.Add(dsDistrib);
         updSets.Add(dsDMP);
         updSets.Add(dsDMPType);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
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

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();
         visits.Clear();

         foreach (Visit v in dsVisit.Data)
            visits[v.created] = v;
      }

      internal override void SetControlData(DataObjectViewer control, Network.DataObject doc)
      {
         if (control is FmDistribView)
            ((FmDistribView)control).SetData(doc, visits);
         else if (control is FmDMPView)
            ((FmDMPView)control).SetData(doc, visits);
         else
            base.SetControlData(control, doc);
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

            if (result is FmDistribView)
               ((FmDistribView)result).SetData(odr.StoreObject, visits);
            else if (result is FmDMPView)
               ((FmDMPView)result).SetData(odr.StoreObject, visits);
            else if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }

         return result;
      }

   }
}
