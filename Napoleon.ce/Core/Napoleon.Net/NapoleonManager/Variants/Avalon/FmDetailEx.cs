using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.Collections;
using System.Drawing;
using System.IO;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      SimpleDataSet<VandAudit> dsAudit = new SimpleDataSet<VandAudit>(VandAudit.OBJECT_NAME);
      SimpleDataSet<VandSales> dsSales = new SimpleDataSet<VandSales>(VandSales.OBJECT_NAME);
      SimpleDataSet<VandReload> dsReload = new SimpleDataSet<VandReload>(VandReload.OBJECT_NAME);
      SimpleDataSet<VandRestock> dsRestock = new SimpleDataSet<VandRestock>(VandRestock.OBJECT_NAME);
      SimpleDataSet<DefectReport> dsDefectReport = new SimpleDataSet<DefectReport>(DefectReport.OBJECT_NAME);

      DataGridView dgvDetailItems = new DataGridView();
      DataGridViewTextBoxColumn clmnCell = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnName = new DataGridViewTextBoxColumn();

      DataGridViewTextBoxColumn clmnCost = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnQty = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnLimit = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnChek = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnLoad = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnUnload = new DataGridViewTextBoxColumn();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();

         documents.Add(new DocumentInfo(dsAudit, ObjType.TObjType.VandAudit));
         documents.Add(new DocumentInfo(dsSales, ObjType.TObjType.VandSales));
         documents.Add(new DocumentInfo(dsReload, ObjType.TObjType.VandReload));
         documents.Add(new DocumentInfo(dsRestock, ObjType.TObjType.VandRestock));
         documents.Add(new DocumentInfo(dsDefectReport, ObjType.TObjType.DefectReport));
      }

      void Init()
      {
         dgvDetailItems.Visible = false;
         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).BeginInit();

         dgvDetailItems.AllowUserToAddRows = false;
         dgvDetailItems.AllowUserToDeleteRows = false;
         dgvDetailItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         dgvDetailItems.Dock = System.Windows.Forms.DockStyle.Fill;
         dgvDetailItems.Location = new System.Drawing.Point(0, 0);
         dgvDetailItems.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         dgvDetailItems.Name = "dgvDetailItems";
         dgvDetailItems.RowHeadersVisible = false;
         dgvDetailItems.Size = new System.Drawing.Size(611, 187);

         dgvDetailItems.Columns.AddRange(new DataGridViewColumn[] {
            clmnCell,
            clmnName,

            clmnCost,
            clmnQty,
            clmnLimit,
            clmnChek,
            clmnLoad,
            clmnUnload
         });

         clmnCell.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnCell.DataPropertyName = "Cell";
         clmnCell.HeaderText = "Ячейка";
         clmnCell.Name = "Cell";

         clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnName.DataPropertyName = "Name";
         clmnName.FillWeight = 400F;
         clmnName.HeaderText = "Наименование";
         clmnName.Name = "Name";

         clmnCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnCost.DataPropertyName = "Cost";
         clmnCost.HeaderText = "Цена";
         clmnCost.Name = "Cost";

         clmnQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnQty.DataPropertyName = "Qty";
         clmnQty.HeaderText = "Кол-во";
         clmnQty.Name = "Qty";

         clmnLimit.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnLimit.DataPropertyName = "Limit";
         clmnLimit.HeaderText = "Лимит";
         clmnLimit.Name = "Limit";

         clmnChek.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnChek.DataPropertyName = "Chek";
         clmnChek.HeaderText = "Чек";
         clmnChek.Name = "Chek";

         clmnLoad.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnLoad.DataPropertyName = "Load";
         clmnLoad.HeaderText = "Загр.";
         clmnLoad.Name = "Load";

         clmnUnload.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnUnload.DataPropertyName = "Unload";
         clmnUnload.HeaderText = "Выгр.";
         clmnUnload.Name = "Unload";

         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).EndInit();
         
         detailPanel.Controls.Add(dgvDetailItems);
      }

      protected override IDataSet GetDuplicate(Network.DataObject dataObject)
      {
         IDataSet ids = null;
         if (dataObject is VandAudit)
         {
            ids = new SimpleDataSet<VandAudit>(VandAudit.OBJECT_NAME, false);
            ((SimpleDataSet<VandAudit>)ids).Add((VandAudit)dataObject);
         } else if( dataObject is VandSales)
         {
            ids = new SimpleDataSet<VandSales>(VandSales.OBJECT_NAME, false);
            ((SimpleDataSet<VandSales>)ids).Add((VandSales)dataObject);
         }
         else if (dataObject is VandRestock)
         {
            ids = new SimpleDataSet<VandRestock>(VandRestock.OBJECT_NAME, false);
            ((SimpleDataSet<VandRestock>)ids).Add((VandRestock)dataObject);
         }
         else if (dataObject is VandReload)
         {
            ids = new SimpleDataSet<VandReload>(VandReload.OBJECT_NAME, false);
            ((SimpleDataSet<VandReload>)ids).Add((VandReload)dataObject);
         }
         return ids;
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsAudit.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsAudit);
         dsSales.Filter = dsAudit.Filter;
         updSets.Add(dsSales);
         dsReload.Filter = dsAudit.Filter;
         updSets.Add(dsReload);
         dsRestock.Filter = dsAudit.Filter;
         updSets.Add(dsRestock);
         dsDefectReport.Filter = dsAudit.Filter;
         updSets.Add(dsDefectReport);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvDetailItems.Visible = false;
         foreach (DocumentInfo di in documents)
         {
            if (di.Type == odr.Doctype.Val)
            {
               if (di.Type == ObjType.TObjType.DefectReport)
               {
                  DefectReport df =  odr.StoreObject as DefectReport;

                  if(df != null)
                     tbVisitText.Text = String.Format("Сведения об аппарате: {0}" + Environment.NewLine + 
                           "Сведение о неисправности: {1}", df.device,
                        df.remark);

                  return tbVisitText;
               }
               else
               {
                  dgvDetailItems.Visible = true;
                  GRSoft.Network.DataObject dobj = odr.StoreObject;
                  Type docType = dobj.GetType();
                  FieldInfo fi = docType.GetField("items");
                  object[] atts = fi.GetCustomAttributes(typeof(ItemTypeAttribute), false);
                  if (atts.Length > 0)
                  {
                     ItemTypeAttribute attr = atts[0] as ItemTypeAttribute;
                     PropertyInfo[] props = attr.ItemType.GetProperties(BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.Public);
                     foreach (DataGridViewColumn clmn in dgvDetailItems.Columns)
                     {
                        if (clmn.DataPropertyName == "Name" || (clmn.DataPropertyName == "Cell" && docType != typeof(VandRestock)))
                        {
                           clmn.Visible = true;
                           continue;
                        }
                        clmn.Visible = (Array.Find(props, (el) => { return (el.Name.Equals(clmn.DataPropertyName)); }) != null);
                     }
                  }
                  List<ItemBase> items = new List<ItemBase>();
                  foreach (ItemBase data in (IList)fi.GetValue(dobj))
                     items.Add(data);
                  dgvDetailItems.DataSource = items;
                  return dgvDetailItems;
               }
            }
         }

         return null; 
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }

      protected override void ShowCorrespondingPhoto(DateTime date, OrderDetailRepresentation o)
      {
         if (o.StoreObject is DefectReport)
         {
            DefectReport df = o.StoreObject as DefectReport;

            List<Image> nativePicture = new List<Image>();
            int i = 0;

            foreach (DefectReport.DefectReportItem item in df.items)
            {
               try
               {
                  if (item.id == null)
                     continue;

                  MemoryStream stream = new MemoryStream(item.id);
                  Image image = new Bitmap(stream);

                  nativePicture.Add(image);
                  imPhoto.Images.Add(image);
                  stream.Close();

                  String tag = (i + 1).ToString();
                  ListViewItem lvi = lvPhoto.Items.Add(tag);
                  lvi.ImageIndex = i;
                  i++;
               }
               catch (Exception ) { }
            }

            imPhoto.Tag = nativePicture;
         }else
            base.ShowCorrespondingPhoto(date, o);
      }
   }

   public class ItemBase : GRSoft.Network.DataObject
   {
      [Reference("ManagerPrice", "id")]
      public Price item = null;

      public string id = "";
      public int cell = 0;

      public int Cell { get { return cell; } }
      public string Name { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }

      public virtual double Cost { get { return 0; } }
      public virtual int Qty { get { return 0; } }
      public virtual int Limit { get { return 0; } }
      public virtual int Chek { get { return 0; } }
      public virtual int Load { get { return 0; } }
      public virtual int Unload { get { return 0; } }
   }

   public class VandAudit : BaseDocument
   {
      public static readonly String OBJECT_NAME = "Audit";

      [ItemType(typeof(Item))]
      public List<Item> items = null;


      public class Item : ItemBase
      {
         public int qty = 0;
         public int limit = 0;
         public double cost = 0;

         public override int Qty { get { return qty; } }
         public override int Limit { get { return limit; } }
         public override double Cost { get { return cost; } }
      }
   }

   public class VandSales : BaseDocument
   {
      public static readonly String OBJECT_NAME = "VandSell";

      [ItemType(typeof(Item))]
      public List<Item> items = null;


      public class Item : ItemBase
      {
         public int chek = 0;
         public int load = 0;
         public int unload = 0;
         public double cost = 0;

         public override int Chek { get { return chek; } }
         public override int Load { get { return load; } }
         public override int Unload { get { return unload; } }
         public override double Cost { get { return cost; } }
      }

      public override double Sum()
      {
         double sum = 0;
         foreach (Item i in items)
            sum += i.chek * i.cost;
         return sum;
      }
   }

   public class VandReload : BaseDocument
   {
      public static readonly String OBJECT_NAME = "VandReload";

      [ItemType(typeof(Item))]
      public List<Item> items = null;


      public class Item : ItemBase
      {
         public int qty = 0;
         public double cost = 0;

         public override int Qty { get { return qty; } }
         public override double Cost { get { return cost; } }
      }
   }

   class VandRestock : BaseDocument
   {
      public static readonly String OBJECT_NAME = "Restock";

      [ItemType(typeof(Item))]
      public List<Item> items = null;

      internal override Org Org { get { return Org.Empty; } }

      public class Item : ItemBase
      {
         public int qty = 0;
         public override int Qty { get { return qty; } }
      }
   }

   class DefectReportInfo : BaseDocument
   {
      public static readonly string OBJECT_NAME = "DefectReportInfo";

      public string device = string.Empty;
   }

   class DefectReport : DefectReportInfo
   {
      public new static readonly string OBJECT_NAME = "DefectReport";

      public class DefectReportItem : GRSoft.Network.DataObject
      {
         public byte[] id = null;
         public int rating = 0;
         public string caption = string.Empty;
      }

      [ItemType(typeof(DefectReportItem))]
      public List<DefectReportItem> items = null;
   }
}
