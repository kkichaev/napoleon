namespace GRSoft.NapoleonManager
{
   partial class FmAction
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAction));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dtpTill = new System.Windows.Forms.DateTimePicker();
         this.dtpFrom = new System.Windows.Forms.DateTimePicker();
         this.lbAction = new System.Windows.Forms.ListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddAction = new System.Windows.Forms.ToolStripButton();
         this.btnDelAction = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.label5 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.tbDescr = new System.Windows.Forms.TextBox();
         this.tbName = new System.Windows.Forms.TextBox();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.cbActionType = new System.Windows.Forms.ComboBox();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.lbPrice = new System.Windows.Forms.ListBox();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.btnAddPrice = new System.Windows.Forms.ToolStripButton();
         this.btnDelPrice = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dtpTill);
         this.splitContainer1.Panel1.Controls.Add(this.dtpFrom);
         this.splitContainer1.Panel1.Controls.Add(this.lbAction);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(838, 433);
         this.splitContainer1.SplitterDistance = 377;
         this.splitContainer1.TabIndex = 0;
         // 
         // dtpTill
         // 
         this.dtpTill.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtpTill.Location = new System.Drawing.Point(225, 2);
         this.dtpTill.Name = "dtpTill";
         this.dtpTill.Size = new System.Drawing.Size(99, 20);
         this.dtpTill.TabIndex = 3;
         // 
         // dtpFrom
         // 
         this.dtpFrom.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtpFrom.Location = new System.Drawing.Point(97, 2);
         this.dtpFrom.Name = "dtpFrom";
         this.dtpFrom.Size = new System.Drawing.Size(99, 20);
         this.dtpFrom.TabIndex = 2;
         // 
         // lbAction
         // 
         this.lbAction.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbAction.FormattingEnabled = true;
         this.lbAction.ItemHeight = 14;
         this.lbAction.Location = new System.Drawing.Point(0, 25);
         this.lbAction.Name = "lbAction";
         this.lbAction.Size = new System.Drawing.Size(377, 396);
         this.lbAction.TabIndex = 0;
         this.lbAction.SelectedIndexChanged += new System.EventHandler(this.lbAction_SelectedIndexChanged);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddAction,
            this.btnDelAction,
            this.btnSave,
            this.toolStripLabel2,
            this.toolStripLabel3,
            this.toolStripSeparator1,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(377, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddAction
         // 
         this.btnAddAction.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddAction.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddAction.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddAction.Name = "btnAddAction";
         this.btnAddAction.Size = new System.Drawing.Size(23, 22);
         this.btnAddAction.Text = "Создать";
         this.btnAddAction.ToolTipText = "Добавить акцию";
         this.btnAddAction.Click += new System.EventHandler(this.btnAddAction_Click);
         // 
         // btnDelAction
         // 
         this.btnDelAction.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelAction.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelAction.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelAction.Name = "btnDelAction";
         this.btnDelAction.Size = new System.Drawing.Size(23, 22);
         this.btnDelAction.Text = "Удалить акцию";
         this.btnDelAction.Click += new System.EventHandler(this.btnDelAction_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(13, 22);
         this.toolStripLabel2.Text = "c";
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Margin = new System.Windows.Forms.Padding(110, 1, 0, 2);
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(21, 22);
         this.toolStripLabel3.Text = "по";
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(110, 0, 0, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.label5);
         this.splitContainer2.Panel1.Controls.Add(this.label4);
         this.splitContainer2.Panel1.Controls.Add(this.tbDescr);
         this.splitContainer2.Panel1.Controls.Add(this.tbName);
         this.splitContainer2.Panel1.Controls.Add(this.dtpFinish);
         this.splitContainer2.Panel1.Controls.Add(this.dtpStart);
         this.splitContainer2.Panel1.Controls.Add(this.cbActionType);
         this.splitContainer2.Panel1.Controls.Add(this.label3);
         this.splitContainer2.Panel1.Controls.Add(this.label2);
         this.splitContainer2.Panel1.Controls.Add(this.label1);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.lbPrice);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer2.Size = new System.Drawing.Size(457, 433);
         this.splitContainer2.SplitterDistance = 234;
         this.splitContainer2.TabIndex = 0;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(8, 97);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(57, 14);
         this.label5.TabIndex = 12;
         this.label5.Text = "Описание";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(9, 71);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(56, 14);
         this.label4.TabIndex = 11;
         this.label4.Text = "Название";
         // 
         // tbDescr
         // 
         this.tbDescr.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbDescr.Location = new System.Drawing.Point(71, 94);
         this.tbDescr.Multiline = true;
         this.tbDescr.Name = "tbDescr";
         this.tbDescr.Size = new System.Drawing.Size(370, 126);
         this.tbDescr.TabIndex = 10;
         this.tbDescr.TextChanged += new System.EventHandler(this.OnDataTextChanged);
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(71, 68);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(371, 20);
         this.tbName.TabIndex = 9;
         this.tbName.TextChanged += new System.EventHandler(this.OnDataTextChanged);
         // 
         // dtpFinish
         // 
         this.dtpFinish.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtpFinish.Location = new System.Drawing.Point(192, 40);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(95, 20);
         this.dtpFinish.TabIndex = 3;
         this.dtpFinish.ValueChanged += new System.EventHandler(this.dtpFinish_ValueChanged);
         // 
         // dtpStart
         // 
         this.dtpStart.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtpStart.Location = new System.Drawing.Point(71, 40);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(95, 20);
         this.dtpStart.TabIndex = 2;
         this.dtpStart.ValueChanged += new System.EventHandler(this.dtpStart_ValueChanged);
         // 
         // cbActionType
         // 
         this.cbActionType.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbActionType.FormattingEnabled = true;
         this.cbActionType.Location = new System.Drawing.Point(71, 12);
         this.cbActionType.Name = "cbActionType";
         this.cbActionType.Size = new System.Drawing.Size(374, 22);
         this.cbActionType.TabIndex = 8;
         this.cbActionType.SelectedIndexChanged += new System.EventHandler(this.cbActionType_SelectedIndexChanged);
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(170, 43);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 1;
         this.label3.Text = "по";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 43);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(53, 14);
         this.label2.TabIndex = 0;
         this.label2.Text = "Период с";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(8, 15);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(57, 14);
         this.label1.TabIndex = 4;
         this.label1.Text = "Тип акции";
         // 
         // lbPrice
         // 
         this.lbPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbPrice.FormattingEnabled = true;
         this.lbPrice.ItemHeight = 14;
         this.lbPrice.Location = new System.Drawing.Point(0, 25);
         this.lbPrice.Name = "lbPrice";
         this.lbPrice.Size = new System.Drawing.Size(457, 158);
         this.lbPrice.Sorted = true;
         this.lbPrice.TabIndex = 1;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.btnAddPrice,
            this.btnDelPrice});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(457, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(49, 22);
         this.toolStripLabel1.Text = "Товары";
         // 
         // btnAddPrice
         // 
         this.btnAddPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddPrice.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddPrice.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddPrice.Name = "btnAddPrice";
         this.btnAddPrice.Size = new System.Drawing.Size(23, 22);
         this.btnAddPrice.Text = "Добавить товар";
         this.btnAddPrice.Click += new System.EventHandler(this.btnAddPrice_Click);
         // 
         // btnDelPrice
         // 
         this.btnDelPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelPrice.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelPrice.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelPrice.Name = "btnDelPrice";
         this.btnDelPrice.Size = new System.Drawing.Size(23, 22);
         this.btnDelPrice.Text = "Удалить товар";
         this.btnDelPrice.Click += new System.EventHandler(this.btnDelPrice_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 411);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(838, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // FmAction
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(838, 433);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAction";
         this.Text = "Редактор акций";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ListBox lbAction;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAddAction;
      private System.Windows.Forms.ToolStripButton btnDelAction;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ListBox lbPrice;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnAddPrice;
      private System.Windows.Forms.ToolStripButton btnDelPrice;
      private System.Windows.Forms.ComboBox cbActionType;
      private System.Windows.Forms.TextBox tbDescr;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.DateTimePicker dtpTill;
      private System.Windows.Forms.DateTimePicker dtpFrom;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
   }
}