namespace GRSoft.NapoleonAdmin
{
    partial class FmProgSettings
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmProgSettings));
            this.label1 = new System.Windows.Forms.Label();
            this.numCameraRes = new System.Windows.Forms.NumericUpDown();
            this.numGpsFreq = new System.Windows.Forms.NumericUpDown();
            this.label2 = new System.Windows.Forms.Label();
            this.numGpsDist = new System.Windows.Forms.NumericUpDown();
            this.label3 = new System.Windows.Forms.Label();
            this.cbGpsBckg = new System.Windows.Forms.CheckBox();
            this.numSendInterval = new System.Windows.Forms.NumericUpDown();
            this.label4 = new System.Windows.Forms.Label();
            this.numGpsWait = new System.Windows.Forms.NumericUpDown();
            this.label5 = new System.Windows.Forms.Label();
            this.numGpsValid = new System.Windows.Forms.NumericUpDown();
            this.label6 = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.numCameraRes)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsFreq)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsDist)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.numSendInterval)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsWait)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsValid)).BeginInit();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label1.Location = new System.Drawing.Point(103, 64);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(234, 16);
            this.label1.TabIndex = 0;
            this.label1.Text = "Макимальное разрешение камеры";
            // 
            // numCameraRes
            // 
            this.numCameraRes.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.numCameraRes.Location = new System.Drawing.Point(343, 62);
            this.numCameraRes.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.numCameraRes.Name = "numCameraRes";
            this.numCameraRes.Size = new System.Drawing.Size(120, 22);
            this.numCameraRes.TabIndex = 1;
            // 
            // numGpsFreq
            // 
            this.numGpsFreq.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.numGpsFreq.Location = new System.Drawing.Point(343, 101);
            this.numGpsFreq.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.numGpsFreq.Name = "numGpsFreq";
            this.numGpsFreq.Size = new System.Drawing.Size(120, 22);
            this.numGpsFreq.TabIndex = 3;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label2.Location = new System.Drawing.Point(207, 103);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(130, 16);
            this.label2.TabIndex = 2;
            this.label2.Text = "Время опроса, сек.";
            // 
            // numGpsDist
            // 
            this.numGpsDist.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.numGpsDist.Location = new System.Drawing.Point(343, 139);
            this.numGpsDist.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.numGpsDist.Name = "numGpsDist";
            this.numGpsDist.Size = new System.Drawing.Size(120, 22);
            this.numGpsDist.TabIndex = 5;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label3.Location = new System.Drawing.Point(163, 141);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(174, 16);
            this.label3.TabIndex = 4;
            this.label3.Text = "Изменение дистанции, м.";
            // 
            // cbGpsBckg
            // 
            this.cbGpsBckg.AutoSize = true;
            this.cbGpsBckg.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.cbGpsBckg.Location = new System.Drawing.Point(343, 177);
            this.cbGpsBckg.Name = "cbGpsBckg";
            this.cbGpsBckg.Size = new System.Drawing.Size(152, 20);
            this.cbGpsBckg.TabIndex = 6;
            this.cbGpsBckg.Text = "Фоновая передача";
            this.cbGpsBckg.UseVisualStyleBackColor = true;
            // 
            // numSendInterval
            // 
            this.numSendInterval.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.numSendInterval.Location = new System.Drawing.Point(343, 212);
            this.numSendInterval.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.numSendInterval.Name = "numSendInterval";
            this.numSendInterval.Size = new System.Drawing.Size(120, 22);
            this.numSendInterval.TabIndex = 8;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label4.Location = new System.Drawing.Point(230, 214);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(107, 16);
            this.label4.TabIndex = 7;
            this.label4.Text = "Интервал, мин.";
            // 
            // numGpsWait
            // 
            this.numGpsWait.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.numGpsWait.Location = new System.Drawing.Point(343, 250);
            this.numGpsWait.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.numGpsWait.Name = "numGpsWait";
            this.numGpsWait.Size = new System.Drawing.Size(120, 22);
            this.numGpsWait.TabIndex = 10;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label5.Location = new System.Drawing.Point(186, 252);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(151, 16);
            this.label5.TabIndex = 9;
            this.label5.Text = "Ожидание коорд., сек.";
            // 
            // numGpsValid
            // 
            this.numGpsValid.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.numGpsValid.Location = new System.Drawing.Point(343, 288);
            this.numGpsValid.Maximum = new decimal(new int[] {
            10000,
            0,
            0,
            0});
            this.numGpsValid.Name = "numGpsValid";
            this.numGpsValid.Size = new System.Drawing.Size(120, 22);
            this.numGpsValid.TabIndex = 12;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.label6.Location = new System.Drawing.Point(159, 290);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(178, 16);
            this.label6.TabIndex = 11;
            this.label6.Text = "Помнить координаты, мин";
            // 
            // button1
            // 
            this.button1.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.button1.Location = new System.Drawing.Point(287, 369);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(105, 27);
            this.button1.TabIndex = 13;
            this.button1.Text = "Записать";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // FmProgSettings
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(655, 423);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.numGpsValid);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.numGpsWait);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.numSendInterval);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.cbGpsBckg);
            this.Controls.Add(this.numGpsDist);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.numGpsFreq);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.numCameraRes);
            this.Controls.Add(this.label1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "FmProgSettings";
            this.Text = "Настройки программы агента";
            ((System.ComponentModel.ISupportInitialize)(this.numCameraRes)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsFreq)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsDist)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.numSendInterval)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsWait)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.numGpsValid)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.NumericUpDown numCameraRes;
        private System.Windows.Forms.NumericUpDown numGpsFreq;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.NumericUpDown numGpsDist;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.CheckBox cbGpsBckg;
        private System.Windows.Forms.NumericUpDown numSendInterval;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.NumericUpDown numGpsWait;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.NumericUpDown numGpsValid;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Button button1;
    }
}