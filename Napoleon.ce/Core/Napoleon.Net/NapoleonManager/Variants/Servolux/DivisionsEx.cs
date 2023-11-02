using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DivisionsEx()
      {

         ToolStripButton btn;
         // = new System.Windows.Forms.ToolStripButton();
         //btn.Name = "btnOrgMatrix";
         //btn.Size = new System.Drawing.Size(23, 22);
         //btn.Text = "Матрицы контрагента";
         //btn.Click += new System.EventHandler((s, e) => { new FmMatrixDesignerEx().Show();});
         //btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         //tb.Items.Add(sp);
         //tb.Items.Add(btn);

         //btn = new System.Windows.Forms.ToolStripButton();
         //btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //btn.Image = Properties.Resources.checkbox;
         //btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         //btn.Name = "rttReqReturn";
         //btn.Size = new System.Drawing.Size(23, 22);
         //btn.Text = "Редактор MML";
         //btn.Click += new System.EventHandler((o, e) => { (new FmMMLEditor()).Show(); });

         //tb.Items.Add(btn);

         //btn = new System.Windows.Forms.ToolStripButton();
         //btn.Name = "btnOrgMatrix";
         //btn.Size = new System.Drawing.Size(23, 22);
         //btn.Text = "Продукт дня";
         //btn.Click += new System.EventHandler((s, e) => { new FmDaysGoods().Show(); });
         //btn.DisplayStyle = ToolStripItemDisplayStyle.Text;
         //tb.Items.Add(btn);

         //tb.Items.Add(sp);

         ToolStripSplitButton tsb = new ToolStripSplitButton();
         tsb = new ToolStripSplitButton();
         tsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;

         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnShedule";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Агенты";
         btn.Click += new System.EventHandler((s, e) => { new FmMakeShedule().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         System.Windows.Forms.ToolStripButton btn1 = new System.Windows.Forms.ToolStripButton();
         btn1.Name = "btnSheduleMerch";
         btn1.Size = new System.Drawing.Size(23, 22);
         btn1.Text = "Мерчендайзеры";
         btn1.Click += new System.EventHandler((s, e) => { new FmMakeSheduleMerch().Show(); });
         btn1.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tsb.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] { btn, btn1 });
         tsb.Name = "tsbRoute";
         tsb.Text = "Маршруты";
         tsb.Size = new System.Drawing.Size(158, 22);
         tb.Items.Add(tsb);

         //tb.Items.Add(sp);
         //btn = new System.Windows.Forms.ToolStripButton();
         //btn.Name = "btnNoOrderRsn";
         //btn.Size = new System.Drawing.Size(23, 22);
         //btn.Text = "Причины отказа";
         //btn.Click += new System.EventHandler((s, e) => { new FmNoOrderRsnList().Show(); });
         //btn.DisplayStyle = ToolStripItemDisplayStyle.Text;
         //tb.Items.Add(btn);

         //ToolStripButton retCause;
         //retCause = new System.Windows.Forms.ToolStripButton();
         //retCause.Name = "btnReturnCause";
         //retCause.Size = new System.Drawing.Size(23, 22);
         //retCause.Text = "Причины возврата";
         //retCause.Click += new System.EventHandler((s, e) => { new FmReturnCauseEditor().Show(); });
         //retCause.DisplayStyle = ToolStripItemDisplayStyle.Text;

         //ToolStripButton retLimit = new System.Windows.Forms.ToolStripButton();
         //retLimit.Name = "btnReturnLimit";
         //retLimit.Size = new System.Drawing.Size(23, 22);
         //retLimit.Text = "Лимиты возврата";
         //retLimit.Click += new System.EventHandler((s, e) => { new FmReturnLimitList().Show(); });
         //retLimit.DisplayStyle = ToolStripItemDisplayStyle.Text;


         //tsb = new ToolStripSplitButton();
         //tsb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         //tsb.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
         //   //retCause,
         //   retLimit});
         //tsb.Name = "tsb";
         //tsb.Size = new System.Drawing.Size(108, 22);
         //tsb.Text = "Возвраты";
         //tb.Items.Add(tsb);

         tsbMatrixDesigner.Visible = false;
         tsbOrgRadiusDocs.Visible = false;

         Size = new System.Drawing.Size(Width + 200, Height + 50);
      }

      protected override void CheckData()
      {
         base.CheckData();
      }
   }
}
