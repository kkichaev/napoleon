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

      protected FmScriptEditEx(PostProcess postProcess)
         : base(postProcess)
      {
         cbTypes = new System.Windows.Forms.ComboBox();
         this.cbTypes.FormattingEnabled = true;
         this.cbTypes.Location = new System.Drawing.Point(449, 2);
         this.cbTypes.Name = "cbTypes";
         this.cbTypes.Size = new System.Drawing.Size(154, 22);
         this.cbTypes.TabIndex = 3;

         Label label2 = new Label();
         label2.AutoSize = true;
         label2.Location = new System.Drawing.Point(388, 6);
         label2.Name = "label2";
         label2.Size = new System.Drawing.Size(57, 14);
         label2.TabIndex = 2;
         label2.Text = "Тип точки";

         panel1.Controls.Add(label2);
         panel1.Controls.Add(this.cbTypes);

         tbName.Width = 280;
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left))));

         OrgType ot = new OrgType();
         ot.name = "<Для всех>";
         DataSet<string, OrgType> types = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME);
         if (types != null)
         {
            List<OrgType> src = new List<OrgType>(types.Values);
            src.Sort();
            src.Insert(0, ot);

            src.ForEach(x => cbTypes.Items.Add(x));
         }
         else
            cbTypes.Items.Add(ot);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         
         if(script != null)
            foreach(OrgType ot in cbTypes.Items)
            {
               if(script.type == ot.id)
               {
                  cbTypes.SelectedItem = ot;
                  break;
               }
            }

         cbTypes.SelectedIndexChanged += cbTypes_SelectedIndexChanged;
      }

      void cbTypes_SelectedIndexChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }

      protected override void Save()
      {
         if (script == null)
            script = new ScriptDef();

         OrgType sel = cbTypes.SelectedItem as OrgType;
         if (sel != null)
            script.type = sel.id;
         base.Save();
      }
   }
}
