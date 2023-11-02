namespace GRSoft.NapoleonManager
{
   partial class FmDistrRepParam
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistrRepParam));
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         this.button2 = new System.Windows.Forms.Button();
         this.button1 = new System.Windows.Forms.Button();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.cbFolder = new System.Windows.Forms.ComboBox();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.cbFolder);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Controls.Add(this.cbDivisions);
         this.panel1.Controls.Add(this.dpv);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(663, 449);
         this.panel1.TabIndex = 0;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.button2);
         this.panel2.Controls.Add(this.button1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 396);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(663, 53);
         this.panel2.TabIndex = 1;
         // 
         // button2
         // 
         this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.button2.Location = new System.Drawing.Point(485, 18);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(75, 23);
         this.button2.TabIndex = 1;
         this.button2.Text = "Отменить";
         this.button2.UseVisualStyleBackColor = true;
         // 
         // button1
         // 
         this.button1.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.button1.Location = new System.Drawing.Point(566, 18);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "OK";
         this.button1.UseVisualStyleBackColor = true;
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(105, 43);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(250, 21);
         this.cbDivisions.TabIndex = 9;
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2018, 7, 25, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(12, 9);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 27);
         this.dpv.Start = new System.DateTime(2018, 7, 25, 0, 0, 0, 0);
         this.dpv.TabIndex = 6;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 46);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(87, 13);
         this.label1.TabIndex = 11;
         this.label1.Text = "Подразделения";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 84);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(80, 13);
         this.label2.TabIndex = 13;
         this.label2.Text = "Группа товара";
         // 
         // cbFolder
         // 
         this.cbFolder.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbFolder.FormattingEnabled = true;
         this.cbFolder.Location = new System.Drawing.Point(104, 79);
         this.cbFolder.Name = "cbFolder";
         this.cbFolder.Size = new System.Drawing.Size(250, 21);
         this.cbFolder.TabIndex = 12;
         // 
         // FmDistrRepParam
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(663, 449);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistrRepParam";
         this.Text = "Наличие товара";
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button button2;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbFolder;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbDivisions;
      private DatePeriodView dpv;
   }
}