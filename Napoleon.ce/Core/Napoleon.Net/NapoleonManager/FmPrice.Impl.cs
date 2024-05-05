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
   public partial class FmPrice : Form
   {
      protected DataSet<string, Price> dsPrice, dsCommonPrice;
      protected DataSet<string, ManagerFolder> dsFolder, dsCommonFolder;
      private Agents dsAgent;
      protected DataSet<string, PricePhoto> dsPhotos;
      protected DataSet<string, PricePhoto> dsNewPhotos;
      protected DataSet<string, PricePhoto> dsDelPhotos;

      protected bool agentsLoading = true;
      protected bool expanded = false;
      private SettingFmPrice setting = null;

      public void __Initing()
      {
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         this.btnDown.Click += new System.EventHandler(this.btnDown_Click);
         this.btnUp.Click += new System.EventHandler(this.btnUp_Click);
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         this.tsbNewPrice.Click += new System.EventHandler(this.tsbNewPrice_Click);
         this.dgvItems.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvItems_DragDrop);
         this.dgvItems.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvItems_DragEnter);
         this.mnuDelItem.Click += new System.EventHandler(this.mnuDelItem_Click);
         this.lvPic.SelectedIndexChanged += new System.EventHandler(this.lvPic_SelectedIndexChanged);
         this.lvPic.DragDrop += new System.Windows.Forms.DragEventHandler(this.lvPic_DragDrop);
         this.lvPic.DragEnter += new System.Windows.Forms.DragEventHandler(this.lvPic_DragEnter);
         this.lvPic.DragOver += new System.Windows.Forms.DragEventHandler(this.lvPic_DragOver);
         this.lvPic.DoubleClick += new System.EventHandler(this.lvPic_DoubleClick);
         this.mnuDelPic.Click += new System.EventHandler(this.mnuDelPic_Click);
         this.miSetting.Click += new System.EventHandler(this.miSetting_Click);
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         this.tgvPrice.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.tgvPrice_CellFormatting);
         this.tgvPrice.DoubleClick += new System.EventHandler(this.tgvPrice_DoubleClick);
         this.tgvPrice.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tgvPrice_MouseDown);
         this.tgvPrice.MouseUp += new System.Windows.Forms.MouseEventHandler(this.tgvPrice_MouseUp);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPrice_FormClosing);
         this.Load += new System.EventHandler(this.FmPrice_Load);


         dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         
         dsCommonPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         dsCommonFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);

         dsAgent = Agents.GetDataSet();
         dsPhotos = (DataSet<string, PricePhoto>)DataModule.Get(PricePhoto.OBJECT_NAME)
            ?? new DataSet<string, PricePhoto>(PricePhoto.OBJECT_NAME);
         dsNewPhotos = new DataSet<string, PricePhoto>(PricePhoto.OBJECT_NAME);
         dsDelPhotos = new DataSet<string, PricePhoto>(PricePhoto.OBJECT_NAME, false);

         cbSizes.Items.Add(new TSize(240, 320));
         cbSizes.Items.Add(new TSize(320, 480));
         cbSizes.Items.Add(new TSize(800, 600));
         cbSizes.SelectedIndex = 1;

         dgvItems.AutoGenerateColumns = false;

#if TcarGrad
         cbAgents.Visible = false;
