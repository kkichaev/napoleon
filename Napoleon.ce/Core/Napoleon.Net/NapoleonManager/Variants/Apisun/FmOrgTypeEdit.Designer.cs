namespace GRSoft.NapoleonManager
{
   partial class FmOrgTypeEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgTypeEdit));
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.tbId = new System.Windows.Forms.TextBox();
         this.tbName = new System.Windows.Forms.TextBox();
         this.btnOK = new System.Windows.Forms.Button();
         this.bntCancel = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(16, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "ID";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(6, 44);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(83, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Наименование";
         // 
         // tbId
         // 
         this.tbId.Location = new System.Drawing.Point(34, 6);
         this.tbId.Name = "tbId";
         this.tbId.Size = new System.Drawing.Size(100, 20);
         this.tbId.TabIndex = 1;
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(95, 41);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(185, 20);
         this.tbName.TabIndex = 0;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(69, 80);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 4;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // bntCancel
         // 
         this.bntCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.bntCancel.Location = new System.Drawing.Point(164, 80);
         this.bntCancel.Name = "bntCancel";
         this.bntCancel.Size = new System.Drawing.Size(75, 23);
         this.bntCancel.TabIndex = 5;
         this.bntCancel.Text = "Отменить";
         this.bntCancel.UseVisualStyleBackColor = true;
         // 
         // FmOrgTypeEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.bntCancel;
         this.ClientSize = new System.Drawing.Size(292, 111);
         this.Controls.Add(this.bntCancel);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.tbId);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgTypeEdit";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "Изменить";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbId;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button bntCancel;
   }
}