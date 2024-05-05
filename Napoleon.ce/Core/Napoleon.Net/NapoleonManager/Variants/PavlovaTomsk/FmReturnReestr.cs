using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReturnReestr : Form
   {
      private SimpleDataSet<Returns> dsReturn = new SimpleDataSet<Returns>(Returns.OBJECT_NAME, false, true);
      private SimpleDataSet<Order> dsReqOrder = new SimpleDataSet<Order>("ReqOrder", false, true);

      public string COMMON_FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" <= ToDate('{1:dd/MM/yyyy} 23:59:59') and \"userid\"='{2}'";
      public string COMMON_FILTER_STR_ALL = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" <= ToDate('{1:dd/MM/yyyy} 23:59:59')";

      protected DataSet<string, Price> dsPrice;
      protected DataSet<string, Org> dsOrg;
      protected Dictionary<BaseDocument, Decision> decisionsChange = new Dictionary<BaseDocument, Decision>();
      Dictionary<string, Decision> decisionsMap = new Dictionary<string, Decision>();

      private SimpleDataSet<Decision> dsDecision = new SimpleDataSet<Decision>(Decision.OBJECT_NAME, false);

      public FmReturnReestr()
      {
         InitializeComponent();

         dgvMaster.AutoGenerateColumns = false;
         dgvDetail.AutoGenerateColumns = false;
         dgvOrderItems.AutoGenerateColumns = false;

         dtpStart.Value = DateTime.Now.Date;
         dtpFinish.Value = DateTime.Now.Date;

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = false;
         List<IDataSet> upd = new List<IDataSet>();

         Agent a = cbAgent.SelectedItem as Agent;

         if (a != null)
         {
            string rf = a.id.Length == 0 ?
               string.Format(COMMON_FILTER_STR_ALL, dtpStart.Value.Date, dtpFinish.Value.Date)
               : string.Format(COMMON_FILTER_STR, dtpStart.Value.Date, dtpFinish.Value.Date, a.id);

            dsReturn.Filter = rf;
            dsReqOrder.Filter = rf;
            dsDecision.Filter = rf;

            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;

            if (a.id.Length != 0)
               dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
            else
               dsOrg = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);

            if (dsPrice.Count == 0)
            {
               upd.Add(dsPrice);
            }

            upd.Add(dsOrg);
            upd.Add(dsReturn);
            upd.Add(dsReqOrder);
            upd.Add(dsDecision);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
         }
      }

      void LoadDocs(IList<DecisionData> list, ICollection docs)
      {
         foreach (BaseDocument  r in docs)
         {
            DecisionData d = new DecisionData(this, r);
            d.doc = r;

            if (decisionsMap.ContainsKey(r.created + r.userid))
            {
               d.decision = decisionsMap[r.created + r.userid].value;
               d.remark = decisionsMap[r.created + r.userid].remark;
            }

            list.Add(d);
         }
      }

      private void DoLoadData()
      {
         decisionsChange.Clear();
         decisionsMap.Clear();

         SortableBindingList<DecisionData> list = new SortableBindingList<DecisionData>();

         foreach (Decision d in dsDecision.Values)
            decisionsMap[d.created.ToString() + d.userid] = d;

         LoadDocs(list, dsReturn.Values);
         LoadDocs(list, dsReqOrder.Values);

         dgvMaster.DataSource = list;
      }

      private void FmReturnReestr_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            List<Agent> al = new List<Agent>();
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               al.Add(da.agent);
            }

            al.Sort();
            al.ForEach(x => cbAgent.Items.Add(x));
         }

         Agent all = new Agent();
         all.name = "<Все>";
         cbAgent.Items.Insert(0, all);

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;

         List<DecisionView> list = new List<DecisionView>();
         list.Add(new DecisionView("", 0));
         list.Add(new DecisionView("Подтвержден", 1));
         list.Add(new DecisionView("Не подтвержден", 2));

         clmnDecision.DataSource = list;
         clmnDecision.DisplayMember = "Title";
         clmnDecision.ValueMember = "Code";
      }

      private void dgvMaster_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         DecisionData r = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as DecisionData;

         Returns rd = r.doc as Returns;
         if (rd != null)
         {
            SortableBindingList<ReturnItem> data = new SortableBindingList<ReturnItem>(rd.items);
            dgvDetail.DataSource = data;
            dgvDetail.BringToFront();
         }
         else
         {
            SortableBindingList<OrderItem> data = new SortableBindingList<OrderItem>(((Order)r.doc).items);
            dgvOrderItems.DataSource = data;
            dgvOrderItems.BringToFront();
         }

      }

      public Decision FindDecision(BaseDocument doc)
      {
         if (!decisionsChange.ContainsKey(doc))
         {
            if (decisionsMap.ContainsKey(doc.created + doc.userid))
               decisionsChange[doc] = decisionsMap[doc.created + doc.userid];
            else
            {
               Decision d = new Decision();
               d.created = doc.created;
               d.userid = doc.userid;
               d.manager = ((Manager)CurrentUser.user).User.login;
               d.doctype = doc is Order ? Decision.ORD_DOC : Decision.RET_DOC;

               decisionsChange[doc] = d;
            }
         }

         return decisionsChange[doc];
      }

      internal void SetDecision(BaseDocument doc, int value)
      {
         Decision d = FindDecision(doc);
         d.value = value;
         btnSave.Enabled = true;         
      }

      internal void SetRemark(BaseDocument doc, string value)
      {
         Decision d = FindDecision(doc);
         d.remark = value;
         btnSave.Enabled = true;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SaveChanges(true);
      }

      private void dgvMaster_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         ((DataGridView)sender).CommitEdit(DataGridViewDataErrorContexts.Commit);
         ((DataGridView)sender).InvalidateRow(((DataGridView)sender).CurrentRow.Index);

      }

      private void FmReturnReestr_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && MessageBox.Show("Сохранить изменения?",
            "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.OK)
            SaveChanges(false);
      }

      class WrData
      {
         public SimpleDataSet<Decision> dcs = new SimpleDataSet<Decision>(Decision.OBJECT_NAME, false, true);
         public SimpleDataSet<Order> ord = new SimpleDataSet<Order>(Order.OBJECT_NAME, false, true);
      }

      private void SaveChanges(bool showDialog)
      {
         dgvMaster.CommitEdit(DataGridViewDataErrorContexts.Commit);

         Dictionary<string, WrData> userdc = new Dictionary<string, WrData>();

         foreach (KeyValuePair<BaseDocument, Decision> entry in decisionsChange)
         {
            Decision d = entry.Value;
            d.dodate = DateTime.Now;

            WrData data;
            if(!userdc.TryGetValue(d.userid, out data))
            {
               data = new WrData();
               userdc[d.userid] = data;
            }

            data.dcs.Add(d);
            if(d.value == Decision.APPROVE && entry.Key is Order)
            {
               data.ord.Add(entry.Key as Order);
            }
         }

         bool res = true;

         foreach (KeyValuePair<string, WrData> entry in userdc)
         {
            List<IDataSet> wrSet = new List<IDataSet>();

            if (entry.Value.dcs.Count > 0)
               wrSet.Add(entry.Value.dcs);
            if(entry.Value.ord.Count > 0)
               wrSet.Add(entry.Value.ord);

            res = DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection(), entry.Key);

            if (!res)
               break;
         }

         if (showDialog && res)
         {
            btnSave.Enabled = false;
            DialogUtil.SavedGood(this);
            decisionsChange.Clear();
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         if (dgvMaster.CurrentRow != null)
         {
            DecisionData data = dgvMaster.CurrentRow.DataBoundItem as DecisionData;

            if (data != null && data.doc != null)
            {
               Param param = new Param();
               param.created = data.doc.created;
               param.userid = data.doc.userid;
               ReportResult.DoReport("return_akt", param, this);
            }
         }
      }

      
   }

   class Param : GRSoft.Network.DataObject
   {
      public DateTime created = DateTime.MinValue;
      public string userid = string.Empty;
   }

   class DecisionData
   {
      public BaseDocument doc;
      FmReturnReestr control;
      public int decision = 0;
      public string remark = string.Empty;

      public DecisionData(FmReturnReestr control, BaseDocument doc)
      {
         this.control = control;
      }

      public String OrgName { get { return doc.OrgName; } }
      public String OrgAddress { get { return doc.Address; } }
      public DateTime Created { get { return doc.created; } }
      public double Sum { get { return doc.Sum(); } }
      public int Decision 
      { 
         get 
         {
            return decision;
         }

         set
         {
            decision = value;
            control.SetDecision(doc, value);
         }
      }

      public string DocType { get { return (doc is Order) ? "заказ" : "возврат"; } }

      public String Agent { get { return doc.AgentName; } }
      public String AgentRemark { get { return doc.Remark; } }
      public String ManagerRemark
      {
         get
         {
            return remark;
         }

         set
         {
            remark = value;
            control.SetRemark(doc, value);
         }
      }
   }

   class DecisionView
   {
      public string Title { get; set; }
      public int Code { get; set; }

      public DecisionView(string title, int code)
      {
         Title = title;
         Code = code;
      }
   }
}
