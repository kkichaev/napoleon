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
  public  class FmDetailEx : FmDetail
   {
      SimpleDataSet<Distrib> dsDistrib = null;
      SimpleDataSet<BankIncass> dsBankIncass = new SimpleDataSet<BankIncass>(BankIncass.OBJECT_NAME);

#if EUROASIA_MONTOR
      string filterBase;
#endif

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
#if EUROASIA_MONTOR
         filterBase = COMMON_FILTER_STR;
         tbnMessage.Visible = false;
         tsReportMenu.Visible = false;
         dgvDetail.ContextMenuStrip = null;
         toolStripSeparator2.Visible = false;
         toolStripSeparator3.Visible = false;
#else
         documents.Add(new DocumentInfo(dsBankIncass, ObjType.TObjType.BankIncass));
#endif
         dsDistrib = (SimpleDataSet<Distrib>)DataModule.Get(Distrib.OBJECT_NAME) ?? new SimpleDataSet<Distrib>(Distrib.OBJECT_NAME);

         List<DocView> docs = new List<DocView>(docViews);
         ObjType ot = new ObjType(ObjType.TObjType.OrgDistrib);
         docs.Add(new DocView(Distrib.OBJECT_NAME, ot.ToString(), typeof(FmDistribView)));
         docViews = docs.ToArray();

         documents.Add(new DocumentInfo(dsDistrib, ObjType.TObjType.OrgDistrib));
      
#if EUROASIA_MONTOR
         sbMode.Visible = false;
#endif
      }


#if EUROASIA_MONTOR
      public override bool IsScriptMode
      {
         get { return false; }
      }
#endif

      protected override void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
#if EUROASIA_MONTOR
         COMMON_FILTER_STR = "\"userid\"='{3}' and " + ((MainFormEx)MainForm.Instance).GetMonitorFilter(agentID);
#endif
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd.AddDays(1));
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

#if EUROASIA_MONTOR
         dsScriptDoc.Filter = String.Format(filterBase + " and " + ((MainFormEx)MainForm.Instance).ScriptFilter(), "created", dateBegin, dateEnd, agentID);
#else
         dsBankIncass.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsBankIncass);
#endif

         dsDistrib.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

         updSets.Add(dsDistrib);
      }

#if EUROASIA_MONTOR
      protected override void AfterRefreshData()
      {
         ((MainFormEx)MainForm.Instance).PrepareDocs();
         base.AfterRefreshData();
      }
#endif

      //protected override void ShowCorrespondingPhoto(DateTime date, OrderDetailRepresentation o)
      //{
      //   BankIncass bi = o.StoreObject as BankIncass;
      //   if (bi != null)
      //   {
      //      byte[] photo = bi.photo;
      //      if (photo != null && photo.Length > 0)
      //      {
      //         lvPhoto.Clear();
      //         imPhoto.Images.Clear();

      //         List<Image> nativePicture = new List<Image>();

      //         MemoryStream stream = new MemoryStream(photo);
      //         Image image = new Bitmap(stream);

      //         nativePicture.Add(image);
      //         imPhoto.Images.Add(image);
      //         stream.Close();

      //         String tag = "1";
      //         ListViewItem lvi = lvPhoto.Items.Add(tag);
      //         lvi.ImageIndex = 0;
      //         imPhoto.Tag = nativePicture;

      //         return;
      //      }
      //   }
      //   base.ShowCorrespondingPhoto(date, o);
      //}

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control res = base.RefreshDetail(odr); 

         if (res == null)
         {
            BankIncass dd = odr.StoreObject as BankIncass;

            if( dd != null )
            {
               StringBuilder str = new StringBuilder("Дата\t" + dd.date.ToShortDateString());
               str.Append("\r\nСумма\t");
               str.Append(dd.Sum().ToString("C", Config.GetCultureInfo()));
               tbVisitText.Text = str.ToString();
               res = tbVisitText;
            }
         }

         return res;
      }

      internal override OrdersDetail CreateOrderDetail()
      {
#if EUROASIA_MONTOR
         return new OrderDetailEx(documents);
#else
         return new ScriptDetail(documents);
#endif
      }

      public override bool LoadIntDocument(BaseDocument doc)
      {
         if (doc is Visit && doc.id.Length == 0)
            return false;

         return base.LoadIntDocument(doc);
      }

      public override bool IsVisitForDoc(DateTime date, OrderDetailRepresentation rep, Visit vis)
      {
         if (rep.StoreObject is BankIncass)
            return ((BankIncass)rep.StoreObject).visitDoc.Equals(vis.created);
         else
            return base.IsVisitForDoc(date, rep, vis);
      }
   }

   

#if EUROASIA_MONTOR
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
