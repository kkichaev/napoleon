using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
    [System.Runtime.InteropServices.ComVisibleAttribute(true)]
    public
     class FmDetailEx : FmDetail
    {
        private DataSet<DateTime, GPSPos> dsGPSPos;
        //ReturnOverviewEx retCtrl;
        private DataSet<int, InvFrg> dsInvFrg;
        SimpleDataSet<PlanogramEdit> dsPlanograms;
        public static DataSet<string, Planograms> planograms = new DataSet<string, Planograms>(Planograms.OBJECT_NAME, false);

        PlanogramControl planogramView;


        public FmDetailEx(FmDetailData data)
           : base(data)
        {
            ToolStripMenuItem wtReport = new ToolStripMenuItem("Отчет о посещениях GPS", Resources.network_satellite, wtReport_Click);
            tsReportMenu.DropDownItems.Add(wtReport);

            dsGPSPos = DataModule.Get("GPSPos") == null ? new DataSet<DateTime, GPSPos>("GPSPos") : (DataSet<DateTime, GPSPos>)DataModule.Get("GPSPos");
            dsInvFrg = (DataSet<int, InvFrg>)DataModule.Get(InvFrg.OBJECT_NAME) ?? new DataSet<int, InvFrg>(InvFrg.OBJECT_NAME);
            dsPlanograms = (SimpleDataSet<PlanogramEdit>)DataModule.Get(PlanogramEdit.OBJECT_NAME) ?? new SimpleDataSet<PlanogramEdit>(PlanogramEdit.OBJECT_NAME);

            DataGridViewTextBoxColumn ProdDate = new DataGridViewTextBoxColumn();
            DataGridViewTextBoxColumn ExpDate = new DataGridViewTextBoxColumn();
            ExpDate.DataPropertyName = "ExpDate";
            ExpDate.HeaderText = "Срок годности";
            ExpDate.Name = "ExpDate";

            ProdDate.DataPropertyName = "ProdDate";
            ProdDate.HeaderText = "Дата производства";
            ProdDate.Name = "ProdDate";

            dgvReturns.AutoGenerateColumns = false;
            dgvReturns.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] { ProdDate, ExpDate });

            planogramView = new PlanogramControl();
            detailPanel.Controls.Add(planogramView);
            planogramView.Dock = DockStyle.Fill;
            planogramView.Visible = true;

            foreach (DocView dv in docViews)
            {
                if (dv.docType == Returns.OBJECT_NAME)
                {
                    dv.viewer = typeof(ReturnOverviewEx);
                    break;
                }
            }

            documents.Add(new DocumentInfo(dsInvFrg, ObjType.TObjType.InvAudit));
            documents.Add(new DocumentInfo(dsPlanograms, ObjType.TObjType.Planogram));

            List<DocView> views = new List<DocView>(docViews);
            views.Add(new DocView(ObjType.TObjType.InvAudit.ToString(), "Инвентаризация", typeof(InvFrgControl)));
            views.Add(new DocView(PlanogramEdit.OBJECT_NAME, "Планограмма", typeof(PlanogramControl)));
            docViews = views.ToArray();
        }

        private void wtReport_Click(object sender, EventArgs e)
        {
            FmVisitReportParams form = new FmVisitReportParams();

            if (form.ShowDialog() == DialogResult.OK)
            {
                SortOrderDetail(SortOrder.Descending, "DateCreated", 5);

                HtmlReportEx r = new HtmlReportEx();
                HtmlReportEx.prec = form.Prec;
                HtmlReportEx.sort = form.SortType;

                OpenLink.NewWindow(String.Format("\"{0}\"", r.makeDetailsFileInfo(dgvDetail,
                   new TimeInterval(dtpBegin.Value, dtpEnd.Value), (cbAgents.Items[cbAgents.SelectedIndex] as Agent))));
            }
        }

        protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
        {
            base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
            dsGPSPos.Filter = string.Format(COMMON_FILTER_STR + " and \"isGSM\" = '0'", "date", dateBegin, dateEnd, agentID);
            dsInvFrg.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
            dsPlanograms.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

            updSets.Add(dsGPSPos);
            updSets.Add(dsInvFrg);
            updSets.Add(dsPlanograms);
            updSets.Add(planograms);
        }

        internal override Control RefreshDetail(OrderDetailRepresentation odr)
        {
            if(odr.Doctype.Val == ObjType.TObjType.Planogram)
            {
                planogramView.SetData(odr.StoreObject);
                return planogramView;
            }
            return base.RefreshDetail(odr);
        }
    }
}
