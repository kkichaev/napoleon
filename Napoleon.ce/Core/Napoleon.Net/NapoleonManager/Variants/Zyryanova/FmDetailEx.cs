using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
    public class FmDetailEx : FmDetail
    {
        protected SimpleDataSet<MerchDoc> dsMonitoring = null;
        MerchDetail merchView = new MerchDetail();

        public FmDetailEx(FmDetailData data)
          : base(data)
        {
            detailPanel.Controls.Add(merchView);
            merchView.Dock = DockStyle.Fill;
            merchView.Visible = true;

            dsMonitoring = (SimpleDataSet<MerchDoc>)DataModule.Get(MerchDoc.OBJECT_NAME) ?? new SimpleDataSet<MerchDoc>(MerchDoc.OBJECT_NAME);

            List<DocView> docs = new List<DocView>(docViews);
            ObjType ot = new ObjType(ObjType.TObjType.Monitoring);
            docs.Add(new DocView(MerchDoc.OBJECT_NAME, ot.ToString(), typeof(MerchDetail)));
            ot = new ObjType(ObjType.TObjType.Merch);

            docViews = docs.ToArray();

            documents.Add(new DocumentInfo(dsMonitoring, ObjType.TObjType.Monitoring));
        }

        internal override Control RefreshDetail(OrderDetailRepresentation odr)
        {

            if (odr.Doctype.Val == ObjType.TObjType.Monitoring)
            {
                merchView.SetData(odr.StoreObject);
                return merchView;
            }
            return base.RefreshDetail(odr);
        }

        protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
        {
            base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
            dsMonitoring.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
            updSets.Add(dsMonitoring);
        }

        protected override void AfterRefreshData()
        {
            base.AfterRefreshData();
        }

    }
}
