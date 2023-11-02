using GRSoft.Network;
using GRSoft.UILib;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
         ToolStripSplitButton tsb = new ToolStripSplitButton();
         tsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         tsb.Image = Properties.Resources.qty2report;
         tsb.ImageTransparentColor = System.Drawing.Color.Magenta;
         tsb.Size = new System.Drawing.Size(23, 22);
         tsb.Text = "Oтчёты";

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         button.Name = "mtxtimw";
         button.Size = new System.Drawing.Size(123, 22);
         button.Text = "Oтчёт по матрице";
         button.Click += new System.EventHandler((s, e) => {
            FmWSReportParams fps = new FmWSReportParams();
            fps.SetReport("Отчет по матрице", "matrix_rep");
            fps.Show();
         });
         tsb.DropDownItems.Add(button);


         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         button.Name = "frgrpt";
         button.Size = new System.Drawing.Size(123, 22);
         button.Text = "Покрытие территории";
         button.Click += new System.EventHandler((s, e) =>
         {
            FmWSReportParams fps = new FmWSReportParams();
            fps.SetReport("Покрытие территории", "coverage_rep");
            fps.Show();
         });
         tsb.DropDownItems.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         button.Name = "problem";
         button.Size = new System.Drawing.Size(123, 22);
         button.Text = "Отчет по проблемам";
         button.Click += new System.EventHandler((s, e) =>
         {
            FmWSReportParams fps = new FmWSReportParams();
            fps.SetReport("Отчет по проблемам", "problem_rep");
            fps.Show();
         });
         tsb.DropDownItems.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         button.Name = "dmp";
         button.Size = new System.Drawing.Size(123, 22);
         button.Text = "Отчет ДМП";
         button.Click += new System.EventHandler((s, e) =>
         {
            FmWSReportParams fps = new FmWSReportParams();
            fps.SetReport("Отчет ДМП", "dmp_rep");
            fps.Show();
         });
         tsb.DropDownItems.Add(button);

         tsbConfig.Items.Add(tsb);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         button.Name = "dmpcheck";
         button.Size = new System.Drawing.Size(123, 22);
         button.Text = "Отчет по просмотрам матриц";
         button.Click += new System.EventHandler((s, e) =>
         {
            FmWSReportParams fps = new FmWSReportParams();
            fps.SetReport("Отчет по просмотрам матриц", "distrcheck_rep");
            fps.rbAgents.Visible = false;
            fps.rbDivisions.Visible = false;
            fps.cbAgents.Visible = false;
            fps.cbDivisions.Visible = false;
            fps.Show();
         });
         tsb.DropDownItems.Add(button);

         tsbConfig.Items.Add(tsb);

      }
   }
}
