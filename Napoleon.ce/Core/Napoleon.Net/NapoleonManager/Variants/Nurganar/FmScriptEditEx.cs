using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class FmScriptEditEx : FmScriptEdit
   {
      CheckBox cbEnableFilter;
      ComboBox cbTareType;

      protected FmScriptEditEx(PostProcess postProcess) : base(postProcess)
      {
         lvDocs.Dock = System.Windows.Forms.DockStyle.None;
         lvDocs.Height = 10;

         Panel panel = new Panel();
         panel.Size = new System.Drawing.Size(groupBox1.Width, 70);
         panel.Dock = DockStyle.Bottom;
         

         groupBox1.Controls.Add(panel);
         lvDocs.Dock = DockStyle.Fill;

         cbEnableFilter = new CheckBox();
         cbEnableFilter.Location = new System.Drawing.Point(10, 0);
         cbEnableFilter.Size = new System.Drawing.Size(groupBox1.Width, 32);
         cbEnableFilter.Text = "Фильтр по типу упаковки при заказе";
         cbEnableFilter.ForeColor = Color.Black;

         panel.Controls.Add(cbEnableFilter);

         cbTareType = new ComboBox();
         cbTareType.Location = new System.Drawing.Point(10, 40);
         cbTareType.Size = new System.Drawing.Size(groupBox1.Width-20, 32);
         
         cbTareType.Items.Add("Кеги");
         cbTareType.Items.Add("Фасовка");

         panel.Controls.Add(cbTareType);

         cbTareType.SelectedIndex = 1;
      }

      private void StateChanged(object sender, EventArgs e)
      {
         MarkDirty(true);
      }

      protected override void BeforeSaveDef()
      {
         base.BeforeSaveDef();

         script.filter = cbEnableFilter.Checked ? 1 : 0;
         script.tareType = cbTareType.SelectedIndex;
      }

      protected override void LoadData()
      {
         base.LoadData();

         if(script != null) 
         {
            cbEnableFilter.Checked = script.filter == 1;
            cbTareType.SelectedIndex = script.tareType;
         }

         cbEnableFilter.CheckedChanged += StateChanged;
         cbTareType.SelectedIndexChanged += StateChanged;
      }
   }
}
