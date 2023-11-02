namespace GRSoft.NapoleonManager
{
    partial class FmActionEdit
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmActionEdit));
            this.tbName = new System.Windows.Forms.TextBox();
            this.tbDescr = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.dtpStart = new System.Windows.Forms.DateTimePicker();
            this.dtpFinish = new System.Windows.Forms.DateTimePicker();
            this.cbCluster = new System.Windows.Forms.ComboBox();
            this.panel1 = new System.Windows.Forms.Panel();
            this.dgvItems = new System.Windows.Forms.DataGridView();
            this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.clmnQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
            this.toolStripButton2 = new System.Windows.Forms.ToolStripButton();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            this.button2 = new System.Windows.Forms.Button();
            this.button3 = new System.Windows.Forms.Button();
            this.label9 = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.btnItem = new System.Windows.Forms.Button();
            this.lbUsed = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.tbQty = new System.Windows.Forms.TextBox();
            this.rbDiscount = new System.Windows.Forms.RadioButton();
            this.rbItem = new System.Windows.Forms.RadioButton();
            this.tbDiscount = new System.Windows.Forms.TextBox();
            this.btnOrg = new System.Windows.Forms.Button();
            this.panel1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
            this.toolStrip1.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tbName
            // 
            this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbName.Location = new System.Drawing.Point(12, 41);
            this.tbName.Name = "tbName";
            this.tbName.Size = new System.Drawing.Size(463, 22);
            this.tbName.TabIndex = 0;
            // 
            // tbDescr
            // 
            this.tbDescr.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbDescr.Location = new System.Drawing.Point(12, 97);
            this.tbDescr.Multiline = true;
            this.tbDescr.Name = "tbDescr";
            this.tbDescr.Size = new System.Drawing.Size(463, 116);
            this.tbDescr.TabIndex = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 21);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(149, 17);
            this.label1.TabIndex = 2;
            this.label1.Text = "Наименование акции";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(12, 77);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(117, 17);
            this.label2.TabIndex = 3;
            this.label2.Text = "Описание акции";
            // 
            // dtpStart
            // 
            this.dtpStart.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dtpStart.Location = new System.Drawing.Point(618, 33);
            this.dtpStart.Name = "dtpStart";
            this.dtpStart.Size = new System.Drawing.Size(289, 22);
            this.dtpStart.TabIndex = 4;
            // 
            // dtpFinish
            // 
            this.dtpFinish.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dtpFinish.Location = new System.Drawing.Point(618, 70);
            this.dtpFinish.Name = "dtpFinish";
            this.dtpFinish.Size = new System.Drawing.Size(289, 22);
            this.dtpFinish.TabIndex = 5;
            // 
            // cbCluster
            // 
            this.cbCluster.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbCluster.AutoCompleteMode = System.Windows.Forms.AutoCompleteMode.Suggest;
            this.cbCluster.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.ListItems;
            this.cbCluster.FormattingEnabled = true;
            this.cbCluster.Location = new System.Drawing.Point(497, 137);
            this.cbCluster.Name = "cbCluster";
            this.cbCluster.Size = new System.Drawing.Size(410, 24);
            this.cbCluster.Sorted = true;
            this.cbCluster.TabIndex = 6;
            // 
            // panel1
            // 
            this.panel1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.panel1.Controls.Add(this.dgvItems);
            this.panel1.Controls.Add(this.toolStrip1);
            this.panel1.Location = new System.Drawing.Point(13, 253);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(894, 182);
            this.panel1.TabIndex = 8;
            // 
            // dgvItems
            // 
            this.dgvItems.AllowUserToAddRows = false;
            this.dgvItems.AllowUserToDeleteRows = false;
            this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnQty});
            this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dgvItems.Location = new System.Drawing.Point(0, 39);
            this.dgvItems.Name = "dgvItems";
            this.dgvItems.RowHeadersVisible = false;
            this.dgvItems.RowTemplate.Height = 24;
            this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvItems.Size = new System.Drawing.Size(894, 143);
            this.dgvItems.TabIndex = 1;
            this.dgvItems.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvItems_CellDoubleClick);
            this.dgvItems.KeyDown += new System.Windows.Forms.KeyEventHandler(this.dgvItems_KeyDown);
            // 
            // clmnItem
            // 
            this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.clmnItem.DataPropertyName = "Name";
            this.clmnItem.HeaderText = "Товар";
            this.clmnItem.Name = "clmnItem";
            this.clmnItem.Resizable = System.Windows.Forms.DataGridViewTriState.True;
            // 
            // clmnQty
            // 
            this.clmnQty.DataPropertyName = "Qty";
            this.clmnQty.HeaderText = "Количество";
            this.clmnQty.Name = "clmnQty";
            // 
            // toolStrip1
            // 
            this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripButton1,
            this.toolStripButton2});
            this.toolStrip1.Location = new System.Drawing.Point(0, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(894, 39);
            this.toolStrip1.TabIndex = 0;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // toolStripButton1
            // 
            this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
            this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolStripButton1.Name = "toolStripButton1";
            this.toolStripButton1.Size = new System.Drawing.Size(36, 36);
            this.toolStripButton1.Text = "Добавить товар";
            this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
            // 
            // toolStripButton2
            // 
            this.toolStripButton2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripButton2.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
            this.toolStripButton2.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.toolStripButton2.Name = "toolStripButton2";
            this.toolStripButton2.Size = new System.Drawing.Size(36, 36);
            this.toolStripButton2.Text = "Удалить товар";
            this.toolStripButton2.Click += new System.EventHandler(this.toolStripButton2_Click);
            // 
            // label3
            // 
            this.label3.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(497, 36);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(116, 17);
            this.label3.TabIndex = 15;
            this.label3.Text = "Срок действия с";
            // 
            // label4
            // 
            this.label4.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(589, 71);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(24, 17);
            this.label4.TabIndex = 16;
            this.label4.Text = "по";
            // 
            // label5
            // 
            this.label5.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(497, 115);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(174, 17);
            this.label5.TabIndex = 17;
            this.label5.Text = "Применить для кластера";
            // 
            // button1
            // 
            this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.button1.Location = new System.Drawing.Point(12, 605);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(174, 40);
            this.button1.TabIndex = 21;
            this.button1.Text = "Завершить досрочно";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // button2
            // 
            this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.button2.Location = new System.Drawing.Point(607, 605);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(124, 40);
            this.button2.TabIndex = 22;
            this.button2.Text = "Отменить";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // button3
            // 
            this.button3.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.button3.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.button3.Location = new System.Drawing.Point(783, 605);
            this.button3.Name = "button3";
            this.button3.Size = new System.Drawing.Size(124, 40);
            this.button3.TabIndex = 23;
            this.button3.Text = "ОК";
            this.button3.UseVisualStyleBackColor = true;
            this.button3.Click += new System.EventHandler(this.button3_Click);
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(13, 226);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(106, 17);
            this.label9.TabIndex = 24;
            this.label9.Text = "Условие акции";
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox1.Controls.Add(this.btnItem);
            this.groupBox1.Controls.Add(this.lbUsed);
            this.groupBox1.Controls.Add(this.label8);
            this.groupBox1.Controls.Add(this.label7);
            this.groupBox1.Controls.Add(this.tbQty);
            this.groupBox1.Controls.Add(this.rbDiscount);
            this.groupBox1.Controls.Add(this.rbItem);
            this.groupBox1.Controls.Add(this.tbDiscount);
            this.groupBox1.Location = new System.Drawing.Point(13, 461);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(894, 118);
            this.groupBox1.TabIndex = 25;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Действие";
            // 
            // btnItem
            // 
            this.btnItem.Location = new System.Drawing.Point(178, 27);
            this.btnItem.Name = "btnItem";
            this.btnItem.Size = new System.Drawing.Size(400, 28);
            this.btnItem.TabIndex = 30;
            this.btnItem.Text = "Выбрать товар...";
            this.btnItem.UseVisualStyleBackColor = true;
            this.btnItem.Click += new System.EventHandler(this.btnItem_Click);
            // 
            // lbUsed
            // 
            this.lbUsed.AutoSize = true;
            this.lbUsed.Location = new System.Drawing.Point(719, 70);
            this.lbUsed.Name = "lbUsed";
            this.lbUsed.Size = new System.Drawing.Size(0, 17);
            this.lbUsed.TabIndex = 29;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(178, 70);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(71, 17);
            this.label8.TabIndex = 28;
            this.label8.Text = "Скидка,%";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Font = new System.Drawing.Font("Microsoft Sans Serif", 7.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label7.Location = new System.Drawing.Point(624, 34);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(244, 17);
            this.label7.TabIndex = 27;
            this.label7.Text = "Количество акционного товара";
            // 
            // tbQty
            // 
            this.tbQty.Location = new System.Drawing.Point(627, 67);
            this.tbQty.Name = "tbQty";
            this.tbQty.Size = new System.Drawing.Size(83, 22);
            this.tbQty.TabIndex = 26;
            // 
            // rbDiscount
            // 
            this.rbDiscount.AutoSize = true;
            this.rbDiscount.Location = new System.Drawing.Point(22, 68);
            this.rbDiscount.Name = "rbDiscount";
            this.rbDiscount.Size = new System.Drawing.Size(76, 21);
            this.rbDiscount.TabIndex = 24;
            this.rbDiscount.TabStop = true;
            this.rbDiscount.Text = "Скидка";
            this.rbDiscount.UseVisualStyleBackColor = true;
            this.rbDiscount.CheckedChanged += new System.EventHandler(this.rbDiscount_CheckedChanged);
            // 
            // rbItem
            // 
            this.rbItem.AutoSize = true;
            this.rbItem.Location = new System.Drawing.Point(22, 30);
            this.rbItem.Name = "rbItem";
            this.rbItem.Size = new System.Drawing.Size(139, 21);
            this.rbItem.TabIndex = 22;
            this.rbItem.TabStop = true;
            this.rbItem.Text = "Товар в подарок";
            this.rbItem.UseVisualStyleBackColor = true;
            this.rbItem.CheckedChanged += new System.EventHandler(this.rbItem_CheckedChanged);
            // 
            // tbDiscount
            // 
            this.tbDiscount.Location = new System.Drawing.Point(255, 67);
            this.tbDiscount.Name = "tbDiscount";
            this.tbDiscount.Size = new System.Drawing.Size(83, 22);
            this.tbDiscount.TabIndex = 21;
            // 
            // btnOrg
            // 
            this.btnOrg.Location = new System.Drawing.Point(497, 180);
            this.btnOrg.Name = "btnOrg";
            this.btnOrg.Size = new System.Drawing.Size(410, 33);
            this.btnOrg.TabIndex = 26;
            this.btnOrg.Text = "Применить для клиента...";
            this.btnOrg.UseVisualStyleBackColor = true;
            this.btnOrg.Click += new System.EventHandler(this.btnOrg_Click);
            // 
            // FmActionEdit
            // 
            this.AcceptButton = this.button3;
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.button2;
            this.ClientSize = new System.Drawing.Size(919, 657);
            this.Controls.Add(this.btnOrg);
            this.Controls.Add(this.label9);
            this.Controls.Add(this.button3);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.cbCluster);
            this.Controls.Add(this.dtpFinish);
            this.Controls.Add(this.dtpStart);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.tbDescr);
            this.Controls.Add(this.tbName);
            this.Controls.Add(this.groupBox1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "FmActionEdit";
            this.Text = "Параметры акции";
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox tbName;
        private System.Windows.Forms.TextBox tbDescr;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.DateTimePicker dtpStart;
        private System.Windows.Forms.DateTimePicker dtpFinish;
        private System.Windows.Forms.ComboBox cbCluster;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.DataGridView dgvItems;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.Button button3;
        private System.Windows.Forms.ToolStripButton toolStripButton1;
        private System.Windows.Forms.ToolStripButton toolStripButton2;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.TextBox tbQty;
        private System.Windows.Forms.RadioButton rbDiscount;
        private System.Windows.Forms.RadioButton rbItem;
        private System.Windows.Forms.TextBox tbDiscount;
        private System.Windows.Forms.Label lbUsed;
        private System.Windows.Forms.Button btnItem;
        private System.Windows.Forms.Button btnOrg;
        private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
        private System.Windows.Forms.DataGridViewTextBoxColumn clmnQty;
    }
}