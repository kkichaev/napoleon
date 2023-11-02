using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmScriptEditEx : FmScriptEdit
   {
      ComboBox cbTypes;
      ComboBox cbChannel;
      CheckBox cbMain;
      DataSet<string, SalesChannel> salesChannels;

      protected FmScriptEditEx(PostProcess postProcess)
         : base(postProcess)
      {
         cbTypes = new System.Windows.Forms.ComboBox();
         this.cbTypes.FormattingEnabled = true;
         this.cbTypes.Location = new System.Drawing.Point(96, 28);
         this.cbTypes.Name = "cbTypes";
         this.cbTypes.Size = new System.Drawing.Size(154, 22);
         this.cbTypes.TabIndex = 3;

         Label label2 = new Label();
         label2.AutoSize = true;
         label2.Location = new System.Drawing.Point(7, 32);
         label2.Name = "label2";
         label2.Size = new System.Drawing.Size(57, 14);
         label2.Text = "Тип визита";

         cbMain = new CheckBox();
         cbMain.Location = new System.Drawing.Point(260, 28);
         cbMain.Size = new System.Drawing.Size(154, 22);
         cbMain.Text = "Основной";

         panel1.Controls.Add(label2);
         panel1.Controls.Add(this.cbTypes);
         panel1.Controls.Add(this.cbMain);

         //tbName.Width = 244;
         //this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left))));
         panel1.Height += 29 + 29;
         panel2.Height -= 29 + 29;
         panel2.Top += 29 + 29;
         groupBox1.Height -= 29 + 29;
         groupBox1.Top += 29 + 29;


         cbChannel = new ComboBox();
         cbChannel.Location = new System.Drawing.Point(96, 28 + 29);
         cbChannel.Name = "cbChannel";
         cbChannel.Size = new System.Drawing.Size(154, 22);
         cbChannel.TabIndex = 3;
         cbChannel.Items.Add(new SalesChannel());

         Label label3 = new Label();
         label3.AutoSize = true;
         label3.Location = new System.Drawing.Point(7, 32 + 29);
         label3.Name = "label2";
         label3.Size = new System.Drawing.Size(57, 14);
         label3.Text = "Канал продаж";

         panel1.Controls.Add(cbChannel);
         panel1.Controls.Add(label3);

         List<string> src = new List<string>(new string[] { "", "В", "М", "Д", "З", "ВМ"});
         src.ForEach(x => cbTypes.Items.Add(x));

         salesChannels = (DataSet<string, SalesChannel>)DataModule.Get(SalesChannel.OBJECT_NAME);
         if (salesChannels != null)
         {
            foreach (SalesChannel sc in salesChannels.Data)
            {
               cbChannel.Items.Add(sc);
            }
         }
      }

      void cbMain_CheckedChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }


      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         if (script != null)
         {
            cbTypes.SelectedItem = script.kind;
            cbMain.Checked = script.isMain > 0;
            if (salesChannels != null)
            {
               foreach (SalesChannel sc in salesChannels.Data)
               {
                  if (script.channel == sc.id)
                  {
                     cbChannel.SelectedItem = sc;
                     break;
                  }
               }
            }
         }

         cbTypes.SelectedIndexChanged += cbSelectedIndexChanged;
         cbChannel.SelectedIndexChanged += cbSelectedIndexChanged;
         cbMain.CheckedChanged += cbMain_CheckedChanged;
      }


      void cbSelectedIndexChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }

      protected override void Save()
      {
         if (script == null)
            script = new ScriptDef();

         string sel = cbTypes.SelectedItem as string;
         if (sel != null)
            script.kind = sel;
         script.isMain = cbMain.Checked ? 1 : 0;

         SalesChannel sc = cbChannel.SelectedItem as SalesChannel;
         if (sc != null)
            script.channel = sc.id;

         base.Save();
      }
   }
}
