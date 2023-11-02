using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DivisionsEx()
      {
         tsbMatrixDesigner.Visible = false;
         setColor.Visible = false;
         Size = new Size(1200, 500);
         CreateBntOrgEdit();
         CreateBtnSlsnet();
         CreateBtnCity();
         CreateOrgAssignment();
         CreateContractEditor();
         CreateScrAssign();
         CreatePartShelfEdit();
         CreateReturnCauseEdit();
      }

      private void CreateReturnCauseEdit()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnReturnCause";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Причины возврата";
         btn.Click += new System.EventHandler((s, e) => { FmReturnCauseEdit.Open(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }

      private void CreateContractEditor()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnContractEditor";
         btn.Text = "Редактор контрактов";
         btn.Click += new System.EventHandler((s, e) => { new FmContract().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }

      private void CreateScrAssign()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnSlsAssignment";
         btn.Text = "Назначение сценариев";
         btn.Click += new System.EventHandler((s, e) => { new FmScrAssign().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }

      private void CreateOrgAssignment()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnOrgAssignment";
         btn.Text = "Назначение точек";
         btn.Click += new System.EventHandler((s, e) => { new FmOrgAssign().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }

      private void CreateBtnCity()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnCity";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Редактор городов";
         btn.Click += new System.EventHandler((s, e) => { new FmCity().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }

      private void CreateBtnSlsnet()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnSlsnet";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Редактор сетей";
         btn.Click += new System.EventHandler((s, e) => { new FmSlsnet().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }

      private void CreateBntOrgEdit()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnOrgEd";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Редактор организаций";
         btn.Click += new System.EventHandler((s, e) => { new FmOrg().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }

      private void CreatePartShelfEdit()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnPartShelfd";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Доля полки";
         btn.Click += new System.EventHandler((s, e) => { new FmPartShelf().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);
      }
   }
}
