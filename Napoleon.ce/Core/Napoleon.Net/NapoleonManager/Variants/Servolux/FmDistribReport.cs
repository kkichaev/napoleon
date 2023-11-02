using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmDistribReport : Form
   {
      static FmDistribReport instance = null;

      static readonly string ALL_TAG = "<все>";

      Data data = new Data();
      DataSet<string, PriceType> dsPriceTypes = new DataSet<string, PriceType>(PriceType.OBJECT_NAME);
      DataSet<string, Price> dsPrice;
      DataSet<string, Org> dsOrgs;

      public FmDistribReport()
      {
         InitializeComponent();
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      internal static void Do(DateTime start, DateTime finish, FmDetailEx owner, Agent agent)
      {
         if(instance == null)
         {
            instance = new FmDistribReport();

            instance.data.begin = start;
            instance.data.end = finish;
            instance.SetAgent(agent);
            instance.dtpBegin.Value = start;
            instance.dtpEnd.Value = finish;

            instance.Show();
         } else
         {
            instance.data.begin = start;
            instance.data.end = finish;
            instance.SetAgent(agent);
            instance.dtpBegin.Value = start;
            instance.dtpEnd.Value = finish;

            instance.RefreshData();
            instance.BringToFront();
         }
      }

      private void SetAgent(Agent agent)
      {
         lbAgent.Text = "Агент: " + agent.name;
         instance.data.userid = agent.id;
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         if( dsPrice.Count == 0)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsPrice);
         }

         dsOrgs = (DataSet<string, Org>)DataModule.GetUserDataSet(data.userid, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true);
         if (dsOrgs.Count == 0)
            upd.Add(dsOrgs);

         upd.Add(dsPriceTypes);

         FmWait.StdDataRefresh(this, upd, LoadData);
      }

      void LoadData()
      {
         cbPriceType.Items.Clear();
         PriceType pt = new PriceType();
         pt.id = "";
         pt.name = ALL_TAG;
         cbPriceType.Items.Add(pt);
         cbPriceType.SelectedIndex = 0;

         List<PriceType> src = new List<PriceType>((IEnumerable<PriceType>)dsPriceTypes.Data);
         src.Sort();
         src.ForEach((x) => { cbPriceType.Items.Add(x); });

         RefreshThState();
         LoadOrgs();
      }

      private void RefreshThState()
      {
         cbThState.Items.Clear();
         cbThState.Items.Add(ALL_TAG);
         cbThState.SelectedIndex = 0;

         PriceType sel = cbPriceType.SelectedItem as PriceType;
         List<String> value = new List<string>();
         foreach(Price p in dsPrice.Data)
         {
            if (p.idType == sel.id && !value.Contains(p.thermalState))
               value.Add(p.thermalState);
         }
         value.Sort();
         value.ForEach((x) => { cbThState.Items.Add(x); });
      }

      void LoadOrgs()
      {
         cbOrg.Items.Clear();
         OrgEx eo = new OrgEx();
         eo.id = "";
         eo.name = ALL_TAG;
         cbOrg.Items.Add(eo);
         cbOrg.SelectedIndex = 0;

         Dictionary<string, bool> used = new Dictionary<string, bool>();
         foreach(Org o in dsOrgs.Data)
         {
            if (used.ContainsKey(o.ido))
               continue;

            used.Add(o.ido, true);
            cbOrg.Items.Add(new OrgEx(o));
         }

         RefreshAddreses();
      }

      private void RefreshAddreses()
      {
         OrgAddress oa = new OrgAddress();
         oa.id = "";
         oa.name = ALL_TAG;
         cbAddress.Items.Clear();
         cbAddress.Items.Add(oa);
         cbAddress.SelectedIndex = 0;

         OrgEx sel = cbOrg.SelectedItem as OrgEx;
         List<OrgAddress> values = new List<OrgAddress>();
         foreach(Org o in dsOrgs.Data)
         {
            if (o.ido == sel.id)
               values.Add(new OrgAddress(o));
         }
         values.Sort();
         values.ForEach((x) => { cbAddress.Items.Add(x); });
      }

      class OrgEx : IComparable<OrgEx>
      {
         public string id;
         public string name;

         public OrgEx() { }
         public OrgEx(Org o) { id = o.ido; name = o.name; }

         public int CompareTo(OrgEx other) { return name.CompareTo(other.name); }
         public override string ToString() { return name; }
      }

      class OrgAddress : IComparable<OrgAddress>
      {
         public string id = "";
         public string name = "";

         public OrgAddress() { }
         public OrgAddress(Org o) { id = o.id; name = o.address; }

         public override string ToString() { return name; }

         public int CompareTo(OrgAddress other)
         {
            return name.CompareTo(other.name);
         }
      }

      public class Data : GRSoft.Network.DataObject
      {
         public string userid = "";
         
         public DateTime begin;
         public DateTime end;

         public string priceType = "";
         public string thState = "";
         public string org = "";
         public string address = "";
      }

      private void cancel_Click(object sender, EventArgs e)
      {
         Close();
      }

      private void ok_Click(object sender, EventArgs e)
      {
         data.begin = dtpBegin.Value.Date;
         data.end = dtpEnd.Value.Date;

         data.priceType = (cbPriceType.SelectedItem as PriceType).id;
         data.thState = ((cbThState.SelectedIndex == 0) ? "" : (string)cbThState.SelectedItem);
         data.org = (cbOrg.SelectedItem as OrgEx).id;
         data.address = (cbAddress.SelectedItem as OrgAddress).id;

         ReportResult.DoReport("distrib_report", data, this);
      }

      private void cbPriceType_SelectedIndexChanged(object sender, EventArgs e)
      {
         RefreshThState();
      }

      private void cbOrg_SelectedIndexChanged(object sender, EventArgs e)
      {
         RefreshAddreses();
      }

   }
}
