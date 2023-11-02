using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.UILib;
using GRSoft.NapoleonManager.Utils;
using System.IO;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Threading;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmPricePhoto : Form
   {
      protected DataSet<string, Price> dsCommonPrice;
      protected DataSet<string, ManagerFolder> dsCommonFolder;
      private DataSet<string, PricePhoto> dsPhotos;
      private DataSet<string, Price> dsOldPrice = new DataSet<string, Price>("OldManagerPrice");
      private SimpleDataSet<Distributor> distribs = new SimpleDataSet<Distributor>(Distributor.OBJECT_NAME);

      Color folderBackColor = Color.LightGray;

      Image emptyImage, emptyFolder;
      // price.id, image.tag
      Dictionary<String, String> pictMap = new Dictionary<string, string>();
      // image.tag realsize image
      Dictionary<String, byte[]> largeImages = new Dictionary<string, byte[]>();
      Dictionary<String, Image> pictures = new Dictionary<string, Image>();

      TreeGridNode[] priceNodes;

      private bool expanded = false;
      bool clearing = false, inPriceMode = true;
      private SettingFmPricePhoto setting = null;

      public FmPricePhoto()
      {
         InitializeComponent();

         cbSizes.Items.Add(new TSize(240, 320));
         cbSizes.Items.Add(new TSize(320, 480));
         cbSizes.Items.Add(new TSize(800, 600));
         cbSizes.SelectedIndex = 1;

         dsCommonPrice = new DataSet<string, Price>("Price", false);
         dsCommonFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);

         dsPhotos = (DataSet<string, PricePhoto>)DataModule.Get(PricePhoto.OBJECT_NAME)
            ?? new DataSet<string, PricePhoto>(PricePhoto.OBJECT_NAME);

         CreateEmptyBitmap();

      }

      private void CreateEmptyBitmap()
      {
         int hgh = 200;
         int wdh = 200;
         Bitmap b = new Bitmap(wdh, hgh);
         using (Graphics G = Graphics.FromImage(b))
         {
            Brush brsh = new SolidBrush(tgvPrice.DefaultCellStyle.BackColor);
            G.FillRectangle(brsh, 0, 0, wdh, hgh);
            brsh.Dispose();
         }
         emptyImage = b;

         b = new Bitmap(wdh, hgh);
         using (Graphics G = Graphics.FromImage(b))
         {
            Brush brsh = new SolidBrush(folderBackColor);
            G.FillRectangle(brsh, 0, 0, wdh, hgh);
            brsh.Dispose();
         }
         emptyFolder = b;

         clmnPhoto.DefaultCellStyle.NullValue = emptyFolder;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
         setting = BaseFormSetting<SettingFmPricePhoto>.Load();
         SetPictureSz();
      }

      void RefreshData()
      {
         List<IDataSet> updSet = new List<IDataSet>();

         if (dsCommonFolder.Count == 0)
         {
            dsCommonFolder.Filter = "\"userid\" is null or \"userid\"=''";
            updSet.Add(dsCommonFolder);
         }

         if (dsCommonPrice.Count == 0)
         {
            //dsCommonPrice.Filter = "\"userid\" is null or \"userid\"=''";
            updSet.Add(dsCommonPrice);
         }

         updSet.Add(dsPhotos);
         updSet.Add(distribs);
         FmWait.StdDataRefresh(this, updSet, DoLoadData);
      }

      private void SetPictureSz()
      {
         Size sz = new Size(setting.szX, setting.szY);
         tgvPrice.RowTemplate.MinimumHeight = setting.szY;
         tgvPrice.RowTemplate.Height = setting.szY;
         clmnPhoto.Width = setting.szX;
      }

      void DoLoadData()
      {
         btnSave.Enabled = false;

         LoadDistr();
         LoadPic();
      }

      private void LoadDistr()
      {
         cbDistr.Items.Clear();
         foreach (Distributor d in distribs.Values)
         {
            cbDistr.Items.Add(d);
         }

         if (cbDistr.Items.Count > 0)
         {
            cbDistr.Sorted = true;
            cbDistr.SelectedIndex = 0;
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
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
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      //Загрузить фотографии
      private void LoadPic()
      {
         pictMap.Clear();
         pictures.Clear();
         largeImages.Clear();

         Size sz = new Size(setting.szX, setting.szY);
         foreach(PricePhoto pp in dsPhotos.Data)
         {
            if (pp.photo != null && pp.photo.Length > 0)
            {
               //Image image = null;

               //using (Stream stream = new MemoryStream(pp.photo))
               //  image = new Bitmap(stream);

               //if (image != null)
               {
                  largeImages.Add(pp.Name, pp.photo);

                  //pictures.Add(pp.Name, ScaleImage(image, sz));
                  foreach (PhotoPriceItem pi in pp.items)
                     if (!pictMap.ContainsKey(pi.id))
                        pictMap.Add(pi.id, pp.Name);
               }
            }
         }
      }

      virtual protected TreeGridNode AddPriceNode(TreeGridNodeCollection parent, Price p)
      {
         PriceRowData prd = new PriceRowData(p, this);
         TreeGridNode result = parent.AddDataItem(prd);
         result.Height = setting.szY;
         return result;
      }

      virtual protected TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      {
         FolderRowData frd = new FolderRowData(f, this);
         TreeGridNode result = parent.AddDataItem(frd);
         result.DefaultCellStyle.BackColor = folderBackColor;

         for (int i = 0; i < result.Cells.Count; i++ )
         {
            result.Cells[FPCost.DisplayIndex].ReadOnly = i != FPName.DisplayIndex;
         }

         return result;
      }

      private void fillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      {
         if (node.Tag is ManagerFolder)
         {
            TreeGridNode child = AddFolderNode(parent, (ManagerFolder)node.Tag);

            List<TreeNode> pnodes = new List<TreeNode>();
            foreach (TreeNode n in node.Nodes)
            {
               if (n.Tag is ManagerFolder)
                  fillGridRecursive(n, child.Nodes);
               else
                  pnodes.Add(n);
            }
            pnodes.ForEach(x => fillGridRecursive(x, child.Nodes));
         }
         else if (node.Tag is Price)
         {
            Price p = (Price)node.Tag;
            AddPriceNode(parent, p).Tag = p;
         }
      }

      //Построить дерево для прайса
      protected virtual void CreatePriceTree()
      {
         TreeView tmpTree = new TreeView();
         ArticlesTreeConstructor treeCnt = new ArticlesTreeConstructor(tmpTree, dsCommonFolder, dsCommonPrice);
         treeCnt.MakeArticlesTree();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         foreach(TreeNode n in tmpTree.Nodes)
            fillGridRecursive(n, tgvPrice.Nodes);

         priceNodes = new TreeGridNode[tgvPrice.Nodes.Count];
         tgvPrice.Nodes.CopyTo(priceNodes, 0);

         tgvPrice.ResumeLayout();
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

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind(this, EventArgs.Empty);
      }

      private void ClearFind(object sender, EventArgs e)
      {
         clearing = true;
         tbFind.Clear();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         if (priceNodes != null)
            foreach (TreeGridNode tn in priceNodes)
               tgvPrice.Nodes.Add(tn);

         tgvPrice.ResumeLayout();
         clearing = false;
         inPriceMode = true;
      }

      void TestNode(TreeGridNode node, FindProc testProc)
      {
         PriceRowData prd = node.DataItem as PriceRowData;
         if (prd != null && testProc(prd.Data))
         {
            TreeGridNode newNode = new TreeGridNode();
            tgvPrice.Nodes.Add(newNode);
            newNode.DataItem = prd;

            newNode.Height = node.Height;
            newNode.DefaultCellStyle = node.DefaultCellStyle;
         }
      }

      void SearchingNode(TreeGridNode node, FindProc testProc)
      {
         TestNode(node, testProc);

         foreach (TreeGridNode child in node.Nodes)
         {
            TestNode(child, testProc);

            if (child.HasChildren)
               SearchingNode(child, testProc);
         }
      }

      void DoSearchPrice(FindProc testProc)
      {
         if (priceNodes == null)
            return;

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         foreach (TreeGridNode tn in priceNodes)
            SearchingNode(tn, testProc);

         tgvPrice.ResumeLayout();
      }

      bool NotHavePicture(Price p)
      {
         return !pictMap.ContainsKey(p.id);
      }

      bool ContainsText(Price p)
      {
         return p.Name.ToUpper().Contains(tbFind.Text.ToUpper());
      }

      private void ChangePictureMode(object sender, EventArgs e)
      {
         if (!inPriceMode)
            ClearFind(this, EventArgs.Empty);
         else
         {
            DoSearchPrice(NotHavePicture);
            inPriceMode = false;
         }
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearchPrice(ContainsText);
      }

      //Раскрыть дерево прайса
      private void ExpandPriceNodes()
      {
         if (tgvPrice.Nodes.Count > 0)
         {
            tgvPrice.SuspendLayout();

            foreach (TreeGridNode node in tgvPrice.Nodes)
               if (expanded)
                  node.Collapse();
               else
                  node.Expand();

            tgvPrice.ResumeLayout();
            expanded = !expanded;
         }
      }

      private void tgvPrice_MouseUp(object sender, MouseEventArgs e)
      {
         //Price p = GetSelectedPrice();

         //if (p != null)
         //{
         //}
      }

      //Возвращает выбранный прайс в дереве или null исли выбрана
      //папка
      private Price GetSelectedPrice()
      {
         TreeGridNode n = tgvPrice.CurrentNode;
         if (n == null)
            return null;

         PriceRowData prd = n.DataItem as PriceRowData;
         return prd == null ? null : prd.Data;
      }

      public static Image ScaleImage(Image src, Size size)
      {
         if (src == null)
            return null;

         int sourceWidth = src.Width;
         int sourceHeight = src.Height;

         float nPercent = 0;
         float nPercentW = 0;
         float nPercentH = 0;

         //float width = Math.Max((float)size.Width, (float)size.Height);
         float width = Math.Min((float)size.Width, (float)size.Height);
         nPercentW = (width / (float)sourceWidth);
         nPercentH = (width / (float)sourceHeight);

         if (nPercentH < nPercentW)
            nPercent = nPercentH;
         else
            nPercent = nPercentW;

         int destWidth = (int)(sourceWidth * nPercent);
         int destHeight = (int)(sourceHeight * nPercent);

         Bitmap b = new Bitmap(destWidth, destHeight);
         Graphics g = Graphics.FromImage((Image)b);
         g.InterpolationMode = InterpolationMode.HighQualityBicubic;

         g.DrawImage(src, 0, 0, destWidth, destHeight);
         g.Dispose();

         return (Image)b;
      }

      //Изменить размер фотографии
      private static Image LoadImage(String fileName, Size size)
      {
         Stream stream = new FileStream(fileName, FileMode.Open, FileAccess.Read); ;
         Image imgToResize;
         using (stream)
            imgToResize = new Bitmap(stream);

         return ScaleImage(imgToResize, size);
      }


      //Возвращает размер картинки на диске в байтах
      private long getImageSize(Image image)
      {
         MemoryStream stream = new MemoryStream();
         long result = 0;
         using (stream)
         {
            image.Save(stream, ImageFormat.Jpeg);
            result = stream.Length;
         }

         return result;
      }

      bool SaveChanges(bool showDialog)
      {
         tgvPrice.CommitEdit(DataGridViewDataErrorContexts.Commit);
         List<ReplacedSet> rpcSet = new List<ReplacedSet>();

         DataSet<String, PricePhoto> wr = new DataSet<String, PricePhoto>(PricePhoto.OBJECT_NAME, false);
         foreach (KeyValuePair<String, String> kv in pictMap)
         {
            if (!largeImages.ContainsKey(kv.Value))
               continue;

            if (wr.ContainsKey(kv.Value) == false)
            {
               PricePhoto pp = new PricePhoto();

               pp.photo = largeImages[kv.Value];
               pp.Name = kv.Value;
               pp.items = new List<PhotoPriceItem>();

               wr.Add(kv.Value, pp);
            }
            wr[kv.Value].items.Add(new PhotoPriceItem(kv.Key));
         }
         rpcSet.Add(new ReplacedSet(wr));

         SimpleDataSet<ManagerFolder> wrFolders = new SimpleDataSet<ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         PutFolders(wrFolders, tgvPrice.Nodes, 0);
         rpcSet.Add(new ReplacedSet(null, wrFolders));
         rpcSet.Add(new ReplacedSet(dsCommonPrice));

         bool result = DataModule.UpdateDataSet(null, null, rpcSet, Config.GetConfig().GetConnection());
         if (!result && showDialog)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);

         return result;
      }

      class SrcData : IComparable<SrcData>
      {
         public ManagerFolder folder;
         public TreeGridNodeCollection nodes;

         public SrcData(TreeGridNode node, ManagerFolder folder)
         {
            this.folder = folder;
            this.nodes = node.Nodes;
         }

         public int CompareTo(SrcData other)
         {
            return folder.name.CompareTo(other.folder.name);
         }
      }

      private void PutFolders(SimpleDataSet<ManagerFolder> wrFolders, TreeGridNodeCollection nodes, int level)
      {
         List<SrcData> src = new List<SrcData>();
         foreach (TreeGridNode tn in nodes)
         {
            FolderRowData frd = tn.DataItem as FolderRowData;
            if (frd != null)
            {
               ManagerFolder mf = frd.Data;
               if (mf.name.Trim().Length == 0 && tn.Nodes.Count == 0)
                  continue;

               src.Add(new SrcData(tn, mf));
               frd.Data.level = level;
            }
         }

         src.Sort();
         foreach (SrcData tn in src)
         {
            wrFolders.Add(tn.folder);
            PutFolders(wrFolders, tn.nodes, level + 1);
         }
      }

      //Сохранить
      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void tgvPrice_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = tgvPrice.HitTest(e.X, e.Y);

         if (info.ColumnIndex == -1 || info.RowIndex == -1 || e.Button != MouseButtons.Right)
            return;

         tgvPrice.CurrentCell = tgvPrice[info.ColumnIndex, info.RowIndex];

         TreeGridNode tn = (TreeGridNode)tgvPrice.Rows[info.RowIndex];

         PriceRowData prd = tn.DataItem as PriceRowData;
         if (prd != null)
         {
            Price price = prd.Data;
            if (pictMap.ContainsKey(price.id))
            {
               mnuChangePic.Text = "Изменить фото";
               mnuDelPic.Enabled = true;
            }
            else
            {
               mnuChangePic.Text = "Добавить фото";
               mnuDelPic.Enabled = false;
            }
            menuPic.Show(PointToScreen(new Point(e.X, e.Y)));
         }
         else
         {
            if (tgvPrice.CurrentCell.IsInEditMode)
               return;
            menuFolder.Show(PointToScreen(new Point(e.X, e.Y)));
         }
      }

      private void miSetting_Click(object sender, EventArgs e)
      {
         FmPriceSetting.ShowInstance(setting);
         setting.Save();
         SetPictureSz();
         LoadPic();
      }

      private void tsbOldPrice_Click(object sender, EventArgs e)
      {
         Type prcType = FormEntries.GetFormType(typeof(FmPrice));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();

         Close();
      }

      private void mnuDelPic_Click(object sender, EventArgs e)
      {
         Price price = GetSelectedPrice();
         if (price != null)
         {
            pictMap.Remove(price.id);
            tgvPrice.InvalidateRow(tgvPrice.CurrentCell.RowIndex);
            btnSave.Enabled = true;
         }
      }

      private void mnuChangePic_Click(object sender, EventArgs e)
      {
         Price price = GetSelectedPrice();
         if (price != null && dialog.ShowDialog() == DialogResult.OK)
         {
            FileInfo fi = new FileInfo(dialog.FileName);
            String tag = Path.GetFileName(fi.Name).Replace('.','_') + "_" + fi.Length.ToString() + ".jpg";

            // здесь неявное преобразование (добавляем path\)
            PricePhoto pp = new PricePhoto();
            pp.Name = tag;
            if (!largeImages.ContainsKey(pp.Name))
            {
               Image img = LoadImage(dialog.FileName, ((TSize)cbSizes.SelectedItem).size);

               MemoryStream strm = new MemoryStream();
               img.Save(strm, ImageFormat.Jpeg);
               largeImages.Add(pp.Name, strm.ToArray());

               Size sz = new Size(setting.szX, setting.szY);
               pictures.Add(pp.Name, ScaleImage(img, sz));
            }

            pictMap[price.id] = pp.Name;
            tgvPrice.InvalidateRow(tgvPrice.CurrentNode.RowIndex);

            btnSave.Enabled = true;
         }
      }

      private void tgvPrice_CellPainting(object sender, DataGridViewCellPaintingEventArgs e)
      {
         if (e.ColumnIndex == clmnPhoto.DisplayIndex && e.RowIndex >= 0)
         {
            PriceRowData prd = ((TreeGridNode)tgvPrice.Rows[e.RowIndex]).DataItem as PriceRowData;
            if (prd != null)
            {
               Price p = prd.Data;
               e.PaintBackground(e.CellBounds, (e.State & DataGridViewElementStates.Selected) != 0);
               
               Image img = emptyImage;
               if (pictMap.ContainsKey(p.id))
               {
                  String name = pictMap[p.id];
                  if( !pictures.ContainsKey(name) )
                  {
                     Image image = null;

                     using (Stream stream = new MemoryStream(largeImages[name]))
                       image = new Bitmap(stream);
                     image = ScaleImage(image, new Size(setting.szX, setting.szY));

                     pictures[name] = (image != null) ? image : emptyImage;
                  }
                  img = pictures[name];
               }
               Point pt = new Point(e.CellBounds.Left, e.CellBounds.Top);
               int offset = (e.CellBounds.Width - img.Size.Width);
               if (offset > 2)
                  pt.X += offset / 2;
               offset = (e.CellBounds.Height - img.Size.Height);
               if (offset > 2)
                  pt.Y += offset / 2;

               e.Graphics.SetClip(e.CellBounds);
               e.Graphics.DrawImage(img, pt);
               e.Graphics.ResetClip();
               using (Pen linePen = new Pen(SystemBrushes.ControlDark, 1.0f))
               {
                  //e.Graphics.DrawRectangle(linePen, e.CellBounds);
                  int bottom = e.CellBounds.Bottom - 1;
                  int right = e.CellBounds.Right - 1;
                  e.Graphics.DrawLine(linePen, e.CellBounds.Left, bottom, e.CellBounds.Right, bottom);
                  e.Graphics.DrawLine(linePen, right, e.CellBounds.Top, right, e.CellBounds.Bottom);
               }
               e.Handled = true;
            }
         }
      }

      public void SetDirty(bool dirty)
      {
         btnSave.Enabled = dirty;
      }

      class PriceRowData
      {
         Price price;
         FmPricePhoto owner;

         public PriceRowData(Price p, FmPricePhoto owner)
         {
            this.price = p;
            this.owner = owner;
         }

         public string Name
         {
            get { return price.name; }
            set
            {
               price.name = value;
               owner.SetDirty(true);
            }
         }

         Price.PriceItem GetCurItem()
         {
            Price.PriceItem result = null;

            Distributor d = owner.cbDistr.SelectedItem as Distributor;

            if (d != null)
            {
               bool founded = false;
               foreach (Price.PriceItem pi in price.items)
               {
                  if (pi.id.Equals(d.id))
                  {
                     result = pi;
                     founded = true;
                     break;
                  }
               }

               if (!founded)
               {
                  Price.PriceItem i = new Price.PriceItem();
                  i.id = d.id;

                  price.items.Add(i);
               }
            }

            return result;
         }

         public string Cost
         {
            get
            {
               double result = 0.0;

               Price.PriceItem i = GetCurItem();

               if (i != null)
                  result = i.cost;
               

               return result.ToString("N2");
            }

            set
            {
               double val = 0;
               if (Double.TryParse(value, out val))
               {
                  Price.PriceItem i = GetCurItem();

                  if(i != null)
                  {
                     i.cost = val;
                     owner.SetDirty(true);
                  }
               }
            }
         }

         public string Qty
         {
            get 
            {
               double result = 0.0;

               Price.PriceItem i = GetCurItem();

               if (i != null)
                  result = i.qty;


               return result.ToString();
            }
            set
            {
               double val = 0;
               if (Double.TryParse(value, out val))
               {
                  Price.PriceItem i = GetCurItem();

                  if (i != null)
                  {
                     i.qty = val;
                     owner.SetDirty(true);
                  }
               }
            }
         }

         public string InPack
         {
            get { return price.inPack.ToString(); }
            set
            {
               double val = 0;
               Double.TryParse(value, out val);
               price.inPack = val;
               owner.SetDirty(true);
            }
         }


         public Price Data { get { return price; } }
      }

      class FolderRowData
      {
         ManagerFolder folder;
         FmPricePhoto owner;

         public FolderRowData(ManagerFolder f, FmPricePhoto owner)
         {
            this.folder = f;
            this.owner = owner;
         }

         public string Name
         {
            get { return folder.name; }
            set
            {
               folder.name = value;
               owner.SetDirty(true);
            }
         }

         public string Cost { get { return ""; } }
         public string Qty { get { return ""; } }
         public string InPack { get { return ""; } }

         public ManagerFolder Data { get { return folder; } }
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         if (tgvPrice.CurrentRow == null)
            return;

         TreeGridNode parent = tgvPrice.CurrentRow;
         if (parent.Nodes == tgvPrice.Nodes)
            return;

         FolderRowData frd = parent.DataItem as FolderRowData;
         if (frd == null)
         {
            parent = parent.Parent;
            frd = parent.DataItem as FolderRowData;
            if(frd == null)
               return;
         }

         Price p = new Price();
         p.id = Guid.NewGuid().ToString().Replace("-","");
         p.fid = frd.Data.id;
         dsCommonPrice[p.id] = p;

         TreeGridNode node = AddPriceNode(parent.Nodes, p);

         if (!parent.IsExpanded)
            parent.Expand();

         tgvPrice.CurrentCell = node.Cells[FPName.DisplayIndex];
         tgvPrice.BeginEdit(true);

         btnSave.Enabled = true;
      }

      private void tsbAddFolder_Click(object sender, EventArgs e)
      {
         TreeGridNodeCollection parent;
         if (tgvPrice.CurrentRow == null)
            parent = tgvPrice.Nodes;
         else
         {
            if (tgvPrice.CurrentRow.Parent.IsExpanded == false)
               tgvPrice.CurrentRow.Parent.Expand();
            parent = tgvPrice.CurrentRow.Parent.Nodes;
            //FolderRowData frd = tgvPrice.CurrentRow.DataItem as FolderRowData;
            //if (frd == null)
            //   parent = tgvPrice.CurrentRow.Parent.Nodes;            
         }
         AddFolder(parent);
      }

      private void AddFolder(TreeGridNodeCollection parent)
      {
         ManagerFolder mf = new ManagerFolder();
         mf.id = Guid.NewGuid().ToString().Replace("-", "");
         dsCommonFolder[mf.id] = mf;

         TreeGridNode tn = AddFolderNode(parent, mf);

         tgvPrice.CurrentCell = tn.Cells[FPName.DisplayIndex];
         tgvPrice.BeginEdit(true);

         btnSave.Enabled = true;
      }

      void RemoveFolder(TreeGridNode curRow)
      {
         FolderRowData frd = curRow.DataItem as FolderRowData;
         ManagerFolder mf = frd.Data;

         bool deleteFolder = false, deleteContent = false;

         if (curRow.Parent == null || curRow.Parent.Nodes == tgvPrice.Nodes)
         {
            string text = "Удалить папку вместе товарами?";
            DialogResult dr = MessageBox.Show(text, "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            deleteContent = (dr == System.Windows.Forms.DialogResult.Yes);
         }
         else
         {
            string text = "Удалить папку вместе товарами или только папку?\n" + "Да - удалится папка и товары в ней\n" +
               "Нет - удалится папка, а товары перенесутся в папку выше\n" + "Отмена - отмена операции";

            DialogResult dr = MessageBox.Show(text, "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            deleteFolder = (dr == System.Windows.Forms.DialogResult.No);
            deleteContent = (dr == System.Windows.Forms.DialogResult.Yes);
         }

         if (deleteContent)
         {
            curRow.Parent.Nodes.Remove(curRow);
            DeleteTree(curRow);
            btnSave.Enabled = true;
         }
         else if (deleteFolder)
         {
            tgvPrice.SuspendLayout();

            TreeGridNode parentNode = curRow.Parent;

            FolderRowData parent = parentNode.DataItem as FolderRowData;
            curRow.Parent.Nodes.Remove(curRow);
            dsCommonFolder.Remove(frd.Data.id);

            foreach (TreeGridNode tn in curRow.Nodes)
            {
               tn.ResetLevel();
               parentNode.Nodes.Add(tn);

               PriceRowData data = tn.DataItem as PriceRowData;
               if (data != null)
                  data.Data.fid = parent.Data.id;
            }

            tgvPrice.ResumeLayout();
            btnSave.Enabled = true;
         }
      }

      private void tsbDel_Click(object sender, EventArgs e)
      {
         TreeGridNode curRow = tgvPrice.CurrentRow;
         if (curRow == null)
            return;

         PriceRowData prd = curRow.DataItem as PriceRowData;
         if( prd != null )
         {
            string text = "Удалить товар " + prd.Data.Name + "?";
            if (MessageBox.Show(text, "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
            {
               dsCommonPrice.Remove(prd.Data.id);

               if (curRow.Parent != null)
                  curRow.Parent.Nodes.Remove(curRow);
               btnSave.Enabled = true;
            }
         } else
         {
            RemoveFolder(curRow);
         }
      }

      private void DeleteTree(TreeGridNode curRow)
      {
         FolderRowData frd = curRow.DataItem as FolderRowData;
         if (frd == null)
            return;

         dsCommonFolder.Remove(frd.Data.id);

         foreach(TreeGridNode ch in curRow.Nodes)
         {
            object data = ch.DataItem;
            PriceRowData prd = data as PriceRowData;
            if( prd != null )
            {
               dsCommonPrice.Remove(prd.Data.id);
            } else
            {
               DeleteTree(ch);
            }
         }
      }

      private void msAddFolder_Click(object sender, EventArgs e)
      {
         if (tgvPrice.CurrentRow == null)
            return;
         if (tgvPrice.CurrentRow.IsExpanded == false)
            tgvPrice.CurrentRow.Expand();
         AddFolder(tgvPrice.CurrentRow.Nodes);
      }

      private void msAddItem_Click(object sender, EventArgs e)
      {
         tsbAdd_Click(sender, e);
      }

      private void msDelFolder_Click(object sender, EventArgs e)
      {
         TreeGridNode curRow = tgvPrice.CurrentRow;
         if (curRow == null)
            return;
         RemoveFolder(curRow);
      }

      private void btnLoadOldPrice_Click(object sender, EventArgs e)
      {
         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsOldPrice);
         FmWait.StdDataRefresh(this, updSet, DoLoadOldPrice);
      }

      private void DoLoadOldPrice()
      {
         bool added = false;
         foreach (Price p in dsOldPrice.Values)
         { 
            if(!dsCommonPrice.ContainsKey(p.id))
            {
               dsCommonPrice[p.id] = p;

               if(!added)
                  added = true;
            }
         }

         if(added)
         {
            CreatePriceTree();
            btnSave.Enabled = true;
         }
      }

      private void cbDistr_SelectedIndexChanged(object sender, EventArgs e)
      {
         CreatePriceTree();
      }
   }

   [Serializable]
   public class SettingFmPricePhoto : SettingFmPrice
   {
      public SettingFmPricePhoto()
      {
         szX = 55;
         szY = 55;
      }
   }

  delegate bool FindProc(Price p);
}
