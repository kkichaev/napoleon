namespace GRSoft.NapoleonManager
{
   partial class FmColorEditor
   {
      /// <summary>
      /// Required designer variable.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary>
      /// Clean up any resources being used.
      /// </summary>
      /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Windows Form Designer generated code

      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmColorEditor));
         this.colorList = new System.Windows.Forms.ListBox();
         this.ok = new System.Windows.Forms.Button();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsAdd = new System.Windows.Forms.ToolStripButton();
         this.tsEdit = new System.Windows.Forms.ToolStripButton();
         this.tsDel = new System.Windows.Forms.ToolStripButton();
         this.toolTip1 = new System.Windows.Forms.ToolTip(this.components);
         this.colorEditor = new System.Windows.Forms.ColorDialog();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // colorList
         // 
         this.colorList.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.colorList.DrawMode = System.Windows.Forms.DrawMode.OwnerDrawFixed;
         this.colorList.FormattingEnabled = true;
         this.colorList.IntegralHeight = false;
         this.colorList.ItemHeight = 20;
         this.colorList.Location = new System.Drawing.Point(-1, 28);
         this.colorList.Name = "colorList";
         this.colorList.Size = new System.Drawing.Size(323, 256);
         this.colorList.TabIndex = 0;
         this.toolTip1.SetToolTip(this.colorList, "Редактирование дополнительных цветов.\r\nЧерный цвет всегда есть в списке цветов");
         this.colorList.DrawItem += new System.Windows.Forms.DrawItemEventHandler(this.colorList_DrawItem);
         this.colorList.DoubleClick += new System.EventHandler(this.colorList_DoubleClick);
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(12, 303);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 1;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsAdd,
            this.tsEdit,
            this.tsDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(321, 39);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsAdd
         // 
         this.tsAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsAdd.Name = "tsAdd";
         this.tsAdd.Size = new System.Drawing.Size(36, 36);
         this.tsAdd.Text = "Добавить";
         this.tsAdd.Click += new System.EventHandler(this.tsAdd_Click);
         // 
         // tsEdit
         // 
         this.tsEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit;
         this.tsEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsEdit.Name = "tsEdit";
         this.tsEdit.Size = new System.Drawing.Size(36, 36);
         this.tsEdit.Text = "Изменить";
         this.tsEdit.Click += new System.EventHandler(this.tsEdit_Click);
         // 
         // tsDel
         // 
         this.tsDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.tsDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsDel.Name = "tsDel";
         this.tsDel.Size = new System.Drawing.Size(36, 36);
         this.tsDel.Text = "Удалить";
         this.tsDel.Click += new System.EventHandler(this.tsDel_Click);
         // 
         // FmColorEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(321, 338);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.colorList);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmColorEditor";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "Редактор цветов";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.ListBox colorList;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsAdd;
      private System.Windows.Forms.ToolStripButton tsEdit;
      private System.Windows.Forms.ToolStripButton tsDel;
      private System.Windows.Forms.ToolTip toolTip1;
      private System.Windows.Forms.ColorDialog colorEditor;
   }
}