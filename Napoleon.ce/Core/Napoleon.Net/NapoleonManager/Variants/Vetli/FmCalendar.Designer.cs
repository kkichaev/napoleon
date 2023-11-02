namespace GRSoft.NapoleonManager
{
   partial class FmCalendar
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCalendar));
         this.monthCalendar1 = new System.Windows.Forms.MonthCalendar();
         this.panel1 = new System.Windows.Forms.Panel();
         this.button1 = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // monthCalendar1
         // 
         this.monthCalendar1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.monthCalendar1.Location = new System.Drawing.Point(0, 0);
         this.monthCalendar1.MaxSelectionCount = 1;
         this.monthCalendar1.Name = "monthCalendar1";
         this.monthCalendar1.TabIndex = 0;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.button1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 162);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(166, 37);
         this.panel1.TabIndex = 1;
         // 
         // button1
         // 
         this.button1.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.button1.Location = new System.Drawing.Point(49, 6);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "ОК";
         this.button1.UseVisualStyleBackColor = true;
         // 
         // FmCalendar
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(166, 199);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.monthCalendar1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.MaximizeBox = false;
         this.MinimizeBox = false;
         this.Name = "FmCalendar";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "Выберите дату";
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.MonthCalendar monthCalendar1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button button1;
   }
}