using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmScriptEditEx : FmScriptEdit
   {
      private static readonly String VIIST_ALLOW_GALLEY_COND_PARAM = "allowGallery";
      private static readonly String ALLOW_GALLERY_TITLE = "Разрешить выбор из галереи";
      private static readonly String REJECT_GALLERY_TITLE = "Запретить выбор из галереи";

      ComboBox cbTypes;

      int needSendItem = -1;
      int imageSendIndex = -1, visitImageIndex = -1, visitGalleryIndex = -1;

      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem tsbSendToCloud;
      private System.Windows.Forms.ToolStripMenuItem tsbAllowGallery;

      protected FmScriptEditEx(PostProcess postProcess)
         : base(postProcess)
      {
         cbTypes = new System.Windows.Forms.ComboBox();
         this.cbTypes.FormattingEnabled = true;
         this.cbTypes.Location = new System.Drawing.Point(449, 2);
         this.cbTypes.Name = "cbTypes";
         this.cbTypes.Size = new System.Drawing.Size(154, 22);
         this.cbTypes.TabIndex = 3;

         Label label2 = new Label();
         label2.AutoSize = true;
         label2.Location = new System.Drawing.Point(352, 6);
         label2.Name = "label2";
         label2.Size = new System.Drawing.Size(57, 14);
         label2.TabIndex = 2;
         label2.Text = "Производитель";

         panel1.Controls.Add(label2);
         panel1.Controls.Add(this.cbTypes);

         tbName.Width = 244;
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left))));

         Supplier ot = new Supplier();
         ot.name = "<Для всех>";
         DataSet<string, Supplier> types = (DataSet<string, Supplier>)DataModule.Get(Supplier.OBJECT_NAME);
         if (types != null)
         {
            List<Supplier> src = new List<Supplier>(types.Values);
            src.Sort();
            src.Insert(0, ot);

            src.ForEach(x => cbTypes.Items.Add(x));
         }
         else
            cbTypes.Items.Add(ot);


         tsbSendToCloud = new ToolStripMenuItem();
         contextMenuStrip1 = new ContextMenuStrip();
         tsbAllowGallery = new ToolStripMenuItem();

         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSendToCloud, tsbAllowGallery});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(241, 48);

         this.tsbSendToCloud.Name = "tsbSendToCloud";
         this.tsbSendToCloud.Size = new System.Drawing.Size(240, 22);
         this.tsbSendToCloud.Text = "Отправлять в нейронную сеть";
         tsbSendToCloud.Click += tsbSendToCloud_Click;
         lvDocs.ContextMenuStrip = contextMenuStrip1;
         contextMenuStrip1.Opening += contextMenuStrip1_Opening;

         
         this.tsbAllowGallery.Name = "tsbAllowGallery";
         this.tsbAllowGallery.Size = new System.Drawing.Size(240, 22);
         this.tsbAllowGallery.Text = "Разрешить выбор из галлереи";
         tsbAllowGallery.Click += tsbAllowGallery_Click;
         
         foreach(ListViewItem lvi in lvDocsAvail.Items)
         {
            if (lvi.Tag is VisitDoc)
               visitImageIndex = lvi.ImageIndex;
         }

         imageSendIndex = imageList1.Images.Count;
         imageList1.Images.Add(Resources.visit_to_cloud);
         visitGalleryIndex = imageList1.Images.Count;
         imageList1.Images.Add(Resources.gallery);
      }

      private void tsbAllowGallery_Click(object sender, EventArgs e)
      {
         ListViewItem lvi = lvDocs.FocusedItem;
         if (lvi != null)
         {
            VisitDoc doc = (lvi.Tag as VisitDoc);

            if (doc != null)
            {
               String cp = doc.condParam;
               doc = new VisitDoc();
               doc.condParam = cp;
               lvi.Tag = doc;

               if (doc.condParam.Trim().Length == 0)
               {
                  doc.condParam = VIIST_ALLOW_GALLEY_COND_PARAM;
                  lvi.ImageIndex = visitGalleryIndex;
                  tsbAllowGallery.Text = REJECT_GALLERY_TITLE;
               }
               else
               {
                  doc.condParam = string.Empty;
                  lvi.ImageIndex = visitImageIndex;
                  tsbAllowGallery.Text = ALLOW_GALLERY_TITLE;
               }
            }
            btnSave.Enabled = true;
         }
      }

      void tsbSendToCloud_Click(object sender, EventArgs e)
      {
         ListViewItem lvi = lvDocs.FocusedItem;
         if(lvi != null)
         {
            if(needSendItem >= 0)
            {
               lvDocs.Items[needSendItem].ImageIndex = visitImageIndex;
            }
            needSendItem = lvi.Index;
            lvi.ImageIndex = imageSendIndex;
            
            btnSave.Enabled = true;
         }
      }

      void contextMenuStrip1_Opening(object sender, System.ComponentModel.CancelEventArgs e)
      {
         bool hide = true;
         ListViewItem lvi = lvDocs.FocusedItem;
         if(lvi != null)
         {
            ScriptDocument sd = lvi.Tag as ScriptDocument;
            hide = !(sd is VisitDoc);

            if (!hide) { }
         }
         e.Cancel = hide;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         if (script != null)
         {
            foreach (Supplier ot in cbTypes.Items)
            {
               if (script.suppl == ot.id)
               {
                  cbTypes.SelectedItem = ot;
                  break;
               }
            }

            cbTypes.SelectedIndexChanged += cbTypes_SelectedIndexChanged;
            int idx = 0;
            foreach (ScriptDefItem item in script.items)
            {
               if (item.needSend == 1)
               {
                  needSendItem = idx;
                  lvDocs.Items[needSendItem].ImageIndex = imageSendIndex;
                  break;
               }

               idx++;
            }

            
               
         }
      }

      protected override void SettingDocItem(ScriptDefItem item, ListViewItem lvi)
      {
         if (lvi != null)
         {
            VisitDoc doc = lvi.Tag as VisitDoc;

            if (doc != null)
            {
               String cp = doc.condParam;
               doc = new VisitDoc();
               doc.condParam = cp;
               lvi.Tag = doc;

               if (item.condParam.Equals(VIIST_ALLOW_GALLEY_COND_PARAM))
               {
                  doc.condParam = item.condParam;
                  lvi.ImageIndex = visitGalleryIndex;
                  tsbAllowGallery.Text = REJECT_GALLERY_TITLE;
               }
            }
         }
      }
      protected override void BeforeSaveItem(ScriptDefItem scriptItem, ListViewItem item)
      {
         scriptItem.needSend = (lvDocs.Items.IndexOf(item) == needSendItem) ? 1 : 0;
      }

      void cbTypes_SelectedIndexChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }

      protected override void Save()
      {
         if (script == null)
            script = new ScriptDef();

         Supplier sel = cbTypes.SelectedItem as Supplier;
         if (sel != null)
         {
            script.suplier = sel;
            script.suppl = sel.id;
         }
         base.Save();
      }
   }
}
