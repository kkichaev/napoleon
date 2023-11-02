namespace GRSoft.NapoleonManager
{
   partial class FmContractPhotos
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmContractPhotos));
         this.listView = new System.Windows.Forms.ListView();
         this.imageList = new System.Windows.Forms.ImageList(this.components);
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // listView
         // 
         this.listView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.listView.LargeImageList = this.imageList;
         this.listView.Location = new System.Drawing.Point(0, 0);
         this.listView.Name = "listView";
         this.listView.Size = new System.Drawing.Size(676, 566);
         this.listView.TabIndex = 0;
         this.listView.UseCompatibleStateImageBehavior = false;
         this.listView.DoubleClick += new System.EventHandler(this.listView_DoubleClick);
         // 
         // imageList
         // 
         this.imageList.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
         this.imageList.ImageSize = new System.Drawing.Size(128, 128);
         this.imageList.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 566);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(676, 55);
         this.panel1.TabIndex = 1;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(589, 17);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 25);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // FmContractPhotos
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(676, 621);
         this.Controls.Add(this.listView);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmContractPhotos";
         this.Text = "Планограммы точек";
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.ListView listView;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.ImageList imageList;
   }
}