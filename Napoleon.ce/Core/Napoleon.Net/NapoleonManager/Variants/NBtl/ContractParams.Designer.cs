
namespace GRSoft.NapoleonManager
{
   partial class ContractParams
   {
      /// <summary> 
      /// Обязательная переменная конструктора.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary> 
      /// Освободить все используемые ресурсы.
      /// </summary>
      /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Код, автоматически созданный конструктором компонентов

      /// <summary> 
      /// Требуемый метод для поддержки конструктора — не изменяйте 
      /// содержимое этого метода с помощью редактора кода.
      /// </summary>
      private void InitializeComponent()
      {
         this.cbPhoto = new System.Windows.Forms.CheckBox();
         this.lbContracts = new System.Windows.Forms.ListBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.lbMatrix = new System.Windows.Forms.ListBox();
         this.label4 = new System.Windows.Forms.Label();
         this.lbItems = new System.Windows.Forms.ListBox();
         this.SuspendLayout();
         // 
         // cbPhoto
         // 
         this.cbPhoto.AutoSize = true;
         this.cbPhoto.Location = new System.Drawing.Point(103, 12);
         this.cbPhoto.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.cbPhoto.Name = "cbPhoto";
         this.cbPhoto.Size = new System.Drawing.Size(75, 21);
         this.cbPhoto.TabIndex = 0;
         this.cbPhoto.Text = "с фото";
         this.cbPhoto.UseVisualStyleBackColor = true;
         // 
         // lbContracts
         // 
         this.lbContracts.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.lbContracts.FormattingEnabled = true;
         this.lbContracts.ItemHeight = 16;
         this.lbContracts.Location = new System.Drawing.Point(8, 37);
         this.lbContracts.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.lbContracts.Name = "lbContracts";
         this.lbContracts.Size = new System.Drawing.Size(449, 68);
         this.lbContracts.TabIndex = 4;
         this.lbContracts.SelectedIndexChanged += new System.EventHandler(this.lbContracts_SelectedIndexChanged);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(5, 14);
         this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(80, 17);
         this.label2.TabIndex = 5;
         this.label2.Text = "Контракты";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(5, 111);
         this.label3.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(68, 17);
         this.label3.TabIndex = 7;
         this.label3.Text = "Матрицы";
         // 
         // lbMatrix
         // 
         this.lbMatrix.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.lbMatrix.FormattingEnabled = true;
         this.lbMatrix.ItemHeight = 16;
         this.lbMatrix.Location = new System.Drawing.Point(9, 130);
         this.lbMatrix.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.lbMatrix.Name = "lbMatrix";
         this.lbMatrix.Size = new System.Drawing.Size(449, 84);
         this.lbMatrix.TabIndex = 6;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(4, 220);
         this.label4.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(77, 17);
         this.label4.TabIndex = 9;
         this.label4.Text = "Категории";
         // 
         // lbItems
         // 
         this.lbItems.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.lbItems.FormattingEnabled = true;
         this.lbItems.ItemHeight = 16;
         this.lbItems.Location = new System.Drawing.Point(8, 240);
         this.lbItems.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.lbItems.Name = "lbItems";
         this.lbItems.Size = new System.Drawing.Size(449, 84);
         this.lbItems.TabIndex = 8;
         // 
         // ContractParams
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.label4);
         this.Controls.Add(this.lbItems);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.lbMatrix);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.lbContracts);
         this.Controls.Add(this.cbPhoto);
         this.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.Name = "ContractParams";
         this.Size = new System.Drawing.Size(468, 337);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      public System.Windows.Forms.CheckBox cbPhoto;
      public System.Windows.Forms.ListBox lbContracts;
      public System.Windows.Forms.ListBox lbMatrix;
      public System.Windows.Forms.ListBox lbItems;
   }
}
