namespace GRSoft.NapoleonManager
{
   partial class FmEditors
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmEditors));
         this.btnQuest = new System.Windows.Forms.Button();
         this.btnMon = new System.Windows.Forms.Button();
         this.btnSklad = new System.Windows.Forms.Button();
         this.btnPrice = new System.Windows.Forms.Button();
         this.btnMatrix = new System.Windows.Forms.Button();
         this.btnCause = new System.Windows.Forms.Button();
         this.btnStopList = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // btnQuest
         // 
         this.btnQuest.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnQuest.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.btnQuest.Location = new System.Drawing.Point(0, 0);
         this.btnQuest.Name = "btnQuest";
         this.btnQuest.Size = new System.Drawing.Size(254, 25);
         this.btnQuest.TabIndex = 0;
         this.btnQuest.Text = "Анкеты";
         this.btnQuest.UseVisualStyleBackColor = true;
         this.btnQuest.Click += new System.EventHandler(this.btnQuest_Click);
         // 
         // btnMon
         // 
         this.btnMon.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnMon.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.btnMon.Location = new System.Drawing.Point(0, 48);
         this.btnMon.Name = "btnMon";
         this.btnMon.Size = new System.Drawing.Size(254, 25);
         this.btnMon.TabIndex = 1;
         this.btnMon.Text = "Мониторинг";
         this.btnMon.UseVisualStyleBackColor = true;
         this.btnMon.Click += new System.EventHandler(this.btnMon_Click);
         // 
         // btnSklad
         // 
         this.btnSklad.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnSklad.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.btnSklad.Location = new System.Drawing.Point(0, 73);
         this.btnSklad.Name = "btnSklad";
         this.btnSklad.Size = new System.Drawing.Size(254, 25);
         this.btnSklad.TabIndex = 2;
         this.btnSklad.Text = "Привязка складов";
         this.btnSklad.UseVisualStyleBackColor = true;
         this.btnSklad.Click += new System.EventHandler(this.btnSklad_Click);
         // 
         // btnPrice
         // 
         this.btnPrice.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnPrice.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.btnPrice.Location = new System.Drawing.Point(0, 98);
         this.btnPrice.Name = "btnPrice";
         this.btnPrice.Size = new System.Drawing.Size(254, 25);
         this.btnPrice.TabIndex = 3;
         this.btnPrice.Text = "Прайс-лист";
         this.btnPrice.UseVisualStyleBackColor = true;
         this.btnPrice.Click += new System.EventHandler(this.btnPrice_Click);
         // 
         // btnMatrix
         // 
         this.btnMatrix.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnMatrix.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.btnMatrix.Location = new System.Drawing.Point(0, 123);
         this.btnMatrix.Name = "btnMatrix";
         this.btnMatrix.Size = new System.Drawing.Size(254, 25);
         this.btnMatrix.TabIndex = 4;
         this.btnMatrix.Text = "Привязка матриц";
         this.btnMatrix.UseVisualStyleBackColor = true;
         this.btnMatrix.Click += new System.EventHandler(this.btnMatrix_Click);
         // 
         // btnCause
         // 
         this.btnCause.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnCause.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.btnCause.Location = new System.Drawing.Point(0, 148);
         this.btnCause.Name = "btnCause";
         this.btnCause.Size = new System.Drawing.Size(254, 25);
         this.btnCause.TabIndex = 5;
         this.btnCause.Text = "Причины отсутствия";
         this.btnCause.UseVisualStyleBackColor = true;
         this.btnCause.Click += new System.EventHandler(this.btnCause_Click);
         // 
         // btnStopList
         // 
         this.btnStopList.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnStopList.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.btnStopList.Location = new System.Drawing.Point(0, 25);
         this.btnStopList.Name = "btnStopList";
         this.btnStopList.Size = new System.Drawing.Size(254, 23);
         this.btnStopList.TabIndex = 6;
         this.btnStopList.Text = "Стоп-лист";
         this.btnStopList.UseVisualStyleBackColor = true;
         this.btnStopList.Click += new System.EventHandler(this.bntStopList_Click);
         // 
         // FmEditors
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(254, 191);
         this.Controls.Add(this.btnCause);
         this.Controls.Add(this.btnMatrix);
         this.Controls.Add(this.btnPrice);
         this.Controls.Add(this.btnSklad);
         this.Controls.Add(this.btnMon);
         this.Controls.Add(this.btnStopList);
         this.Controls.Add(this.btnQuest);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmEditors";
         this.Text = "Редакторы";
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Button btnQuest;
      private System.Windows.Forms.Button btnMon;
      private System.Windows.Forms.Button btnSklad;
      private System.Windows.Forms.Button btnPrice;
      private System.Windows.Forms.Button btnMatrix;
      private System.Windows.Forms.Button btnCause;
      private System.Windows.Forms.Button btnStopList;
   }
}