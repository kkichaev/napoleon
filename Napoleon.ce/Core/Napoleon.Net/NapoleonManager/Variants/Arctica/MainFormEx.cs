using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      SimpleDataSet<PhoneCall> dsPhones = new SimpleDataSet<PhoneCall>(PhoneCall.OBJECT_NAME);


      public MainFormEx()
      {

         DataGridViewTextBoxColumn dgvCalls = new DataGridViewTextBoxColumn();
         dgvCalls.FillWeight = 55.3934F;
         dgvCalls.HeaderText = "Звонков";
         dgvCalls.Name = "dgvCalls";
         dgvCalls.DataPropertyName = "Calls";

         DataGridViewTextBoxColumn dgvOrdCall = new DataGridViewTextBoxColumn();
         dgvOrdCall.FillWeight = 55.3934F;
         dgvOrdCall.HeaderText = "Заявок по звонку";
         dgvOrdCall.Name = "dgvOrdCall";
         dgvOrdCall.DataPropertyName = "ByPhone";

         DataGridViewTextBoxColumn dgvCallSum = new DataGridViewTextBoxColumn();
         dgvCallSum.FillWeight = 55.3934F;
         dgvCallSum.HeaderText = "Сумма по звонку";
         dgvCallSum.Name = "dgvCallSum";
         dgvCallSum.DefaultCellStyle.Format = "C";
         dgvCallSum.DataPropertyName = "PhoneSum";

         int idx = tgvAgentsSummarySum.DisplayIndex + 1;
         tgvAgentsSummary.Columns.Insert(idx++, dgvCalls);
         tgvAgentsSummary.Columns.Insert(idx++, dgvOrdCall);
         tgvAgentsSummary.Columns.Insert(idx++, dgvCallSum);

         //ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         //button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //button.Image = Properties.Resources.audit_frg;
         //button.ImageTransparentColor = System.Drawing.Color.Magenta;
         //button.Name = "mecrh";
         //button.Size = new System.Drawing.Size(23, 22);
         //button.Text = "Oтчёт по работе";
         //button.Click += new System.EventHandler((s, e) => {
         //   HtmlReportEx rep = new HtmlReportEx();
         //   OpenLink.NewWindow(String.Format("\"{0}\"", 
         //      rep.makeAgentSummaryFileInfo(tgvAgentsSummary, new TimeInterval(GetStartDate(), GetFinishDate()))));
         //});

         //tsbConfig.Items.Add(button);

      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         String crdFilter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd); ;
         dsPhones.Filter = crdFilter;
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);

         updSets.Add(dsPhones);
      }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig) { }

      protected override SummaryDivisionData CreateSummaryDivisionData(Division d)
      {
         return new SummaryDivisionDataEx(d);
      }

      protected override SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryDataEx(agent, config);
      }

      protected override void PostAddData()
      {
         IDataSet cdata = DataModule.Get(PhoneCall.OBJECT_NAME);
         if (cdata != null)
            foreach (PhoneCall i in cdata.Data)
               this.Add(i);
      }

      internal virtual void Add(PhoneCall i)
      {
         if (i.agent != null && ContainsKey(i.AgentID))
         {
            SummaryDataEx sd = (SummaryDataEx)this[i.AgentID];
            sd.Add(i);
         }
      }
   }

   class SummaryDataEx : SummaryData
   {
      public Dictionary<string, bool> calls = new Dictionary<string, bool>();
      public Dictionary<string, bool> ordByCalls = new Dictionary<string, bool>();
      public Dictionary<string, bool> orderCnt = new Dictionary<string, bool>();

      public double phoneSum = 0;

      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config) : base(a, config) { }

      public int ByPhone { get { return ordByCalls.Count; } }
      public int Calls { get { return calls.Count; } }
      public double PhoneSum { get { return phoneSum; } }

      public override void Add(Order o)
      {
         if (o.byPhone != 0)
         {
            phoneSum += o.Sum();
            ordByCalls[o.id] = true;
            calls[o.id] = true;
         }
         else
         {
            AddOrg(o);
            AddOrder(o.id, o.userid, o.created);
            sum += o.Sum();
            orderCnt[o.id] = true;
         }
      }

      public override void Add(VisitInfo doc)
      {
         if(!doc.id.EndsWith("\t"))
            base.Add(doc);
      }

      public override int GetOrders()
      {
         return orderCnt.Count;
      }

      public override int GetVisitCount()
      {
         return base.GetVisitCount();
      }

      public void Add(PhoneCall pc)
      {
         //AddOrg(pc);
         calls[pc.id] = true;
      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      public int byPhone = 0;
      public int calls = 0;
      public double phoneSum = 0;

      public SummaryDivisionDataEx(Division d) : base(d) { }

      internal override void AddChildDivision(SummaryDivisionData chData)
      {
         base.AddChildDivision(chData);
         byPhone += ((SummaryDivisionDataEx)chData).byPhone;
         calls += ((SummaryDivisionDataEx)chData).Calls;
         phoneSum += ((SummaryDivisionDataEx)chData).PhoneSum;
      }

      public override void Add(SummaryData data)
      {
         base.Add(data);
         byPhone += ((SummaryDataEx)data).ByPhone;
         calls += ((SummaryDataEx)data).Calls;
         phoneSum += ((SummaryDataEx)data).PhoneSum;
      }

      public int ByPhone { get { return byPhone; } }
      public int Calls { get { return calls; } }
      public double PhoneSum { get { return phoneSum; } }
   }
}