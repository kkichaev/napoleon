namespace GRSoft.NapoleonManager
{
   partial class FmOrgPlanogram
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgPlanogram));
         this.lvPhotos = new System.Windows.Forms.ListView();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.SuspendLayout();
         // 
         // lvPhotos
         // 
         this.lvPhotos.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvPhotos.LargeImageList = this.imageList1;
         this.lvPhotos.Location = new System.Drawing.Point(0, 0);
         this.lvPhotos.Name = "lvPhotos";
         this.lvPhotos.Size = new System.Drawing.Size(447, 386);
         this.lvPhotos.TabIndex = 0;
         this.lvPhotos.UseCompatibleStateImageBehavior = false;
         this.lvPhotos.DoubleClick += new System.EventHandler(this.lvPhotos_DoubleClick);
         // 
         // imageList1
         // 
         this.imageList1.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
         this.imageList1.ImageSize = new System.Drawing.Size(198, 198);
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // FmOrgPlanogram
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(447, 386);
         this.Controls.Add(this.lvPhotos);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgPlanogram";
         this.Text = "Планограммы точки по контракту";
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.ListView lvPhotos;
      private System.Windows.Forms.ImageList imageList1;
   }
}