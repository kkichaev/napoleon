namespace GRSoft.NapoleonManager
{
   partial class FmTaskEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTaskEdit));
         this.label1 = new System.Windows.Forms.Label();
         this.tbTask = new System.Windows.Forms.TextBox();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnClear = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(5, 10);
         this.label1.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(59, 15);
         this.label1.TabIndex = 0;
         this.label1.Text = "Текс задачи";
         // 
         // tbTask
         // 
         this.tbTask.Location = new System.Drawing.Point(3, 35);
         this.tbTask.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.tbTask.Multiline = true;
         this.tbTask.Name = "tbTask";
         this.tbTask.Size = new System.Drawing.Size(288, 82);
         this.tbTask.TabIndex = 1;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(11, 119);
         this.btnOK.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(62, 27);
         this.btnOK.TabIndex = 2;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(88, 119);
         this.btnCancel.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(62, 27);
         this.btnCancel.TabIndex = 3;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnClear
         // 
         this.btnClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClear.Location = new System.Drawing.Point(255, 1);
         this.btnClear.Name = "btnClear";
         this.btnClear.Size = new System.Drawing.Size(35, 32);
         this.btnClear.TabIndex = 4;
         this.btnClear.UseVisualStyleBackColor = true;
         this.btnClear.Click += new System.EventHandler(this.btnClear_Click);
         // 
         // FmTaskEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(5F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(297, 156);
         this.Controls.Add(this.btnClear);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.tbTask);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmTaskEdit";
         this.Text = "Редактировать задачу";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbTask;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnClear;
   }
}