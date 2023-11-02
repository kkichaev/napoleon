namespace GRSoft.NapoleonManager
{
   partial class FmSelMtx
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSelMtx));
         this.panel1 = new System.Windows.Forms.Panel();
         this.button2 = new System.Windows.Forms.Button();
         this.button1 = new System.Windows.Forms.Button();
         this.listBox = new System.Windows.Forms.ListBox();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.button2);
         this.panel1.Controls.Add(this.button1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 235);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(283, 38);
         this.panel1.TabIndex = 0;
         // 
         // button2
         // 
         this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.button2.Location = new System.Drawing.Point(100, 5);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(91, 26);
         this.button2.TabIndex = 1;
         this.button2.Text = "Отменить";
         this.button2.UseVisualStyleBackColor = true;
         // 
         // button1
         // 
         this.button1.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.button1.Location = new System.Drawing.Point(3, 5);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(91, 26);
         this.button1.TabIndex = 0;
         this.button1.Text = "ОК";
         this.button1.UseVisualStyleBackColor = true;
         // 
         // listBox
         // 
         this.listBox.Dock = System.Windows.Forms.DockStyle.Fill;
         this.listBox.FormattingEnabled = true;
         this.listBox.ItemHeight = 17;
         this.listBox.Location = new System.Drawing.Point(0, 0);
         this.listBox.Name = "listBox";
         this.listBox.Size = new System.Drawing.Size(283, 235);
         this.listBox.TabIndex = 1;
         // 
         // FmSelMtx
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 17F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(283, 273);
         this.Controls.Add(this.listBox);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmSelMtx";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "Выберите матрицу";
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.ListBox listBox;
      private System.Windows.Forms.Button button2;
   }
}