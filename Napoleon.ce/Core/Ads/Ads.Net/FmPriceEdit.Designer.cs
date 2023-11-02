namespace GRSoft.Ads
{
   partial class FmPriceEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPriceEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.tbCost = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.tbQty = new System.Windows.Forms.TextBox();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 104);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(327, 43);
         this.panel1.TabIndex = 3;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(211, 10);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 25);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(130, 10);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 25);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Наименование";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 59);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(64, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Стоимость";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(12, 28);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(309, 20);
         this.tbName.TabIndex = 1;
         // 
         // tbCost
         // 
         this.tbCost.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbCost.Location = new System.Drawing.Point(12, 79);
         this.tbCost.Name = "tbCost";
         this.tbCost.Size = new System.Drawing.Size(309, 20);
         this.tbCost.TabIndex = 2;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(15, 106);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(69, 14);
         this.label3.TabIndex = 4;
         this.label3.Text = "Количество";
         this.label3.Visible = false;
         // 
         // tbQty
         // 
         this.tbQty.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbQty.Location = new System.Drawing.Point(12, 124);
         this.tbQty.Name = "tbQty";
         this.tbQty.Size = new System.Drawing.Size(309, 20);
         this.tbQty.TabIndex = 5;
         this.tbQty.Visible = false;
         // 
         // FmPriceEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(327, 147);
         this.Controls.Add(this.tbQty);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.tbCost);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceEdit";
         this.Text = "FmPriceEdit";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPriceEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.TextBox tbCost;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox tbQty;
   }
}