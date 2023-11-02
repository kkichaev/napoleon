using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrg : Form
   {
      private DataSet<string, Org> dsOrg;
      private DataSet<string, Org> dsRemOrg = new DataSet<string, Org>(Org.OBJECT_NAME, false);
      private DataSet<string, Slsnet> dsSlsnet;
      private DataSet<string, City> dsCity;
      DataSet<string, ContractDef> dsContract;
      SimpleDataSet<OrgMatrix> dsOrgMatrix;
      SimpleDataSet<OrgMatrix> rmvMatrix;
      DataSet<int, Matrix> dsMatrix;
      DataSet<string, GoodsMatrix> dsGoodsMatrix;

      SearchHelper srchHelper;

      private SortableBindingList<Org> datasource = new SortableBindingList<Org>();

      public FmOrg()
      {
         InitializeComponent();

         btnSave.Enabled = false;
         grid.AutoGenerateColumns = false;
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsCity = (DataSet<string, City>)DataModule.Get(City.OBJECT_NAME) ?? new DataSet<string, City>(City.OBJECT_NAME);
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);

         dsMatrix = new DataSet<int, Matrix>(Matrix.OBJECT_NAME, false);

         dsOrgMatrix = new SimpleDataSet<OrgMatrix>(OrgMatrix.OBJECT_NAME, false);
         rmvMatrix = new SimpleDataSet<OrgMatrix>(OrgMatrix.OBJECT_NAME, false);

         dsGoodsMatrix = (DataSet<string, GoodsMatrix>)DataModule.Get(GoodsMatrix.OBJECT_NAME) ?? new DataSet<string, GoodsMatrix>(GoodsMatrix.OBJECT_NAME);
         srchHelper = new SearchHelper(grid, tbFindClear, tbFind);

         btnLoad.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsSlsnet);
         upd.Add(dsCity);
         upd.Add(dsOrg);
         upd.Add(dsOrgMatrix);
         upd.Add(dsMatrix);
         upd.Add(dsGoodsMatrix);

         dsMatrix.Filter = "\"cdef\" is not null";
         dsGoodsMatrix.Filter = "\"name\" is null or \"name\" is not null";
         dsOrg.Filter = "\"id\" is null or \"id\" is not null";

         if(dsContract.Count == 0 )
         {
            const string PERIOD_FILTER_STR = "\"start\" < ToDate('{1:dd/MM/yyyy}') and \"finish\" >= ToDate('{0:dd/MM/yyyy}')";
            dsContract.Filter = string.Format(PERIOD_FILTER_STR, DateTime.Now, DateTime.Now);
            upd.Add(dsContract);
         }

         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      private void DoLoadData()
      {
         datasource.Clear();
         foreach(Org o in dsOrg.Values)
            datasource.Add(o);
         grid.DataSource = datasource;
         btnSave.Enabled = false;
         srchHelper.SetData(datasource);
         btnLoad.Enabled = true;
      }

      private bool Save(List<IDataSet> wrSet )
      {
         if(dsOrg.Count > 0)
            wrSet.Add(dsOrg);
         if (dsOrgMatrix.Count > 0)
            wrSet.Add(dsOrgMatrix);

         List<IDataSet> rmSet = new List<IDataSet>();

         if(dsRemOrg.Count > 0)
            rmSet.Add(dsRemOrg);
         if (rmvMatrix.Count > 0)
            rmSet.Add(rmvMatrix);

         return DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
      }

      private void FmOrg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save(new List<IDataSet>());
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmOrgEdit dialog = new FmOrgEdit();

         if (dialog.ShowDialog() == DialogResult.OK)
         {
            Org org = new Org();
            org.id = Org.GenId();
            org.name = dialog.Org;
            org.slsnet = dialog.Slsnet;
            org.cid = dialog.CityName;
            org.address = dialog.Address;
            org.nameLPR = dialog.NameLPR;
            org.contactsLPR = dialog.ContactsLPR;
            org.bithdayLPR = dialog.BithdayLPR;
            org.visitDays = dialog.VisitDays;
            org.responsible = dialog.Responsible;
            org.equipment = dialog.Equipment;
            org.matrixsku = dialog.MatrixSku;
            org.orderday = dialog.OrderDay;
            org.promoplan = dialog.PromoPlan;

            dsOrg.Add(org.id, org);
            datasource.Add(org);
            btnSave.Enabled = true;
         }
      }

      private void FmOrg_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save(new List<IDataSet>()))
         {
            dsRemOrg.Clear();
            rmvMatrix.Clear();
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            Org org = grid.CurrentRow.DataBoundItem as Org;

            if (org != null)
            {
               FmOrgEdit dialog = new FmOrgEdit();
               dialog.CityName = org.cid;
               dialog.Org = org.name;
               dialog.Address = org.address;
               dialog.Slsnet = org.slsnet;
               dialog.MatrixName = org.goodsMatrix;
               dialog.NameLPR = org.nameLPR;
               dialog.ContactsLPR = org.contactsLPR;
               dialog.BithdayLPR = org.bithdayLPR;
               dialog.VisitDays = org.visitDays;
               dialog.Responsible = org.responsible;
               dialog.Equipment = org.equipment;
               dialog.MatrixSku = org.matrixsku;
               dialog.OrderDay = org.orderday;
               dialog.PromoPlan = org.promoplan;
               dialog.ProviderNumber = org.providerNumber;
               dialog.Code = org.code;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  org.cid = dialog.CityName;
                  org.slsnet = dialog.Slsnet;
                  org.name = dialog.Org;
                  org.address = dialog.Address;
                  org.goodsMatrix = dialog.MatrixName;
                  org.nameLPR = dialog.NameLPR;
                  org.contactsLPR = dialog.ContactsLPR;
                  org.bithdayLPR = dialog.BithdayLPR;
                  org.visitDays = dialog.VisitDays;
                  org.responsible = dialog.Responsible;
                  org.equipment = dialog.Equipment;
                  org.matrixsku = dialog.MatrixSku;
                  org.orderday = dialog.OrderDay;
                  org.promoplan = dialog.PromoPlan;
                  org.providerNumber = dialog.ProviderNumber;
                  org.code = dialog.Code;

                  btnSave.Enabled = true;
                  grid.Refresh();
               }
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null)
         {
            Org org = grid.CurrentRow.DataBoundItem as Org;

            if (org != null && DialogUtil.AskToDel(this))
            {
               dsRemOrg.Add(org.id, org);
               dsOrg.Remove(org.id);
               datasource.Remove(org);
               btnSave.Enabled = true;
            }
         }
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         SortableBindingList<OrgMatrix> src = new SortableBindingList<OrgMatrix>();
         Org org = grid.Rows[e.RowIndex].DataBoundItem as Org;
         if( org != null )
         {
            foreach(OrgMatrix om in dsOrgMatrix.Data)
            {
               if(om.id == org.id)
               {
                  if (dsContract.ContainsKey(om.cdef))
                     om.contract = dsContract[om.cdef];
                  src.Add(om);
               }
            }
         }

         dgvItems.DataSource = src;
      }

      void EditOrgMatrix(OrgMatrix om, bool needAdd)
      {
         if( FmOrgMatrixEdit.Edit(om, new List<ContractDef>((IEnumerable<ContractDef>)dsContract.Data), new List<Matrix>((IEnumerable<Matrix>)dsMatrix.Data)) )
         {
            SortableBindingList<OrgMatrix> src = (dgvItems.DataSource as SortableBindingList<OrgMatrix>);
            if (needAdd)
            {
               src.Add(om);
               dsOrgMatrix.Add(om);
            }
            else
               src.ResetItem(src.IndexOf(om));
            btnSave.Enabled = true;
         }
      }

      // add
      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow == null)
            return;

         Org org = grid.CurrentRow.DataBoundItem as Org;
         if( org != null )
         {
            OrgMatrix om = new OrgMatrix();
            om.id = org.id;
            EditOrgMatrix(om, true);
         }
      }

      // edit
      private void toolStripButton2_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         OrgMatrix om = dgvItems.CurrentRow.DataBoundItem as OrgMatrix;
         EditOrgMatrix(om, false);
      }

      // remove
      private void toolStripButton3_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         OrgMatrix om = dgvItems.CurrentRow.DataBoundItem as OrgMatrix;
         (dgvItems.DataSource as SortableBindingList<OrgMatrix>).Remove(om);
         rmvMatrix.Add(om);
         
         foreach(KeyValuePair<int, OrgMatrix> kv in dsOrgMatrix)
         {
            if (kv.Value == om)
            {
               dsOrgMatrix.Remove(kv.Key);
               break;
            }
         }
         btnSave.Enabled = true;
      }

      private void btnLoad_Click(object sender, EventArgs e)
      {
         OpenFileDialog dlg = new OpenFileDialog();
         if (dlg.ShowDialog() == DialogResult.OK)
            LoadOrg(dlg.FileName);
      }

      private void LoadOrg(string file)
      {
         Dictionary<string, City> cc = new Dictionary<string, City>(); //city cache
         Dictionary<string, Slsnet> sc = new Dictionary<string, Slsnet>(); //sls cache
         List<string> oc = new List<string>(); // org cache

         foreach (City c in dsCity.Values)
            cc[c.Name] = c;

         foreach (Slsnet s in dsSlsnet.Values)
            sc[s.Name] = s;

         foreach (Org o in dsOrg.Values)
            if (!oc.Contains(o.name))
               oc.Add(o.name);

         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", file);
         var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [Лист1$]", connectionString);
         var ds = new DataSet();

         adapter.Fill(ds, "name");

         DataTable data = ds.Tables["name"];

         foreach (DataRow row in data.Rows)
         {
            object[] r = row.ItemArray;

            if (r[0].ToString().Length == 0)
               break;

            Org org = new Org();
            org.id = GRSoft.Network.DataObject.GenId();
            org.name = r[0].ToString().Trim();

            if(oc.Contains(org.name)) // skip if org presents
               continue;

            org.address = r[3].ToString().Trim();

            string city = r[2].ToString().Trim();

            if (!cc.ContainsKey(city))
            {
               City c = new City();
               c.id = GRSoft.Network.DataObject.GenId();
               c.name = city;
               cc[city] = c;
               dsCity[c.id] = c;
            }

            org.cid = city;

            string sls = r[1].ToString().Trim();

            if (!sc.ContainsKey(sls))
            {
               Slsnet s = new Slsnet();
               s.id = GRSoft.Network.DataObject.GenId();
               s.name = sls;
               sc[sls] = s;
               dsSlsnet[s.id] = s;
            }

            org.sid = sc[sls].id;

            dsOrg[org.id] = org;
         }

         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(dsCity);
         wrSet.Add(dsSlsnet);

         if (!Save(wrSet))
            DialogUtil.UpdateErrMsg(this);
         else
            btnRefresh.PerformClick();
      }
   }

   class SearchHelper
   {
      DataGridView grid;
      Timer timer;
      IList data;
      TextSetter textSetter;
      int srchColumn = -1;

      string srchText = "";

      interface TextSetter
      {
         void SetText(string newText);
      }

      [System.Runtime.CompilerServices.MethodImpl(System.Runtime.CompilerServices.MethodImplOptions.Synchronized)]
      public void Clear(object sender, EventArgs e)
      {
         textSetter.SetText("");
         grid.DataSource = data;
      }

      void TextChanged(string newText)
      {
         timer.Stop();
         srchText = newText.ToUpper();
         if (srchText.Length > 0)
            timer.Start();
         else
            Clear(this, EventArgs.Empty);
      }

      void DoSearch()
      {
         timer.Stop();
         if (data == null)
            return;

         if (srchColumn == -1 || ((IList)grid.DataSource).Count > 0)
         {
            if (grid.CurrentCell == null)
            {
               if (srchColumn == -1)
                  return;
            }
            else
               srchColumn = grid.CurrentCell.ColumnIndex;
         }
         string prop = grid.Columns[srchColumn].DataPropertyName;
         ConstructorInfo ci = data.GetType().GetConstructor(Type.EmptyTypes);
         if (ci == null)
            return;

         PropertyInfo pi = null;
         IList dest = (IList)ci.Invoke(null);
         foreach(object di in data)
         {
            if (pi == null)
            {
               pi = di.GetType().GetProperty(prop);
               if (pi == null)
                  break;
            }
            object oval = pi.GetValue(di, null);
            string val = (oval == null) ? "" : oval.ToString();
            if (val.ToUpper().Contains(srchText))
               dest.Add(di);
         }

         grid.DataSource = dest;
         if (dest.Count > 0)
            grid.CurrentCell = grid.Rows[0].Cells[srchColumn];
      }

      public SearchHelper(DataGridView grid, ToolStripButton clearBtn, ToolStripTextBox text)
      {
         this.grid = grid;
         clearBtn.Click += Clear;
         text.TextChanged += (o, e) => { TextChanged(text.Text); };

         timer = new Timer();
         timer.Interval = 500;
         timer.Tick += (o, e) => { DoSearch(); };

         textSetter = new TSBTextSetter(text);
      }

      public void SetData(IList data) { this.data = data; }


      class TSBTextSetter : TextSetter
      {
         ToolStripTextBox tb;
         public TSBTextSetter(ToolStripTextBox tb) { this.tb = tb; }
         public void SetText(string newText) { tb.Text = newText; }
      }
   }
}
