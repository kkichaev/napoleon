namespace GRSoft.NapoleonManager
{
   partial class InputQty
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(InputQty));
         this.label1 = new System.Windows.Forms.Label();
         this.nmQty = new System.Windows.Forms.NumericUpDown();
         ((System.ComponentModel.ISupportInitialize)(this.nmQty)).BeginInit();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 13);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(144, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Введите  изменения плана";
         // 
         // nmQty
         // 
         this.nmQty.Location = new System.Drawing.Point(166, 11);
         this.nmQty.Maximum = new decimal(new int[] {
            100000,
            0,
            0,
            0});
         this.nmQty.Minimum = new decimal(new int[] {
            100000,
            0,
            0,
            -2147483648});
         this.nmQty.Name = "nmQty";
         this.nmQty.Size = new System.Drawing.Size(63, 20);
         this.nmQty.TabIndex = 2;
         // 
         // InputQty
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.BackColor = System.Drawing.SystemColors.AppWorkspace;
         this.ClientSize = new System.Drawing.Size(245, 39);
         this.Controls.Add(this.nmQty);
         this.Controls.Add(this.label1);
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Name = "InputQty";
         this.ShowIcon = false;
         this.ShowInTaskbar = false;
         this.StartPosition = System.Windows.Forms.FormStartPosition.Manual;
         this.Text = "Введите количество";
         this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.InputQty_KeyDown);
         ((System.ComponentModel.ISupportInitialize)(this.nmQty)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.NumericUpDown nmQty;
   }
}