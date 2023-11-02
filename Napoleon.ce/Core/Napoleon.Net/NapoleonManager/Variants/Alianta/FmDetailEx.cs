using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      private SimpleDataSet<AliantaOffer> dsOffer;
      private OfferOverview ofrView;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsOffer = (SimpleDataSet<AliantaOffer>)DataModule.Get(AliantaOffer.OBJECT_NAME) ??
            new SimpleDataSet<AliantaOffer>(AliantaOffer.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsOffer, ObjType.TObjType.Offer));
         ofrView = new OfferOverview();
         ofrView.Dock = System.Windows.Forms.DockStyle.Fill;
         detailPanel.Controls.Add(ofrView);
      }


      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsOffer.Filter = filter;

         updSets.Add(dsOffer);
      }

      internal override System.Windows.Forms.Control RefreshDetail(GRSoft.NapoleonManager.OrderDetailRepresentation odr)
      {
         if (odr.Doctype.Val == ObjType.TObjType.Offer)
         {
            AliantaOffer p = odr.StoreObject as AliantaOffer;

            if (p != null)
            {
               ofrView.SetData(p);
            }

            return ofrView;
         }
         else return null;
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if(docType == AliantaOffer.OBJECT_NAME)
         {
            return new DocView(AliantaOffer.OBJECT_NAME, "Ком.предложение", typeof(OfferOverview));
         }
         return base.GetDocView(docType);
      }
   }

   
}
