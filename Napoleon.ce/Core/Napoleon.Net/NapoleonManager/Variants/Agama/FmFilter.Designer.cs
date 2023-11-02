namespace GRSoft.NapoleonManager
{
   partial class FmFilter
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmFilter));
         this.list = new System.Windows.Forms.CheckedListBox();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnSelect = new System.Windows.Forms.Button();
         this.btnReset = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // list
         // 
         this.list.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.list.FormattingEnabled = true;
         this.list.Location = new System.Drawing.Point(1, 1);
         this.list.Name = "list";
         this.list.Size = new System.Drawing.Size(291, 259);
         this.list.TabIndex = 0;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(4, 319);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(88, 319);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 2;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnSelect
         // 
         this.btnSelect.Location = new System.Drawing.Point(1, 268);
         this.btnSelect.Name = "btnSelect";
         this.btnSelect.Size = new System.Drawing.Size(112, 23);
         this.btnSelect.TabIndex = 3;
         this.btnSelect.Text = "Выбрать все";
         this.btnSelect.UseVisualStyleBackColor = true;
         this.btnSelect.Click += new System.EventHandler(this.btnSelect_Click);
         // 
         // btnReset
         // 
         this.btnReset.Location = new System.Drawing.Point(119, 268);
         this.btnReset.Name = "btnReset";
         this.btnReset.Size = new System.Drawing.Size(117, 23);
         this.btnReset.TabIndex = 4;
         this.btnReset.Text = "Сбросить все";
         this.btnReset.UseVisualStyleBackColor = true;
         this.btnReset.Click += new System.EventHandler(this.btnReset_Click);
         // 
         // FmFilter
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(292, 348);
         this.Controls.Add(this.btnReset);
         this.Controls.Add(this.btnSelect);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.list);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmFilter";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "Фильтр";
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.CheckedListBox list;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnSelect;
      private System.Windows.Forms.Button btnReset;
   }
}