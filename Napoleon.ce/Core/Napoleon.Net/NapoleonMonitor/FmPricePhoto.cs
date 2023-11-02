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
      int ImageCellIndex;
      protected DataSet<string, Price> dsPrice, dsCommonPrice;
      protected DataSet<string, ManagerFolder> dsFolder, dsCommonFolder;
      private DataSet<string, Agent> dsAgent;
      private DataSet<string, PricePhoto> dsPhotos;

      Color folderBackColor = Color.LightGray;

      Image emptyImage, emptyFolder;
      // price.id, image.tag
      Dictionary<String, String> pictMap = new Dictionary<string, string>();
      // image.tag realsize image
      Dictionary<String, byte[]> largeImages = new Dictionary<string, byte[]>();
      Dictionary<String, Image> pictures = new Dictionary<string, Image>();

      TreeGridNode[] priceNodes;

      private bool agentsLoading = true;
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

         dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         
         dsCommonPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         dsCommonFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);

         dsAgent = (DataSet<string, Agent>)DataModule.Get(Agent.OBJECT_NAME) 
            ?? new DataSet<string, Agent>(Agent.OBJECT_NAME);
         dsPhotos = (DataSet<string, PricePhoto>)DataModule.Get(PricePhoto.OBJECT_NAME)
            ?? new DataSet<string, PricePhoto>(PricePhoto.OBJECT_NAME);

         CreateEmptyBitmap();

#if TcarGrad
         cbAgents.Visible = false;
