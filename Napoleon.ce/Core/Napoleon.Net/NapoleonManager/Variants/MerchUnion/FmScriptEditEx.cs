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
      ComboBox cbTypes;

      int needSendItem = -1;
      int imageSendIndex = -1, visitImageIndex = -1;

      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem tsbSendToCloud;

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
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSendToCloud});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(241, 48);

         this.tsbSendToCloud.Name = "tsbSendToCloud";
         this.tsbSendToCloud.Size = new System.Drawing.Size(240, 22);
         this.tsbSendToCloud.Text = "Отправлять в нейронную сеть";
         tsbSendToCloud.Click += tsbSendToCloud_Click;
         lvDocs.ContextMenuStrip = contextMenuStrip1;
         contextMenuStrip1.Opening += contextMenuStrip1_Opening;

         foreach(ListViewItem lvi in lvDocsAvail.Items)
         {
            if (lvi.Tag is VisitDoc)
               visitImageIndex = lvi.ImageIndex;
         }

         imageSendIndex = imageList1.Images.Count;
         imageList1.Images.Add(Resources.visit_to_cloud);
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
         }
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
