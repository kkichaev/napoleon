using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Reflection.Emit;
using System.Text;
using System.Windows.Forms;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class FmMMLEditor : Form, IStackedHeaderGenerator
   {
      StackedHeaderDecorator headerDecorator = null;
      static int ClmnWdth = 70;

      DataSet<string, SalesTypes> dgvSlsType = new DataSet<string, SalesTypes>(SalesTypes.OBJECT_NAME, false);
      DataSet<string, OrgType> dgvOrgType = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);
      DataSet<string, ManagerFolder> dsFolders;
      DataSet<string, Price> dsPrice;
      List<SalesTypes> salesTypes;
      List<OrgType> orgTypes;

      SimpleDataSet<MMLFeatures> dsFeatures = new SimpleDataSet<MMLFeatures>(MMLFeatures.OBJECT_NAME);
      
      public FmMMLEditor()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;

         dgvItems.CurrentCellDirtyStateChanged += dgvItems_CurrentCellDirtyStateChanged;
         dgvItems.CellPainting += dgvItems_CellPainting;

         headerDecorator = new StackedHeaderDecorator(this, dgvItems);

         dsFolders = DataModule.Get(ManagerFolder.OBJECT_NAME) as DataSet<string, ManagerFolder> ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

         dsPrice = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ??
            new DataSet<string, Price>(Price.OBJECT_NAME);

         //List<RowData> src = new List<RowData>();

         //RowData rd = new RowData("test");
         //rd.changed += rd_changed;
         //src.Add(rd);
         //rd = new RowData("test1");
         //rd.changed += rd_changed;
         //src.Add(rd);

         //Totals tot = new Totals("Totals", src);
         //src.Add(tot);

         //dgvItems.DataSource = src;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dgvSlsType);
         upd.Add(dgvOrgType);
         upd.Add(dsFeatures);

         if (dsPrice.Count == 0)
            upd.Add(dsPrice);
         if (dsFolders.Count == 0)
            upd.Add(dsFolders);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         dgvItems.Columns.Clear();
         dgvItems.Columns.Add(clmnName);

         Dictionary<string, CheckedData> isChecked = new Dictionary<string, CheckedData>();
         foreach(MMLFeatures mf in dsFeatures.Data)
         {
            foreach(MMLFeatures.Item i in mf.items)
            {
               CheckedData chD;
               if(isChecked.TryGetValue(i.id, out chD) == false)
               {
                  chD = new CheckedData();
                  isChecked[i.id] = chD;
               }
               if (mf.IsOrgType)
                  chD.orgType[mf.id] = true;
               else
                  chD.salesType[mf.id] = true;
            }
         }

         salesTypes = new List<SalesTypes>(dgvSlsType.Values);
         salesTypes.Sort();
         int index = 1;
         foreach (SalesTypes st in salesTypes)
         {
            DataGridViewCheckBoxColumn cc = new DataGridViewCheckBoxColumn();
            cc.Width = ClmnWdth;
            cc.DataPropertyName = "SalesType" + index++.ToString();
            dgvItems.Columns.Add(cc);
         }

         orgTypes = new List<OrgType>(dgvOrgType.Values);
         orgTypes.Sort();
         index = 1;
         foreach (OrgType st in orgTypes)
         {
            DataGridViewCheckBoxColumn cc = new DataGridViewCheckBoxColumn();
            cc.Width = ClmnWdth;
            cc.DataPropertyName = "OrgType" + index++.ToString();
            dgvItems.Columns.Add(cc);
         }

         headerDecorator.Recreate();

         Dictionary<String, List<Price>> price = new Dictionary<string, List<Price>>();
         foreach(Price p in dsPrice.Data)
         {
            List<Price> src;
            if( !price.TryGetValue(p.fid, out src) )
            {
               src = new List<Price>();
               price.Add(p.fid, src);
            }
            src.Add(p);
         }

         List<ManagerFolder> folders = new List<ManagerFolder>(dsFolders.Values);
         folders.Sort();

         Type rowType = RowData.CreateChildType(salesTypes, orgTypes);
         ConstructorInfo ci = rowType.GetConstructor(RowData.ConsType);

         Type listType = typeof(List<>).MakeGenericType(new Type[] { rowType });
         ConstructorInfo lci = listType.GetConstructor(Type.EmptyTypes);
         IList dataSrc = (IList)lci.Invoke(null);

         foreach(ManagerFolder mf in folders)
         {
            List<Price> src;
            if (!price.TryGetValue(mf.id, out src))
               continue;

            src.Sort();

            object[] prms = new object[] { mf.name, "", new CheckedData() };
            RowData t = (RowData)ci.Invoke(prms);
            dataSrc.Add(t); // итог в начале группы
            foreach (Price p in src)
            {
               CheckedData chD;
               if (isChecked.TryGetValue(p.id, out chD) == false)
                  chD = new CheckedData();

               prms = new object[] { p.name + " " + p.thermalState + "/" + p.packName, p.id, chD };
               RowData dr = (RowData)ci.Invoke(prms);
               dr.changed += rd_changed;
               t.AddRow(dr);
               dataSrc.Add(dr);
            }
            //dataSrc.Add(t); // итог в конце группы
         }

         dgvItems.DataSource = dataSrc;
      }

      void rd_changed(object sender, EventArgs e)
      {
         foreach (DataGridViewRow r in dgvItems.Rows)
         {
            RowData t = r.DataBoundItem as RowData;
            if (t != null && t.HaveRow((RowData)sender))
            {
               dgvItems.InvalidateRow(r.Index);
               tsbSave.Enabled = true;
               break;
            }
         }
      }

      void dgvItems_CellPainting(object sender, DataGridViewCellPaintingEventArgs e)
      {
         if (e.ColumnIndex < 0 || e.RowIndex < 0)
            return;
         RowData tr = dgvItems.Rows[e.RowIndex].DataBoundItem as RowData;
         if (tr != null)
         {
            string pn = dgvItems.Columns[e.ColumnIndex].DataPropertyName;
            tr.Paint(e, pn);
         }
      }

      void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      public class CheckedData
      {
         public Dictionary<string, bool> salesType = new Dictionary<string, bool>();
         public Dictionary<string, bool> orgType = new Dictionary<string, bool>();
      }

      public class RowData
      {
         protected string name;
         protected string id;
         CheckedData checkedData;

         List<RowData> src;
         static Font textFont;

         public event EventHandler changed;

         public RowData(string name, string id, CheckedData chD)
         {
            this.name = name;
            this.id = id;
            this.checkedData = chD;
         }

         public string ID { get { return id; } }
         public CheckedData Data { get { return checkedData; } }

         public void AddRow(RowData r)
         {
            if (src == null)
               src = new List<RowData>();
            src.Add(r); 
         }

         public bool HaveRow(RowData r) { return src != null && src.Contains(r); }


         public string Name { get { return name; } }

         public void CheckSalesType(string id, bool value)
         {
            if (value)
               checkedData.salesType[id] = true;
            else
               checkedData.salesType.Remove(id);

            if (changed != null)
               changed.Invoke(this, EventArgs.Empty);
         }

         public void CheckOrgType(string id, bool value)
         {
            if (value)
               checkedData.orgType[id] = true;
            else
               checkedData.orgType.Remove(id);

            if (changed != null)
               changed.Invoke(this, EventArgs.Empty);
         }

         public bool IsChecked(string id, bool orgType)
         {
            if (orgType)
               return checkedData.orgType.ContainsKey(id);

            return checkedData.salesType.ContainsKey(id);
         }

         public void Paint(DataGridViewCellPaintingEventArgs e, string propName)
         {
            if (src == null)
               return;

            string value = "";
            System.Drawing.StringFormat sf = new System.Drawing.StringFormat();
            if (e.ColumnIndex == 0)
            {
               value = name;
               sf.Alignment = StringAlignment.Near;
               sf.LineAlignment = StringAlignment.Center;
            }
            else
            {
               sf.Alignment = StringAlignment.Center;
               sf.LineAlignment = StringAlignment.Center;

               value = DoCount(propName).ToString();
            }
            e.CellStyle.SelectionBackColor = e.CellStyle.BackColor = Color.LightGray;

            bool selected = ((e.State & DataGridViewElementStates.Selected) != 0);
            if ((e.PaintParts & DataGridViewPaintParts.Background | DataGridViewPaintParts.SelectionBackground | DataGridViewPaintParts.ContentBackground) != 0)
               e.PaintBackground(e.ClipBounds, selected);



            if (textFont == null)
               textFont = new Font(e.CellStyle.Font, FontStyle.Bold);

            RectangleF rf = new RectangleF(e.CellBounds.X, e.CellBounds.Y, e.CellBounds.Width, e.CellBounds.Height);
            using (SolidBrush sb = new SolidBrush(selected ? e.CellStyle.SelectionForeColor : e.CellStyle.ForeColor))
               e.Graphics.DrawString(value, textFont, sb, rf, sf);

            e.Handled = true;
         }

         int DoCount(string propName)
         {
            int ret = 0;
            PropertyInfo pi = GetType().GetProperty(propName);
            if (pi != null)
            {
               foreach (RowData rd in src)
               {
                  Boolean val = (Boolean)pi.GetValue(rd, null);
                  if (val)
                     ret++;
               }
            }

            return ret;
         }
         public static Type[] ConsType
         {
            get
            {
               return new Type[] {
                     typeof(string),
                     typeof(string),
                     typeof(CheckedData),
               };
            }
         }

         internal static Type CreateChildType(List<SalesTypes> salesTypes, List<OrgType> orgTypes)
         {
            Type retType = typeof(RowData);
            try
            {
               AssemblyBuilder assemblyBuilder = AppDomain.CurrentDomain.DefineDynamicAssembly(new AssemblyName("GRSoft.NapoleonManager"), AssemblyBuilderAccess.Run);
               ModuleBuilder moduleBuilder = assemblyBuilder.DefineDynamicModule("Dynamic.dll");

               TypeBuilder typeBuilder = moduleBuilder.DefineType("RowDataEx");
               typeBuilder.SetParent(typeof(RowData));

               Type[] consType = ConsType;
               ConstructorBuilder cb = typeBuilder.DefineConstructor(MethodAttributes.Public, CallingConventions.Standard, consType);
               ILGenerator ilg = cb.GetILGenerator();
               ilg.Emit(OpCodes.Ldarg_0);
               ilg.Emit(OpCodes.Ldarg_1);
               ilg.Emit(OpCodes.Ldarg_2);
               ilg.Emit(OpCodes.Ldarg_3);
               ilg.Emit(OpCodes.Call, typeof(RowData).GetConstructor(consType));
               ilg.Emit(OpCodes.Ret);

               int index = 1;
               foreach(SalesTypes st in salesTypes)
               {
                  string propName = "SalesType" + index++.ToString();
                  CreateProperty(typeBuilder, propName, false, st.id);
               }
               
               index = 1;
               foreach (OrgType st in orgTypes)
               {
                  string propName = "OrgType" + index++.ToString();
                  CreateProperty(typeBuilder, propName, true, st.id);
               }

               retType = typeBuilder.CreateType();
            } catch(Exception e)
            {
               MessageBox.Show(e.Message);
               retType = typeof(RowData);
            }
            
            return retType;
         }

         static void CreateProperty(TypeBuilder typeBuilder, string propName, bool isOrgType, string id)
         {
            PropertyBuilder propertyBuilder = typeBuilder.DefineProperty(propName, System.Reflection.PropertyAttributes.None, typeof(bool), Type.EmptyTypes);

            MethodBuilder getMethodBuilder = typeBuilder.DefineMethod("get_" + propName, MethodAttributes.Public, CallingConventions.HasThis, typeof(bool), Type.EmptyTypes);
            ILGenerator ilg = getMethodBuilder.GetILGenerator();
            ilg.Emit(OpCodes.Ldarg_0);
            ilg.Emit(OpCodes.Ldstr, id);
            ilg.Emit(isOrgType? OpCodes.Ldc_I4_1 : OpCodes.Ldc_I4_0);
            ilg.Emit(OpCodes.Call, typeof(RowData).GetMethod("IsChecked"));
            ilg.Emit(OpCodes.Ret);
            propertyBuilder.SetGetMethod(getMethodBuilder);

            MethodBuilder setMethodBuilder = typeBuilder.DefineMethod("set_" + propName, MethodAttributes.Public, CallingConventions.HasThis, null, new Type[] { typeof(bool) });
            ilg = setMethodBuilder.GetILGenerator();
            ilg.Emit(OpCodes.Ldarg_0);
            ilg.Emit(OpCodes.Ldstr, id);
            ilg.Emit(OpCodes.Ldarg_1);
            ilg.Emit(OpCodes.Call, typeof(RowData).GetMethod(isOrgType ? "CheckOrgType" : "CheckSalesType"));
            ilg.Emit(OpCodes.Ret);
            propertyBuilder.SetSetMethod(setMethodBuilder);
         }
      }

      public Header GenerateStackedHeader()
      {
         int index = 0;

         Header ret = new Header();
         ret.Children.Add(new Header { Name = clmnName.HeaderText, ColumnId = index++, X = 0 });

         if (salesTypes == null)
            return ret;

         List<Header> headList = ret.Children;
         int x = clmnName.Width;

         Header h = new Header {Name="Места продаж", X = x};
         foreach (SalesTypes st in salesTypes)
         {
            Header ch = new Header { Name = st.name, ColumnId = index++, X = x };
            x += ClmnWdth;
            h.Children.Add(ch);
         }
         ret.Children.Add(h);

         h = new Header() { Name = "Формат ТТ", X = x };
         foreach (OrgType ot in orgTypes)
         {
            Header ch = new Header { Name = ot.name, ColumnId = index++, X = x };
            x += ClmnWdth;
            h.Children.Add(ch);
         }
         ret.Children.Add(h);

         return ret;
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         Dictionary<string, MMLFeatures> sales = new Dictionary<string, MMLFeatures>();
         Dictionary<string, MMLFeatures> orgTypes = new Dictionary<string, MMLFeatures>();

         SimpleDataSet<MMLFeatures> wr = new SimpleDataSet<MMLFeatures>(MMLFeatures.OBJECT_NAME, false);
         IList src = (IList)dgvItems.DataSource;
         foreach(object obj in src)
         {
            RowData rd = (RowData)obj;
            if (rd.ID == "")
               continue;

            MMLFeatures.Item item = new MMLFeatures.Item();
            item.id = rd.ID;

            CheckedData cd = rd.Data;
            foreach(string id in cd.salesType.Keys)
            {
               MMLFeatures ml;
               if (!sales.TryGetValue(id, out ml))
               {
                  ml = new MMLFeatures();
                  ml.id = id;
                  ml.kind = MMLFeatures.SALES_PLACE_KIND;
                  wr.Add(ml);
                  sales[id] = ml;
               }
               ml.items.Add(item);
            }

            foreach (string id in cd.orgType.Keys)
            {
               MMLFeatures ml;
               if (!orgTypes.TryGetValue(id, out ml))
               {
                  ml = new MMLFeatures();
                  ml.id = id;
                  ml.kind = MMLFeatures.ORG_TYPE_KIND;
                  wr.Add(ml);
                  orgTypes[id] = ml;
               }
               ml.items.Add(item);
            }
         }

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         ReplacedSet rs = new ReplacedSet(wr);
         rpl.Add(rs);

         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (ret)
         {
            //needSaveOrders.Clear();
         }

         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }
   }
}
