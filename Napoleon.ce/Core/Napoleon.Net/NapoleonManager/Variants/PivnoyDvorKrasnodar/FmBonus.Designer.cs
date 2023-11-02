namespace GRSoft.NapoleonManager
{
   partial class FmBonus
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBonus));
         this.lblFrom = new System.Windows.Forms.Label();
         this.dtFromDate = new System.Windows.Forms.DateTimePicker();
         this.lblTill = new System.Windows.Forms.Label();
         this.dtTillDate = new System.Windows.Forms.DateTimePicker();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.btnSelect = new System.Windows.Forms.Button();
         this.tbItem = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.tbQty = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.button1 = new System.Windows.Forms.Button();
         this.button2 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // lblFrom
         // 
         this.lblFrom.AutoSize = true;
         this.lblFrom.Location = new System.Drawing.Point(52, 24);
         this.lblFrom.Name = "lblFrom";
         this.lblFrom.Size = new System.Drawing.Size(20, 13);
         this.lblFrom.TabIndex = 4;
         this.lblFrom.Text = "С: ";
         // 
         // dtFromDate
         // 
         this.dtFromDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtFromDate.Location = new System.Drawing.Point(78, 21);
         this.dtFromDate.Name = "dtFromDate";
         this.dtFromDate.Size = new System.Drawing.Size(108, 20);
         this.dtFromDate.TabIndex = 4;
         this.dtFromDate.ValueChanged += new System.EventHandler(this.MarkChanged);
         // 
         // lblTill
         // 
         this.lblTill.AutoSize = true;
         this.lblTill.Location = new System.Drawing.Point(52, 50);
         this.lblTill.Name = "lblTill";
         this.lblTill.Size = new System.Drawing.Size(27, 13);
         this.lblTill.TabIndex = 2;
         this.lblTill.Text = "По: ";
         // 
         // dtTillDate
         // 
         this.dtTillDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtTillDate.Location = new System.Drawing.Point(78, 47);
         this.dtTillDate.Name = "dtTillDate";
         this.dtTillDate.Size = new System.Drawing.Size(108, 20);
         this.dtTillDate.TabIndex = 4;
         this.dtTillDate.ValueChanged += new System.EventHandler(this.MarkChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "ItemName";
         this.dataGridViewTextBoxColumn1.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Width = 350;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Qty";
         dataGridViewCellStyle1.Format = "N0";
         dataGridViewCellStyle1.NullValue = "0";
         this.dataGridViewTextBoxColumn2.DefaultCellStyle = dataGridViewCellStyle1;
         this.dataGridViewTextBoxColumn2.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Bonus";
         dataGridViewCellStyle2.Format = "N0";
         dataGridViewCellStyle2.NullValue = "0";
         this.dataGridViewTextBoxColumn3.DefaultCellStyle = dataGridViewCellStyle2;
         this.dataGridViewTextBoxColumn3.HeaderText = "Бонус";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // btnSelect
         // 
         this.btnSelect.Location = new System.Drawing.Point(319, 80);
         this.btnSelect.Name = "btnSelect";
         this.btnSelect.Size = new System.Drawing.Size(39, 25);
         this.btnSelect.TabIndex = 9;
         this.btnSelect.Text = "...";
         this.btnSelect.UseVisualStyleBackColor = true;
         this.btnSelect.Click += new System.EventHandler(this.btnSelect_Click);
         // 
         // tbItem
         // 
         this.tbItem.BackColor = System.Drawing.SystemColors.Window;
         this.tbItem.Location = new System.Drawing.Point(78, 83);
         this.tbItem.Name = "tbItem";
         this.tbItem.ReadOnly = true;
         this.tbItem.Size = new System.Drawing.Size(235, 20);
         this.tbItem.TabIndex = 8;
         this.tbItem.TextChanged += new System.EventHandler(this.MarkChanged);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(35, 84);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(41, 13);
         this.label1.TabIndex = 10;
         this.label1.Text = "Товар:";
         // 
         // tbQty
         // 
         this.tbQty.Location = new System.Drawing.Point(78, 109);
         this.tbQty.Name = "tbQty";
         this.tbQty.Size = new System.Drawing.Size(57, 20);
         this.tbQty.TabIndex = 12;
         this.tbQty.TextChanged += new System.EventHandler(this.MarkChanged);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(49, 112);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(23, 13);
         this.label2.TabIndex = 11;
         this.label2.Text = "шт.";
         // 
         // button1
         // 
         this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button1.Location = new System.Drawing.Point(319, 155);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 13;
         this.button1.Text = "ОК";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // button2
         // 
         this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.button2.Location = new System.Drawing.Point(12, 155);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(75, 23);
         this.button2.TabIndex = 14;
         this.button2.Text = "Отмена";
         this.button2.UseVisualStyleBackColor = true;
         this.button2.Click += new System.EventHandler(this.button2_Click);
         // 
         // FmBonus
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.button2;
         this.ClientSize = new System.Drawing.Size(419, 190);
         this.Controls.Add(this.button2);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.tbQty);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.btnSelect);
         this.Controls.Add(this.tbItem);
         this.Controls.Add(this.lblFrom);
         this.Controls.Add(this.dtFromDate);
         this.Controls.Add(this.lblTill);
         this.Controls.Add(this.dtTillDate);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmBonus";
         this.Text = "Акция на товар";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label lblFrom;
      private System.Windows.Forms.DateTimePicker dtFromDate;
      private System.Windows.Forms.Label lblTill;
      private System.Windows.Forms.DateTimePicker dtTillDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.Button btnSelect;
      private System.Windows.Forms.TextBox tbItem;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbQty;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.Button button2;
      
      
   }
}