#endif
      }

      private void FmPrice_Load(object sender, EventArgs e)
      {
         FillAgents();
         setting = BaseFormSetting<SettingFmPrice>.Load();
         SetPictureSz();
      }

      private void SetPictureSz()
      {
         Size sz = new Size(setting.szX, setting.szY);
         pictures.ImageSize = sz;
      }

      protected virtual void FillAgents()
      {
         cbAgents.Items.Clear();
         List<Agent> list = new List<Agent>();

         if (CurrentUser.user != null)
            foreach (Agent a in CurrentUser.user.GetAgents().Data)
               list.Add(a);

         list.Sort();

         cbAgents.Items.Add("Общий прайс");
         cbAgents.Items.AddRange(list.ToArray());
         cbAgents.SelectedIndex = 0;

         expanded = false;
         agentsLoading = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (cbAgents.SelectedIndex < 0)
            return;

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
            dsPrice = DataModule.GetUserDataSet(agent.id, Price.OBJECT_NAME, typeof(DataSet<string, Price>), true) as DataSet<string, Price>;
            dsFolder = DataModule.GetUserDataSet(agent.id, ManagerFolder.OBJECT_NAME, typeof(DataSet<string, ManagerFolder>), true) as DataSet<string, ManagerFolder>;

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

      Thread shower = null;

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

         List<PricePhoto> pp = null;
         Invoke(new EmptyParamHandler(delegate() 
            {
               BeforeProceeded();

               if (agentsLoading)
               {
                  FillAgents();
               }

               CreatePriceTree();
               pp = LoadPic();
            }));


         ShowPhoto(pp);
      }

      private void ShowPhoto(List<PricePhoto> pp)
      {
         if (pp != null)
         {
            shower = new Thread(ShowPics);
            shower.Start(pp);
         }
      }

      private void ShowPics(object param)
      {
         List<PricePhoto> list = (List<PricePhoto>)param;

         foreach (PricePhoto pp in list)
         {
            Invoke(new EmptyParamHandler(() => { lvPic.Items.Add(string.Empty, pp.Name).Tag = pp; }));
            Thread.Sleep(100);
         }

         //Console.WriteLine("Show finished: " + DateTime.Now.ToString());
      }

      //Загрузить фотографии
      private List<PricePhoto> LoadPic()
      {
         List<PricePhoto> list = new List<PricePhoto>();
         pictures.Images.Clear();
         lvPic.Items.Clear();

         //DateTime st = DateTime.Now;
         //Console.WriteLine("Start Load: " + st.ToString());
         //int cnt = 0;
         foreach(PricePhoto pp in dsPhotos.Data)
         {
            if (pp.photo != null && pp.photo.Length > 0)
            {
               Stream stream = new MemoryStream(pp.photo);
               Image image = null;

               try
               {
                  using (stream)
                     image = new Bitmap(stream);

                  if (image != null)
                  {
                     pictures.Images.Add(pp.Name, resizeImage(image, pictures.ImageSize, true));
                     //cnt++;
                     list.Add(pp);
                     //lvPic.Items.Add(string.Empty, pp.Name).Tag = pp;
                  }
               }
               catch (Exception) { }
            }
         }

         //Console.WriteLine("Finish Load: " + (st - DateTime.Now).ToString() + " count: " + cnt.ToString() );
         lvPic.LargeImageList = pictures;
         lvPic.SmallImageList = pictures;

         return list;
      }

      virtual protected TreeGridNode AddPriceNode(TreeGridNodeCollection parent, Price p)
      {
         double cost = getCost(p);
         TreeGridNode result = parent.Add(p.name, cost, GetQty(p));
         result.Tag = p;
         return result;
      }

      virtual protected double GetQty(Price p)
      {
         return p.qty;
      }

      virtual protected double getCost(Price p)
      {
         return p.cost != null && p.cost.Length > 0 ? p.cost[0] : 0.0;
      }

      virtual protected TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      {
         TreeGridNode result = parent.Add(dsFolder[f.id].name, null, null);
         result.Tag = f;
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
         treeCnt.RemoveEmptyNodes();

         tgvPrice.SuspendLayout();
         tgvPrice.Nodes.Clear();
         tgvPrice.Rows.Clear();

         BeforeCreatePriceTree();

         foreach(TreeNode n in tmpTree.Nodes)
            fillGridRecursive(n, tgvPrice.Nodes);

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

      //Искать вперед
      private void btnDown_Click(object sender, EventArgs e)
      {
         Find(Direction.DOWN);
      }

      //Искать в направлениее Direction
      private void Find(Direction dir)
      {
         expanded = false;
         ExpandPriceNodes();
         TreeGridNode node = tgvPrice.CurrentRow;

         if (node != null)
         {
            int index = node.RowIndex;

            while (!IsFindOver(dir,ref index))
            {
               if (tgvPrice.Rows[index].Cells[0].Value.ToString().ToUpper().Contains(tbFind.Text.ToUpper()))
               {
                  tgvPrice.CurrentCell = tgvPrice.Rows[index].Cells[0];
                  break;
               }
            }
         }
      }

      //Искать назад
      private void btnUp_Click(object sender, EventArgs e)
      {
         Find(Direction.UP);
      }

      //Вычислить следующий индекс в соответсвии с направление поиска
      private int Next(Direction dir, int value) 
      {
         if (dir == Direction.UP)
            return --value;
         else
            return ++value;
      }

      //Поиск окончен
      private bool IsFindOver(Direction dir,ref int index)
      {
         index = Next(dir, index);

         if (dir == Direction.UP)
            return index < 0;
         else
            return index >= tgvPrice.Rows.Count;
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
            foreach(ListViewItem item in lvPic.Items)
               if (item.Tag != null && item.Tag is PricePhoto)
               {
                  PricePhoto pp = (PricePhoto)item.Tag;
                  if (pp.items != null)
                     foreach(PhotoPriceItem ppi in pp.items)
                        if (ppi.id.Equals(p.id))
                        {
                           item.Selected = true;
                           lvPic.FocusedItem = item;
                           break;
                        }
               }
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

      //Добавить фотографию
      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (dialog.ShowDialog() == DialogResult.OK)
         {
            new Thread(new ParameterizedThreadStart(delegate(object obj)
               {
                  BeginInvoke(new EmptyParamHandler(delegate()
                  {
                     FmWait.ShowForm(this, true);
                  }));
                  
                  List<PricePhoto> ppList = new List<PricePhoto>();
                  Dictionary<string, Image> images = new Dictionary<string, Image>();

                  foreach (string fileName in dialog.FileNames)
                  {
                     
                     PricePhoto pp = new PricePhoto();
                     pp.LocalPath = fileName;
                     pp.Name = String.Format("{0}.jpg", DateTime.Now.Ticks);
                     Image img = resizeWithSelectedSize(pp, (Size)obj);

                     if (img != null)
                     {
                        dsNewPhotos.Add(pp.Name, pp);
                        images.Add(pp.Name, img);
                        ppList.Add(pp);
                     }
                  }

                  BeginInvoke(new EmptyParamHandler(delegate()
                  {
                     foreach (PricePhoto pp in ppList)
                        lvPic.Items.Add(string.Empty, pp.Name).Tag = pp;

                     foreach(KeyValuePair<string, Image> pair in images)
                        pictures.Images.Add(pair.Key, resizeImage(pair.Value, pictures.ImageSize, true));

                     btnSave.Enabled = true;
                     FmWait.CloseForm();
                  }));
               }
            )).Start(((TSize)cbSizes.SelectedItem).size);
         }
      }

      //Изменить размер фотографии
      public static Image resizeImage(Image imgToResize, Size size)
      {
         return resizeImage(imgToResize, size, false);
      }

      //Изменить размер фотографии
      public static Image resizeImage(Image imgToResize, Size size, bool scale)
      {
         int sourceWidth = imgToResize.Width;
         int sourceHeight = imgToResize.Height;

         float nPercent = 0;
         float nPercentW = 0;
         float nPercentH = 0;

         // определим что есть ширина и высота  из изображения
         if( size.Width > size.Height)
         {
            if( sourceWidth < sourceHeight)
               size = new Size(size.Height, size.Width);
         } else
         {
            if( sourceHeight > sourceWidth)
               size = new Size(size.Height, size.Width);
         }

         nPercentW = ((float)size.Width / (float)sourceWidth);
         nPercentH = ((float)size.Height / (float)sourceHeight);

         if (nPercentH < nPercentW)
            nPercent = nPercentH;
         else
            nPercent = nPercentW;

         int destWidth = (int)(sourceWidth * nPercent);
         int destHeight = (int)(sourceHeight * nPercent);

         Bitmap b = new Bitmap(destWidth, destHeight);

         if(scale)
            b = new Bitmap(size.Width, size.Height);

         Graphics g = Graphics.FromImage((Image)b);
         g.InterpolationMode = InterpolationMode.HighQualityBicubic;

         g.DrawImage(imgToResize, 0, 0, destWidth, destHeight);
         g.Dispose();

         return (Image)b;
      }

      //Изменить размер фотографии в соответсвии с
      //выбранным размером
      private Image resizeWithSelectedSize(PricePhoto pricePhoto, Size size)
      {
         Image result = null;

         if (pricePhoto != null)
         {
            
            Stream stream = null;

            if (pricePhoto.LocalPath.Length > 0)
               stream = new FileStream(pricePhoto.LocalPath, FileMode.Open, FileAccess.Read);
            else if (pricePhoto.photo.Length > 0)
               stream = new MemoryStream(pricePhoto.photo);

            if (stream != null)
            {
               try
               {
                  using (stream)
                     result = new Bitmap(stream);

                  result = resizeImage(result, size);

                  string ext = Path.GetExtension(pricePhoto.LocalPath);

                  ImageFormat im = ImageFormat.Jpeg;

                  if (ext.Equals(".png"))
                     im = ImageFormat.Png;

                  int imageSize = (int)getImageSize(result, im);
                  label.Text = String.Format("Размер: {0} X {1}, на диске: {2}",
                     result.Width, result.Height, imageSize);

                  pricePhoto.photo = new byte[imageSize];

                  Stream writeStream = new MemoryStream(pricePhoto.photo);
                  result.Save(writeStream, im);
               }
               catch (Exception e)
               {
                  Invoke(new EmptyParamHandler(() =>
                  {
                     MessageBox.Show(this, string.Format("Ошибка при обработке файла {0}", pricePhoto.LocalPath), "Ошибка",
                        MessageBoxButtons.OK, MessageBoxIcon.Error);
                  }));
               }
            }
         }

         return result;
      }

      //Возвращает размер картинки на диске в байтах
      public static long getImageSize(Image image, ImageFormat im)
      {
         long result = 0;

         using (MemoryStream stream = new MemoryStream())
         {
            image.Save(stream, im);
            result = stream.Length;
         }

         return result;
      }

      //Сохранить
      private void btnSave_Click(object sender, EventArgs e)
      {
         save();
      }

      protected virtual void save()
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();
         List<ReplacedSet> rpcSet = new List<ReplacedSet>();

         SimpleDataSet<PricePhoto> wr = new SimpleDataSet<PricePhoto>(PricePhoto.OBJECT_NAME, false);
         foreach (PricePhoto pp in dsNewPhotos.Data)
            if (pp.photo != null)
               wr.Add(pp);
         if (wr.Count > 0)
            wrSet.Add(wr);

         if (dsDelPhotos.Count > 0)
            rmvSet.Add(dsDelPhotos);

         BeforeWrite(wrSet, rmvSet, rpcSet);

         bool result = false;

         result = DataModule.UpdateDataSet
            (wrSet, rmvSet, rpcSet, Config.GetConfig().GetConnection());

         if (!result)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
         {
            btnSave.Enabled = false;
            dsNewPhotos.Clear();
         }

         AfterWrite(result, wrSet, rmvSet, rpcSet);
      }

      virtual protected void BeforeWrite(List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      {
      }

      virtual protected void AfterWrite(Boolean result, List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      { 
      }

      //Список картинок события смены индекса
      private void lvPic_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (((ListView)sender).SelectedItems.Count > 0)
         {
            UpdatePhotoItemsList(sender);
            tgvPrice.Refresh();
         }
      }

      //Обновить список привязанных SKU к фотографии
      private void UpdatePhotoItemsList(object sender)
      {
         ListViewItem item = ((ListView)sender).SelectedItems[0];
         List<Price> list = new List<Price>();

         if (item.Tag != null && item.Tag is PricePhoto)
         {
            PricePhoto pp = (PricePhoto)item.Tag;

            if (pp.items != null)
               foreach (PhotoPriceItem ppi in pp.items)
               {
                  if (dsPrice.ContainsKey(ppi.id))
                     list.Add(dsPrice[ppi.id]);
               }
         }

         dgvItems.DataSource = list;
      }

      //Двойной щелчок на прайсе, добавить SKU к выбранной картинке
      private void tgvPrice_DoubleClick(object sender, EventArgs e)
      {
         Price price = GetSelectedPrice();

         if (price != null && lvPic.SelectedItems.Count > 0)
         {
            ListViewItem item = lvPic.SelectedItems[0];

            if (item != null && item.Tag != null && item.Tag is PricePhoto)
            {
               PricePhoto pp = (PricePhoto)item.Tag;

               if (pp.items == null)
                  pp.items = new List<PhotoPriceItem>();

               PhotoPriceItem ppi = new PhotoPriceItem();
               ppi.id = price.id;
               pp.items.Add(ppi);

               UpdatePhotoItemsList(lvPic);
            }
         }
      }

      //Прорисовка прайса
      protected void tgvPrice_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         TreeGridNode node = (TreeGridNode)tgvPrice.Rows[e.RowIndex];
         Price p = node.Tag as Price;
         
         if (p != null && lvPic.SelectedItems.Count > 0)
         {
            PricePhoto pp = lvPic.SelectedItems[0].Tag as PricePhoto;

            if(pp != null && pp.items != null) 
               foreach(PhotoPriceItem ppi in pp.items)
                  if (ppi.id.Equals(p.id))
                  {
                     e.CellStyle.BackColor = Color.LightBlue;
                     return;
                  }
         }

         e.CellStyle.BackColor = Color.White;
      }

      //Удалить картинку
      private void mnuDelPic_Click(object sender, EventArgs e)
      {
         ListViewItem selItem = lvPic.SelectedItems.Count > 0 ? lvPic.SelectedItems[0] : null;

         if (selItem != null && 
            selItem.Tag != null && 
            selItem.Tag is PricePhoto &&
            MessageBox.Show("Запись будет удалена, удалить?", "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) == DialogResult.OK)
         {
            PricePhoto pp = (PricePhoto)selItem.Tag;

            pictures.Images.RemoveByKey(pp.Name);
            selItem.Remove();

            if(dsPhotos.ContainsKey(pp.Name))
               dsPhotos.Remove(pp.Name);
            
            if (dsNewPhotos.ContainsKey(pp.Name))
               dsNewPhotos.Remove(pp.Name);

            dsDelPhotos.Add(pp.Name, pp);
            btnSave.Enabled = true;
         }
      }

      private void mnuDelItem_Click(object sender, EventArgs e)
      {
         DataGridViewRow curRow = dgvItems.CurrentRow;

         if (curRow != null)
         {
            Price price = curRow.DataBoundItem as Price;

            if (price != null && lvPic.SelectedItems.Count > 0)
            {
               ListViewItem selItem = lvPic.SelectedItems[0];
               
               if (selItem != null && selItem.Tag is PricePhoto)
               {
                  PricePhoto pp = (PricePhoto)selItem.Tag;
                  
                  foreach(PhotoPriceItem item in pp.items)
                     if(item.id.Equals(price.id))
                     {
                        pp.items.Remove(item);
                        break;
                     }

                  if (!dsNewPhotos.ContainsKey(pp.Name))
                     dsNewPhotos.Add(pp.Name, pp);

                  UpdatePhotoItemsList(lvPic);
                  btnSave.Enabled = true;
               }
            }

         }
      }

      private void tgvPrice_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = tgvPrice.HitTest(e.X, e.Y);

         if (info.ColumnIndex == -1 || info.RowIndex == -1 || e.Button != MouseButtons.Left)
            return;

         tgvPrice.CurrentCell = tgvPrice[info.ColumnIndex, info.RowIndex];
         Price price = GetSelectedPrice();

         if (price != null)
            tgvPrice.DoDragDrop(price, DragDropEffects.Copy);
      }

      private void dgvItems_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      private void dgvItems_DragDrop(object sender, DragEventArgs e)
      {
         PriceDropped(e);
      }

      private void PriceDropped(DragEventArgs e)
      {
         if (e.Data.GetDataPresent(typeof(Price))
            && lvPic.SelectedItems.Count > 0)
         {
            PricePhoto pp = lvPic.SelectedItems[0].Tag as PricePhoto;

            if (pp != null)
            {
               Price price = e.Data.GetData(typeof(Price)) as Price;

               if (pp.items == null)
                  pp.items = new List<PhotoPriceItem>();

               PhotoPriceItem ppi = new PhotoPriceItem();
               ppi.id = price.id;

               pp.items.Add(ppi);

               if (!dsNewPhotos.ContainsKey(pp.Name))
                  dsNewPhotos.Add(pp.Name, pp);

               UpdatePhotoItemsList(lvPic);
               btnSave.Enabled = true;
            }
         }
      }

      private void lvPic_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      private void lvPic_DragOver(object sender, DragEventArgs e)
      {
         Point clientPoint = lvPic.PointToClient(new Point(e.X, e.Y));
         ListViewHitTestInfo info = lvPic.HitTest(clientPoint.X, clientPoint.Y);

         if (info != null && 
            info.Item != null &&
            !info.Item.Selected)
               info.Item.Selected = true;
      }

      private void lvPic_DragDrop(object sender, DragEventArgs e)
      {
         PriceDropped(e);
      }

      private void lvPic_DoubleClick(object sender, EventArgs e)
      {
         ListViewItem selected = (sender as ListView).SelectedItems[0];

         if (selected != null && dsPhotos.ContainsKey(selected.ImageKey))
         {
            PricePhoto pp = dsPhotos[selected.ImageKey];

            if (pp != null && pp.photo != null && pp.photo.Length > 0)
            {
               Stream stream = new MemoryStream(pp.photo);
               Image image = null;

               using (stream)
                  image = new Bitmap(stream);

               if (image != null)
               {
                  FmViewPhoto.ShowPhoto(image, selected.ImageKey);
               }
            }
         }
      }

      private void miSetting_Click(object sender, EventArgs e)
      {
         FmPriceSetting.ShowInstance(setting);
         setting.Save();
         SetPictureSz();
         ShowPhoto(LoadPic());
      }

      private void tsbNewPrice_Click(object sender, EventArgs e)
      {
         if (btnSave.Enabled)
            btnSave_Click(this, EventArgs.Empty);

         Type prcType = FormEntries.GetFormType(typeof(FmPricePhoto));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();

         Close();
      }

      private void FmPrice_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (shower != null && shower.IsAlive)
            shower.Abort();

         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            btnSave.PerformClick();
      }
   }

   public class TSize
   {
      public Size size;

      public TSize(int width, int height)
      {
         size = new Size(width, height);
      }

      public override string ToString()
      {
         return String.Format("{0} X {1}", size.Width, size.Height);
      } 
   }

   [Serializable]
   public class SettingFmPrice : BaseFormSetting<SettingFmPrice>
   {
      public int szX = 115;
      public int szY = 115;
   } 

}
