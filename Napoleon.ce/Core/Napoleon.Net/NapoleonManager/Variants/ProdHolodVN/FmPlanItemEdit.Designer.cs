namespace GRSoft.NapoleonManager
{
   partial class FmPlanItemEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPlanItemEdit));
         this.label1 = new System.Windows.Forms.Label();
         this.tbQuantity = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.lbPriceItems = new System.Windows.Forms.ListBox();
         this.cbName = new System.Windows.Forms.ComboBox();
         this.label3 = new System.Windows.Forms.Label();
         this.cbUnit = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.btnEdit = new System.Windows.Forms.Button();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStripStatusLabel1 = new System.Windows.Forms.ToolStripStatusLabel();
         this.lblCount = new System.Windows.Forms.ToolStripStatusLabel();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.clear = new System.Windows.Forms.Button();
         this.label5 = new System.Windows.Forms.Label();
         this.name = new System.Windows.Forms.TextBox();
         this.statusStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 63);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(41, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Кол-во";
         // 
         // tbQuantity
         // 
         this.tbQuantity.Location = new System.Drawing.Point(114, 59);
         this.tbQuantity.Name = "tbQuantity";
         this.tbQuantity.Size = new System.Drawing.Size(104, 20);
         this.tbQuantity.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 9);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(96, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "Тип пункта плана";
         // 
         // lbPriceItems
         // 
         this.lbPriceItems.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.lbPriceItems.FormattingEnabled = true;
         this.lbPriceItems.Location = new System.Drawing.Point(114, 116);
         this.lbPriceItems.Name = "lbPriceItems";
         this.lbPriceItems.Size = new System.Drawing.Size(272, 238);
         this.lbPriceItems.Sorted = true;
         this.lbPriceItems.TabIndex = 3;
         // 
         // cbName
         // 
         this.cbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbName.FormattingEnabled = true;
         this.cbName.Location = new System.Drawing.Point(114, 5);
         this.cbName.Name = "cbName";
         this.cbName.Size = new System.Drawing.Size(272, 21);
         this.cbName.Sorted = true;
         this.cbName.TabIndex = 4;
         this.cbName.SelectionChangeCommitted += new System.EventHandler(this.cbName_SelectionChangeCommitted);
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 36);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(82, 13);
         this.label3.TabIndex = 5;
         this.label3.Text = "Ед. измерения";
         // 
         // cbUnit
         // 
         this.cbUnit.FormattingEnabled = true;
         this.cbUnit.Location = new System.Drawing.Point(114, 32);
         this.cbUnit.Name = "cbUnit";
         this.cbUnit.Size = new System.Drawing.Size(104, 21);
         this.cbUnit.Sorted = true;
         this.cbUnit.TabIndex = 6;
         this.cbUnit.SelectionChangeCommitted += new System.EventHandler(this.cbUnit_SelectionChangeCommitted);
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(12, 116);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(46, 13);
         this.label4.TabIndex = 7;
         this.label4.Text = "Товары";
         // 
         // btnEdit
         // 
         this.btnEdit.Location = new System.Drawing.Point(12, 142);
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(75, 23);
         this.btnEdit.TabIndex = 8;
         this.btnEdit.Text = "Изменить";
         this.btnEdit.UseVisualStyleBackColor = true;
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripStatusLabel1,
            this.lblCount});
         this.statusStrip1.Location = new System.Drawing.Point(0, 392);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(398, 22);
         this.statusStrip1.TabIndex = 9;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStripStatusLabel1
         // 
         this.toolStripStatusLabel1.Name = "toolStripStatusLabel1";
         this.toolStripStatusLabel1.Size = new System.Drawing.Size(60, 17);
         this.toolStripStatusLabel1.Text = "Выбрано:";
         // 
         // lblCount
         // 
         this.lblCount.Name = "lblCount";
         this.lblCount.Size = new System.Drawing.Size(13, 17);
         this.lblCount.Text = "0";
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(311, 363);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 10;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(225, 363);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 11;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // clear
         // 
         this.clear.Location = new System.Drawing.Point(12, 171);
         this.clear.Name = "clear";
         this.clear.Size = new System.Drawing.Size(75, 23);
         this.clear.TabIndex = 8;
         this.clear.Text = "Очистить";
         this.clear.UseVisualStyleBackColor = true;
         this.clear.Click += new System.EventHandler(this.clear_Click);
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(12, 89);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(57, 13);
         this.label5.TabIndex = 0;
         this.label5.Text = "Название";
         // 
         // name
         // 
         this.name.Location = new System.Drawing.Point(114, 85);
         this.name.Name = "name";
         this.name.Size = new System.Drawing.Size(272, 20);
         this.name.TabIndex = 1;
         // 
         // FmPlanItemEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(398, 414);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.clear);
         this.Controls.Add(this.btnEdit);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.cbUnit);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.cbName);
         this.Controls.Add(this.lbPriceItems);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.name);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.tbQuantity);
         this.Controls.Add(this.label1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPlanItemEdit";
         this.Text = "Редактировать";
         this.Load += new System.EventHandler(this.FmPlanItemEdit_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmPlanItemEdit_FormClosed);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPlanItemEdit_FormClosing);
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbQuantity;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ListBox lbPriceItems;
      private System.Windows.Forms.ComboBox cbName;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbUnit;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Button btnEdit;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripStatusLabel toolStripStatusLabel1;
      private System.Windows.Forms.ToolStripStatusLabel lblCount;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button clear;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.TextBox name;
   }
}