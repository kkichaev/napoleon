namespace GRSoft.NapoleonManager
{
   partial class FmViewPhoto
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmViewPhoto));
         this.pbPhoto = new System.Windows.Forms.PictureBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.captionPnl = new System.Windows.Forms.Panel();
         this.lbDate = new System.Windows.Forms.Label();
         this.lbOrg = new System.Windows.Forms.Label();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnPrint = new System.Windows.Forms.ToolStripButton();
         this.bntRotLeft = new System.Windows.Forms.ToolStripButton();
         this.btnRotRight = new System.Windows.Forms.ToolStripButton();
         this.saveFileDialog1 = new System.Windows.Forms.SaveFileDialog();
         this.printDialog1 = new System.Windows.Forms.PrintDialog();
         ((System.ComponentModel.ISupportInitialize)(this.pbPhoto)).BeginInit();
         this.panel1.SuspendLayout();
         this.captionPnl.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // pbPhoto
         // 
         this.pbPhoto.Dock = System.Windows.Forms.DockStyle.Fill;
         this.pbPhoto.Location = new System.Drawing.Point(7, 8);
         this.pbPhoto.Name = "pbPhoto";
         this.pbPhoto.Size = new System.Drawing.Size(535, 346);
         this.pbPhoto.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
         this.pbPhoto.TabIndex = 0;
         this.pbPhoto.TabStop = false;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.pbPhoto);
         this.panel1.Controls.Add(this.captionPnl);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(549, 408);
         this.panel1.TabIndex = 1;
         // 
         // captionPnl
         // 
         this.captionPnl.BackColor = System.Drawing.Color.White;
         this.captionPnl.Controls.Add(this.lbDate);
         this.captionPnl.Controls.Add(this.lbOrg);
         this.captionPnl.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.captionPnl.Location = new System.Drawing.Point(7, 354);
         this.captionPnl.Name = "captionPnl";
         this.captionPnl.Size = new System.Drawing.Size(535, 46);
         this.captionPnl.TabIndex = 1;
         // 
         // lbDate
         // 
         this.lbDate.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lbDate.ForeColor = System.Drawing.Color.Red;
         this.lbDate.Location = new System.Drawing.Point(378, 0);
         this.lbDate.Name = "lbDate";
         this.lbDate.Size = new System.Drawing.Size(154, 45);
         this.lbDate.TabIndex = 4;
         this.lbDate.Text = "lbDate";
         this.lbDate.Visible = false;
         // 
         // lbOrg
         // 
         this.lbOrg.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lbOrg.ForeColor = System.Drawing.Color.Red;
         this.lbOrg.Location = new System.Drawing.Point(5, 0);
         this.lbOrg.Name = "lbOrg";
         this.lbOrg.Size = new System.Drawing.Size(367, 45);
         this.lbOrg.TabIndex = 3;
         this.lbOrg.Text = "lbOrg";
         this.lbOrg.Visible = false;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave,
            this.btnPrint,
            this.bntRotLeft,
            this.btnRotRight});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(549, 25);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // btnPrint
         // 
         this.btnPrint.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPrint.Image = ((System.Drawing.Image)(resources.GetObject("btnPrint.Image")));
         this.btnPrint.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPrint.Name = "btnPrint";
         this.btnPrint.Size = new System.Drawing.Size(23, 22);
         this.btnPrint.Text = "toolStripButton1";
         this.btnPrint.Click += new System.EventHandler(this.btnPrint_Click);
         // 
         // bntRotLeft
         // 
         this.bntRotLeft.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.bntRotLeft.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.object_rotate_left_3;
         this.bntRotLeft.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.bntRotLeft.Name = "bntRotLeft";
         this.bntRotLeft.Size = new System.Drawing.Size(23, 22);
         this.bntRotLeft.Text = "Повернуть налево";
         this.bntRotLeft.Click += new System.EventHandler(this.bntRotRight_Click);
         // 
         // btnRotRight
         // 
         this.btnRotRight.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRotRight.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.object_rotate_right_3;
         this.btnRotRight.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRotRight.Name = "btnRotRight";
         this.btnRotRight.Size = new System.Drawing.Size(23, 22);
         this.btnRotRight.Text = "Повернуть направо";
         this.btnRotRight.Click += new System.EventHandler(this.toolStripButton2_Click);
         // 
         // saveFileDialog1
         // 
         this.saveFileDialog1.Filter = "jpg|*.jpg";
         // 
         // printDialog1
         // 
         this.printDialog1.UseEXDialog = true;
         // 
         // FmViewPhoto
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(549, 433);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmViewPhoto";
         this.Text = "Просмотр";
         ((System.ComponentModel.ISupportInitialize)(this.pbPhoto)).EndInit();
         this.panel1.ResumeLayout(false);
         this.captionPnl.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.PictureBox pbPhoto;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SaveFileDialog saveFileDialog1;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.Panel captionPnl;
      private System.Windows.Forms.Label lbDate;
      private System.Windows.Forms.Label lbOrg;
      private System.Windows.Forms.ToolStripButton btnPrint;
      private System.Windows.Forms.PrintDialog printDialog1;
      private System.Windows.Forms.ToolStripButton bntRotLeft;
      private System.Windows.Forms.ToolStripButton btnRotRight;
   }
}