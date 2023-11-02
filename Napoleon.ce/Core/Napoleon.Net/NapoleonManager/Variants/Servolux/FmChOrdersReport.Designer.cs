namespace GRSoft.NapoleonManager
{
   partial class FmChOrdersReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmChOrdersReport));
         this.cancel = new System.Windows.Forms.Button();
         this.ok = new System.Windows.Forms.Button();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.lbFactories = new System.Windows.Forms.CheckedListBox();
         this.linkLabel1 = new System.Windows.Forms.LinkLabel();
         this.linkLabel2 = new System.Windows.Forms.LinkLabel();
         this.cbInKG = new System.Windows.Forms.CheckBox();
         this.cbInBox = new System.Windows.Forms.CheckBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.checkBox3 = new System.Windows.Forms.CheckBox();
         this.tvAgents = new System.Windows.Forms.TreeView();
         this.linkLabel3 = new System.Windows.Forms.LinkLabel();
         this.linkLabel4 = new System.Windows.Forms.LinkLabel();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(366, 456);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 31;
         this.cancel.Text = "Закрыть";
         this.cancel.UseVisualStyleBackColor = true;
         this.cancel.Click += new System.EventHandler(this.cancel_Click);
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(12, 456);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 30;
         this.ok.Text = "Excel";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(229, 15);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 13);
         this.label3.TabIndex = 29;
         this.label3.Text = "по";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 15);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(42, 13);
         this.label2.TabIndex = 28;
         this.label2.Text = "Дата с";
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(254, 12);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(141, 20);
         this.dtpEnd.TabIndex = 27;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(60, 12);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(141, 20);
         this.dtpBegin.TabIndex = 26;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // lbFactories
         // 
         this.lbFactories.CheckOnClick = true;
         this.lbFactories.FormattingEnabled = true;
         this.lbFactories.Location = new System.Drawing.Point(15, 125);
         this.lbFactories.Name = "lbFactories";
         this.lbFactories.Size = new System.Drawing.Size(201, 199);
         this.lbFactories.TabIndex = 34;
         // 
         // linkLabel1
         // 
         this.linkLabel1.AutoSize = true;
         this.linkLabel1.Location = new System.Drawing.Point(16, 48);
         this.linkLabel1.Name = "linkLabel1";
         this.linkLabel1.Size = new System.Drawing.Size(65, 13);
         this.linkLabel1.TabIndex = 37;
         this.linkLabel1.TabStop = true;
         this.linkLabel1.Text = "Выбор SKU";
         this.linkLabel1.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel1_LinkClicked);
         // 
         // linkLabel2
         // 
         this.linkLabel2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.linkLabel2.AutoSize = true;
         this.linkLabel2.Location = new System.Drawing.Point(362, 48);
         this.linkLabel2.Name = "linkLabel2";
         this.linkLabel2.Size = new System.Drawing.Size(79, 13);
         this.linkLabel2.TabIndex = 38;
         this.linkLabel2.TabStop = true;
         this.linkLabel2.Text = "Очиcтить SKU";
         this.linkLabel2.Visible = false;
         this.linkLabel2.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel2_LinkClicked);
         // 
         // cbInKG
         // 
         this.cbInKG.AutoSize = true;
         this.cbInKG.Checked = true;
         this.cbInKG.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbInKG.Location = new System.Drawing.Point(20, 32);
         this.cbInKG.Name = "cbInKG";
         this.cbInKG.Size = new System.Drawing.Size(46, 17);
         this.cbInKG.TabIndex = 39;
         this.cbInKG.Text = "в кг";
         this.cbInKG.UseVisualStyleBackColor = true;
         // 
         // cbInBox
         // 
         this.cbInBox.AutoSize = true;
         this.cbInBox.Checked = true;
         this.cbInBox.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbInBox.Location = new System.Drawing.Point(20, 56);
         this.cbInBox.Name = "cbInBox";
         this.cbInBox.Size = new System.Drawing.Size(73, 17);
         this.cbInBox.TabIndex = 40;
         this.cbInBox.Text = "в ящиках";
         this.cbInBox.UseVisualStyleBackColor = true;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.cbInKG);
         this.groupBox1.Controls.Add(this.cbInBox);
         this.groupBox1.Location = new System.Drawing.Point(15, 342);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(201, 96);
         this.groupBox1.TabIndex = 41;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Выводить отчет";
         // 
         // checkBox3
         // 
         this.checkBox3.AutoSize = true;
         this.checkBox3.Checked = true;
         this.checkBox3.CheckState = System.Windows.Forms.CheckState.Checked;
         this.checkBox3.Location = new System.Drawing.Point(19, 102);
         this.checkBox3.Name = "checkBox3";
         this.checkBox3.Size = new System.Drawing.Size(73, 17);
         this.checkBox3.TabIndex = 42;
         this.checkBox3.Text = "Фабрики";
         this.checkBox3.UseVisualStyleBackColor = true;
         this.checkBox3.CheckedChanged += new System.EventHandler(this.checkBox3_CheckedChanged);
         // 
         // tvAgents
         // 
         this.tvAgents.CheckBoxes = true;
         this.tvAgents.Location = new System.Drawing.Point(234, 125);
         this.tvAgents.Name = "tvAgents";
         this.tvAgents.Size = new System.Drawing.Size(195, 199);
         this.tvAgents.TabIndex = 44;
         this.tvAgents.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvAgents_AfterCheck);
         // 
         // linkLabel3
         // 
         this.linkLabel3.AutoSize = true;
         this.linkLabel3.Location = new System.Drawing.Point(16, 74);
         this.linkLabel3.Name = "linkLabel3";
         this.linkLabel3.Size = new System.Drawing.Size(112, 13);
         this.linkLabel3.TabIndex = 45;
         this.linkLabel3.TabStop = true;
         this.linkLabel3.Text = "Выбор контрагентов";
         this.linkLabel3.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel3_LinkClicked);
         // 
         // linkLabel4
         // 
         this.linkLabel4.AutoSize = true;
         this.linkLabel4.Location = new System.Drawing.Point(315, 74);
         this.linkLabel4.Name = "linkLabel4";
         this.linkLabel4.Size = new System.Drawing.Size(126, 13);
         this.linkLabel4.TabIndex = 46;
         this.linkLabel4.TabStop = true;
         this.linkLabel4.Text = "Очистить контрагентов";
         this.linkLabel4.Visible = false;
         this.linkLabel4.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel4_LinkClicked);
         // 
         // FmChOrdersReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(453, 491);
         this.Controls.Add(this.linkLabel4);
         this.Controls.Add(this.linkLabel3);
         this.Controls.Add(this.tvAgents);
         this.Controls.Add(this.checkBox3);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.linkLabel2);
         this.Controls.Add(this.linkLabel1);
         this.Controls.Add(this.lbFactories);
         this.Controls.Add(this.cancel);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmChOrdersReport";
         this.Text = "Подрезка заказов";
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.CheckedListBox lbFactories;
      private System.Windows.Forms.LinkLabel linkLabel1;
      private System.Windows.Forms.LinkLabel linkLabel2;
      private System.Windows.Forms.CheckBox cbInKG;
      private System.Windows.Forms.CheckBox cbInBox;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.CheckBox checkBox3;
      private System.Windows.Forms.TreeView tvAgents;
      private System.Windows.Forms.LinkLabel linkLabel3;
      private System.Windows.Forms.LinkLabel linkLabel4;
   }
}