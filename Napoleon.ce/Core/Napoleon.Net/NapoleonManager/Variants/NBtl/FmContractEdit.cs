using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.Common;
using System.Data.OleDb;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmContractEdit : Form
   {
      public List<Price> removed = new List<Price>();
      List<ContractOrgImg> orgImg = new List<ContractOrgImg>();

      ContractDef contract;

      public FmContractEdit()
      {
         InitializeComponent();

         cbSizes.Items.Add(new TSize(240, 320));
         cbSizes.Items.Add(new TSize(320, 480));
         cbSizes.Items.Add(new TSize(800, 600));
         cbSizes.SelectedIndex = 1;

         contract = new ContractDef();
         contract.id = ContractDef.GenId();
         contract.start = DateTime.Now;
         contract.finish = DateTime.Now;
         contract.items = new List<ContractIDeftem>();
         contract.orgImg = new List<ContractOrgImg>();

         RefreshData();
      }

      public ContractDef Contract
      {
         get { return contract; }
         set { contract = value; RefreshData(); }
      }

      void RefreshData()
      {
         dpv.Start = contract.start;
         dpv.Finish = contract.finish;
         tbName.Text = contract.name;
         Photo = contract.photo;
         OrgImg = contract.orgImg;

         List<Price> list = new List<Price>();

         foreach (ContractIDeftem i in contract.items)
            list.Add(i.item);

         Items = list;
      }

      void UpdateContract()
      {
         contract.start = Start;
         contract.finish = Finish;
         contract.name = tbName.Text.Trim();
         contract.photo = Photo;
         contract.orgImg = OrgImg;

         contract.items.Clear();
         foreach (Price p in Items)
         {
            ContractIDeftem ci = new ContractIDeftem();
            ci.id = p.id;
            ci.item = p;

            contract.items.Add(ci);
            p.cdef = contract.id;
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (DialogResult == System.Windows.Forms.DialogResult.OK)
            UpdateContract();

         base.OnClosing(e);
      }

      public DateTime Start { get { return dpv.Start; } set { dpv.Start = value; } }
      public DateTime Finish { get { return dpv.Finish; } set { dpv.Finish = value; } }
      //public string Contract { get { return tbName.Text.Trim(); } set { tbName.Text = value; } }
      public byte[] Photo
      {
         get
         {
            byte[] result = null;

            if (picture.Image != null)
            {
               using (MemoryStream writeStream = new MemoryStream())
               {
                  picture.Image.Save(writeStream, ImageFormat.Jpeg);
                  result = writeStream.ToArray();
               }
            }

            return result;
         }

         set 
         {
            if (value != null)
            {
               MemoryStream stream = new MemoryStream(value);
               picture.Image =  Image.FromStream(stream);  
            }
         }
      }

      public List<ContractOrgImg> OrgImg
      {
         get { return orgImg; }
         set { orgImg = value; }
      }

      private static ImageCodecInfo GetEncoderInfo(String mimeType)
      {
         int j;
         ImageCodecInfo[] encoders;
         encoders = ImageCodecInfo.GetImageEncoders();
         for (j = 0; j < encoders.Length; ++j)
         {
            if (encoders[j].MimeType == mimeType)
               return encoders[j];
         }
         return null;
      }

      public IList<Price> Items { 
         get 
         {
            List<Price> result = new List<Price>();

            foreach(TreeNode n in tree.Nodes)
            {
               GroupNode gn = n as GroupNode;
               foreach (TreeNode p in n.Nodes)
               {
                  PriceNode pn = p as PriceNode;
                  pn.Price.my = gn.isMy ? 1 : 0;
                  if (pn != null)
                     result.Add(pn.Price);
               }
            }
            return result; 
         } 
         set 
         {
            tree.Nodes.Clear();

            List<Price> list = new List<Price>(value);
            list.Sort((lhs, rhs) => { return CmpPrice(lhs, rhs); });

            GroupNode gn = null;

            foreach (Price p in list)
            {
               if (gn == null || gn.Text != p.group)
               {
                  gn = new GroupNode();
                  gn.Text = p.group;
                  gn.isMy = p.my > 0;
                  tree.Nodes.Add(gn);
               }

               gn.Nodes.Add(new PriceNode(p));
            }

            tree.ExpandAll();
         } 
      }

      private int CmpPrice(Price lhs, Price rhs)
      {
         int result = rhs.my - lhs.my;

         if (result == 0)
            result = lhs.group.CompareTo(rhs.group);

         if (result == 0)
            result = lhs.Name.CompareTo(rhs.Name);

         return result;
      }

      private void FmContractEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK) 
         { 
            if (tbName.Text.Trim().Length == 0)
            {
               e.Cancel = true;
               tbName.Focus();
               DialogUtil.HaveToValueMsg(this);
            }
            else if (IsEmptyContract())
            {
               const string BLOCK_CREATED_EMPTY_DOC = "Невозможно создать пустой контракт";
               MessageBox.Show(this, BLOCK_CREATED_EMPTY_DOC, DialogUtil.TITLE_ERR, MessageBoxButtons.OK);
               e.Cancel = true;
            }
         }
      }

      private bool IsEmptyContract()
      {
         return Items.Count == 0;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmPriceEdit dialog = new FmPriceEdit();
         GroupNode gr = GetSelectedGr();

         if (dialog.ShowDialog() == DialogResult.OK && gr is GroupNode)
         {
            Price p = new Price();
            p.id = Price.GenId();
            p.name = dialog.ItemName;
            p.group = gr.Text;
            p.my = gr.isMy ? 1 : 0;

            PriceNode pn = new PriceNode(p);
            gr.Nodes.Add(pn);
            tree.SelectedNode = pn;
            tree.Focus();
            pn.Expand();
         }
      }

      private GroupNode GetSelectedGr()
      {
         TreeNode result = tree.SelectedNode;

         if (result != null && result is PriceNode)
            result = result.Parent;

         return result as GroupNode;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         PriceNode pn = GetSelPrice();

         if (pn != null)
         {
            Price p = pn.Price;

            if (p != null)
            {
               FmPriceEdit dialog = new FmPriceEdit();
               dialog.ItemName = p.name;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  p.name = dialog.ItemName;
                  pn.Text = p.name;

                  tree.Invalidate();
               }
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         PriceNode sel = GetSelPrice();

         if (sel != null && DialogUtil.AskToDel(this))
         {
            removed.Add(sel.Price);
            tree.Nodes.Remove(sel);
         }
      }

      private PriceNode GetSelPrice()
      {
         TreeNode result = tree.SelectedNode;

         return result as PriceNode;
      }

      private void btnAddGr_Click(object sender, EventArgs e)
      {
         FmPriceEdit dialog = new FmPriceEdit();

         if (dialog.ShowDialog() == DialogResult.OK)
         {
            TreeNode node = MakeDefNode();
            node.Text = dialog.ItemName;
            tree.SelectedNode = tree.Nodes[tree.Nodes.Add(node)];
            tree.Focus();
         }
      }

      public TreeNode MakeDefNode()
      {
         GroupNode result = new GroupNode();
         result.isMy = tree.Nodes.Count == 0;
         return result;
      }

      private void tree_DrawNode(object sender, DrawTreeNodeEventArgs e)
      {
         GroupNode cn = e.Node as GroupNode;

         if (cn != null)
            cn.Draw(sender, e);
         else
            e.DrawDefault = true;
      }

      private void tree_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == System.Windows.Forms.MouseButtons.Right)
         {
            TreeView tv = (TreeView)sender;
            tv.SelectedNode = tv.GetNodeAt(new Point(e.X, e.Y));
         }
      }

      private void btnEditGr_Click(object sender, EventArgs e)
      {
         FmPriceEdit dialog = new FmPriceEdit();
         TreeNode gr = GetSelectedGr();

         if (gr != null)
         {
            dialog.ItemName = gr.Text;
            if (dialog.ShowDialog() == DialogResult.OK)
            {
               GroupNode grn = (GroupNode)gr;
               string s = dialog.ItemName;
               grn.Text = s;

               foreach (TreeNode t in grn.Nodes)
               {
                  PriceNode p = t as PriceNode;
                  if (p != null)
                     p.Price.group = s;
               }
            }
         }
      }

      private void btnDelGr_Click(object sender, EventArgs e)
      {
         GroupNode gn = GetSelectedGr();

         if (gn != null && DialogUtil.AskToDel(this))
         {
            foreach (TreeNode t in gn.Nodes)
            {
               PriceNode p = t as PriceNode;

               if (p != null)
                  removed.Add(p.Price);
            }

            tree.Nodes.Remove(gn);
         }
      }

      public static Image resizeWithSelectedSize(String path, Size size)
      {
         Image result = null;

         if (path != null)
         {
            Stream stream = null;

            if (path.Length > 0)
               stream = new FileStream(path, FileMode.Open, FileAccess.Read);

            if (stream != null)
            {
               using (stream)
                  result = new Bitmap(stream);

               result = FmPrice.resizeImage(result, size);
            }
         }

         return result;
      }

      private void btnAddImg_Click(object sender, EventArgs e)
      {
         OpenFileDialog dialog = new OpenFileDialog();

         if (dialog.ShowDialog() == DialogResult.OK)
         {
            new Thread(new ParameterizedThreadStart(delegate(object obj)
               {
                  BeginInvoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));

                  string fileName = dialog.FileName;
                  Image img = resizeWithSelectedSize(fileName, (Size)obj);

                  BeginInvoke(new EmptyParamHandler(delegate()
                  {
                     picture.Image = img;
                     FmWait.CloseForm();
                  }));
               }
            )).Start(((TSize)cbSizes.SelectedItem).size);
         }
      }

      private void btnDelImg_Click(object sender, EventArgs e)
      {
         if (picture.Image != null && DialogUtil.AskToDel(this))
            picture.Image = null;
      }

      private void miSetMy_Click(object sender, EventArgs e)
      {
         ResetMy();
         GroupNode gn = GetSelectedGr();
         gn.isMy = true;

         tree.Invalidate();
      }

      private void ResetMy()
      {
         foreach (TreeNode n in tree.Nodes)
         {
            GroupNode g = n as GroupNode;
            if (g != null)
               g.isMy = false;
         }
      }

      private void btnItems_Click(object sender, EventArgs e)
      {
         FmContractPlanogram f = new FmContractPlanogram();
         f.imgSize = (TSize)cbSizes.SelectedItem;
         f.OrgImg = orgImg;

         if (f.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            orgImg = f.OrgImg;

         //FmContractPhotos f = new FmContractPhotos();
         //f.imgSize = (TSize)cbSizes.SelectedItem;
         //f.OrgImg = orgImg;

         //if (f.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         //   orgImg = f.OrgImg;
      }

      private void tsbMatrixDesigner_Click(object sender, EventArgs e)
      {
         FmMatrixDesignerEx.Open(contract.id);
      }

      private void btnLoad_Click(object sender, EventArgs e)
      {
         OpenFileDialog dlg = new OpenFileDialog();
         if (dlg.ShowDialog() == DialogResult.OK)
            LoadItems(dlg.FileName);
      }

      private void LoadItems(string file)
      {
         tree.BeginUpdate();
         List<string> pc = new List<string>(); // price cache

         foreach (ContractIDeftem c in contract.items)
            if (c.item != null && !pc.Contains(c.item.Name))
               pc.Add(c.item.Name);

         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", file);
         var objConn = new OleDbConnection(connectionString);
         objConn.Open();
         var dt = objConn.GetOleDbSchemaTable(OleDbSchemaGuid.Tables, null);

         if (dt == null)
         {
            return;
         }

         Dictionary<string, TreeNode> nc = new Dictionary<string, TreeNode>(); // nodecashe

         foreach (TreeNode n in tree.Nodes)
            nc[n.Text] = n;

         foreach (DataRow sh in dt.Rows)
         {
            string group = sh["TABLE_NAME"].ToString().Trim();
            var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [" + group + "]", connectionString);
            var ds = new DataSet();
            adapter.Fill(ds, group);

            DataTable data = ds.Tables[group];

            if (group.StartsWith("'"))
               group = group.Substring(1, group.Length - 3); // remove $ character
            else
               group = group.Substring(0, group.Length - 1); // remove $ character

            foreach (DataRow row in data.Rows)
            {
               object[] r = row.ItemArray;

               if (r[0].ToString().Length == 0)
                  break;

               Price p = new Price();
               p.id = GRSoft.Network.DataObject.GenId();
               p.name = r[0].ToString().Trim();

               if (pc.Contains(p.name)) // skip if org presents
                  continue;

               p.group = group;
               p.cdef = contract.id;

               if (!nc.ContainsKey(group))
               {
                  GroupNode gn = new GroupNode();
                  gn.Text = group;
                  tree.Nodes.Add(gn);
                  nc[group] = gn;
               }

               PriceNode pn = new PriceNode(p);
               nc[group].Nodes.Add(pn);
            }
         }

         tree.EndUpdate();
      }

      private void tsbOrgMatrix_Click(object sender, EventArgs e)
      {
         new FmOrgGoodsMatrixEdit().Show();
      }
         
   }

   public class PriceNode : TreeNode
   {
      private Price price;

      public PriceNode(Price price)
      {
         this.price = price;
         Text = price.Name;
      }

      public Price Price { get { return price; } }
   }

   public class GroupNode : TreeNode
   {
      public bool isMy = false;
      public void Draw(object parent, DrawTreeNodeEventArgs e) 
      {
         if (isMy)
            MyGroupDraw(parent, e);
         else
            e.DrawDefault = true;
      }

      public static void MyGroupDraw(object parent, DrawTreeNodeEventArgs e)
      {
         Font fnt = new Font(((TreeView)parent).Font, FontStyle.Bold);
         SizeF sz = e.Graphics.MeasureString(e.Node.Text, fnt);
         Rectangle rect = e.Bounds;
         rect.Width = (int)sz.Width;
         const int FIX_RECT = 1;
         rect.Offset(FIX_RECT, 0);
         rect.Inflate(FIX_RECT, 0);

         Brush fb = Brushes.Black;

         if (e.Node.IsSelected)
         {
            fb = Brushes.White;

            const string HIGHLIGHT = "Highlight";
            using (Brush bkg = new SolidBrush(Color.FromName(HIGHLIGHT)))
            {
               const int BSZ = 1;
               Rectangle brd = new Rectangle(rect.Left - BSZ, rect.Top, rect.Width + BSZ, rect.Height);

               e.Graphics.FillRectangle(bkg, brd);

               if ((e.State & TreeNodeStates.Focused) == TreeNodeStates.Focused)
                  using (Pen focusPen = new Pen(Color.Black))
                  {
                     focusPen.DashStyle = System.Drawing.Drawing2D.DashStyle.Dot;
                     Rectangle focusBounds = brd;
                     focusBounds.Size = new Size(focusBounds.Width - 1, focusBounds.Height - 1);
                     e.Graphics.DrawRectangle(focusPen, focusBounds);
                  }
            }
         }

         e.Graphics.DrawString(e.Node.Text, fnt, fb, rect);
      }
   }
}
