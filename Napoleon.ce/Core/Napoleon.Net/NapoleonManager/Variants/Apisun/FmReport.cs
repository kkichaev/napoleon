using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmReport : Form
   {
      DataSet<string, Price> dsPrice;
      DataSet<int, Folder> dsFolder;
      DataSet<int, OrgRemnants> dsRemnants;
      DataSet<string, OrgEx> dsOrg;
      DataSet<int, VisitInfo> dsVisit;
      DataSet<string, OrgType> dsOrgType;
      DataSet<string, Dealer> dsDealer;
      DataSet<string, Org> dsCommonOrg;
      SettingFmReport setting;
      protected const string COMMON_FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy} 23:59:59') and \"userid\" in({2})";

      public FmReport()
      {
         InitializeComponent();
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? 
            new DataSet<string, Price>(Price.OBJECT_NAME);
         dsFolder = (DataSet<int, Folder>)DataModule.Get(Folder.OBJECT_NAME) ?? 
            new DataSet<int, Folder>(Folder.OBJECT_NAME);
         dsRemnants = (DataSet<int, OrgRemnants>)DataModule.Get(OrgRemnants.OBJECT_NAME) ?? 
            new DataSet<int, OrgRemnants>(OrgRemnants.OBJECT_NAME);
         dsVisit = new DataSet<int, VisitInfo>(Visit.OBJECT_NAME, false);
         dsOrg = new DataSet<string, OrgEx>(Org.COMMON_OBJECT_NAME, false);
         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ??
            new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         dsDealer = (DataSet<string, Dealer>)DataModule.Get(Dealer.OBJECT_NAME) ??
            new DataSet<string, Dealer>(Dealer.OBJECT_NAME);

         dsCommonOrg = (DataSet<string, Org>) DataModule.Get(Org.COMMON_OBJECT_NAME) ??
            new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsCommonOrg.Filter = "id is not null";

         setting = BaseFormSetting<SettingFmReport>.Load();
      }

      private void button1_Click(object sender, EventArgs e)
      {
         String agentIds = GetSelectedAgent();
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSet = new List<IDataSet>();
         dsPrice.Filter = "folderID >= 0";
         updSet.Add(dsPrice);
         updSet.Add(dsFolder);
         updSet.Add(dsDealer);
         updSet.Add(dsOrg);
         updSet.Add(dsOrgType);
         updSet.Add(dsVisit);
         updSet.Add(dsRemnants);

         if (tcParams.SelectedIndex == 0)
         {
            if (agentIds.Length > 0)
            {
               dsOrg.Filter = "\"id\" in (select \"id\" from agentorg where \"userid\" in (" + GetSelectedAgent() + "))";
               dsVisit.Filter = string.Format(COMMON_FILTER_STR, dtpBegin.Value.Date, dtpEnd.Value.Date, agentIds);
               dsRemnants.Filter = string.Format(COMMON_FILTER_STR, dtpBegin.Value.Date, dtpEnd.Value.Date, agentIds);
            }
            else
            {
               MessageBox.Show("Выберите агента, или подразделение для отчета!");
               return;
            }
         }
         else
         {
            const string ORG_FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy} 23:59:59') and \"id\" in({2})";

            string orgfilter = GetOrgFilter();
            dsOrg.Filter = string.Format("\"id\" in ({0})", orgfilter);
            dsVisit.Filter = string.Format(ORG_FILTER_STR, dtpBegin.Value.Date, dtpEnd.Value.Date, orgfilter);
            dsRemnants.Filter = string.Format(ORG_FILTER_STR, dtpBegin.Value.Date, dtpEnd.Value.Date, orgfilter);
         }

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), updSet, FmWait.ProgressIndicator));
      }

      private string GetOrgFilter()
      {
         StringBuilder result = new StringBuilder();

         if(lbOrg.CheckedItems.Count > 0)
            foreach (object o in lbOrg.CheckedItems)
            {
               if (result.Length > 0)
                  result.Append(",");

               result.Append("'").Append((o as Org).id).Append("'");
            }

         return result.ToString();
      }

      private String Quotes(String str)
      {
         StringBuilder result = new StringBuilder();

         result.Append("'").Append(str).Append("'");

         return result.ToString();
      }

      private string GetSelectedAgent()
      {
         StringBuilder result = new StringBuilder();

         if (rbAgents.Checked)
         {
            Agent a = (Agent)cbAgents.SelectedItem;
            if(a != null)
               result.Append(Quotes(a.id));
         }
         else if (rbDivision.Checked)
         {
            Division d = cbDivisions.SelectedItem as Division;

            if (d != null)
            {
               List<Division.DivisionAgent> list = d.GetAllAgents();

               for (int i = 0; i < list.Count; i++)
               {
                  result.Append(Quotes(list[i].agent.id));

                  if (i + 1 < list.Count)
                     result.Append(",");
               }
            }
         }

         return result.ToString();
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsPrice);
            list.Add(dsFolder);
            list.Add(dsRemnants);
            list.Add(dsVisit);
            list.Add(dsOrg);
            Report rpt = new Report();
            List<string> invisible = new List<string>();
            Invoke(new EmptyParamHandler(delegate()
               {
                  foreach (Control c in groupBox.Controls)
                  {
                     if (c is CheckBox && !((CheckBox)c).Checked)
                        invisible.Add(c.Tag.ToString());
                  }

                  if (tcParams.SelectedIndex == 0)
                     invisible.Add("13");
               }));
            rpt.build(this, invisible);
            rpt.Dispose();
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void FmReport_Load(object sender, EventArgs e)
      {
         cbAddress.Checked = setting.address;
         cbName.Checked = setting.name;
         cbData.Checked = setting.date;
         cbPhone.Checked = setting.phone;
         cbOrgType.Checked = setting.orgType;
         cbDealer.Checked = setting.dealer;
         cbLicense.Checked = setting.license;
         cbCheif.Checked = setting.cheif;
         cbContact.Checked = setting.contact;
         cbPrice.Checked = setting.price;
         cbResult.Checked = setting.result;
         cbAvgTraff.Checked = setting.avgtraff;
         cbVisitCnt.Checked = setting.visitcnt;
         cbSkucnt.Checked = setting.skucnt;
         cbEmail.Checked = setting.email;
         
         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         LoadOrgList();
      }

      private void FmReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.address = cbAddress.Checked;
         setting.name = cbName.Checked;
         setting.date = cbData.Checked;
         setting.phone = cbPhone.Checked;
         setting.orgType = cbOrgType.Checked;
         setting.dealer = cbDealer.Checked;
         setting.license = cbLicense.Checked;
         setting.cheif = cbCheif.Checked;
         setting.contact = cbContact.Checked;
         setting.price = cbPrice.Checked;
         setting.result = cbResult.Checked;
         setting.avgtraff = cbAvgTraff.Checked;
         setting.visitcnt = cbVisitCnt.Checked;
         setting.skucnt = cbSkucnt.Checked;
         setting.email = cbEmail.Checked;

         setting.Save();
      }

      [Serializable]
      private class SettingFmReport : BaseFormSetting<SettingFmReport>
      {
         public bool address = true;
         public bool name = true;
         public bool date = true;
         public bool phone = true;
         public bool orgType = true;
         public bool dealer = true;
         public bool license = true;
         public bool cheif = true;
         public bool contact = true;
         public bool price = true;
         public bool result = true;
         public bool avgtraff = true;
         public bool visitcnt = true;
         public bool skucnt = true;
         public bool email = true;
      }

      class ReportData
      {
         public string address = string.Empty;
         public string name = string.Empty;
         public DateTime data = DateTime.MinValue;
         public string phone = string.Empty;
         public string orgType = string.Empty;
         public string dealer = string.Empty;
         public string license = string.Empty;
         public string cheif = string.Empty;
         public string contact = string.Empty;
         public string result = string.Empty;
         public string avgTraff = string.Empty;
         public List<Price> price = new List<Price>();
         public List<Visit.VisitItem> visitItem = new List<Visit.VisitItem>();
         public string orgid = string.Empty;
         public string agent = string.Empty;
         public string email = string.Empty;
      }

      class Report : Excel
      {
         int photoClmn = 1;
         public void build(FmReport rpt, List<String> invisibleClmns)
         {
            object sheet = AddSheet();
            SetSheetName(GetSheetByIndex(1), "посещения");
            SetSheetName(GetSheetByIndex(2), "не посетил");

            string fid = string.Empty;

            List<Price> priceList = new List<Price>();
            priceList.AddRange(rpt.dsPrice.Values);

            Dictionary<int, Folder> folders = new Dictionary<int, Folder>();
            
            foreach (Folder fld in rpt.dsFolder.Data)
               if (!folders.ContainsKey(fld.id))
                  folders.Add(fld.id, fld);

            priceList.Sort(new Comparison<Price>(delegate(Price p1, Price p2)
            {
               int result = 0; 

               if (p1.folderID != p2.folderID && 
                  folders.ContainsKey(p1.folderID) &&
                  folders.ContainsKey(p2.folderID))
               {
                  Folder f1 = folders[p1.folderID];
                  Folder f2 = folders[p2.folderID];

                  result = f1.name.CompareTo(f2.name);
               }

               if (result == 0)
                  result = p1.name.CompareTo(p2.name);

               return result;
            }));

            const int COLUMN_BEGIN_PRICE = 16;
            const int PRICE_ROW = 2;
            int begin = COLUMN_BEGIN_PRICE;
            const int FOLDER_ROW = 1;
            int f = -1;

            for (int i = COLUMN_BEGIN_PRICE; i <= priceList.Count + COLUMN_BEGIN_PRICE; i++)
               SetColumnWidth(i, 3);

            Dictionary<string, int> priceMap = new Dictionary<string, int>();
            for (int i = 0; i < priceList.Count; i++)
            {
               Price p = priceList[i];

               if (f == -1)
                  f = p.folderID;
               else if (f != p.folderID && folders.ContainsKey(f))
               {
                  SetValue(FOLDER_ROW, begin, folders[f].name);
                  MergeCells(FOLDER_ROW, begin, FOLDER_ROW, COLUMN_BEGIN_PRICE + i - 1);
                  begin = COLUMN_BEGIN_PRICE + i;
                  f = p.folderID;
               }

               SetValue(PRICE_ROW, COLUMN_BEGIN_PRICE + i, p.name);
               priceMap.Add(p.id, COLUMN_BEGIN_PRICE + i);
            }

            if(rpt.dsFolder.ContainsKey(f))
               SetValue(FOLDER_ROW, begin, rpt.dsFolder[f].name);

            MergeCells(FOLDER_ROW, begin, FOLDER_ROW, COLUMN_BEGIN_PRICE + priceList.Count - 1);

            photoClmn = COLUMN_BEGIN_PRICE + priceList.Count;

            SetOrientation(PRICE_ROW, COLUMN_BEGIN_PRICE, PRICE_ROW, priceList.Count + COLUMN_BEGIN_PRICE, 90);
            SetRowHeight(PRICE_ROW, 100.0);

            SetValue(PRICE_ROW, 1, "адрес");
            SetColumnWidth(1, 25);
            SetValue(PRICE_ROW, 2, "наименование");
            SetColumnWidth(2, 25);
            SetValue(PRICE_ROW, 3, "кол-во посещений");
            SetColumnWidth(3, 10);
            SetValue(PRICE_ROW, 4, "дата");
            SetColumnWidth(4, 10);
            SetValue(PRICE_ROW, 5, "телефон");
            SetColumnWidth(5, 12);
            SetValue(PRICE_ROW, 6, "вид ТТ");
            SetValue(PRICE_ROW, 7, "оптовик");
            SetValue(PRICE_ROW, 8, "лицензия");
            SetValue(PRICE_ROW, 9, "директор");
            SetValue(PRICE_ROW, 10, "контактное лицо");
            SetValue(PRICE_ROW, 11, "средняя проходимость");
            SetValue(PRICE_ROW, 12, "результат переговоров");
            SetValue(PRICE_ROW, 13, "торговый агент");
            SetValue(PRICE_ROW, 14, "общее кол -во SKU");
            SetValue(PRICE_ROW, 15, "email");
            SetCellBoldFont(1, 1, PRICE_ROW, priceList.Count + COLUMN_BEGIN_PRICE-1, true);
            object range = GetRange(1, 1, PRICE_ROW, priceList.Count + COLUMN_BEGIN_PRICE-1);
            SetBackColor(range, Color.LightGreen);

            Dictionary<string, ReportData> reportData = new Dictionary<string, ReportData>();
            //List<OrgRemnants> remnants = new List<OrgRemnants>();
            //remnants.AddRange(rpt.dsRemnants.Values);
            //remnants.Sort((lhs, rhs) => lhs.created.CompareTo(rhs.created));

            Dictionary<string, OrgEx> unvisited = new Dictionary<string, OrgEx>(rpt.dsOrg);
            Dictionary<string, int> visitCnt = new Dictionary<string, int>();

            foreach (OrgRemnants r in rpt.dsRemnants.Values)
            {
               if (r.org != null && unvisited.ContainsKey(r.org.id))
                  unvisited.Remove(r.org.id);

               ReportData rd = null;
               string key = r.created.Date.Ticks.ToString() + r.org.id;
               if (reportData.ContainsKey(key))
                  rd = reportData[key];
               else
               {
                  rd = new ReportData();

                  if (r.org != null && rpt.dsOrg.ContainsKey(r.org.id))
                  {
                     OrgEx orgEx = rpt.dsOrg[r.org.id];
                     rd.address = orgEx.address;
                     rd.name = orgEx.name;
                     rd.data = r.created;
                     rd.phone = orgEx.contactPhone;
                     rd.orgid = r.org.id;
                     rd.agent = r.agent.Name;
                     rd.email = orgEx.email;

                     if(rpt.dsOrgType.ContainsKey(orgEx.orgType))
                        rd.orgType = rpt.dsOrgType[orgEx.orgType].name;

                     foreach (OrgDealerItem item in orgEx.dealers)
                     {
                        rd.dealer += item.item.name;
                        rd.dealer += ", ";
                     }

                     if (rd.dealer.Length > 2)
                        rd.dealer = rd.dealer.Substring(0, rd.dealer.Length - 2);

                     if (orgEx.license == 0)
                        rd.license = "нет";
                     else
                        rd.license = "есть";

                     rd.cheif = orgEx.cheif;
                     rd.contact = orgEx.contact;
                     rd.avgTraff = orgEx.avgTraff == 0 ? string.Empty : orgEx.avgTraff.ToString();

                     reportData.Add(key, rd);

                     if (visitCnt.ContainsKey(orgEx.id))
                        visitCnt[orgEx.id]++;
                     else
                        visitCnt.Add(orgEx.id, 1);
                  }

                  foreach (OrgRemnantsItem ori in r.items)
                     rd.price.Add(ori.item);
               }
            }
           
            foreach (VisitInfo v in rpt.dsVisit.Data)
            {
               if (v.org != null && unvisited.ContainsKey(v.org.id))
                  unvisited.Remove(v.org.id);

               ReportData rd = null;
               string key = v.created.Date.Ticks.ToString() + v.org.id;
               if (reportData.ContainsKey(key))
                  rd = reportData[key];
               else
               {
                  rd = new ReportData();

                  if (v.org != null && rpt.dsOrg.ContainsKey(v.org.id))
                  {
                     OrgEx orgEx = rpt.dsOrg[v.org.id];
                     rd.orgid = v.org.id;
                     rd.address = orgEx.address;
                     rd.name = orgEx.name;
                     rd.data = v.created;
                     rd.phone = orgEx.contactPhone;
                     rd.agent = v.agent.Name;

                     if (rpt.dsOrgType.ContainsKey(orgEx.orgType))
                        rd.orgType = rpt.dsOrgType[orgEx.orgType].name;

                     foreach (OrgDealerItem item in orgEx.dealers)
                     {
                        rd.dealer += item.item.name;
                        rd.dealer += ", ";
                     }

                     if (rd.dealer.Length > 2)
                        rd.dealer = rd.dealer.Substring(0, rd.dealer.Length - 2);


                     if (orgEx.license == 0)
                        rd.license = "нет";
                     else
                        rd.license = "есть";

                     rd.cheif = orgEx.cheif;
                     rd.contact = orgEx.contact;
                     rd.avgTraff = orgEx.avgTraff == 0 ? string.Empty : orgEx.avgTraff.ToString();

                     reportData.Add(key, rd);

                     if(visitCnt.ContainsKey(orgEx.id))
                        visitCnt[orgEx.id]++;
                     else
                        visitCnt.Add(orgEx.id, 1);
                  }
               }

               rd.result += " " + v.remark;
               //rd.visitItem.AddRange(v.items);
            }

            const int DATA_ROW_BEGIN = 3;
            int row = DATA_ROW_BEGIN;

            List<ReportData> list = new List<ReportData>(reportData.Values);
            list.Sort(new Comparison<ReportData>(delegate(ReportData r1, ReportData r2) 
               {
                  int result = r1.data.CompareTo(r2.data);

                  if (result == 0)
                     result = r1.name.CompareTo(r2.name);

                  return result;
               }));

            foreach (ReportData rd in list)
            {
               SetValue(row, 1, rd.address);
               SetValue(row, 2, rd.name);
               SetValue(row, 3, visitCnt.ContainsKey(rd.orgid) ? visitCnt[rd.orgid] : 0);
               SetValue(row, 4, rd.data.ToString("dd.MM.yyyy"));
               SetValue(row, 5, rd.phone);
               SetValue(row, 6, rd.orgType);
               SetValue(row, 7, rd.dealer);
               SetValue(row, 8, rd.license);
               SetValue(row, 9, rd.cheif);
               SetValue(row, 10, rd.contact);
               SetValue(row, 11, rd.avgTraff);
               SetValue(row, 12, rd.result);
               SetValue(row, 13, rd.agent);
               SetValue(row, 14, rd.price.Count == 0 ? "" : rd.price.Count.ToString());
               SetValue(row, 15, rd.email);

               foreach(Price p in rd.price)
               {
                  if (priceMap.ContainsKey(p.id))
                  {
                     int c = priceMap[p.id];
                     SetBackColor(GetRange(row, c, row, c), Color.Red);
                     SetCellHorizontalAlign(row, c, row, c, xlCenter);
                     SetValue(row, c, "+");
                  }
               }

               MakePhotos(row, rd.visitItem);
               row++;
            }

            SetValue(row, 3, visitCnt.Count);

            foreach (string rng in invisibleClmns)
            {
               if (rng.Equals("Price"))
               {
                  for (int i = COLUMN_BEGIN_PRICE; i <= priceList.Count + COLUMN_BEGIN_PRICE; i++)
                     HideColumn(i);
               }
               else
                  try
                  {
                     HideColumn(Int32.Parse(rng));
                  }
                  catch { }
            }

            range = GetRange(1, 1, row-1, priceList.Count + COLUMN_BEGIN_PRICE-1);
            SetBorders(range, xlContinuous);
            FreezePanes("C2");

            SetSelectedSheet(2);

            SetValue(PRICE_ROW, 1, "адрес");
            SetColumnWidth(1, 25);
            SetValue(PRICE_ROW, 2, "наименование");
            SetColumnWidth(2, 25);
            SetValue(PRICE_ROW, 3, "телефон");
            SetColumnWidth(4, 12);
            SetValue(PRICE_ROW, 4, "вид ТТ");
            SetValue(PRICE_ROW, 5, "оптовик");
            SetValue(PRICE_ROW, 6, "лицензия");
            SetValue(PRICE_ROW, 7, "директор");
            SetValue(PRICE_ROW, 8, "контактное лицо");
            SetValue(PRICE_ROW, 9, "средняя проходимость");

            List<OrgEx> unvList = new List<OrgEx>();
            unvList.AddRange(unvisited.Values);
            unvList.Sort((lhs, rhs) => lhs.Name.CompareTo(rhs.Name));

            int cell = PRICE_ROW + 1;

            foreach (OrgEx o in unvList)
            {
               SetValue(cell, 1, o.address);
               SetValue(cell, 2, o.name);
               SetValue(cell, 3, o.contactPhone);

               if (rpt.dsOrgType.ContainsKey(o.orgType))
                  SetValue(cell, 4, rpt.dsOrgType[o.orgType].name);
               else
                  SetValue(cell, 4, "код " + o.orgType);

               string dealer = string.Empty;
               foreach (OrgDealerItem item in o.dealers)
               {
                  dealer += item.item.name;
                  dealer += ", ";
               }

               if (dealer.Length > 2)
                  dealer = dealer.Substring(0, dealer.Length - 2);

               SetValue(cell, 5, dealer);
               SetValue(cell, 6, o.license == 0 ? "нет" : "есть");

               SetValue(cell, 7, o.cheif);
               SetValue(cell, 8, o.contact);
               SetValue(cell, 9, o.avgTraff == 0 ? string.Empty : o.avgTraff.ToString());

               cell++;
            }
            
            
            SetSelectedSheet(1);
            SetSelectedCell("A1");
            Visible = true;
         }

         private void MakePhotos(int row, List<Visit.VisitItem> items)
         {
            //int fileIndex = 0;
            //int idx = 1;
               
            //foreach (Visit.VisitItem item in items)
            //{
            //   const string NAME = "rmr";
            //   string path = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + "\\" + NAME;
            //   DirectoryInfo dir = new DirectoryInfo(path);

            //   if (!dir.Exists)
            //      dir.Create();

            //   string caption = item.caption;
            //   if (caption.Length == 0)
            //   {
            //      caption = "Фото" + idx.ToString();
            //      idx++;
            //   }

            //   string fileName = GRSoft.Network.DataObject.GenId() + "_" + fileIndex.ToString() + "_" + caption + ".jpg";
            //   FileStream fs = new FileStream(path + "\\" + fileName, FileMode.OpenOrCreate);
            //   fs.Write(item.id, 0, item.id.Length);
            //   fs.Close();

            //   MakeHyperlinks(row, photoClmn + fileIndex, "", path + "\\" + fileName);
            //   SetValue(row, photoClmn + fileIndex, caption);
            //   fileIndex++;
            //}
         }
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = true;
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = true;
         cbDivisions.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsCommonOrg);

         FmWait.StdDataRefresh(this, upd, () => { LoadOrgList(); });
      }

      private void LoadOrgList()
      {
         List<Org> list = new List<Org>();

         foreach (Org o in dsCommonOrg.Values)
            list.Add(o);

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         lbOrg.Items.AddRange(list.ToArray());
      }
   }
}