#endif
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
      }

      private void FmPrice_Load(object sender, EventArgs e)
      {
         FillAgents();
         setting = BaseFormSetting<SettingFmPricePhoto>.Load();
         SetPictureSz();
      }

      private void SetPictureSz()
      {
         Size sz = new Size(setting.szX, setting.szY);
         tgvPrice.RowTemplate.MinimumHeight = setting.szY;
         tgvPrice.RowTemplate.Height = setting.szY;
         clmnPhoto.Width = setting.szX;
      }

      private void FillAgents()
      {
         cbAgents.Items.Clear();
         List<Agent> list = new List<Agent>();

         foreach (Agent a in DataModule.Get(Agent.OBJECT_NAME).Data)
            list.Add(a);

         if (list.Count > 0)
         {
            list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
         }

         cbAgents.Items.Add("Общий прайс");
         cbAgents.Items.AddRange(list.ToArray());
         cbAgents.SelectedIndex = 0;

         expanded = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSet = new List<IDataSet>();

         Agent agent = cbAgents.SelectedItem as Agent;
         if (dsCommonFolder.Count == 0)
         {
            dsCommonFolder.Filter = "\"userid\" is null or \"userid\"=''";
            updSet.Add(dsCommonFolder);
         }

         if (dsCommonPrice.Count == 0)
         {
            dsCommonPrice.Filter = "\"userid\" is null or \"userid\"=''";
            updSet.Add(dsCommonPrice);
         }
         if (agent != null)
         {
            String filter = String.Format("\"userid\" in ('{0}')", agent.id); ;
            dsPrice.Filter = filter;
            dsFolder.Filter = filter;

            updSet.Add(dsPrice);
            updSet.Add(dsFolder);
         }

         if (dsAgent.Count == 0)
         {
            updSet.Add(dsAgent);
            agentsLoading = true;
         }

         updSet.Add(dsPhotos);

         BeforeRefresh(updSet);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator));
      }

      protected virtual void BeforeRefresh(List<IDataSet> updSet)
      {
      }

      protected virtual void BeforeProceeded()
      {
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         if (dsFolder.Count == 0)
            foreach (KeyValuePair<string, ManagerFolder> f in dsCommonFolder)
               dsFolder.Add(f.Key, f.Value);
         if (dsPrice.Count == 0)
            foreach (KeyValuePair<string, Price> f in dsCommonPrice)
               dsPrice.Add(f.Key, f.Value);

         Invoke(new EmptyParamHandler(delegate() 
            {
               BeforeProceeded();

               if (agentsLoading)
               {
                  FillAgents();
                  agentsLoading = false;
               }

               LoadPic();
               CreatePriceTree();
            }));
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
         double cost = p.cost != null && p.cost.Length > 0 ? p.cost[0] : 0.0;
//         Image img = ( pictMap.ContainsKey(p.id) ) ? pictures[pictMap[p.id]] : emptyImage;
         Image img = emptyImage;
         
         // Нужно для установки новой картинки
         ImageCellIndex = 3;
         TreeGridNode result = parent.Add(p.name, cost, p.qty, img);
         result.Tag = p;
         result.Height = setting.szY;
         return result;
      }

      virtual protected TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      {
         TreeGridNode result = parent.Add(dsFolder[f.id].name, null, null, emptyFolder);
         result.Tag = f;
         result.DefaultCellStyle.BackColor = folderBackColor;
         return result;
      }

      private void fillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      {
         if (node.Tag is ManagerFolder)
         {
            TreeGridNode child = AddFolderNode(parent, (ManagerFolder)node.Tag);

            foreach (TreeNode n in node.Nodes)
               fillGridRecursive(n, child.Nodes);
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
         ArticlesTreeConstructor treeCnt = new ArticlesTreeConstructor(tmpTree, dsFolder, dsPrice);
         treeCnt.MakeArticlesTree();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         BeforeCreatePriceTree();

         foreach(TreeNode n in tmpTree.Nodes)
            fillGridRecursive(n, tgvPrice.Nodes);

         priceNodes = new TreeGridNode[tgvPrice.Nodes.Count];
         tgvPrice.Nodes.CopyTo(priceNodes, 0);
         AfterCreatePriceTree();

         tgvPrice.ResumeLayout();
      }

      protected virtual void BeforeCreatePriceTree()
      { 
      }

      protected virtual void AfterCreatePriceTree()
      {
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
         Price p = node.Tag as Price;
         if (p != null && testProc(p))
         {
            TreeGridNode newNode = new TreeGridNode();
            tgvPrice.Nodes.Add(newNode);

            newNode.Tag = node.Tag;
            newNode.Height = node.Height;
            newNode.DefaultCellStyle = node.DefaultCellStyle;

            int pos = 0;
            foreach (DataGridViewCell cell in node.Cells)
            {
               DataGridViewCell dest = newNode.Cells[pos++];
               dest.Value = cell.Value;
               dest.Style.BackColor = cell.Style.BackColor;
            }
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
         Price p = GetSelectedPrice();

         if (p != null)
         {
         }
      }

      //Возвращает выбранный прайс в дереве или null исли выбрана
      //папка
      private Price GetSelectedPrice()
      {
         TreeGridNode n = tgvPrice.CurrentNode;
         if (n == null)
            return null;

         Price p = n.Tag as Price;
         return p;
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

      //Сохранить
      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();
         List<ReplacedSet> rpcSet = new List<ReplacedSet>();

         BeforeWrite(wrSet, rmvSet, rpcSet);

         DataSet<String, PricePhoto> wr = new DataSet<String, PricePhoto>(PricePhoto.OBJECT_NAME, false);
         foreach (KeyValuePair<String, String> kv in pictMap)
         {
            if( !largeImages.ContainsKey(kv.Value) )
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
         ReplacedSet rs = new ReplacedSet(wr);
         rpcSet.Add(rs);

         bool result = DataModule.UpdateDataSet(wrSet, rmvSet, rpcSet, Config.GetConfig().GetConnection());
         if (!result)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         else
            btnSave.Enabled = false;

         AfterWrite(result, wrSet, rmvSet, rpcSet);
      }

      virtual protected void BeforeWrite(List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      {
      }

      virtual protected void AfterWrite(Boolean result, List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      { 
      }

      //Прорисовка прайса
      protected void tgvPrice_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         TreeGridNode node = (TreeGridNode)tgvPrice.Rows[e.RowIndex];
         Price p = node.Tag as Price;
         
         //if (p != null && lvPic.SelectedItems.Count > 0)
         //{
         //   PricePhoto pp = lvPic.SelectedItems[0].Tag as PricePhoto;

         //   if(pp != null && pp.items != null) 
         //      foreach(PhotoPriceItem ppi in pp.items)
         //         if (ppi.id.Equals(p.id))
         //         {
         //            e.CellStyle.BackColor = Color.LightBlue;
         //            return;
         //         }
         //}

         //e.CellStyle.BackColor = Color.White;
      }

      private void tgvPrice_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = tgvPrice.HitTest(e.X, e.Y);

         if (info.ColumnIndex == -1 || info.RowIndex == -1 || e.Button != MouseButtons.Right)
            return;

         tgvPrice.CurrentCell = tgvPrice[info.ColumnIndex, info.RowIndex];
         Price price = GetSelectedPrice();

         if (price != null)
         {
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
            tgvPrice.CurrentNode.Cells[ImageCellIndex].Value = emptyImage;
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
            tgvPrice.CurrentNode.Cells[ImageCellIndex].Value = pictures[pp.Name];
            tgvPrice.InvalidateRow(tgvPrice.CurrentNode.RowIndex);

            btnSave.Enabled = true;
         }
      }

      private void tgvPrice_CellPainting(object sender, DataGridViewCellPaintingEventArgs e)
      {
         if (e.ColumnIndex == ImageCellIndex && e.RowIndex >= 0)
         {
            Price p = tgvPrice.Rows[e.RowIndex].Tag as Price;
            if (p != null)
            {
               e.PaintBackground(e.ClipBounds, (e.State & DataGridViewElementStates.Selected) != 0);
               
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
               e.Graphics.DrawImage(img, pt);
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
