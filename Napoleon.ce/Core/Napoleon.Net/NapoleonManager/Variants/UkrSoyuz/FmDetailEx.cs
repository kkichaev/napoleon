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
        protected SimpleDataSet<ExchDoc> dsExch = null;
        FmExchangeView exchView = new FmExchangeView();

        public FmDetailEx(FmDetailData data)
          : base(data)
        {
            detailPanel.Controls.Add(exchView);
            exchView.Dock = DockStyle.Fill;
            exchView.Visible = true;

            dsExch = (SimpleDataSet<ExchDoc>)DataModule.Get(ExchDoc.OBJECT_NAME) ?? new SimpleDataSet<ExchDoc>(ExchDoc.OBJECT_NAME);

            List<DocView> docs = new List<DocView>(docViews);
            ObjType ot = new ObjType(ObjType.TObjType.Monitoring);
            docs.Add(new DocView(Monitoring.OBJECT_NAME, ot.ToString(), typeof(FmExchangeView)));
            ot = new ObjType(ObjType.TObjType.Merch);

            docViews = docs.ToArray();

            documents.Add(new DocumentInfo(dsExch, ObjType.TObjType.Exchange));
        }

        internal override Control RefreshDetail(OrderDetailRepresentation odr)
        {

            if (odr.Doctype.Val == ObjType.TObjType.Exchange)
            {
                exchView.SetData(odr.StoreObject);
                return exchView;
            }
            return base.RefreshDetail(odr);
        }

        protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
        {
            base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
            dsExch.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
            updSets.Add(dsExch);
        }

        protected override void AfterRefreshData()
        {
            base.AfterRefreshData();
        }

        protected override IDataSet GetDuplicate(Network.DataObject dataObject)
        {
            ExchDoc src = dataObject as ExchDoc;
            if (src == null)
                return null;

            SimpleDataSet<ExchDoc> dest = new SimpleDataSet<ExchDoc>(ExchDoc.OBJECT_NAME, false);
            dest.Add(src);
            return dest;
        }

    }
}
