namespace GRSoft.Ads
{
   partial class FmUserOrderPrepareReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmUserOrderPrepareReport));
         this.gbMainData = new System.Windows.Forms.GroupBox();
         this.cbAddress = new System.Windows.Forms.CheckBox();
         this.cbText = new System.Windows.Forms.CheckBox();
         this.cbDate = new System.Windows.Forms.CheckBox();
         this.cbBrigade = new System.Windows.Forms.CheckBox();
         this.cbNumber = new System.Windows.Forms.CheckBox();
         this.cbItems = new System.Windows.Forms.CheckBox();
         this.gbDetailData = new System.Windows.Forms.GroupBox();
         this.cbCost = new System.Windows.Forms.CheckBox();
         this.cbQty = new System.Windows.Forms.CheckBox();
         this.cbName = new System.Windows.Forms.CheckBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnExcel = new System.Windows.Forms.Button();
         this.btnClose = new System.Windows.Forms.Button();
         this.gbMainData.SuspendLayout();
         this.gbDetailData.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // gbMainData
         // 
         this.gbMainData.Controls.Add(this.cbAddress);
         this.gbMainData.Controls.Add(this.cbText);
         this.gbMainData.Controls.Add(this.cbDate);
         this.gbMainData.Controls.Add(this.cbBrigade);
         this.gbMainData.Controls.Add(this.cbNumber);
         this.gbMainData.Location = new System.Drawing.Point(12, 12);
         this.gbMainData.Name = "gbMainData";
         this.gbMainData.Size = new System.Drawing.Size(310, 100);
         this.gbMainData.TabIndex = 0;
         this.gbMainData.TabStop = false;
         this.gbMainData.Text = "Заявки";
         // 
         // cbAddress
         // 
         this.cbAddress.AutoSize = true;
         this.cbAddress.Location = new System.Drawing.Point(102, 18);
         this.cbAddress.Name = "cbAddress";
         this.cbAddress.Size = new System.Drawing.Size(57, 17);
         this.cbAddress.TabIndex = 7;
         this.cbAddress.Tag = "Address";
         this.cbAddress.Text = "Адрес";
         this.cbAddress.UseVisualStyleBackColor = true;
         // 
         // cbText
         // 
         this.cbText.AutoSize = true;
         this.cbText.Location = new System.Drawing.Point(12, 72);
         this.cbText.Name = "cbText";
         this.cbText.Size = new System.Drawing.Size(89, 17);
         this.cbText.TabIndex = 5;
         this.cbText.Tag = "Remark";
         this.cbText.Text = "Содержание";
         this.cbText.UseVisualStyleBackColor = true;
         // 
         // cbDate
         // 
         this.cbDate.AutoSize = true;
         this.cbDate.Location = new System.Drawing.Point(12, 54);
         this.cbDate.Name = "cbDate";
         this.cbDate.Size = new System.Drawing.Size(69, 17);
         this.cbDate.TabIndex = 2;
         this.cbDate.Tag = "Created";
         this.cbDate.Text = "Создана";
         this.cbDate.UseVisualStyleBackColor = true;
         // 
         // cbBrigade
         // 
         this.cbBrigade.AutoSize = true;
         this.cbBrigade.Location = new System.Drawing.Point(12, 36);
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(68, 17);
         this.cbBrigade.TabIndex = 1;
         this.cbBrigade.Tag = "BrigadeName";
         this.cbBrigade.Text = "Бригада";
         this.cbBrigade.UseVisualStyleBackColor = true;
         // 
         // cbNumber
         // 
         this.cbNumber.AutoSize = true;
         this.cbNumber.Location = new System.Drawing.Point(12, 18);
         this.cbNumber.Name = "cbNumber";
         this.cbNumber.Size = new System.Drawing.Size(60, 17);
         this.cbNumber.TabIndex = 0;
         this.cbNumber.Tag = "Number";
         this.cbNumber.Text = "Номер";
         this.cbNumber.UseVisualStyleBackColor = true;
         // 
         // cbItems
         // 
         this.cbItems.AutoSize = true;
         this.cbItems.Location = new System.Drawing.Point(16, 122);
         this.cbItems.Name = "cbItems";
         this.cbItems.Size = new System.Drawing.Size(84, 17);
         this.cbItems.TabIndex = 1;
         this.cbItems.Text = "Материалы";
         this.cbItems.UseVisualStyleBackColor = true;
         this.cbItems.CheckedChanged += new System.EventHandler(this.cbItems_CheckedChanged);
         // 
         // gbDetailData
         // 
         this.gbDetailData.Controls.Add(this.cbCost);
         this.gbDetailData.Controls.Add(this.cbQty);
         this.gbDetailData.Controls.Add(this.cbName);
         this.gbDetailData.Location = new System.Drawing.Point(16, 145);
         this.gbDetailData.Name = "gbDetailData";
         this.gbDetailData.Size = new System.Drawing.Size(306, 81);
         this.gbDetailData.TabIndex = 2;
         this.gbDetailData.TabStop = false;
         this.gbDetailData.Text = "Материалы";
         // 
         // cbCost
         // 
         this.cbCost.AutoSize = true;
         this.cbCost.Location = new System.Drawing.Point(7, 56);
         this.cbCost.Name = "cbCost";
         this.cbCost.Size = new System.Drawing.Size(52, 17);
         this.cbCost.TabIndex = 2;
         this.cbCost.Tag = "Cost";
         this.cbCost.Text = "Цена";
         this.cbCost.UseVisualStyleBackColor = true;
         // 
         // cbQty
         // 
         this.cbQty.AutoSize = true;
         this.cbQty.Location = new System.Drawing.Point(7, 37);
         this.cbQty.Name = "cbQty";
         this.cbQty.Size = new System.Drawing.Size(85, 17);
         this.cbQty.TabIndex = 1;
         this.cbQty.Tag = "Qty";
         this.cbQty.Text = "Количество";
         this.cbQty.UseVisualStyleBackColor = true;
         // 
         // cbName
         // 
         this.cbName.AutoSize = true;
         this.cbName.Location = new System.Drawing.Point(7, 18);
         this.cbName.Name = "cbName";
         this.cbName.Size = new System.Drawing.Size(102, 17);
         this.cbName.TabIndex = 0;
         this.cbName.Tag = "Name";
         this.cbName.Text = "Наименование";
         this.cbName.UseVisualStyleBackColor = true;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnExcel);
         this.panel1.Controls.Add(this.btnClose);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 234);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(334, 39);
         this.panel1.TabIndex = 3;
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(201, 9);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 1;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // btnClose
         // 
         this.btnClose.Location = new System.Drawing.Point(36, 8);
         this.btnClose.Name = "btnClose";
         this.btnClose.Size = new System.Drawing.Size(75, 23);
         this.btnClose.TabIndex = 0;
         this.btnClose.Text = "Закрыть";
         this.btnClose.UseVisualStyleBackColor = true;
         // 
         // FmUserOrderPrepareReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(334, 273);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.gbDetailData);
         this.Controls.Add(this.cbItems);
         this.Controls.Add(this.gbMainData);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmUserOrderPrepareReport";
         this.Text = "Сформировать отчет по заявкам";
         this.Load += new System.EventHandler(this.FmOrderPrepareReport_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmOrderPrepareReport_FormClosed);
         this.gbMainData.ResumeLayout(false);
         this.gbMainData.PerformLayout();
         this.gbDetailData.ResumeLayout(false);
         this.gbDetailData.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.GroupBox gbMainData;
      private System.Windows.Forms.CheckBox cbItems;
      private System.Windows.Forms.GroupBox gbDetailData;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.Button btnClose;
      private System.Windows.Forms.CheckBox cbDate;
      private System.Windows.Forms.CheckBox cbBrigade;
      private System.Windows.Forms.CheckBox cbNumber;
      private System.Windows.Forms.CheckBox cbAddress;
      private System.Windows.Forms.CheckBox cbText;
      private System.Windows.Forms.CheckBox cbCost;
      private System.Windows.Forms.CheckBox cbQty;
      private System.Windows.Forms.CheckBox cbName;
   }
}