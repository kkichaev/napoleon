using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
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

      public FmContractEdit()
      {
         InitializeComponent();

         cbSizes.Items.Add(new TSize(240, 320));
         cbSizes.Items.Add(new TSize(320, 480));
         cbSizes.Items.Add(new TSize(800, 600));
         cbSizes.SelectedIndex = 1;

         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
      }

      public DateTime Start { get { return dpv.Start; } set { dpv.Start = value; } }
      public DateTime Finish { get { return dpv.Finish; } set { dpv.Finish = value; } }
      public string Contract { get { return tbName.Text.Trim(); } set { tbName.Text = value; } }
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
            if (Contract.Length == 0)
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
            p.name = dialog.Price;
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
               dialog.Price = p.name;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  p.name = dialog.Price;
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
            node.Text = dialog.Price;
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
            dialog.Price = gr.Text;
            if (dialog.ShowDialog() == DialogResult.OK)
            {
               GroupNode grn = (GroupNode)gr;
               string s = dialog.Price;
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
         FmContractPhotos f = new FmContractPhotos();
         f.imgSize = (TSize)cbSizes.SelectedItem;
         f.OrgImg = orgImg;

         if (f.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            orgImg = f.OrgImg;
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

      private void MyGroupDraw(object parent, DrawTreeNodeEventArgs e)
      {
         Font fnt = new Font(((TreeView)parent).Font, FontStyle.Bold);
         SizeF sz = e.Graphics.MeasureString(e.Node.Text, fnt);
         Rectangle rect = e.Bounds;
         rect.Width = (int)sz.Width;
         const int FIX_RECT = 1;
         rect.Offset(FIX_RECT, 0);
         rect.Inflate(FIX_RECT, 0);

         Brush fb = Brushes.Black;

         if (IsSelected)
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
