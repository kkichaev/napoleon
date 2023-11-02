using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmSelectContrAgentEx : FmSelectContrAgent
   {
      ToolStripComboBox cbEntity = new ToolStripComboBox();

      public FmSelectContrAgentEx()
      {
         Size = new System.Drawing.Size(800, 600);

         DataGridViewTextBoxColumn c = new DataGridViewTextBoxColumn();
         c.DataPropertyName = "EntName";
         c.HeaderText = "Название юр. лица";
         c.Name = "entyty";
         c.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;

         dgvOrgs.Columns.Add(c);

         c = new DataGridViewTextBoxColumn();
         c.DataPropertyName = "OrderCreated";
         c.HeaderText = "Дата заказа";
         c.Name = "orderCreated";

         dgvOrgs.Columns.Add(c);

         toolStrip1.Items.Add(new ToolStripLabel("Юр. лицо"));
         cbEntity.Items.Add("Все");
         cbEntity.SelectedIndexChanged += tcbFilter_SelectedIndexChanged;

         IDataSet ds = DataModule.Get(Entity.OBJECT_NAME);

         if (ds != null)
         {
            List<Entity> list = new List<Entity>();

            foreach(GRSoft.Network.DataObject d in ds.Data)
            {
               list.Add((Entity)d);
            }

            list.Sort((x, y) => { return x.name.CompareTo(y.name); });

            foreach (Entity e in list)
               cbEntity.Items.Add(e);
         }

         cbEntity.SelectedIndex = 0;
         toolStrip1.Items.Add(cbEntity);
      }

      protected override bool CheckFilter(Org o)
      {
         bool result = base.CheckFilter(o);

         if(result && cbEntity.SelectedIndex > 0)
         {
            Entity e = cbEntity.SelectedItem as Entity;

            if (e != null)
               result = e.id.Equals(o.entity);
         }

         return result;
      }

      protected override bool ResetFilter()
      {
         return base.ResetFilter() && cbEntity.SelectedIndex == 0;
      }
      
   }
}
