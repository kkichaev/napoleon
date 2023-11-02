namespace GRSoft.NapoleonManager
{
    partial class BonusPrice
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

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
         this.tbItem = new System.Windows.Forms.TextBox();
         this.btnSelect = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.tbQty = new System.Windows.Forms.TextBox();
         this.SuspendLayout();
         // 
         // tbItem
         // 
         this.tbItem.BackColor = System.Drawing.SystemColors.Window;
         this.tbItem.Location = new System.Drawing.Point(88, 3);
         this.tbItem.Name = "tbItem";
         this.tbItem.ReadOnly = true;
         this.tbItem.Size = new System.Drawing.Size(235, 20);
         this.tbItem.TabIndex = 0;
         this.tbItem.TextChanged += new System.EventHandler(this.tbItem_TextChanged);
         // 
         // btnSelect
         // 
         this.btnSelect.Location = new System.Drawing.Point(329, 0);
         this.btnSelect.Name = "btnSelect";
         this.btnSelect.Size = new System.Drawing.Size(39, 25);
         this.btnSelect.TabIndex = 1;
         this.btnSelect.Text = "...";
         this.btnSelect.UseVisualStyleBackColor = true;
         this.btnSelect.Click += new System.EventHandler(this.btnSelect_Click);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(3, 5);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(87, 14);
         this.label1.TabIndex = 2;
         this.label1.Text = "Акция на товар";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(374, 6);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(23, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "шт.";
         // 
         // tbQty
         // 
         this.tbQty.Location = new System.Drawing.Point(403, 3);
         this.tbQty.Name = "tbQty";
         this.tbQty.Size = new System.Drawing.Size(57, 20);
         this.tbQty.TabIndex = 4;
         this.tbQty.TextChanged += new System.EventHandler(this.tbQty_TextChanged);
         // 
         // BonusPrice
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.tbQty);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.btnSelect);
         this.Controls.Add(this.tbItem);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Name = "BonusPrice";
         this.Size = new System.Drawing.Size(466, 28);
         this.ResumeLayout(false);
         this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox tbItem;
        private System.Windows.Forms.Button btnSelect;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox tbQty;
    }
}
