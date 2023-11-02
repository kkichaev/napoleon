using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.Collections;
using System.Drawing;
using System.IO;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      SimpleDataSet<Storcheck> dsDocs = new SimpleDataSet<Storcheck>(Storcheck.OBJECT_NAME);
      DocView ssView;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         documents.Add(new DocumentInfo(dsDocs, ObjType.TObjType.Storcheck));
         ssView = new DocView(Storcheck.OBJECT_NAME, "Сторчек", typeof(StorcheckView)) ;
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsDocs.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsDocs);
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType == Storcheck.OBJECT_NAME)
            return ssView;
         return base.GetDocView(docType);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         foreach (DocumentInfo di in documents)
         {
            if (di.Type == odr.Doctype.Val)
            {
               if (di.Type == ObjType.TObjType.Storcheck)
               {
                  tbVisitText.Text = "Сторчек";
                  return tbVisitText;
               }
            }
         }

         return null;
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }
   }

   public class StorcheckView : UserControl, DataObjectViewer
   {
      public StorcheckView()
      {

      }

      public void SetData(Network.DataObject dataObject)
      {
      }
   }
}
