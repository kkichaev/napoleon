namespace GRSoft.NapoleonManager
{
   partial class FmDataBase
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDataBase));
         this.btnOrgEd = new System.Windows.Forms.Button();
         this.btnCityEd = new System.Windows.Forms.Button();
         this.btnSlsnetEd = new System.Windows.Forms.Button();
         this.btnOrgAssign = new System.Windows.Forms.Button();
         this.edContractEd = new System.Windows.Forms.Button();
         this.btnScriptAssign = new System.Windows.Forms.Button();
         this.btnShelfPart = new System.Windows.Forms.Button();
         this.btnReturnCause = new System.Windows.Forms.Button();
         this.btnPlan = new System.Windows.Forms.Button();
         this.btnPrice = new System.Windows.Forms.Button();
         this.btnAgentPlan = new System.Windows.Forms.Button();
         this.btnVisitPlan = new System.Windows.Forms.Button();
         this.btnCodeTT = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // btnOrgEd
         // 
         this.btnOrgEd.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnOrgEd.Location = new System.Drawing.Point(0, 0);
         this.btnOrgEd.Name = "btnOrgEd";
         this.btnOrgEd.Size = new System.Drawing.Size(266, 23);
         this.btnOrgEd.TabIndex = 0;
         this.btnOrgEd.Text = "Редактор организаций";
         this.btnOrgEd.UseVisualStyleBackColor = true;
         this.btnOrgEd.Click += new System.EventHandler(this.btnOrgEd_Click);
         // 
         // btnCityEd
         // 
         this.btnCityEd.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnCityEd.Location = new System.Drawing.Point(0, 23);
         this.btnCityEd.Name = "btnCityEd";
         this.btnCityEd.Size = new System.Drawing.Size(266, 23);
         this.btnCityEd.TabIndex = 1;
         this.btnCityEd.Text = "Редактор городов";
         this.btnCityEd.UseVisualStyleBackColor = true;
         this.btnCityEd.Click += new System.EventHandler(this.btnCityEd_Click);
         // 
         // btnSlsnetEd
         // 
         this.btnSlsnetEd.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnSlsnetEd.Location = new System.Drawing.Point(0, 46);
         this.btnSlsnetEd.Name = "btnSlsnetEd";
         this.btnSlsnetEd.Size = new System.Drawing.Size(266, 23);
         this.btnSlsnetEd.TabIndex = 2;
         this.btnSlsnetEd.Text = "Редактор сетей";
         this.btnSlsnetEd.UseVisualStyleBackColor = true;
         this.btnSlsnetEd.Click += new System.EventHandler(this.btnSlsnetEd_Click);
         // 
         // btnOrgAssign
         // 
         this.btnOrgAssign.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnOrgAssign.Location = new System.Drawing.Point(0, 69);
         this.btnOrgAssign.Name = "btnOrgAssign";
         this.btnOrgAssign.Size = new System.Drawing.Size(266, 23);
         this.btnOrgAssign.TabIndex = 3;
         this.btnOrgAssign.Text = "Назначение точек";
         this.btnOrgAssign.UseVisualStyleBackColor = true;
         this.btnOrgAssign.Click += new System.EventHandler(this.btnOrgAssign_Click);
         // 
         // edContractEd
         // 
         this.edContractEd.Dock = System.Windows.Forms.DockStyle.Top;
         this.edContractEd.Location = new System.Drawing.Point(0, 92);
         this.edContractEd.Name = "edContractEd";
         this.edContractEd.Size = new System.Drawing.Size(266, 23);
         this.edContractEd.TabIndex = 4;
         this.edContractEd.Text = "Редактор контрактов";
         this.edContractEd.UseVisualStyleBackColor = true;
         this.edContractEd.Click += new System.EventHandler(this.edContractEd_Click);
         // 
         // btnScriptAssign
         // 
         this.btnScriptAssign.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnScriptAssign.Location = new System.Drawing.Point(0, 115);
         this.btnScriptAssign.Name = "btnScriptAssign";
         this.btnScriptAssign.Size = new System.Drawing.Size(266, 23);
         this.btnScriptAssign.TabIndex = 5;
         this.btnScriptAssign.Text = "Назначение сценариев";
         this.btnScriptAssign.UseVisualStyleBackColor = true;
         this.btnScriptAssign.Click += new System.EventHandler(this.btnScriptAssign_Click);
         // 
         // btnShelfPart
         // 
         this.btnShelfPart.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnShelfPart.Location = new System.Drawing.Point(0, 138);
         this.btnShelfPart.Name = "btnShelfPart";
         this.btnShelfPart.Size = new System.Drawing.Size(266, 23);
         this.btnShelfPart.TabIndex = 6;
         this.btnShelfPart.Text = "Доля полки";
         this.btnShelfPart.UseVisualStyleBackColor = true;
         this.btnShelfPart.Click += new System.EventHandler(this.btnShelfPart_Click);
         // 
         // btnReturnCause
         // 
         this.btnReturnCause.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnReturnCause.Location = new System.Drawing.Point(0, 161);
         this.btnReturnCause.Name = "btnReturnCause";
         this.btnReturnCause.Size = new System.Drawing.Size(266, 23);
         this.btnReturnCause.TabIndex = 7;
         this.btnReturnCause.Text = "Причины возврата";
         this.btnReturnCause.UseVisualStyleBackColor = true;
         this.btnReturnCause.Visible = false;
         this.btnReturnCause.Click += new System.EventHandler(this.btnReturnCause_Click);
         // 
         // btnPlan
         // 
         this.btnPlan.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnPlan.Location = new System.Drawing.Point(0, 184);
         this.btnPlan.Name = "btnPlan";
         this.btnPlan.Size = new System.Drawing.Size(266, 23);
         this.btnPlan.TabIndex = 8;
         this.btnPlan.Text = "План";
         this.btnPlan.UseVisualStyleBackColor = true;
         this.btnPlan.Click += new System.EventHandler(this.btnPlan_Click);
         // 
         // btnPrice
         // 
         this.btnPrice.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnPrice.Location = new System.Drawing.Point(0, 207);
         this.btnPrice.Name = "btnPrice";
         this.btnPrice.Size = new System.Drawing.Size(266, 23);
         this.btnPrice.TabIndex = 9;
         this.btnPrice.Text = "Редактор товара";
         this.btnPrice.UseVisualStyleBackColor = true;
         this.btnPrice.Click += new System.EventHandler(this.btnPrice_Click);
         // 
         // btnAgentPlan
         // 
         this.btnAgentPlan.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnAgentPlan.Location = new System.Drawing.Point(0, 230);
         this.btnAgentPlan.Name = "btnAgentPlan";
         this.btnAgentPlan.Size = new System.Drawing.Size(266, 23);
         this.btnAgentPlan.TabIndex = 10;
         this.btnAgentPlan.Text = "Плановая доля полки по агенту";
         this.btnAgentPlan.UseVisualStyleBackColor = true;
         this.btnAgentPlan.Click += new System.EventHandler(this.btnAgentPlan_Click);
         // 
         // btnVisitPlan
         // 
         this.btnVisitPlan.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnVisitPlan.Location = new System.Drawing.Point(0, 253);
         this.btnVisitPlan.Name = "btnVisitPlan";
         this.btnVisitPlan.Size = new System.Drawing.Size(266, 23);
         this.btnVisitPlan.TabIndex = 11;
         this.btnVisitPlan.Text = "План по визитам";
         this.btnVisitPlan.UseVisualStyleBackColor = true;
         this.btnVisitPlan.Click += new System.EventHandler(this.btnVisitPlan_Click);
         // 
         // btnCodeTT
         // 
         this.btnCodeTT.Dock = System.Windows.Forms.DockStyle.Top;
         this.btnCodeTT.Location = new System.Drawing.Point(0, 276);
         this.btnCodeTT.Name = "btnCodeTT";
         this.btnCodeTT.Size = new System.Drawing.Size(266, 23);
         this.btnCodeTT.TabIndex = 12;
         this.btnCodeTT.Text = "Привязка кодов товара";
         this.btnCodeTT.UseVisualStyleBackColor = true;
         this.btnCodeTT.Click += new System.EventHandler(this.btnEditPLU_Click);
         // 
         // FmDataBase
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(266, 357);
         this.Controls.Add(this.btnCodeTT);
         this.Controls.Add(this.btnVisitPlan);
         this.Controls.Add(this.btnAgentPlan);
         this.Controls.Add(this.btnPrice);
         this.Controls.Add(this.btnPlan);
         this.Controls.Add(this.btnReturnCause);
         this.Controls.Add(this.btnShelfPart);
         this.Controls.Add(this.btnScriptAssign);
         this.Controls.Add(this.edContractEd);
         this.Controls.Add(this.btnOrgAssign);
         this.Controls.Add(this.btnSlsnetEd);
         this.Controls.Add(this.btnCityEd);
         this.Controls.Add(this.btnOrgEd);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDataBase";
         this.Text = "База данных";
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Button btnOrgEd;
      private System.Windows.Forms.Button btnCityEd;
      private System.Windows.Forms.Button btnSlsnetEd;
      private System.Windows.Forms.Button btnOrgAssign;
      private System.Windows.Forms.Button edContractEd;
      private System.Windows.Forms.Button btnScriptAssign;
      private System.Windows.Forms.Button btnShelfPart;
      private System.Windows.Forms.Button btnReturnCause;
      private System.Windows.Forms.Button btnPlan;
      private System.Windows.Forms.Button btnPrice;
      private System.Windows.Forms.Button btnAgentPlan;
      private System.Windows.Forms.Button btnVisitPlan;
      private System.Windows.Forms.Button btnCodeTT;
   }
}