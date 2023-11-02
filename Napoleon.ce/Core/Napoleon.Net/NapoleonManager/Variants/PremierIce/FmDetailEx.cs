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
        SimpleDataSet<InvFrg> dsInvFrg;
        SimpleDataSet<InvEqu> dsInvEqu;
        SimpleDataSet<DebetWork> dsDebetWork;


        public FmDetailEx(FmDetailData detailData)
         : base(detailData)
        {

            dsInvFrg = (SimpleDataSet<InvFrg>)DataModule.Get(InvFrg.OBJECT_NAME) ?? new SimpleDataSet<InvFrg>(InvFrg.OBJECT_NAME);
            dsInvEqu = (SimpleDataSet<InvEqu>)DataModule.Get(InvEqu.OBJECT_NAME) ?? new SimpleDataSet<InvEqu>(InvEqu.OBJECT_NAME);


            dsDebetWork = (SimpleDataSet<DebetWork>)DataModule.Get(DebetWork.OBJECT_NAME) ?? new SimpleDataSet<DebetWork>(DebetWork.OBJECT_NAME);

            documents.Add(new DocumentInfo(dsInvFrg, ObjType.TObjType.InvAudit));
            documents.Add(new DocumentInfo(dsInvEqu, ObjType.TObjType.ControlEquip));
            documents.Add(new DocumentInfo(dsDebetWork, ObjType.TObjType.DebetWork));

            List<DocView> views = new List<DocView>(docViews);
            views.Add(new DocView(ObjType.TObjType.InvAudit.ToString(), "Инвентаризация", typeof(InvFrgControl)));
            views.Add(new DocView(ObjType.TObjType.ControlEquip.ToString(), "Контроль оборудования", typeof(InvEquControl)));
            views.Add(new DocView(ObjType.TObjType.DebetWork.ToString(), "Работа с ПДЗ", typeof(DebetWorkView)));

            docViews = views.ToArray();

        }


        protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
        {
            base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

            dsInvFrg.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
            updSets.Add(dsInvFrg);

            dsInvEqu.Filter = dsInvFrg.Filter;
            updSets.Add(dsInvEqu);

            dsDebetWork.Filter = dsInvFrg.Filter;
            updSets.Add(dsDebetWork);
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

            if(result == null)
            {
                foreach (Control cc in detailPanel.Controls)
                    if (cc.Name.Equals(dv.viewer.Name))
                    {
                        result = cc;
                        break;
                    }
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


    }

    public class InvFrg : BaseDocument
    {
        public static readonly string OBJECT_NAME = "InvFrg";

        [ItemType(typeof(InvFrgItem))]
        public List<InvFrgItem> items = new List<InvFrgItem>();
    }

    public class DebetWork : BaseDocument
    {
        public static readonly string OBJECT_NAME = "DebetWork";
    }


    public class InvFrgItem : GRSoft.Network.DataObject
    {
        public string id = string.Empty;
        public string inputNumber = string.Empty;
        public string number = string.Empty;
        public string name = string.Empty;

        public string Item { get { return name; } }
        public string InputNumber { get { return inputNumber; } }
        public string Number { get { return number; } }
    }

    public class InvEqu : BaseDocument
    {
        public static readonly string OBJECT_NAME = "AuditEquip";

        [ItemType(typeof(InvEquItem))]
        public List<InvEquItem> items = new List<InvEquItem>();
    }

    public class InvEquItem : GRSoft.Network.DataObject
    {
        public string id = string.Empty;
        public string inputNumber = string.Empty;
        public string number = string.Empty;
        public string name = string.Empty;
        public int exists = 0;

        public string Item { get { return id; } }
        public string Number { get { return number; } }
        public bool Exists { get { return exists > 0; } }
    }
}