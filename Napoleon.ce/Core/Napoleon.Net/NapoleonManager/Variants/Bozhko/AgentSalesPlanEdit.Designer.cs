namespace GRSoft.NapoleonManager
{
   partial class AgentSalesPlanEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AgentSalesPlanEdit));
         this.lblAgent = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOk = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // lblAgent
         // 
         this.lblAgent.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.lblAgent.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lblAgent.Location = new System.Drawing.Point(12, 9);
         this.lblAgent.Name = "lblAgent";
         this.lblAgent.Size = new System.Drawing.Size(340, 33);
         this.lblAgent.TabIndex = 0;
         this.lblAgent.Text = "Агент";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(75, 55);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(277, 20);
         this.tbName.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 58);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(57, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Название";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(56, 88);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(13, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "с";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(52, 114);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 13);
         this.label3.TabIndex = 4;
         this.label3.Text = "по";
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(75, 84);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(145, 20);
         this.dtpStart.TabIndex = 5;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(75, 110);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(145, 20);
         this.dtpEnd.TabIndex = 6;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(12, 169);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 7;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOk
         // 
         this.btnOk.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOk.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOk.Location = new System.Drawing.Point(277, 169);
         this.btnOk.Name = "btnOk";
         this.btnOk.Size = new System.Drawing.Size(75, 23);
         this.btnOk.TabIndex = 8;
         this.btnOk.Text = "Сохранить";
         this.btnOk.UseVisualStyleBackColor = true;
         // 
         // AgentSalesPlanEdit
         // 
         this.AcceptButton = this.btnOk;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(364, 204);
         this.Controls.Add(this.btnOk);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.lblAgent);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "AgentSalesPlanEdit";
         this.Text = "План";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label lblAgent;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOk;
   }
}